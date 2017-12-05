package ru.vpcb.popularmovie;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import java.net.URL;
import ru.vpcb.popularmovie.utils.NetworkUtils;

import static ru.vpcb.popularmovie.utils.Constants.BUNDLE_LOADER_MOVIE_ID;
import static ru.vpcb.popularmovie.utils.Constants.BUNDLE_LOADER_PAGE_ID;
import static ru.vpcb.popularmovie.utils.Constants.BUNDLE_LOADER_QUERY_ID;
import static ru.vpcb.popularmovie.utils.Constants.BUNDLE_LOADER_STRING_ID;
import static ru.vpcb.popularmovie.utils.Constants.EMPTY_ID;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public class LoaderUri implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Bundle> {
    private Context mContext;
    private ICallbackUri mCallback;

    public interface ICallbackUri {
        void showProgress();
        void onComplete(Bundle data);
        void showError();
    }

    public LoaderUri(Context context, ICallbackUri callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public Loader<Bundle> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<Bundle>(mContext) {
            private Bundle mCached;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                if (mCached != null) {

                    deliverResult(mCached);        // выдать кэшированные данные
                } else {
                    mCallback.showProgress();
                    forceLoad();                  // запускать только если результата нет
                }
            }

            @Override
            public Bundle loadInBackground() {
                try {
                    int type = args.getInt(BUNDLE_LOADER_QUERY_ID);
                    int page = args.getInt(BUNDLE_LOADER_PAGE_ID);
                    int id = args.getInt(BUNDLE_LOADER_MOVIE_ID);
                    URL url = NetworkUtils.buildUrl(type,page,id);
                    String searchResult = NetworkUtils.getResponseFromHttpUrl(url);

                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_LOADER_QUERY_ID,args.getInt(BUNDLE_LOADER_QUERY_ID));
                    bundle.putString(BUNDLE_LOADER_STRING_ID,searchResult);
                    return bundle;
                } catch (Exception e) {

                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_LOADER_QUERY_ID, EMPTY_ID);
                    bundle.putString(BUNDLE_LOADER_STRING_ID,e.toString()); // e.printStackTrace();
                    return bundle;
                }
            }

            @Override
            public void deliverResult(Bundle data) {
                mCached = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Bundle> loader, Bundle data) {
        int id = data.getInt(BUNDLE_LOADER_QUERY_ID);
        String s = data.getString(BUNDLE_LOADER_STRING_ID);
        if (id == EMPTY_ID || s == null || s.isEmpty()) {
            mCallback.showError();
        } else {
            mCallback.onComplete(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Bundle> loader) {
    }

}
