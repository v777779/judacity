/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.android_me.ui;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.android.android_me.ui.MainActivity.BUNDLE_BODY_INDEX_ID;
import static com.example.android.android_me.ui.MainActivity.BUNDLE_HEAD_INDEX_ID;
import static com.example.android.android_me.ui.MainActivity.BUNDLE_INTENT_ID;
import static com.example.android.android_me.ui.MainActivity.BUNDLE_LEGS_INDEX_ID;

// This activity will display a custom Android image composed of three body parts: head, body, and legs
public class AndroidMeActivity extends AppCompatActivity {
    private int mHeadIndex;
    private int mBodyIndex;
    private int mLegsIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_me);

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Only create new fragments when there is no previously saved state
        if (savedInstanceState == null) {

            // TODO (5) Retrieve list index values that were sent through an intent; use them to display the desired Android-Me body part image
            // Use setListindex(int index) to set the list index for all BodyPartFragments


            mHeadIndex = 1;
            mBodyIndex = 0;
            mLegsIndex = 0;

            if (getIntent() != null && getIntent().hasExtra(BUNDLE_INTENT_ID)) {
                Bundle bundle = getIntent().getBundleExtra(BUNDLE_INTENT_ID);
                    mHeadIndex =  bundle.getInt(BUNDLE_HEAD_INDEX_ID);
                    mBodyIndex = bundle.getInt(BUNDLE_BODY_INDEX_ID);
                    mLegsIndex = bundle.getInt(BUNDLE_LEGS_INDEX_ID);
            }

            // Create a new head BodyPartFragment
            BodyPartFragment headFragment = new BodyPartFragment();

            // Set the list of image id's for the head fragment and set the position to the second image in the list
            headFragment.setImageIds(AndroidImageAssets.getHeads());
            headFragment.setListIndex(mHeadIndex);

            // Add the fragment to its container using a FragmentManager and a Transaction
            FragmentManager fragmentManager = getSupportFragmentManager();


            fragmentManager.beginTransaction()
                    .add(R.id.head_container, headFragment)
                    .commit();

            // Create and display the body and leg BodyPartFragments

            BodyPartFragment bodyFragment = new BodyPartFragment();
            bodyFragment.setImageIds(AndroidImageAssets.getBodies());
            bodyFragment.setListIndex(mBodyIndex);
            fragmentManager.beginTransaction()
                    .add(R.id.body_container, bodyFragment)
                    .commit();

            BodyPartFragment legFragment = new BodyPartFragment();
            legFragment.setImageIds(AndroidImageAssets.getLegs());
            legFragment.setListIndex(mLegsIndex);
            fragmentManager.beginTransaction()
                    .add(R.id.leg_container, legFragment)
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            NavUtils.navigateUpFromSameTask(this);   // надо задать Manifest.PARENT
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
