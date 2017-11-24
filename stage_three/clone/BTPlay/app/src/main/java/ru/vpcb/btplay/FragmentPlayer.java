package ru.vpcb.btplay;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.gson.Gson;

import java.util.List;

import static ru.vpcb.btplay.utils.Constants.BUTTON_DOWN_ALPHA;
import static ru.vpcb.btplay.utils.Constants.BUTTON_DOWN_DELAY;
import static ru.vpcb.btplay.utils.Constants.BUTTON_UP_ALPHA;
import static ru.vpcb.btplay.utils.Constants.BUTTON_UP_DELAY;
import static ru.vpcb.btplay.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.btplay.utils.Constants.RECIPE_SCREEN_WIDE;
import static ru.vpcb.btplay.utils.Constants.RECIPE_STEP_POSITION;
import static ru.vpcb.btplay.utils.Constants.TAG_FDETAIL;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 15-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class FragmentPlayer extends Fragment implements IFragmentHelper {

    private List<String> mCardList;
    private RecyclerView mRecyclerView;
    private FragmentMainAdapter mRecyclerAdapter;
    private int mSpan;
    private int mPosition;
    private int mPositionMax;

    TextView mHeadText;
    TextView mBodyText;
    ImageButton mPrevButton;
    ImageButton mNextButton;
    TextView mNavigationText;
    View mPrevExt;
    View mNextExt;
    private Context mContext;
    private RecipeItem mRecipeItem;
    private boolean mIsWide;
    TextView mVideoText;
    private List<RecipeItem.Step> mStepList;
    private RecipeItem.Step mCurrentStep;

    public FragmentPlayer() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        Bundle playerArgs = getArguments();
        mPosition = 0;
        mPositionMax = 0;
        mIsWide = false;
        mRecipeItem = null;
        mStepList = null;
        try {                                                               // null will be catch by Exception e
            mPosition = playerArgs.getInt(RECIPE_STEP_POSITION, 0);
            mIsWide = playerArgs.getBoolean(RECIPE_SCREEN_WIDE, false);
            mRecipeItem = new Gson().fromJson(playerArgs.getString(RECIPE_POSITION, null), RecipeItem.class);
            mStepList = mRecipeItem.getSteps();
            mPositionMax = mStepList.size();
            if (mPositionMax < 0 || mPositionMax < mPosition) {
                mPositionMax = mPosition;
            }
        } catch (Exception e) {
            Log.d(TAG_FDETAIL, e.getMessage());
        }

        mCurrentStep = getCurrentStep();

        mHeadText = rootView.findViewById(R.id.fp_head_text);
        mBodyText = rootView.findViewById(R.id.fp_body_text);
        mPrevButton = rootView.findViewById(R.id.prev_button);
        mNextButton = rootView.findViewById(R.id.next_button);
        mNavigationText = rootView.findViewById(R.id.navigation_text);
        mPrevExt = rootView.findViewById(R.id.prev_button_extended);
        mNextExt = rootView.findViewById(R.id.next_button_extended);
        mVideoText = rootView.findViewById(R.id.fp_video_text);

        Resources res = getResources();
// head text
// navigation text
        if (mHeadText != null) {
            mHeadText.setText(getHeaderText());
            mNavigationText.setText(getHeaderText());
        }
// body text
        if (mBodyText != null) {
            String stepText;
            if (mCurrentStep == null) {
                stepText = getString(R.string.play_body_empty);
            } else {
                stepText = mCurrentStep.getDescription();
                if (stepText == null || stepText.isEmpty()) {
                    stepText = getString(R.string.play_body_empty);
                } else {
                    stepText = stepText.replaceAll("[^\\x00-\\xBE]", "");  // clear from broken symbols
                }
            }
            mBodyText.setText(stepText);
        }

// video text
        if (mCurrentStep != null) {
            String videoURL = mCurrentStep.getVideoURL();
            if (videoURL == null || videoURL.isEmpty()) {
                mVideoText.setText("No video");
                mVideoText.setTextSize(getResources().getDimension(R.dimen.head_text_size));
                mVideoText.setTypeface(mVideoText.getTypeface(), Typeface.BOLD);
                mVideoText.setGravity(Gravity.CENTER);
            } else mVideoText.setText(videoURL);
        }


        setupButtonsAnimation();


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        try {
//            mFragmentCallback = (IFragmentCallback) context;
//        } catch (ClassCastException e) {
//            e.printStackTrace();
//        }
        mContext = context;
    }

    @Override
    public void onCallback(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentPlayer playerFragment = new FragmentPlayer();
        Bundle playerArgs = new Bundle();

        playerArgs.putString(RECIPE_POSITION, new Gson().toJson(mRecipeItem));
        playerArgs.putInt(RECIPE_STEP_POSITION, position);
        playerArgs.putBoolean(RECIPE_SCREEN_WIDE, mIsWide);
        playerFragment.setArguments(playerArgs);

        if (mIsWide) {

            fragmentManager.beginTransaction()
                    .replace(R.id.fc_p_container, playerFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, playerFragment)
                    .commit();


        }

//        Snackbar.make(getView(), "Clicked on Fragment Player " + " stack: " +
//                        fragmentManager.getBackStackEntryCount(),
//                Snackbar.LENGTH_SHORT).show();

    }


    @Override
    public List<FragmentDetailItem> getItemList() {
        return null;
    }

    @Override
    public int getSpanHeight() {
        return 0;
    }


    private void setupButtonsAnimation() {
        if (mPrevExt == null || mPrevButton == null ||
                mNextExt == null || mNextButton == null ||
                mNavigationText == null) {
            return;
        }
        mPrevExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrevButton.callOnClick();
            }
        });


        mNextExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNextButton.callOnClick();


            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition > 1) {
                    mPosition--;

                    onCallback(mPosition);
                }
//                prevButtonAnimate();          // replaced by FrameLayout clickable

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition < mPositionMax) {
                    mPosition++;

                    onCallback(mPosition);
                }
//                nextButtonAnimate();          // replaced by FrameLayout clickable
            }
        });


    }



    private RecipeItem.Step getCurrentStep() {
        if (mStepList == null || mPosition == 0 || mPosition > mStepList.size()) {
            return null;
        }
        return mStepList.get(mPosition - 1);
    }

    @SuppressWarnings("deprecated")
    public static Spanned fromHtml(String data) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(data);
        }
        return result;
    }

    private String getHeaderText() {
        String stepHeader;
        Resources res = getResources();
        if (mPosition == 1) {
            stepHeader = res.getString(R.string.play_header_intro);
        } else if (mPosition <= mPositionMax) {
            stepHeader = res.getString(R.string.play_header_step, mPosition - 1);
        } else {
            stepHeader = res.getString(R.string.play_header_empty);
        }

        return stepHeader;
    }

    private void prevButtonAnimate() {
        mPrevButton.setImageResource(R.drawable.ic_skip_prev_black_24dp);
        mPrevButton.animate()
                .alpha(BUTTON_DOWN_ALPHA)
                .setDuration(BUTTON_DOWN_DELAY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mPrevButton.setImageResource(R.drawable.ic_skip_prev_white_24dp);
                        mPrevButton.animate()
                                .alpha(BUTTON_UP_ALPHA)
                                .setDuration(BUTTON_UP_DELAY)
                                .start();
                    }
                })
                .start();
    }

    private void nextButtonAnimate() {
        mNextButton.setImageResource(R.drawable.ic_skip_next_black_24dp);
        mNextButton.animate()
                .alpha(BUTTON_DOWN_ALPHA)
                .setDuration(BUTTON_DOWN_DELAY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mNextButton.setImageResource(R.drawable.ic_skip_next_white_24dp);
                        mNextButton.animate()
                                .alpha(BUTTON_UP_ALPHA)
                                .setDuration(BUTTON_UP_DELAY)
                                .start();
                    }
                })
                .start();
     }
}
