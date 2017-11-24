package ru.vpcb.btplay;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.vpcb.btplay.network.LoaderDb;
import ru.vpcb.btplay.network.LoaderUri;
import ru.vpcb.btplay.utils.NetworkData;
import ru.vpcb.btplay.utils.RecipeData;

import static ru.vpcb.btplay.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.btplay.utils.Constants.*;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentMain extends Fragment implements IFragmentHelper,
        LoaderUri.ICallbackUri, LoaderDb.ICallbackDb {


    private RecyclerView mRecyclerView;
    private FragmentMainAdapter mRecyclerAdapter;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;

    private LoaderUri mLoader;
    private LoaderDb mLoaderDb;
    private int mSpan;
    private int mSpanHeight;


    private List<RecipeItem> mList;
    private IFragmentCallback mFragmentCallback;
    private Context mContext;

    public FragmentMain() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
// loaders here for dynamic fragments
// loaders for static fragments can be in onViewCreated
        mLoader = new LoaderUri(getContext(), this);
        mLoaderDb = new LoaderDb(getContext(), this);
        if (NetworkData.isOnline(getContext())) {
            getLoaderManager().initLoader(LOADER_RECIPES_ID, new Bundle(), mLoader); // empty bundle FFU
        }
        getLoaderManager().initLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU

        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_main_recycler, container, false);
        // load mock data

        mRecyclerView = rootView.findViewById(R.id.fc_recycler);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setDisplayMetrics();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), mSpan);
        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                    // item size fixed

        mRecyclerAdapter = new FragmentMainAdapter(mContext, this);     //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mProgressBar = rootView.findViewById(R.id.progress_bar);
        mErrorMessage = rootView.findViewById(R.id.error_message);

// loaders

        if (!NetworkData.isOnline(getContext())) {
            showError();
        }

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mFragmentCallback = (IFragmentCallback) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        mContext = context;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        getLoaderManager().restartLoader(LOADER_RECIPES_ID, new Bundle(), mLoader); // empty bundle FFU
//    }


    @Override
    public void onCallback(int position) {
        FragmentDetail detailFragment = new FragmentDetail();

        RecipeItem recipeItem = mFragmentCallback.getRecipe(position);
        Bundle detailArgs = new Bundle();
        detailArgs.putString(RECIPE_POSITION, new Gson().toJson(recipeItem));
        detailFragment.setArguments(detailArgs);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();

//        Snackbar.make(getView(), "Clicked Fragment Main position: " + position + " stack: " +
//                fragmentManager.getBackStackEntryCount(), Snackbar.LENGTH_SHORT)
//                .setAction("Action", null).show();
    }


    @Override
    public List<FragmentDetailItem> getItemList() {
        return null;
    }

    @Override
    public int getSpanHeight() {
        return mSpanHeight;
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showResult() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onComplete(Bundle data) {
        showResult();

        String s = null;
        if (data != null) {
            s = data.getString(BUNDLE_LOADER_STRING_ID);
        }

        try {
            Type listType = new TypeToken<List<RecipeItem>>() {
            }.getType();
            List<RecipeItem> list = new Gson().fromJson(s, listType);
// test!!!
//        TestData.addImages(list);
            RecipeData.bulkInsertBackground(mContext.getContentResolver(), getLoaderManager(), list, mLoaderDb);
        } catch (JsonSyntaxException e) {
            Log.d(TAG_FMAIN, e.getMessage());
        }

    }


    @Override
    public void onComplete(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0 || mRecyclerAdapter == null) {   // нет адаптера выходим
            return;
        }
        showResult(); // только после загрузки базы данных
        if (!NetworkData.isOnline(getContext())) {
            Snackbar.make(getView(), "No connection. Local data used", Snackbar.LENGTH_LONG).show();
        }
        List<RecipeItem> list = new ArrayList<>();
        Gson gson = new GsonBuilder()
//                    .setLenient()
//                    .setPrettyPrinting()
                .create();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                String recipeJson = cursor.getString(cursor.getColumnIndex(COLUMN_RECIPE_VALUE));
                RecipeItem recipeItem = gson.fromJson(recipeJson, RecipeItem.class);
                list.add(recipeItem);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        mFragmentCallback.setRecipeList(list);
        mRecyclerAdapter.swapCursor(cursor);
    }

    @Override
    public void onReset() {

    }


    private void setDisplayMetrics() {
        DisplayMetrics dp = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dp);
        boolean isLand = dp.widthPixels > dp.heightPixels;
        double width = dp.widthPixels / dp.density;

        if (!isLand) {
            mSpan = 1;
            if (width >= HIGH_WIDTH_PORTRAIT) {
                mSpan = (int) Math.round(width / HIGH_SCALE_PORTRAIT);
                mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);
            } else {
                mSpan = (int) Math.round(width / LOW_SCALE_PORTRAIT);
                mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);
            }
        } else {
            if (width >= HIGH_WIDTH_LANDSCAPE) {
                mSpan = (int) Math.round(width / HIGH_SCALE_LANDSCAPE);
                mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);
            } else {
                mSpan = (int) Math.round(width / LOW_SCALE_LANDSCAPE);
                mSpanHeight = (int) (dp.widthPixels / mSpan / SCREEN_RATIO);
            }
        }

        if (mSpan < MIN_SPAN) mSpan = MIN_SPAN;
        if (mSpan > MAX_SPAN) mSpan = MAX_SPAN;
        if (mSpanHeight < MIN_HEIGHT) mSpanHeight = MIN_HEIGHT;

    }


}
