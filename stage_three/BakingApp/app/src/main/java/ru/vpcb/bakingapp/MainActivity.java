package ru.vpcb.bakingapp;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IFragmentCallback {

    List<RecipeItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // обязательно без Manifest.PARENT
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

        setContentView(R.layout.fragment_main);         // dynamic version container
        if (savedInstanceState == null) {
            FragmentMain mainFragment = new FragmentMain();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mainFragment)
                    .commit();
        }

        mList = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public RecipeItem getRecipe(int position) {
        if (mList == null || mList.isEmpty() || position < 0 || position > mList.size() - 1) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public void setRecipeList(List<RecipeItem> list) {
        mList = list;
    }



}
