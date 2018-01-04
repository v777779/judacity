package com.example.android.rviewpager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.example.android.rviewpager.MainActivity.BUNDLE_IMAGE_RESOURCE;

public class DetailActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;

    @Nullable
    @BindView(R.id.toolbar_image)
    ImageView mToolbarImage;

    @Nullable
    @BindView(R.id.fab)
    FloatingActionButton mFab;



    private String mImageId;

    private Unbinder mUnbinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

// set an exit transition
        getWindow().setExitTransition(new Explode());
        getWindow().setSharedElementExitTransition(new Explode());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUnbinder = ButterKnife.bind(this);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(BUNDLE_IMAGE_RESOURCE)) {
            mImageId = intent.getStringExtra(BUNDLE_IMAGE_RESOURCE);
        }

        loadImageFromAssets(mImageId);


        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private boolean isActive;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                isActive = true;
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    mFab.animate().scaleX(0).scaleY(0).setDuration(250).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                          isActive = false;
                        }
                    }).start();

                } else {
                    //Expanded
                    mFab.animate().scaleX(1).scaleY(1).setDuration(250).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            isActive = false;
                        }
                    }).start();

                }


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private void loadImageFromAssets(String imageName) {
        try {
            InputStream in = getAssets().open("images/" + imageName);
            Drawable drawable = Drawable.createFromStream(in, null);
            mToolbarImage.setImageDrawable(drawable);

        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
    }


}
