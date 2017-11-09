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

import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * This test demos a user clicking the decrement button and verifying that it properly decrease
 * the quantity the total cost.
 */

// TODO (1) Add annotation to specify AndroidJUnitRunner class as the default test runner
@RunWith(AndroidJUnit4.class)
public class OrderActivityBasicTest {
    // TODO (2) Add the rule that provides functional testing of a single activity
    // TODO (3) Finish writing this test which will:
    //          - Check that the initial quantity is zero
    //          - Click on the decrement button
    //          - Verify that the decrement button won't decrease the quantity 0 and cost below $0.00
private static final String TAG = OrderActivity.class.getSimpleName();

    @Rule
    public ActivityTestRule<OrderActivity> mActivityRule = new ActivityTestRule<OrderActivity>(OrderActivity.class);

    @Test
    public void clickIncrementButton_ChangesQuantityAndCost() {
        // 1. Find the View
        // 2. Perform action on the view
        // 3. Check if the view does what you expected
        Matcher<View> matcherButtonView = withId(R.id.increment_button);        // 1.
        onView(matcherButtonView).perform(click());                             // 2.

        Matcher<View> matcherQuantityView = withId(R.id.quantity_text_view);   // 3.
        Matcher<View> matcherCostView = withId(R.id.cost_text_view);
        onView(matcherQuantityView).check(matches(withText("1")));
        onView(matcherCostView).check(matches(withText("$5.00")));


    }

    private String getValue(Matcher<View> matcher) {
        final String[] strs = new String[3];
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                strs[2] = ""+isAssignableFrom(TextView.class);
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                strs[1] = "Get Text from TextView";
                return "Get Text from TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView textView = (TextView) view;
                strs[0] = textView.getText().toString();
            }
        });
        return strs[0];
    }

    @Test
    public void clickDecrementButton_ChangesQuantityAndCost() {
        Matcher<View> matcherQuantityView = withId(R.id.quantity_text_view);   // 3.
        Matcher<View> matcherCostView = withId(R.id.cost_text_view);
        String s;

        Matcher<View> matcherIncButtonView = withId(R.id.increment_button);        // 1.
        Log.v(TAG,getValue(matcherQuantityView));
        Log.v(TAG, getValue(matcherCostView));
        onView(matcherIncButtonView).perform(click());                             // 2. $5
        Log.v(TAG,getValue(matcherQuantityView));
        Log.v(TAG, getValue(matcherCostView));
        onView(matcherIncButtonView).perform(click());                             // 2. $10
        Log.v(TAG,getValue(matcherQuantityView));
        Log.v(TAG, getValue(matcherCostView));

        Matcher<View> matcherDecButtonView = withId(R.id.decrement_button);        // 1.
        onView(matcherDecButtonView).perform(click());                             // 2.
        Log.v(TAG,getValue(matcherQuantityView));
        Log.v(TAG, getValue(matcherCostView));

        onView(matcherQuantityView).check(matches(withText("1")));
        onView(matcherCostView).check(matches(withText("$5.00")));
// office task
        onView(matcherDecButtonView).perform(click());                             // 2.
        onView(matcherQuantityView).check(matches(withText("0")));                 // 3.
        onView(matcherCostView).check(matches(withText("$0.00")));
// decrement on zero value
        onView(matcherDecButtonView).perform(click());                             // 2.
        onView(matcherQuantityView).check(matches(withText("0")));                 // 3.
        onView(matcherCostView).check(matches(withText("$0.00")));

    }
}