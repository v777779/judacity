package com.example.android.rviewpager;



import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.example.android.rviewpager.MainActivity.BUNDLE_IMAGE_RESOURCE;
import static com.example.android.rviewpager.MainActivity.listImagesLoader;

public class DetailActivity extends AppCompatActivity implements ICallback {

    @Nullable
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    @Nullable
    @BindView(R.id.viewpager_container)
    ViewPager mPager;

    private List<String> mList;
    private String mImageId;
    private Unbinder mUnbinder;
    private ViewpagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

//        Transition move = TransitionInflater.from(this).inflateTransition(R.transition.move);
// set an exit transition
//        getWindow().setExitTransition(new Explode());
//        getWindow().setSharedElementExitTransition(move);

        postponeEnterTransition();

        setContentView(R.layout.activity_detail_container);


        mUnbinder = ButterKnife.bind(this);
        mList = listImagesLoader();

        Intent intent = getIntent();
        Bundle options = null;
        if (intent != null && intent.hasExtra(BUNDLE_IMAGE_RESOURCE)) {
            mImageId = intent.getStringExtra(BUNDLE_IMAGE_RESOURCE);
            options = intent.getBundleExtra("BUNDLE_OPTIONS");
        }

//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = FragmentDetail.newInstance(mImageId);
//        fm.beginTransaction()
//               .add(R.id.fragment_container,fragment,"fragment")
//                .commit();



        Resources res = getResources();

        mPagerAdapter = new ViewpagerAdapter(getSupportFragmentManager(), null, options);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        res.getInteger(R.integer.pager_side_margin), res.getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPagerMargin)));



// callback
        mPagerAdapter.swap(mList);
        for (int i = 0; i < mList.size(); i++) {
                if(mList.get(i).equals(mImageId)) {
                    mPager.setCurrentItem(i);
                    ((FragmentDetail) mPagerAdapter.getItem(i)).setTransitionName(getString(R.string.transition_image));
                    break;
                }
        }

    }



// callbacks

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();

    }


    @Override
    public void onCallback(Uri uri, View view) {

    }

    @Override
    public void onCallback(int mode) {

    }

    @Override
    public void onCallback(View view, int position) {

    }
}
