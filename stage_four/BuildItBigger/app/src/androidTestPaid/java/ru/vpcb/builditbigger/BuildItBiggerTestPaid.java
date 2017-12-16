package ru.vpcb.builditbigger;

import android.content.res.Resources;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;
import static ru.vpcb.constants.Constants.TEST_POSITION_0;
import static ru.vpcb.constants.Constants.TEST_POSITION_5;
import static ru.vpcb.constants.Constants.TEST_RESPONSE;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BuildItBiggerTestPaid {
    /**
     * Object empty object used for waiting response from Endpoint via synchronization wait/notify
     */
    private final Object mSyncObject = new Object();
    /**
     * Static flag used to set other static flags once per suite
     */
    private static boolean mIsSet = false;
    /**
     * The flag is true if orientation is landscape
     */
    private static boolean mIsLand;
    /**
     * The flag is true if the smallest screen width greater or equal than 550dp
     */
    private static boolean mIsWide;
    /**
     * The flag is true if this is paid flavor
     */
    private boolean mIsOnline;
    /**
     * Activity Resources object
     */
    private Resources mRes;
    /**
     * ViewInteractive RecyclerView test object for wide screens only
     */
    private ViewInteraction mRecycler;
    /**
     * ViewInteractive RecyclerView Item test object for wide screens only
     */
    private ViewInteraction mItem;
    /**
     * ViewInteractive button object
     */
    private ViewInteraction mButton;
    /**
     * ViewInteractive button object
     */
    private ViewInteraction mText;
    /**
     * IdlingResource for Endpoint callback testing
     */
    private IdlingResource mIdlingResource;
    /**
     * MainActivity Rule object provides MainActivity instance for test
     */
    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);


    /**
     * Setup  mIsLand, mIsWide flags and lock them for the next tests.
     * Waits when Activity loads database and fills List of RecipeItems
     */
    @Before
    public void setUp() {
        mRes = mainActivityRule.getActivity().getResources();
        mainActivityRule.getActivity().setTestMode(); // setup test request and respond

        if (!mIsSet) {
            mIsSet = true;
            mIsLand = mRes.getBoolean(R.bool.is_land);
            mIsWide = mRes.getBoolean(R.bool.is_wide);
            mIsSet = true;
        }
        registerIdlingResource();

// timber
        if (!mainActivityRule.getActivity().mIsTimber) {
            Timber.plant(new Timber.DebugTree());
            mainActivityRule.getActivity().mIsTimber = true;
        }

    }

    private void checkAnswer() {
        mText = onView(withText(TEST_RESPONSE));
        while (!mIdlingResource.isIdleNow()) {
        }
        mText = onView(withText(TEST_RESPONSE));
        mText.check(matches(isDisplayed()));
    }


    @Test
    public void useAppContext() throws Exception {
        if (!mIsWide) {
// text
            mText = onView(withId(R.id.front_text));
            mText.check(matches(withText(mRes.getString(R.string.welcome_message))));
            mText.check(matches(isDisplayed()));

            mButton = onView(withId(R.id.joke_button));
            mButton.check(matches(withText(mRes.getString(R.string.button_get))));
            mButton.check(matches(isDisplayed()));

            mButton.perform(click());
            checkAnswer();
        } else {
// text
            mText = onView(withId(R.id.joke_text));
            mText.check(matches(withText(mRes.getString(R.string.welcome_message))));
            mText.check(matches(isDisplayed()));
// button   paid flavor wide screens all have button
            mButton = onView(withId(R.id.joke_button));
            mButton.check(matches(withText(mRes.getString(R.string.button_get))));
            mButton.check(matches(isDisplayed()));
            mButton.perform(click());
            checkAnswer();
            mText.perform(setTextInTextView(""));
// recycler
            mRecycler = onView(allOf(withId(R.id.joke_recycler), isDisplayed()));
            mItem = mRecycler.perform(actionOnItemAtPosition(TEST_POSITION_0, scrollTo()));
            mItem.check(matches(isDisplayed()));
            mItem = mRecycler.perform(actionOnItemAtPosition(TEST_POSITION_5, scrollTo()));
            mItem.check(matches(isDisplayed()));
// click
            mRecycler.perform(actionOnItemAtPosition(TEST_POSITION_0, click()));
            checkAnswer();
        }

        Timber.i(mRes.getString(R.string.test_message, mRes.getString(R.string.app_name)));
    }


    @After
    public void tearDown() throws Exception {
        unregisterIdlingResource();
    }

    public void registerIdlingResource() {
        mIdlingResource = mainActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }


    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }


    private  ViewAction setTextInTextView(final String value){
        return new ViewAction() {
            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TextView.class));
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((TextView) view).setText(value);
            }

            @Override
            public String getDescription() {
                return "replace text";
            }
        };
    }
}
