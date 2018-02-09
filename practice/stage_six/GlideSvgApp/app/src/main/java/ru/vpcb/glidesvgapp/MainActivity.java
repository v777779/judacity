package ru.vpcb.glidesvgapp;

import android.content.ContentResolver;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Preconditions;

import java.io.File;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView mImageRes;
    private ImageView mImageNet;
    private RequestBuilder<PictureDrawable> requestBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                loadNet();
//                cycleScaleType(view);
            }
        });

        //glide
        mImageRes = findViewById(R.id.image_res);
        mImageNet = findViewById(R.id.image_net);

        requestBuilder = GlideApp.with(this)
                .as(PictureDrawable.class)
                .placeholder(R.drawable.back_copy2)
                .error(R.drawable.image_error)
//                .transition(withCrossFade())  // switched off to show image only
                .listener(new SvgSoftwareLayerSetter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            cycleScaleType(getWindow().getDecorView());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void clearImage(View view) {
        mImageRes.setImageResource(R.drawable.dot_dot_dot);
        mImageNet.setImageResource(R.drawable.dot_dot_dot);
    }

    public void clearCache(View v) {
        Log.w(TAG, "clearing cache");
        GlideRequests glideRequests = GlideApp.with(this);
        glideRequests.clear(mImageRes);
        glideRequests.clear(mImageNet);
        GlideApp.get(this).clearMemory();
        File cacheDir = Preconditions.checkNotNull(Glide.getPhotoCacheDir(this));
        if (cacheDir.isDirectory()) {
            for (File child : cacheDir.listFiles()) {
                if (!child.delete()) {
                    Log.w(TAG, "cannot delete: " + child);
                }
            }
        }
       reload();
    }

    public void cycleScaleType(View v) {
        ImageView.ScaleType curr = mImageRes.getScaleType();
        Log.w(TAG, "cycle: current=" + curr);
        ImageView.ScaleType[] all = ImageView.ScaleType.values();
        int nextOrdinal = (curr.ordinal() + 1) % all.length;
        ImageView.ScaleType next = all[nextOrdinal];
        Log.w(TAG, "cycle: next=" + next);
        mImageRes.setScaleType(next);
        mImageNet.setScaleType(next);
        reload();
    }

    private void reload() {
        loadRes();
        loadNet();
    }

    private void loadRes() {
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() +
                "/" + R.raw.android_toy_h);
        requestBuilder.load(uri).into(mImageRes);
    }

    private void loadNetStandard(View view) {
        String imageURL = "http://upload.wikimedia.org/wikipedia/de/3/3c/AS_Monaco.svg";
                Glide.with(this)
                .load(imageURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.empty_loading)
                        .error(R.drawable.error_loading)
                )
                .into(mImageNet);

    }

    private void loadNet() {
//        Uri uri = Uri.parse("http://www.clker.com/cliparts/u/Z/2/b/a/6/android-toy-h.svg");
        Uri uri = Uri.parse("http://upload.wikimedia.org/wikipedia/de/3/3c/AS_Monaco.svg");
        requestBuilder.load(uri).into(mImageNet);
    }


}
