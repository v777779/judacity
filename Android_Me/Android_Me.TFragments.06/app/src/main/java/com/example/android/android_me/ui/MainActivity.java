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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

import java.util.ArrayList;
import java.util.List;

// This activity is responsible for displaying the master list of all images
// Implement the MasterListFragment callback, OnImageClickListener
public class MainActivity extends AppCompatActivity implements MasterListFragment.OnImageClickListener {
    private static final int INDEX_RANGE = 12;
    public static final String BUNDLE_HEAD_INDEX_ID = "bundle_head_index_id";
    public static final String BUNDLE_BODY_INDEX_ID = "bundle_body_index_id";
    public static final String BUNDLE_LEGS_INDEX_ID = "bundle_legs_index_id";
    public static final String BUNDLE_INTENT_ID = "bundle_intent_id";

    private int mHeadIndex;
    private int mBodyIndex;
    private int mLegsIndex;
    private Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHeadIndex = 1;
        mBodyIndex = 0;
        mLegsIndex = 0;

        mNext = (Button) findViewById(R.id.grid_view_next_button);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AndroidMeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_HEAD_INDEX_ID, mHeadIndex);
                bundle.putInt(BUNDLE_BODY_INDEX_ID, mBodyIndex);
                bundle.putInt(BUNDLE_LEGS_INDEX_ID, mLegsIndex);
                intent.putExtra(BUNDLE_INTENT_ID,bundle);
                startActivity(intent);

            }
        });

    }

    // Define the behavior for onImageSelected
    public void onImageSelected(int position) {
        // Create a Toast that displays the position that was clicked
        Toast.makeText(this, "Position clicked = " + position, Toast.LENGTH_SHORT).show();
        // TODO (2) Based on where a user has clicked, store the selected list index for the head, body, and leg BodyPartFragments
        // TODO (3) Put this information in a Bundle and attach it to an Intent that will launch an AndroidMeActivity
        // TODO (4) Get a reference to the "Next" button and launch the intent when this button is clicked

        switch (position / INDEX_RANGE) {
            case 0:
                mHeadIndex = position;
                break;
            case 1:
                mBodyIndex = position % INDEX_RANGE;
                break;
            case 2:
                mLegsIndex = position % INDEX_RANGE;
                break;
            default:
        }
    }


}
