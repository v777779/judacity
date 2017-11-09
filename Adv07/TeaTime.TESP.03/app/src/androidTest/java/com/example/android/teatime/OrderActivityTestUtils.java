package com.example.android.teatime;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * Created by V1 on 09-Nov-17.
 */

public class OrderActivityTestUtils {
    private static String TAG = OrderActivityTestUtils.class.getSimpleName();
    public static void logText(String s) {
            Log.v(TAG, s);
    }

    public static void logTextView(Matcher<View> matcher) {
        try {
            Log.v(TAG, getValue(matcher));
        }catch (NoMatchingViewException e) {
            Log.v(TAG, e.getMessage());
        }
    }

    public static String getValue(Matcher<View> matcher) {
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
}
