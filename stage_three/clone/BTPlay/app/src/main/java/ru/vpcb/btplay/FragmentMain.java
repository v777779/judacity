package ru.vpcb.btplay;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.vpcb.btplay.network.LoaderDb;
import ru.vpcb.btplay.network.LoaderUri;
import ru.vpcb.btplay.utils.NetworkData;
import ru.vpcb.btplay.utils.FragmentData;
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
        try {
            Bundle detailArgs = new Bundle();
            detailArgs.putString(RECIPE_POSITION, new Gson().toJson(recipeItem));
            detailFragment.setArguments(detailArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Type listType = new TypeToken<List<RecipeItem>>() {
        }.getType();
        List<RecipeItem> list = new Gson().fromJson(s, listType);
//test!!!
        String[] links = new String[]{
                "http://i.ndtvimg.com/i/2015-08/10-best-baking-recipes-2_625x350_81438697411.jpg",
                "http://www.fnstatic.co.uk/images/content/recipe/forest-fruit-cake.jpg",
                "http://www.bakewithstork.com/assets/Recipes/_resampled/croppedimage733456-Birthday-Cake-with-Cream-and-Fresh-Fruit.jpg",
                "http://www.countrycrock.com/Images/347/347-1131912-Baked_Goods_recipe_Landing_Hero.png"
        };
        String[] linkThumbs = new String[]{
                "http://www.rudyanddelilah.com/wp-content/uploads/2017/06/Grandma-Rudys-Frozen-Grape-Salad-Thumbnail-600x570.jpg",
                "https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/BBB84-Chocolate-Cake-Thumbnail-FINAL-landscape.jpeg",
                "http://cdn-image.myrecipes.com/sites/default/files/image/recipes/ck/04/10/orange-cake-ck-701058-x.jpg",
                "http://img.taste.com.au/GdH23_iI/w720-h480-cfill-q80/taste/2016/11/orange-almond-sour-cream-cake-2332-1.jpeg",
                "https://renditions-tastemade.akamaized.net/61426465-hasselback-baked-apple-lc/thumbnail-1920x1080-00001.png",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSZK7FntIsI7rJ_lHW3Gwa6CdKgteg7wpzSP4xtPCaIGxuuY0GX",
                "https://www.biggerbolderbaking.com/wp-content/uploads/2015/05/BBB66-Brownie-Layer-Cake-Thumbnail-v.1-1024x576.jpg",
                "https://www.biggerbolderbaking.com/wp-content/uploads/2014/09/BBB32-Homemade-Donuts-Thumbnail-newest-1024x576.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/e/ec/Cinnamon_rolls%2C_ready_for_cutting_and_baking.jpg",
                "http://cdn.playbuzz.com/cdn/065503d0-c66b-41f7-8b5b-9dad0cfd018a/20330f15-4343-42dc-baea-bcb702896f49.jpg",
                "https://realfood.tesco.com/media/images/BramelyAppleTart_HERO-a3bdb18a-e8dc-46c4-84e8-f7505faf7fb2-0-472x310.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1irZ8PRAUBDJy7eRSq3zNmWizICUCSP3vr5aiqP7P_SIySrfk",
                "https://i0.wp.com/bakingamoment.com/wp-content/uploads/2016/09/9901featured2.jpg?resize=720%2C720&ssl=1",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOQvBvITmMUX4enzf1ENsHa12umUnYnAl04z2-CjFhzWAdXr0UNQ",
                "http://www.waitrose.com/content/dam/waitrose/recipes/images/m/WRWK081216_Martha-Collison_Pudding_Macarons.jpg/_jcr_content/renditions/cq5dam.thumbnail.400.400.png",
                "http://3o45wf6y35-flywheel.netdna-ssl.com/wp-content/uploads/2014/08/pumpkin-pie-mini-tarts-thumbnail-1024x687.jpg",
        };
        Random rnd = new Random();
        for (RecipeItem recipeItem : list) {
            if (recipeItem.getImage().isEmpty()) {
                recipeItem.setImage(links[rnd.nextInt(links.length)]);
            }
            List<RecipeItem.Step> steps = recipeItem.getSteps();
            for (RecipeItem.Step step : steps) {
                if (step.getThumbnailURL().isEmpty()) {
                    step.setThumbnailURL(linkThumbs[rnd.nextInt(linkThumbs.length)]);
                }
            }
        }
// test!!!

        RecipeData.bulkInsertBackground(mContext.getContentResolver(), getLoaderManager(), list, mLoaderDb);

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
            } catch (Exception e) {
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
