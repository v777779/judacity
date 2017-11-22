package ru.vpcb.btplay;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import static ru.vpcb.btplay.utils.Constants.BUTTON_DOWN_ALPHA;
import static ru.vpcb.btplay.utils.Constants.BUTTON_DOWN_DELAY;
import static ru.vpcb.btplay.utils.Constants.BUTTON_UP_ALPHA;
import static ru.vpcb.btplay.utils.Constants.BUTTON_UP_DELAY;

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

    IFragmentCallback mFragmentCallback;


    public FragmentPlayer() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        Bundle args = getArguments();
        if (args != null) {
            mPosition = args.getInt("position", 0);
            mPositionMax = args.getInt("positionMax", 0);
        }
//test !!!
        mPositionMax = 8;

        mBodyText = rootView.findViewById(R.id.fp_body_text);
        mHeadText = rootView.findViewById(R.id.fp_head_text);
        mPrevButton = rootView.findViewById(R.id.prev_button);
        mNextButton = rootView.findViewById(R.id.next_button);
        mNavigationText = rootView.findViewById(R.id.navigation_text);
        mPrevExt = rootView.findViewById(R.id.prev_button_extended);
        mNextExt = rootView.findViewById(R.id.next_button_extended);

        if (mBodyText != null) {
            mBodyText.setText("Normal text  positon: " + (mPosition + 1));
        }

        setupButtonsAnimation();
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mFragmentCallback = (IFragmentCallback)context;
        }catch (ClassCastException e) {
            e.printStackTrace();
        }
        mContext = context;
    }

    @Override
    public void onCallback(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Snackbar.make(getView(), "Clicked on Fragment Player " + " stack: " +
                        fragmentManager.getBackStackEntryCount(),
                Snackbar.LENGTH_SHORT).show();

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
                if (mPosition > 0) {
                    mPosition--;
                    mNavigationText.setText("Step " + (mPosition + 1));
                }

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
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition < mPositionMax) {
                    mPosition++;
                    mNavigationText.setText("Step " + (mPosition + 1));
                }
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
        });
    }

}
