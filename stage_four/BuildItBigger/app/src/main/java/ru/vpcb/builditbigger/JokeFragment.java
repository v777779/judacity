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

/**
 *  JokeFragment class receives joke message from  calling activity via bundle object.
 *  Shows joke message and companion image in fragment container
 *  Used for wide screen devices only
 *
 */
public class JokeFragment extends Fragment {
    /**
     *  TextView used to show joke message value
     */
    private TextView mJokeText;
    /**
     * ImageView used to show companion image
     */
    private ImageView mJokeImage;
    /**
     * String value of joke message, used for rotation support
     */
    private String mJokeTextId;
    /**
     * Integer value of companion image imageId, used for rotation support
     */
    private int mJokeImageId;

    /**
     * Creates layout of Fragment
     * Extract received String joke message and int imageId values
     * Set joke message and image values to show.
     * Set button and listener which closes activity in click.
     * Unlock IdlingResource via calling onCompleteIdling() of MainActivity
     *
     * @param inflater  LayoutInflater  used for inflating layout
     * @param container ViewGroup  Fragment container
     * @param savedInstanceState Bundle storage of parameters
     *                           Bundle parameters: <br>
     *                           String      mJokeTextId      value of joke message.<br>
     *                           Integer     mJokeImageId     value of joke companion imageId.<br>
     *
     * @return View object root view of Fragment
     */
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

    /**
     * Saves parameters to Bundle storage object
     *
     * @param outState Bundle storage object for parameters.
     *                 Bundle Parameters: <br>
     *                 String      mJokeTextId      value of joke message
     *                 Integer     mJokeImageId     value of joke companion imageId
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_JOKE_TEXT_ID, mJokeTextId);
        outState.putInt(BUNDLE_JOKE_IMAGE_ID, mJokeImageId);
    }

    /**
     *  Creates instance of JokeFragment with parameters
     *  Put parameters into Bundle object and saves it to args of Fragment
     *
     * @param s    String   joke message
     * @param id   Integer  companion image imageId
     * @return  JokeFragmen object with parameters
     */
    public static Fragment newInstance(String s, int id) {
        Fragment fragment = new JokeFragment();
        Bundle args = new Bundle();
        args.putString(INTENT_STRING_EXTRA, s);
        args.putInt(INTENT_IMAGE_EXTRA, id);
        fragment.setArguments(args);
        return fragment;
    }

}
