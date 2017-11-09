package com.example.android.teatime;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MenuActivityTest {

    @Rule
    public ActivityTestRule<MenuActivity> mActivityTestRule = new ActivityTestRule<>(MenuActivity.class);

    @Test
    public void menuActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.grid_item_layout),
                        childAtPosition(
                                allOf(withId(R.id.tea_grid_view),
                                        withParent(withId(R.id.activity_main))),
                                1),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.text1), withText("Small ($5/cup)"),
                        childAtPosition(
                                allOf(withId(R.id.tea_size_spinner),
                                        childAtPosition(
                                                withId(R.id.sizeLinearLayout),
                                                1)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Small ($5/cup)")));

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.sugar_spinner),
                        withParent(allOf(withId(R.id.sugarLinearLayout),
                                withParent(withId(R.id.activity_tea_detail))))));
        appCompatSpinner.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("25% - Slightly Sweet"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(android.R.id.text1), withText("25% - Slightly Sweet"),
                        childAtPosition(
                                allOf(withId(R.id.sugar_spinner),
                                        childAtPosition(
                                                withId(R.id.sugarLinearLayout),
                                                1)),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("25% - Slightly Sweet")));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.increment_button), withText("+"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.increment_button), withText("+"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.cost_text_view), withText("$10.00"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_tea_detail),
                                        10),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("$10.00")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
