package ru.vpcb.btplay;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.vpcb.btplay.network.LoaderDb;
import ru.vpcb.btplay.network.LoaderUri;
import ru.vpcb.btplay.utils.NetworkData;
import ru.vpcb.btplay.utils.FragmentData;
import ru.vpcb.btplay.utils.RecipeData;

import static ru.vpcb.btplay.utils.Constants.BUNDLE_LOADER_STRING_ID;
import static ru.vpcb.btplay.utils.Constants.LOADER_RECIPES_DB_ID;
import static ru.vpcb.btplay.utils.Constants.LOADER_RECIPES_ID;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentMain extends Fragment implements IFragmentHelper,
        LoaderUri.ICallbackUri, LoaderDb.ICallbackDb {

    private List<String> mList;
    private RecyclerView mRecyclerView;
    private FragmentMainAdapter mRecyclerAdapter;
    private int mSpan;
    private LoaderUri mLoader;
    private LoaderDb mLoaderDb;

    // test!!!
    private Toast mToast;


    public FragmentMain() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mLoader = new LoaderUri(getContext(), this);
        mLoaderDb = new LoaderDb(getContext(), this);

        if (NetworkData.isOnline(getContext())) {
            getLoaderManager().initLoader(LOADER_RECIPES_ID, new Bundle(), mLoader); // empty bundle FFU
        } else {
            showError();
        }

        getLoaderManager().initLoader(LOADER_RECIPES_DB_ID, null, mLoaderDb); // empty bundle FFU

        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_main_recycler, container, false);
        mList = FragmentData.loadMockCards();                               // load mock data

        mRecyclerView = rootView.findViewById(R.id.fc_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);                          // connect to LayoutManager
        mRecyclerView.setHasFixedSize(true);                                    // item size fixed
        mRecyclerAdapter = new FragmentMainAdapter(rootView.getContext(), this);     //context  and data
        mRecyclerView.setAdapter(mRecyclerAdapter);

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        getLoaderManager().restartLoader(LOADER_RECIPES_ID, new Bundle(), mLoader); // empty bundle FFU
//    }

    @Override
    public void onCallback(int position) {
//        Toast.makeText(getContext(),"Clicked position: "+position,Toast.LENGTH_SHORT).show();


        FragmentDetail detailFragment = new FragmentDetail();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();

        Snackbar.make(getView(), "Clicked position: " + position + " stack: " +
                fragmentManager.getBackStackEntryCount(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public List<String> getList() {
        return new ArrayList<>(mList);
    }

    @Override
    public List<FragmentDetailItem> getItemList() {
        return null;
    }

    @Override
    public RecyclerView getRecycler() {
        return null;
    }


    @Override
    public void showProgress() {
        showToast("Main Progress");
    }

    @Override
    public void onComplete(Bundle data) {
        String s = null;
        if (data != null) {
            s = data.getString(BUNDLE_LOADER_STRING_ID);
        }
        final List<RecipeItem> listRecipeItem = RecipeItem.getRecipeList(s);

        showToast("Main Complete");

        new AsyncTask<Void, Void,Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return RecipeData.bulkInsert(getActivity().getContentResolver(),
                        getLoaderManager(),listRecipeItem, mLoaderDb);
            }
        }.execute();
    }

    @Override
    public void showError() {
        showToast("Main Error");
    }

    @Override
    public void onComplete(Cursor cursor) {
        if (cursor == null || mRecyclerAdapter == null) {   // нет адаптера выходим
            showToast("Main No Database");
            return;
        }
        mRecyclerAdapter.swapCursor(cursor);
        showToast("Main Database Complete");

    }

    @Override
    public void onReset() {

    }

    // test!!!
    private void showToast(final String s) {

        if (mToast != null) {
            mToast.cancel();
        }
        Snackbar.make(getView(),s, BaseTransientBottomBar.LENGTH_SHORT).show();
//        mToast = Toast.makeText(getContext(), s, Toast.LENGTH_SHORT);
//        mToast.show();

    }
}
