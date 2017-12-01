package ru.vpcb.bakingapp;

import android.content.res.Resources;
import android.database.Cursor;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.gson.Gson;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ru.vpcb.bakingapp.data.RecipeItem;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;
import static ru.vpcb.bakingapp.MainActivity.getDescription;
import static ru.vpcb.bakingapp.MainActivity.getIngredientString;
import static ru.vpcb.bakingapp.MainActivity.getRecipeName;
import static ru.vpcb.bakingapp.MainActivity.getShortDescription;
import static ru.vpcb.bakingapp.MainActivity.getStepName;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_VALUE;
import static ru.vpcb.bakingapp.utils.Constants.MIN_WIDTH_WIDE_SCREEN;
import static ru.vpcb.bakingapp.utils.Constants.SYSTEM_UI_HIDE_FLAGS;
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
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_2;
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_3;
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_4;
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_5;
import static ru.vpcb.bakingapp.utils.Constants.TEST_STEP_9;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 01-Dec-17
 * Email: vadim.v.voronov@gmail.com
 */

@RunWith(AndroidJUnit4.class)
public class BackingAppTestPortrait {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);


    private boolean mIsLand;
    private boolean mIsWide;
    private List<RecipeItem> mList;
    private boolean mIsOnline;
    private Resources mRes;
    private RecipeItem mRecipeItem;
    private List<RecipeItem.Step> mSteps;
    private List<RecipeItem.Ingredient> mIngredients;
    private String mIngredientsString;
    private static boolean mIsSet = false;


    @Before
    public void setUp() {
        mRes = mainActivity.getActivity().getResources();
        mIsOnline = mainActivity.getActivity().isOnline(mainActivity.getActivity());
        if (!mIsSet) {
            DisplayMetrics dp = new DisplayMetrics();
            mainActivity.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dp);
            mIsLand = dp.widthPixels > dp.heightPixels;
            if (!mIsLand) {
                mIsWide = dp.widthPixels / dp.density >= MIN_WIDTH_WIDE_SCREEN;
            } else {
                mIsWide = dp.heightPixels / dp.density >= MIN_WIDTH_WIDE_SCREEN;
            }

            mIsSet = true;
        }


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

    private void setRecipeLists(int index) {
        assertThat(mRes.getString(R.string.test_message_error), mList != null && !mList.isEmpty());

        mRecipeItem = mList.get(index);
        assertThat(mRes.getString(R.string.test_message_recipe), mRecipeItem != null);
        mSteps = mRecipeItem.getSteps();
        mIngredients = mRecipeItem.getIngredients();
        assertThat(mRes.getString(R.string.test_message_steps),
                mSteps != null || mSteps.isEmpty());
        assertThat(mRes.getString(R.string.test_message_ingredients),
                mIngredients != null || mIngredients.isEmpty());

        mIngredientsString = getIngredientString(mRes, mIngredients);
    }


    @Test
    public void testRecycler1() {
        setRecipeLists(TEST_RECIPE_0);

        if (!mIsWide) {
            onView(withText(getRecipeName(mRes, mList.get(TEST_RECIPE_0)))).check(matches(isDisplayed()));
            onView(withText(getRecipeName(mRes, mList.get(TEST_RECIPE_3)))).check(doesNotExist());
        } else {
            onView(withText(getRecipeName(mRes, mList.get(TEST_RECIPE_0)))).check(matches(isDisplayed()));
            onView(withText(getRecipeName(mRes, mList.get(TEST_RECIPE_1)))).check(matches(isDisplayed()));
            onView(withText(getRecipeName(mRes, mList.get(TEST_RECIPE_2)))).check(matches(isDisplayed()));
            onView(withText(getRecipeName(mRes, mList.get(TEST_RECIPE_3)))).check(matches(isDisplayed()));
        }
    }

    private void sleep(int sleep) {
        try {
            Thread.sleep(sleep);          // Espresso doesn't work with expand correctly
        } catch (Exception e) {

        }
    }

    private void checkRecyclerText(RecipeItem.Step step) {
        Matcher<View> childRecycler = isDescendantOfA(withId(R.id.fc_recycler));

        String stepTitle = getStepName(mRes, step);
        String stepText = getShortDescription(mRes, step);
        onView(allOf(withText(stepTitle), childRecycler)).check(matches(isDisplayed()));
        onView(allOf(withText(stepText), childRecycler)).check(matches(isDisplayed()));
    }

    private void checkRecyclerNoText(RecipeItem.Step step) {
        Matcher<View> childRecycler = isDescendantOfA(withId(R.id.fc_recycler));

        String stepTitle = getStepName(mRes, step);
        String stepText = getShortDescription(mRes, step);
        onView(allOf(withText(stepTitle), childRecycler)).check(doesNotExist());
        onView(allOf(withText(stepText), childRecycler)).check(doesNotExist());
    }


    private void checkTextWide(RecipeItem.Step step) {
        Matcher<View> childOfRecycler = isDescendantOfA(withId(R.id.fc_recycler));

        onView(allOf(withText(getStepName(mRes, step)), childOfRecycler)).check(matches(isDisplayed()));
        onView(allOf(withText(getShortDescription(mRes, step)), childOfRecycler)).check(matches(isDisplayed()));
        onView(withId(R.id.fp_head_text)).check(matches(withText(getRecipeName(mRes, mRecipeItem))));
        onView(withId(R.id.fp_body_text)).check(matches(withText(getDescription(mRes, step))));
    }

    @Test
    public void testRecycler2() {
        ViewInteraction recyclerView = onView(allOf(withId(R.id.fc_recycler), isDisplayed()));
        Matcher<View> childRecycler = isDescendantOfA(withId(R.id.fc_recycler));

        if (!mIsWide) {
            setRecipeLists(TEST_RECIPE_2);     // fills mRecipeName, mStep, mIngredients, mingredientString

            recyclerView.perform(actionOnItemAtPosition(TEST_RECIPE_2, click()));
            onView(withText(getRecipeName(mRes, mRecipeItem))).check(matches(isDisplayed()));
            checkRecyclerText(mSteps.get(TEST_STEP_1));
            checkRecyclerText(mSteps.get(TEST_STEP_2));

            checkRecyclerNoText(mSteps.get(TEST_STEP_12));

// ingredients part
            onView(allOf(withText(mRes.getString(R.string.ingredients_expand)), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(mRes.getString(R.string.ingredients_collapse)), childRecycler)).check(doesNotExist());
            onView(allOf(withText(mIngredientsString), childRecycler)).check(doesNotExist());
// click expand
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_0, click()));
            sleep(TEST_EXPAND_TIMEOUT);         // Espresso doesn't work with expand correctly

            onView(allOf(withText(mRes.getString(R.string.ingredients_collapse)), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(mRes.getString(R.string.ingredients_expand)), childRecycler)).check(doesNotExist());
            onView(allOf(withText(mIngredientsString), childRecycler)).check(matches(isDisplayed()));

            onView(allOf(withText(mRes.getString(R.string.ingredients_title)), childRecycler, withEffectiveVisibility(VISIBLE)))
                    .check(matches(isDisplayed()));
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_0, click()));
// click collapse
        } else {
            setRecipeLists(TEST_RECIPE_2);   // fills mRecipeName, mStep, mIngredients, mingredientString
            recyclerView.perform(actionOnItemAtPosition(TEST_RECIPE_2, click()));
// recycler
// visibility
            onView(allOf(withText(getRecipeName(mRes, mRecipeItem)), childRecycler)).check(matches(isDisplayed()));
            checkRecyclerText(mSteps.get(TEST_STEP_2));
            checkRecyclerText(mSteps.get(TEST_STEP_4));
//            checkRecyclerText(mSteps.get(TEST_STEP_7));
            checkRecyclerNoText(mSteps.get(TEST_STEP_12));

// ingredients part
            onView(allOf(withText(mRes.getString(R.string.ingredients_expand)), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(mRes.getString(R.string.ingredients_collapse)), childRecycler)).check(doesNotExist());
            onView(allOf(withText(mIngredientsString), childRecycler)).check(doesNotExist());

            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_0, click()));
            sleep(TEST_EXPAND_TIMEOUT);             // Espresso doesn't work with expand correctly

            onView(allOf(withText(mRes.getString(R.string.ingredients_expand)), childRecycler)).check(doesNotExist());
            onView(allOf(withText(mRes.getString(R.string.ingredients_collapse)), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(mIngredientsString), childRecycler)).check(matches(isDisplayed()));
            onView(allOf(withText(mRes.getString(R.string.ingredients_title)), childRecycler, withEffectiveVisibility(VISIBLE)))
                    .check(matches(isDisplayed()));
// intro
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_0 + 1, click()));
            checkIsVideoVisible(mSteps.get(TEST_STEP_0));
            checkTextWide(mSteps.get(TEST_STEP_0));
// step1
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_1 + 1, click()));
            checkIsVideoVisible(mSteps.get(TEST_STEP_1));
            checkTextWide(mSteps.get(TEST_STEP_1));
// step4
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_4 + 1, click()));
            checkIsVideoVisible(mSteps.get(TEST_STEP_4));
            checkTextWide(mSteps.get(TEST_STEP_4));
// step5
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_5 + 1, click()));
            checkIsVideoVisible(mSteps.get(TEST_STEP_5));                                                      // video
            checkTextWide(mSteps.get(TEST_STEP_5));
//step12
            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_12 + 1, click()));
            checkIsVideoVisible(mSteps.get(TEST_STEP_12));                                                      // video
            checkTextWide(mSteps.get(TEST_STEP_12));
        }
    }


    private void checkIsVideoVisible(RecipeItem.Step step) {
        String videoURL = step.getVideoURL();
        boolean isVideoEnabled = !(videoURL == null || videoURL.isEmpty() || !mIsOnline);

        if (isVideoEnabled) {
            onView(withId(R.id.exoplayer_view)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.exoplayer_view)).check(matches(not(isDisplayed())));
        }
    }


    private void checkTextNarrow(RecipeItem.Step step) {
        onView(withId(R.id.fp_head_text)).check(matches(withText(getRecipeName(mRes, mRecipeItem))));
        onView(withId(R.id.fp_body_text)).check(matches(withText(getDescription(mRes, step))));
        onView(withId(R.id.navigation_text)).check(matches(withText(getStepName(mRes, step))));
    }


    @Test
    public void testRecycler3() {

        setRecipeLists(TEST_RECIPE_1);

        if (!mIsWide) {  // not tablet
            ViewInteraction recyclerView = onView(allOf(withId(R.id.fc_recycler), isDisplayed()));
            ViewInteraction prevButton = onView(withId(R.id.prev_button));
            ViewInteraction nextButton = onView(withId(R.id.next_button));

            recyclerView.perform(actionOnItemAtPosition(TEST_RECIPE_1, click()));  // brownies


            recyclerView.perform(actionOnItemAtPosition(TEST_STEP_5 + 1, click()));  // step5
            onView(withId(R.id.fp_text_card)).check(matches(isDisplayed()));
            onView(withId(R.id.fp_navigation_bar)).check(matches(isDisplayed()));
// step5
            checkIsVideoVisible(mSteps.get(TEST_STEP_5));   // video if connected
            checkTextNarrow(mSteps.get(TEST_STEP_5));
            if (!mIsOnline) {                               // Snackbar.LENGTH_SHORT delay
                sleep(TEST_SNACKBAR_TIMEOUT);
            }
// step4
            prevButton.perform(click());
            checkIsVideoVisible(mSteps.get(TEST_STEP_4));

// step3
            prevButton.perform(click());
            checkIsVideoVisible(mSteps.get(TEST_STEP_3));

// step2
            prevButton.perform(click());
            checkIsVideoVisible(mSteps.get(TEST_STEP_2));
            checkTextNarrow(mSteps.get(TEST_STEP_2));
// step1
            prevButton.perform(click());
            checkIsVideoVisible(mSteps.get(TEST_STEP_1));


            prevButton.perform(click());    // intro video
            checkIsVideoVisible(mSteps.get(TEST_STEP_0));
            checkTextNarrow(mSteps.get(TEST_STEP_0));

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
// step 9
            nextButton.perform(click());
            prevButton.check(matches(isDisplayed()));
            nextButton.check(matches(not(isDisplayed())));
// step8
            prevButton.perform(click());
            prevButton.check(matches(isDisplayed()));
            nextButton.check(matches(isDisplayed()));
// step9
            nextButton.perform(click());    // step9 video
            prevButton.check(matches(isDisplayed()));
            nextButton.check(matches(not(isDisplayed())));
            checkIsVideoVisible(mSteps.get(TEST_STEP_9));
            checkTextNarrow(mSteps.get(TEST_STEP_9));


        }


    }


}
