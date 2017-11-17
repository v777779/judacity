package ru.vpcb.btplay.network;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import java.net.URL;

import ru.vpcb.btplay.utils.NetworkData;

import static ru.vpcb.btplay.utils.Constants.*;

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
// stub for future
                        int type = args.getInt(BUNDLE_LOADER_RECIPE_ID);   //bundle.put()
// network util
                    URL url = NetworkData.buildUrl();
                    String searchResult = NetworkData.getResponseFromHttpUrl(url);

                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_LOADER_RECIPE_ID,RECIPE_RESPONSE_ID); //bundle.put()
                    bundle.putString(BUNDLE_LOADER_STRING_ID,searchResult); //bundle.put()
                    return bundle;
                } catch (Exception e) {

                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_LOADER_RECIPE_ID, RECIPE_EMPTY_ID);
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
        int id = data.getInt(BUNDLE_LOADER_RECIPE_ID);
        String s = data.getString(BUNDLE_LOADER_STRING_ID);
        if (id == RECIPE_EMPTY_ID || s == null || s.isEmpty()) {
            mCallback.showError();
        } else {
            mCallback.onComplete(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Bundle> loader) {
    }

}
