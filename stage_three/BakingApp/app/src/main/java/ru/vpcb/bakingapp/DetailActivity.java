package ru.vpcb.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;

import ru.vpcb.bakingapp.data.LoaderDb;
import ru.vpcb.bakingapp.data.RecipeItem;
import timber.log.Timber;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static ru.vpcb.bakingapp.MainActivity.getLoadPreference;
import static ru.vpcb.bakingapp.MainActivity.isOnline;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_EXPANDED;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_INTENT;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_DETAIL_WIDGET_FILLED;
import static ru.vpcb.bakingapp.utils.Constants.BUNDLE_ERROR_CONNECTION;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_ERROR_NAME;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_ERROR_TAG;
import static ru.vpcb.bakingapp.utils.Constants.FRAGMENT_PLAYER_NAME;
import static ru.vpcb.bakingapp.utils.Constants.LOADER_RECIPES_DB_ID;
import static ru.vpcb.bakingapp.utils.Constants.MESSAGE_ERROR_ID;
import static ru.vpcb.bakingapp.utils.Constants.MESSAGE_PLAYER_ID;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_SCREEN_WIDE;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_STEP_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.STEP_DEFAULT_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_SHOW_FLAGS;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_RECIPE_ID;
import static ru.vpcb.bakingapp.utils.Constants.WIDGET_WIDGET_ID;
import static ru.vpcb.bakingapp.widget.RecipeWidgetService.startFillWidgetAction;

public class DetailActivity extends AppCompatActivity implements IFragmentHelper, LoaderDb.ICallbackDb {

    private RecyclerView mRecyclerView;
    private DetailAdapter mRecyclerAdapter;
    private int mPosition;
    private boolean mIsWide;
    private RecipeItem mRecipeItem;
    private Context mContext;
    private boolean mIsExpanded;
    private View mRootView;
    private String mWidgetId;
    private String mRecipeId;
    private boolean mIsWidgetFilled;
    private LoaderDb mLoaderDb;
    private Cursor mCursor;
    private boolean mIsLoadImages;
    private boolean mIsSavedInstance;
    private boolean mIsErrorShowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // обязательно без Manifest.PARENT
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

        mContext = this;
        mRootView = findViewById(R.id.fragment_container);
// timber
        if (!MainActivity.mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            MainActivity.mIsTimber = true;
        }
// preferences
        mIsLoadImages = getLoadPreference(mContext);
// intent
        mRecipeItem = null;
        try {
            Intent intent = getIntent();
            Bundle detailArgs = intent.getBundleExtra(BUNDLE_DETAIL_INTENT);
            mWidgetId = detailArgs.getString(WIDGET_WIDGET_ID, "");
            mRecipeId = detailArgs.getString(WIDGET_RECIPE_ID, "");
        } catch (Exception e) {
            Timber.d(e.getMessage());
            finish();
            return;
        }

// savedInstance
        mIsSavedInstance = savedInstanceState != null;
        if (savedInstanceState != null) {
            mIsExpanded = savedInstanceState.getBoolean(BUNDLE_DETAIL_EXPANDED, false);
            mPosition = savedInstanceState.getInt(BUNDLE_DETAIL_POSITION, STEP_DEFAULT_POSITION);
            mIsWidgetFilled = savedInstanceState.getBoolean(BUNDLE_DETAIL_WIDGET_FILLED, false);
            mIsErrorShowed = savedInstanceState.getBoolean(BUNDLE_ERROR_CONNECTION, false);

        } else {
            mPosition = STEP_DEFAULT_POSITION;
            mIsExpanded = false;
            mIsWidgetFilled = mWidgetId.isEmpty();
            mIsErrorShowed = false;
        }

        mRecyclerView = mRootView.findViewById(R.id.fc_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);


        mRecyclerView.setLayoutManager(layoutManager);                              // connect to LayoutManager
        mRecyclerView.setHasFixedSize(false);                                       // item size fixed
        mRecyclerAdapter = new DetailAdapter(mContext, this, mRecipeItem, mIsLoadImages);      //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setExpanded(mIsExpanded);
        mRecyclerView.setHasFixedSize(true);
        mIsWide = mRootView.findViewById(R.id.fc_p_container) != null;

//        if (mIsWide &&  savedInstanceState == null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentPlayer playerFragment = getFragmentPlayer();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.fc_p_container, playerFragment)
//                    .addToBackStack(FRAGMENT_PLAYER_NAME)
//                    .commit();
//        }

// show fill widget n=button
        if (!mIsWidgetFilled) {
            mRootView.findViewById(R.id.widget_button).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(R.id.widget_button).setVisibility(View.GONE);
        }

// loader for one recipe
        mLoaderDb = new LoaderDb(this, this, mRecipeId);
        getSupportLoaderManager().initLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU

        mRootView.setSystemUiVisibility(SYSTEM_UI_SHOW_FLAGS);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mIsWide) {
//                getSupportFragmentManager().popBackStack(FRAGMENT_PLAYER_NAME, POP_BACK_STACK_INCLUSIVE);
                finish();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_DETAIL_EXPANDED, mIsExpanded);
        outState.putInt(BUNDLE_DETAIL_POSITION, mPosition);
        outState.putBoolean(BUNDLE_DETAIL_WIDGET_FILLED, mIsWidgetFilled);
        outState.putBoolean(BUNDLE_ERROR_CONNECTION, mIsErrorShowed);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU

    }

    private String getRecipeName() {
        if (mRecipeItem == null) {
            return "";
        }
        return mRecipeItem.getName();

    }

    public void onClickTapWidget(View view) {
        startFillWidgetAction(mContext, mRecipeId, mWidgetId);
        mRootView.findViewById(R.id.widget_button).setVisibility(View.GONE);
        Snackbar.make(mRootView, getString(R.string.widget_list_added), Snackbar.LENGTH_SHORT).show();
//        Toast.makeText(mContext, getString(R.string.widget_list_added), Toast.LENGTH_LONG).show();
        mIsWidgetFilled = true;
    }

    @Override
    public void onCallback(int position) {
        mPosition = position;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentPlayer playerFragment = getFragmentPlayer();

        fragmentManager.popBackStack(FRAGMENT_PLAYER_NAME, POP_BACK_STACK_INCLUSIVE);

        if (mIsWide) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, playerFragment)
                    .addToBackStack(FRAGMENT_PLAYER_NAME)
                    .commit();
        }
    }

    private FragmentPlayer getFragmentPlayer() {
        FragmentPlayer playerFragment = new FragmentPlayer();
        Bundle playerArgs = new Bundle();
        playerArgs.putString(RECIPE_POSITION, new Gson().toJson(mRecipeItem));
        playerArgs.putInt(RECIPE_STEP_POSITION, mPosition);
        playerArgs.putBoolean(RECIPE_SCREEN_WIDE, mIsWide);
        playerFragment.setArguments(playerArgs);
        return playerFragment;
    }


    private RecipeItem getRecipeItem(Cursor cursor) {
        RecipeItem recipeItem = null;
        try {
            cursor.moveToFirst();
            String recipeJson = cursor.getString(mCursor.getColumnIndex(COLUMN_RECIPE_VALUE));
            recipeItem = new Gson().fromJson(recipeJson, RecipeItem.class);
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
        return recipeItem;
    }

    public void showErrorDialog() {
        FragmentError fragmentError = new FragmentError();
        fragmentError.setLayoutId(R.layout.fragment_error_detail);
        fragmentError.setStyle(R.style.dialog_title_style, R.style.CustomDialog);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(FRAGMENT_ERROR_NAME, POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(fragmentError, FRAGMENT_ERROR_TAG);
        ft.addToBackStack(FRAGMENT_ERROR_NAME);
        ft.commit();

    }

    private void showPlayerFragment() {
        if (mIsWide && !mIsSavedInstance) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentPlayer playerFragment = getFragmentPlayer();
            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .addToBackStack(FRAGMENT_PLAYER_NAME)
                    .commit();
        }
    }

    private void showErrorHandler() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_ERROR_ID) {
                    showError();
                    showErrorDialog();
                }
            }
        };
        handler.sendEmptyMessage(MESSAGE_ERROR_ID);
    }

    private void showPlayerHandler() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_PLAYER_ID) {
                    showPlayerFragment();
                }
            }
        };
        handler.sendEmptyMessage(MESSAGE_PLAYER_ID);
    }

    public void showError() {
        if (mIsErrorShowed) return;
        Snackbar.make(mRootView, getString(R.string.message_error), Snackbar.LENGTH_LONG).show();
        Timber.d(getString(R.string.message_error));
        mIsErrorShowed = true;
    }


    @Override
    public void onComplete(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0 || mRecyclerAdapter == null) {   // нет адаптера выходим
            showErrorHandler();
            return;
        }
//        showResult(); // только после загрузки базы данных
        if (!isOnline(mContext)) {
            showError();
        }

        mCursor = cursor;
        mRecipeItem = getRecipeItem(mCursor);
        mRecyclerAdapter.swapRecipe(mRecipeItem);
        showPlayerHandler();

    }

    @Override
    public void onReset() {

    }
}
