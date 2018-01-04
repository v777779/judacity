package com.example.android.rviewpager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ICallback {

    @Nullable
    @BindView(R.id.recycler)
    RecyclerView  mRecyclerView;

    private Unbinder mUnbinder;
    private RecyclerAdapter mRecyclerAdapter;
    private GridLayoutManager mGridLayout;
    private int mSpan;
    private int mHeight;


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
                startActivity(new Intent(MainActivity.this,DetailActivity.class));
            }
        });

        mUnbinder = ButterKnife.bind(this);
        setupRecycler();

        listImagesLoader();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private void setupRecycler() {
        mSpan = 2;
        mHeight = 150;

        mRecyclerAdapter = new RecyclerAdapter(this, mHeight);
        mRecyclerAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(
                this,
                mSpan,
                GridLayout.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void listImagesLoader() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(String.format("image_%03d.jpg",i+1));
        }

        mRecyclerAdapter.setCursor(list);
    }

    @Override
    public void onCallback(Uri uri, View view) {

    }

    @Override
    public void onCallback(int position) {

        startActivity(new Intent(this,DetailActivity.class));
    }
}
