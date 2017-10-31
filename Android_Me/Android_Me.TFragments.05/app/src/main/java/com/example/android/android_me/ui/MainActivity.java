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

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.android.android_me.R;

// This activity is responsible for displaying the master list of all images
// TODO (4) Implement the MasterListFragment callback, OnImageClickListener
public class MainActivity extends AppCompatActivity
        implements MasterListFragment.OnImageClickListener {

private Toast mToast;
private Snackbar mSnack;
    private View mParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mParent = findViewById(R.id.master_list_fragment);

    }


    // TODO (5) Define the behavior for onImageSelected; create a Toast that displays the position clicked
    @Override
    public void onImageSelected(int position) {
        if(mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, "Selected position: " + position, Toast.LENGTH_SHORT);
        mToast.show();

        if(mSnack != null) {
            mSnack.dismiss();
        }
        mSnack = Snackbar.make(mParent, "Selected position: " + position, Snackbar.LENGTH_LONG);

        mSnack.setAction("Action", null).show();
    }
}
