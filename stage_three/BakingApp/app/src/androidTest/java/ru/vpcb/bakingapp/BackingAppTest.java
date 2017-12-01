package ru.vpcb.bakingapp;

import android.content.res.Resources;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.google.gson.Gson;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import ru.vpcb.bakingapp.data.RecipeItem;
import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_WIDTH_LANDSCAPE;
import static ru.vpcb.bakingapp.utils.Constants.HIGH_WIDTH_PORTRAIT;

import static ru.vpcb.bakingapp.utils.Constants.TEST_EXPAND_TIMEOUT;
import static ru.vpcb.bakingapp.utils.Constants.TEST_LOAD_DATABASE_TRIALS;
import static ru.vpcb.bakingapp.utils.Constants.TEST_RECIPE_0;
import static ru.vpcb.bakingapp.utils.Constants.TEST_RECIPE_1;
import static ru.vpcb.bakingapp.utils.Constants.TEST_RECIPE_2;
import static ru.vpcb.bakingapp.utils.Constants.TEST_RECIPE_3;
import static ru.vpcb.bakingapp.utils.Constants.TEST_SNACKBAR_TIMEOUT;
import static ru.vpcb.bakingapp.utils.Constants.TEST_START_TIMEOUT;
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_0;
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_1;
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_12;
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_4;
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_5;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 01-Dec-17
 * Email: vadim.v.voronov@gmail.com
 */

@RunWith(AndroidJUnit4.class)
public class BackingAppTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);


    private boolean mIsLand;
    private boolean mIsWide;
    private List<RecipeItem> mList;
    private boolean mIsOnline;
    private Resources mResources;


    @Before
    public void setUp() {
        DisplayMetrics dp = new DisplayMetrics();
        mainActivity.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dp);
        mResources = mainActivity.getActivity().getResources();

        mIsLand = dp.heightPixels < dp.widthPixels;
        double width = dp.widthPixels / dp.density;
        if (!mIsLand)
            mIsWide = width >= HIGH_WIDTH_PORTRAIT;
        else
            mIsWide = width >= HIGH_WIDTH_LANDSCAPE;

        mIsOnline = mainActivity.getActivity().isOnline(mainActivity.getActivity());

        Cursor cursor = mainActivity.getActivity().getCursor();
        int trials = TEST_LOAD_DATABASE_TRIALS;
        while (trials > 0 && (cursor == null || cursor.getCount() == 0) && mIsOnline) {
            sleep(TEST_EXPAND_TIMEOUT);
            trials--;
            cursor = mainActivity.getActivity().getCursor();
        }
        mList = getRecipeItemList();
        sleep(TEST_START_TIMEOUT);

    }

    private List<RecipeItem> getRecipeItemList() {
        List<RecipeItem> list = new ArrayList<>();
        Cursor cursor = mainActivity.getActivity().getCursor();

        if (cursor == null) return list;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            RecipeItem recipeItem = new Gson().fromJson(cursor.getString(
                    cursor.getColumnIndex(COLUMN_RECIPE_VALUE)), RecipeItem.class);
            if (recipeItem == null) continue;
            list.add(recipeItem);
        }
        return list;
    }


    String recipe_name1 = "Brownies";

    String ingredients_expand = "expand ingredients";
    String ingredients_collapse = "collapse";
    String ingredients_head = "INGREDIENTS:";
    String recipe1_ingredients_body = "8. Cocoa powder, 40G";

    String recipe1_step2_head = "Step 2";
    String recipe1_step2_body = "Melt butter and bittersweet chocolate.";

    String recipe1_step5_head = "Step 5";
    String recipe1_step5_body = "Add eggs.";

    String recipe1_step9_head = "Step 9";

    String recipe1_intro_navigation = "Intro";
    String recipe1_intro_fp_body = "Recipe Introduction";

    String recipe1_step2_navigation = "Step 2";
    String recipe1_step2_fp_body = "2. Melt the butter and bittersweet chocolate together in a microwave";

    String recipe1_step5_navigation = "Step 5";
    String recipe1_step5_fp_body = "5. Crack 3 eggs into the chocolate mixture";

    String recipe1_step9_navigation = "Step 9";
    String recipe1_step9_fp_body = "9. Cut and serve";


    String recipe_name2 = "Yellow Cake";
    String recipe2_intro_head = "Intro";
    String recipe2_intro_body = "Recipe Introduction";
    String recipe2_intro_fp_body = "Recipe Introduction";


    String recipe2_step1_head = "Step 1";
    String recipe2_step1_body = "Starting prep";
    String recipe2_step1_fp_body = "1. Preheat the oven to 350";


    String recipe2_step2_head = "Step 2";
//    String recipe2_step2_body = "Combine dry ingredients";
//    String recipe2_step2_fp_body = "2. Combine the cake four";

    String recipe2_step4_head = "Step 4";
    String recipe2_step4_body = "Add butter and milk to dry ingredients.";
    String recipe2_step4_fp_body = "4. Add 283 grams (20 tablespoons)";

    String recipe2_step5_head = "Step 5";
    String recipe2_step5_body = "Add egg mixture to batter.";
    String recipe2_step5_fp_body = "5. Scrape down the sides of the bowl";

    String recipe2_step9_head = "Step 9";
//    String recipe2_step9_body = "Prepare egg whites";
//    String recipe2_step9_fp_body = "Prepare egg whites";


    String recipe2_step12_head = "Step 12";
    String recipe2_step12_body = "Finish buttercream icing.";
    String recipe2_step12_fp_body = "12. With the mixer still running";


    String recipe2_ingredients_body = "8. Unsalted butter, softened and cut into 1 in. cubes, 961G";

    @Test
    public void testRecycler1() {
        assertThat(mResources.getString(R.string.test_message_error), mList != null && !mList.isEmpty());
        final int last = mList.size() - 1;
        if (!mIsWide) {
            if (!mIsLand) {  // portrait
                onView(withText(mList.get(TEST_RECIPE_0).getName())).check(matches(isDisplayed()));
                onView(withText(mList.get(last).getName())).check(doesNotExist());
            } else {         // landscape
                onView(withText(mList.get(TEST_RECIPE_0).getName())).check(matches(isDisplayed()));
                onView(withText(mList.get(last).getName())).check(matches(not(isDisplayed())));
            }
        } else {
            onView(withText(mList.get(TEST_RECIPE_0).getName())).check(matches(isDisplayed()));
            onView(withText(mList.get(TEST_RECIPE_1).getName())).check(matches(isDisplayed()));
            onView(withText(mList.get(TEST_RECIPE_2).getName())).check(matches(isDisplayed()));
            onView(withText(mList.get(TEST_RECIPE_3).getName())).check(matches(isDisplayed()));
        }
    }

    private void sleep(int sleep) {
        try {
            Thread.sleep(sleep);          // Espresso doesn't work with expand correctly
        } catch (Exception e) {

        }
    }

    @Test
    public void testRecycler2() {
        assertThat(mResources.getString(R.string.test_message_error), mList != null && !mList.isEmpty());
        ViewInteraction recyclerView = onView(allOf(withId(R.id.fc_recycler), isDisplayed()));
        Matcher<View> childRecycler = isDescendantOfA(withId(R.id.fc_recycler));
        RecipeItem recipeItem;

        if (!mIsWide) {
            recipeItem = mList.get(TEST_RECIPE_1);                                      // brownies
            recyclerView.perform(actionOnItemAtPosition(TEST_RECIPE_1, click()));       // brownies
            onView(withText(recipe_name1)).check(matches(isDisplayed()));
            onView(allOf(withText(recipe1_step2_head), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(recipe1_step2_body), childRecycler)).check(matches(isDisplayed()));
            if (!mIsLand) {
                onView(allOf(withText(recipe1_step5_head), childRecycler)).check(matches(isDisplayed()));
                onView(allOf(withText(recipe1_step5_body), childRecycler)).check(matches(isDisplayed()));
            }
            onView(withText(recipe1_step9_head)).check(doesNotExist());
// ingredients part
            onView(allOf(withText(ingredients_expand), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(ingredients_collapse), childRecycler)).check(doesNotExist());
            onView(allOf(withText(containsString(recipe1_ingredients_body)), childRecycler)).check(doesNotExist());
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_0, click()));

            sleep(TEST_EXPAND_TIMEOUT);         // Espresso doesn't work with expand correctly
            onView(allOf(withText(ingredients_collapse), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(ingredients_expand), childRecycler)).check(doesNotExist());

            onView(allOf(withText(containsString(recipe1_ingredients_body)), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(ingredients_head), childRecycler, withEffectiveVisibility(VISIBLE)))
                    .check(matches(isDisplayed()));
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_0, click()));

        } else {
            recyclerView.perform(actionOnItemAtPosition(TEST_RECIPE_2, click()));  // yellow cake
// recycler
// visibility
            onView(allOf(withText(recipe_name2), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(recipe2_step2_head), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(recipe2_step5_head), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(recipe2_step9_head), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(recipe2_step12_head), childRecycler)).check(doesNotExist());

// ingredients part
            onView(allOf(withText(ingredients_expand), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(ingredients_collapse), childRecycler)).check(doesNotExist());
            onView(allOf(withText(containsString(recipe2_ingredients_body)), childRecycler)).check(doesNotExist());
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_0, click()));

            sleep(TEST_EXPAND_TIMEOUT);             // Espresso doesn't work with expand correctly
            onView(allOf(withText(ingredients_expand), childRecycler)).check(doesNotExist());
            onView(allOf(withText(ingredients_collapse), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(containsString(recipe2_ingredients_body)), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(ingredients_head), childRecycler, withEffectiveVisibility(VISIBLE)))
                    .check(matches(isDisplayed()));
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_0, click()));

// intro

            checkIsVideoVisible();  // video if connected
            checkTextWide(recipe2_intro_head, recipe2_intro_body, recipe_name2, recipe2_intro_fp_body);

// step1
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_1, click()));
            onView(withId(R.id.exoplayer_view)).check(matches(not(isDisplayed())));     // no video
            checkTextWide(recipe2_step1_head, recipe2_step1_body, recipe_name2, recipe2_step1_fp_body);

// step4
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_4, click()));
            onView(withId(R.id.exoplayer_view)).check(matches(not(isDisplayed())));     // no video
            checkTextWide(recipe2_step4_head, recipe2_step4_body, recipe_name2, recipe2_step4_fp_body);

// step5
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_5, click()));
            checkIsVideoVisible();                                                      // video
            checkTextWide(recipe2_step5_head, recipe2_step5_body, recipe_name2, recipe2_step5_fp_body);

//step12
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_12, click()));
            checkIsVideoVisible();                                                      // video
            checkTextWide(recipe2_step12_head, recipe2_step12_body, recipe_name2, recipe2_step12_fp_body);

        }

    }


    private void checkTextNarrow(String fp_head, String fp_body, String navigation) {
        onView(withId(R.id.fp_head_text)).check(matches(withText(fp_head)));
        onView(withId(R.id.fp_body_text)).check(matches(withText(containsString(fp_body))));
        onView(withId(R.id.navigation_text)).check(matches(withText(containsString(navigation))));
    }

    private void checkTextWide(String head, String body, String fp_head, String fp_body) {
        Matcher<View> childOfRecycler = isDescendantOfA(withId(R.id.fc_recycler));
        try {
            onView(allOf(withText(head), childOfRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(body), childOfRecycler)).check(matches(isDisplayed()));
            onView(withId(R.id.fp_head_text)).check(matches(withText(containsString(fp_head))));
            onView(withId(R.id.fp_body_text)).check(matches(withText(containsString(fp_body))));
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }

    }


    private void checkIsVideoVisible() {
        if (mIsOnline) {
            onView(withId(R.id.exoplayer_view)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.exoplayer_view)).check(matches(not(isDisplayed())));
        }
    }


    @Test
    public void testRecycler3() {
        assertThat(mResources.getString(R.string.test_message_error), mList != null && !mList.isEmpty());


        if (!mIsWide) {  // not tablet

            ViewInteraction recyclerView = onView(allOf(withId(R.id.fc_recycler), isDisplayed()));
            ViewInteraction prevButton = onView(withId(R.id.prev_button));
            ViewInteraction nextButton = onView(withId(R.id.next_button));

            recyclerView.perform(actionOnItemAtPosition(1, click()));  // brownies

            if (mIsLand) {
                recyclerView.perform(actionOnItemAtPosition(2, click()));  // step1
                checkIsVideoVisible();
                onView(withId(R.id.fp_text_card)).check(doesNotExist());
                onView(withId(R.id.fp_navigation_bar)).check(doesNotExist());

            } else {
                recyclerView.perform(actionOnItemAtPosition(6, click()));  // step5
                onView(withId(R.id.fp_text_card)).check(matches(isDisplayed()));
                onView(withId(R.id.fp_navigation_bar)).check(matches(isDisplayed()));

// step5
                checkIsVideoVisible();  // video if connected
                checkTextNarrow(recipe_name1, recipe1_step5_fp_body, recipe1_step5_navigation);

                if (!mIsOnline) {  // snackbar_time_delay
                    sleep(TEST_SNACKBAR_TIMEOUT);
                }

                prevButton.perform(click());    // step4 video
                checkIsVideoVisible();

                prevButton.perform(click());    // step3 placeholder
                onView(withId(R.id.exoplayer_view)).check(matches(not(isDisplayed())));

                prevButton.perform(click());    // step2 video
                checkIsVideoVisible();
                checkTextNarrow(recipe_name1, recipe1_step2_fp_body, recipe1_step2_navigation);


                prevButton.perform(click());    // step1 placeholder
                onView(withId(R.id.exoplayer_view)).check(matches(not(isDisplayed())));

                prevButton.perform(click());    // intro video
                checkIsVideoVisible();
                checkTextNarrow(recipe_name1, recipe1_intro_fp_body, recipe1_intro_navigation);

                prevButton.check(matches(not(isDisplayed())));
                nextButton.check(matches(isDisplayed()));

                nextButton.perform(click());    // step1
                prevButton.check(matches(isDisplayed()));
                nextButton.check(matches(isDisplayed()));

                nextButton.perform(click());
                nextButton.perform(click());
                nextButton.perform(click());
                nextButton.perform(click());
                nextButton.perform(click());
                nextButton.perform(click());
                nextButton.perform(click());

                nextButton.perform(click());    // step 9
                prevButton.check(matches(isDisplayed()));
                nextButton.check(matches(not(isDisplayed())));

                prevButton.perform(click());    // step8
                prevButton.check(matches(isDisplayed()));
                nextButton.check(matches(isDisplayed()));

                nextButton.perform(click());    // step9 video
                prevButton.check(matches(isDisplayed()));
                nextButton.check(matches(not(isDisplayed())));
                checkIsVideoVisible();
                checkTextNarrow(recipe_name1, recipe1_step9_fp_body, recipe1_step9_navigation);

            }

        }


    }


    //    public void mainActivityTest() {
//        ViewInteraction recyclerView = onView(  allOf(withId(R.id.fc_recycler), isDisplayed()));
//        recyclerView.perform(actionOnItemAtPosition(2, click()));
//
//    }


    private String getValue(Matcher<View> matcher) {
        final String[] strs = new String[3];
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                strs[2] = "" + isAssignableFrom(TextView.class);
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

    private static class CustomAssertions {
        private static ViewAssertion doesNotExistOrGone() {
            return new ViewAssertion() {
                @Override
                public void check(View view, NoMatchingViewException noView) {
                    if (view != null && view.getVisibility() != View.GONE) {
                        throw new IllegalArgumentException();
                    }
                }
            };
        }

    }

    private boolean isExist(ViewInteraction view) {
        try {
            view.check(CustomAssertions.doesNotExistOrGone());
            return false;
        } catch (Exception e) {
        }
        return true;
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
