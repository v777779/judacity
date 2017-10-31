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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

// This activity is responsible for displaying the master list of all images
// Implement the MasterListFragment callback, OnImageClickListener
public class MainActivity extends AppCompatActivity implements MasterListFragment.OnImageClickListener {

    // Variables to store the values for the list index of the selected images
    // The default value will be index = 0
    private int headIndex;
    private int bodyIndex;
    private int legIndex;

    // TODO (3) Create a variable to track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO (4) If you are making a two-pane display, add new BodyPartFragments to create an initial Android-Me image
        // Also, for the two-pane display, get rid of the "Next" button in the master list fragment
        if (findViewById(R.id.android_me_linear_layout) != null) {  // тут расчет на то, что система поменяла layout 600dp
            mTwoPane = true;

            Button nextButton = (Button) findViewById(R.id.next_button);
            nextButton.setVisibility(View.GONE);    // убираем вообще с стандартного Layout для Tablet

            GridView gridView = (GridView) findViewById(R.id.images_grid_view);
            DisplayMetrics dp = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dp);
            View fragmentListView = (View) findViewById(R.id.master_list_fragment);
            ViewGroup.LayoutParams lp = fragmentListView.getLayoutParams();


            if (dp.widthPixels < dp.heightPixels) {
                gridView.setNumColumns(2);              // задаем 2 колонки для Tablet  portrait
                View layout = findViewById(R.id.master_list_fragment);
                lp.width = 400;
                fragmentListView.setLayoutParams(lp);

            } else {
                gridView.setNumColumns(3);              // задаем 2 колонки для Tablet  landscape
                lp.width = 600;
                fragmentListView.setLayoutParams(lp);
            }

            if (savedInstanceState == null) {

                BodyPartFragment headFragment = new BodyPartFragment();   // заполнение контейнеров
                headFragment.setImageIds(AndroidImageAssets.getHeads());
                int headIndex = getIntent().getIntExtra("headIndex", 0);
                headFragment.setListIndex(headIndex);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.head_container, headFragment)
                        .commit();
                BodyPartFragment bodyFragment = new BodyPartFragment();
                bodyFragment.setImageIds(AndroidImageAssets.getBodies());
                int bodyIndex = getIntent().getIntExtra("bodyIndex", 0);
                bodyFragment.setListIndex(bodyIndex);

                fragmentManager.beginTransaction()
                        .add(R.id.body_container, bodyFragment)
                        .commit();

                BodyPartFragment legFragment = new BodyPartFragment();
                legFragment.setImageIds(AndroidImageAssets.getLegs());
                int legIndex = getIntent().getIntExtra("legIndex", 0);
                legFragment.setListIndex(legIndex);

                fragmentManager.beginTransaction()
                        .add(R.id.leg_container, legFragment)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }


    }

    // Define the behavior for onImageSelected
    public void onImageSelected(int position) {
        // Create a Toast that displays the position that was clicked
        Toast.makeText(this, "Position clicked = " + position, Toast.LENGTH_SHORT).show();

        // TODO (5) Handle the two-pane case and replace existing fragments right when a new image is selected from the master list
        // The two-pane case will not need a Bundle or Intent since a new activity will not be started;
        // This is all happening in this MainActivity and one fragment will be replaced at a time


        // Based on where a user has clicked, store the selected list index for the head, body, and leg BodyPartFragments

        // bodyPartNumber will be = 0 for the head fragment, 1 for the body, and 2 for the leg fragment
        // Dividing by 12 gives us these integer values because each list of images resources has a size of 12
        int bodyPartNumber = position / 12;

        // Store the correct list index no matter where in the image list has been clicked
        // This ensures that the index will always be a value between 0-11
        int listIndex = position - 12 * bodyPartNumber;


        if (mTwoPane) {
            BodyPartFragment bodyPartFragment = new BodyPartFragment();
            switch (bodyPartNumber) {
                case 0:
                    headIndex = listIndex;
                    bodyPartFragment.setImageIds(AndroidImageAssets.getHeads());
                    bodyPartFragment.setListIndex(headIndex);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.head_container, bodyPartFragment)
                            .commit();
                    break;
                case 1:
                    bodyIndex = listIndex;
                    bodyPartFragment.setImageIds(AndroidImageAssets.getBodies());
                    bodyPartFragment.setListIndex(bodyIndex);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.body_container, bodyPartFragment)
                            .commit();

                    break;
                case 2:
                    legIndex = listIndex;

                    bodyPartFragment.setImageIds(AndroidImageAssets.getLegs());
                    bodyPartFragment.setListIndex(legIndex);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.leg_container, bodyPartFragment)
                            .commit();
                    break;
                default:
                    break;
            }
        } else {

            // Set the currently displayed item for the correct body part fragment
            switch (bodyPartNumber) {
                case 0:
                    headIndex = listIndex;
                    break;
                case 1:
                    bodyIndex = listIndex;
                    break;
                case 2:
                    legIndex = listIndex;
                    break;
                default:
                    break;
            }

            // Put this information in a Bundle and attach it to an Intent that will launch an AndroidMeActivity
            Bundle b = new Bundle();
            b.putInt("headIndex", headIndex);
            b.putInt("bodyIndex", bodyIndex);
            b.putInt("legIndex", legIndex);

            // Attach the Bundle to an intent
            final Intent intent = new Intent(this, AndroidMeActivity.class);
            intent.putExtras(b);

            // The "Next" button launches a new AndroidMeActivity
            Button nextButton = (Button) findViewById(R.id.next_button);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(intent);
                }
            });

        }
    }

}
