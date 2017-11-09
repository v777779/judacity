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

package com.example.android.teatime;

import android.support.test.espresso.DataInteraction;
import android.support.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * This test demos a user clicking on a GridView item in MenuActivity which opens up the
 * corresponding OrderActivity.
 * <p>
 * This test does not utilize Idling Resources yet. If idling is set in the MenuActivity,
 * then this test will fail. See the IdlingResourcesTest for an identical test that
 * takes into account Idling Resources.
 */


// TODO (1) Add annotation to specify AndroidJUnitRunner class as the default test runner
@RunWith(AndroidJUnit4.class)
public class MenuActivityScreenTest {


    // TODO (2) Add the rule that provides functional testing of a single activity
    // TODO (3) Finish writing this test which will click on a gridView Tea item and verify that
    // the OrderActivity opens up with the correct tea name displayed.
    @Rule
    public ActivityTestRule<MenuActivity> mActivityTestRule =
            new ActivityTestRule<>(MenuActivity.class);

    @Test
    public void clickGridViewItem_OpensOrderActivity() {

        Matcher<View> matcherAdapter = withId(R.id.tea_grid_view);
        Matcher<View> matcherItem = withId(R.id.tea_name_text_view);
        DataInteraction dataItem = onData(anything()).inAdapterView(matcherAdapter).atPosition(4); //1.

        OrderActivityTestUtils.logTextView(matcherItem);

        // Click on item
        dataItem.perform(click());  // 2.

        // Verify that the decrement button decreases the quantity by 1


        OrderActivityTestUtils.logTextView(matcherItem);

        onView(matcherItem).check(matches(withText("Honey Lemon Tea")));



    }

}