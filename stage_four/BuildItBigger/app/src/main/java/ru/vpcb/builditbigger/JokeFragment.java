package ru.vpcb.builditbigger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import static ru.vpcb.constants.Constants.BUNDLE_FRONT_IMAGE_ID;
import static ru.vpcb.constants.Constants.BUNDLE_FRONT_TEXT_ID;
import static ru.vpcb.constants.Constants.BUNDLE_JOKE_IMAGE_ID;
import static ru.vpcb.constants.Constants.BUNDLE_JOKE_LIST;
import static ru.vpcb.constants.Constants.BUNDLE_JOKE_TEXT_ID;
import static ru.vpcb.constants.Constants.BUNDLE_POSITION;
import static ru.vpcb.constants.Constants.INTENT_IMAGE_EXTRA;
import static ru.vpcb.constants.Constants.INTENT_STRING_EXTRA;


public class JokeFragment extends Fragment {
    private TextView mJokeText;
    private ImageView mJokeImage;
    private String mJokeTextId;
    private int mJokeImageId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_fragment, container, false); // attention!!!

        mJokeText = rootView.findViewById(R.id.joke_text);
        mJokeImage = rootView.findViewById(R.id.joke_image);

        if (savedInstanceState != null) {
            mJokeTextId = savedInstanceState.getString(BUNDLE_JOKE_TEXT_ID);
            mJokeImageId = savedInstanceState.getInt(BUNDLE_JOKE_IMAGE_ID);
        } else {
            Bundle args = getArguments();
            if (args != null) {
                mJokeTextId = args.getString(INTENT_STRING_EXTRA);
                mJokeImageId = args.getInt(INTENT_IMAGE_EXTRA);

                if (mJokeTextId == null)  mJokeTextId = "";
                if (mJokeImageId  == 0)   mJokeImageId = JokeUtils.getGridImage();

            }
        }
        mJokeText.setText(mJokeTextId);
        mJokeImage.setImageResource(mJokeImageId);

        ((ICallback)getActivity()).onCompleteIdling(); // test procedure
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_JOKE_TEXT_ID, mJokeTextId);
        outState.putInt(BUNDLE_JOKE_IMAGE_ID, mJokeImageId);
    }

    public static Fragment newInstance(String s, int id) {
        Fragment fragment = new JokeFragment();
        Bundle args = new Bundle();
        args.putString(INTENT_STRING_EXTRA, s);
        args.putInt(INTENT_IMAGE_EXTRA, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
