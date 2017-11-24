package ru.vpcb.bakingapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import static ru.vpcb.bakingapp.utils.Constants.RECIPE_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_SCREEN_WIDE;
import static ru.vpcb.bakingapp.utils.Constants.RECIPE_STEP_POSITION;
import static ru.vpcb.bakingapp.utils.Constants.TAG_FDETAIL;

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
    private int mPosition;
    private int mPositionMax;

    TextView mNameText;
    TextView mHeadText;
    TextView mBodyText;
    ImageView mPrevButton;
    ImageView mNextButton;
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

        if (mHeadText != null && mRecipeItem != null) {
            mHeadText.setText(mRecipeItem.getName());
        }
        setupNavTextView();
        setupNavButtons();
        setupBodyTextView();
        if (mCurrentStep == null) {
            setVideoTextEmpty();
        } else {
            String videoURL = mCurrentStep.getVideoURL();
            if (videoURL == null || videoURL.isEmpty()) {
                setVideoTextEmpty();
            } else {
                setVideoText(videoURL);
            }
        }
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

    private void setupNavButtonsVisibility() {
        if (mPrevButton == null || mNextButton == null) {
            return;
        }

        if (mPosition <= 1) {
            mPrevButton.setVisibility(View.INVISIBLE);
        }else {
            mPrevButton.setVisibility(View.VISIBLE);
        }
        if (mPosition >= mPositionMax) {
            mNextButton.setVisibility(View.INVISIBLE);
        }else {
            mNextButton.setVisibility(View.VISIBLE);
        }
    }

    private void setupNavButtons() {
        if (mPrevExt == null || mNextExt == null) {
            return;
        }
        setupNavButtonsVisibility();

        mPrevExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition > 1) {
                    mPosition--;
                    onCallback(mPosition);
                }
                setupNavButtonsVisibility();
            }
        });

        mNextExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition < mPositionMax) {
                    mPosition++;
                    onCallback(mPosition);

                }
                setupNavButtonsVisibility();
            }
        });
    }

    private void setupNavTextView() {
        if (mNavigationText == null) {
            return;
        }
        if (mCurrentStep == null) {
            mNavigationText.setText(getString(R.string.play_header_empty));

        } else {
            String stepText = getString(R.string.play_header_step, "" + mCurrentStep.getId());
            if (mPosition == 1) {
                stepText = getString(R.string.play_header_intro);
            }
            mNavigationText.setText(stepText);
        }
    }

    private void setupBodyTextView() {
        if (mBodyText == null) {
            return;
        }
        if (mCurrentStep == null) {
            mBodyText.setText(getString(R.string.play_body_error));
        } else {
            String stepText = mCurrentStep.getDescription();
            if (stepText == null || stepText.isEmpty()) {
                stepText = getString(R.string.play_body_empty);
            } else {
                stepText = stepText.replaceAll("[^\\x00-\\xBE]", "");  // clear from broken symbols
            }
            mBodyText.setText(stepText);
        }
    }

    private void setAllText() {
        Resources res = getResources();
        if (mRecipeItem != null) {
            mNameText.setText(mRecipeItem.getName());
        }
        mHeadText.setVisibility(View.GONE);

        if (mCurrentStep == null) {
            String emptyText = res.getString(R.string.play_header_empty);

            mHeadText.setText(emptyText);
            if (mNavigationText != null) {
                mNavigationText.setText(emptyText);
            }
            mBodyText.setText(res.getString(R.string.play_body_error));
            setVideoTextEmpty();
        } else {
            String stepText;
            if (mPosition == 1) {
                stepText = mContext.getString(R.string.play_header_intro);
            } else {
                stepText = mContext.getString(R.string.play_header_step, "" + mCurrentStep.getId());
            }
            mHeadText.setText(stepText);
            if (mNavigationText != null) {
                mNavigationText.setText(stepText);
            }

            stepText = mCurrentStep.getDescription();
            if (stepText == null || stepText.isEmpty()) {
                stepText = getString(R.string.play_body_empty);
            } else {
                stepText = stepText.replaceAll("[^\\x00-\\xBE]", "");  // clear from broken symbols
            }
            mBodyText.setText(stepText);

            // video text
            String videoURL = mCurrentStep.getVideoURL();
            if (videoURL == null || videoURL.isEmpty()) {
                setVideoTextEmpty();
            } else {
                setVideoText(videoURL);
            }
        }
    }

    private void setDifferentText() {
        Resources res = getResources();
        if (mNavigationText != null) {
            if (mCurrentStep == null) {
                mNavigationText.setText(res.getString(R.string.play_header_empty));
            } else {
                String stepText = res.getString(R.string.play_header_step, "" + mCurrentStep.getId());
                if (mPosition == 1) {
                    stepText = res.getString(R.string.play_header_intro);
                }
                mNavigationText.setText(stepText);
            }
        }
        if (mBodyText != null) {
            if (mCurrentStep == null) {
                mBodyText.setText(res.getString(R.string.play_body_error));

            } else {
                String stepText = mCurrentStep.getDescription();
                if (stepText == null || stepText.isEmpty()) {
                    stepText = getString(R.string.play_body_empty);
                } else {
                    stepText = stepText.replaceAll("[^\\x00-\\xBE]", "");  // clear from broken symbols
                }
                mBodyText.setText(stepText);
            }
        }
    }

    private RecipeItem.Step getCurrentStep() {
        if (mStepList == null || mPosition == 0 || mPosition > mStepList.size()) {
            return null;
        }
        return mStepList.get(mPosition - 1);
    }


    private void setVideoTextEmpty() {
        mVideoText.setText("No video");
        mVideoText.setGravity(Gravity.CENTER);
    }

    private void setVideoText(String s) {
        mVideoText.setText(s);
        mVideoText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
        mVideoText.setTypeface(null, Typeface.NORMAL);
        mVideoText.setGravity(Gravity.LEFT);

    }

}