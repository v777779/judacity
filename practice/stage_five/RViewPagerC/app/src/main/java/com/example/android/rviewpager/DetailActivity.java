package com.example.android.rviewpager;



import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
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
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    private String mImageId;
    private Unbinder mUnbinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        Transition move = TransitionInflater.from(this).inflateTransition(R.transition.move);
// set an exit transition
        getWindow().setExitTransition(new Explode());
        getWindow().setSharedElementExitTransition(move);

        setContentView(R.layout.activity_detail_container);



        mUnbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(BUNDLE_IMAGE_RESOURCE)) {
            mImageId = intent.getStringExtra(BUNDLE_IMAGE_RESOURCE);
        }


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = FragmentDetail.newInstance(mImageId);
        fm.beginTransaction()
               .add(R.id.fragment_container,fragment,"fragment")
                .commit();

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


}
