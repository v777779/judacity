package ru.vpcb.bottomactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ru.vpcb.bottomactivity.MainActivity.START_CLICK_STRING_RESOURCE;

public class FragmentJoke extends Fragment {
    private static final String BUNDLE_JOKE_STRING = "bundle_joke_string";
    private static final String BUNDLE_JOKE_IMAGE_ID = "bundle_joke_image_id";
    private static final int[] IMAGE_GRID_IDS = new int[]{
            R.drawable.joke_001, R.drawable.joke_002,
            R.drawable.joke_005, R.drawable.joke_006,
            R.drawable.joke_007, R.drawable.joke_009,
            R.drawable.joke_010, R.drawable.joke_011,
            R.drawable.joke_012, R.drawable.joke_014,
            R.drawable.joke_015, R.drawable.joke_016,
            R.drawable.joke_017, R.drawable.joke_018,
            R.drawable.joke_019
    };

    private static final int[] IMAGE_FRONT_IDS = new int[]{
            R.drawable.front_001,R.drawable.front_002,R.drawable.front_003
    };

    private Random mRnd;
    private TextView mTextJoke;
    private ImageView mImageJoke;


    public static List<Integer> getJokeList() {
        List<Integer> list = new ArrayList<>();
        for (int imageId : IMAGE_GRID_IDS) {
            list.add(imageId);
        }
        return list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_fragment, container, false); // attention!!!

        Log.d("Fragment", "thread = " + Thread.currentThread().getName());

        mRnd = new Random();
        mTextJoke = rootView.findViewById(R.id.joke_text);
        mImageJoke = rootView.findViewById(R.id.joke_image);

        Bundle args = getArguments();
        if (args != null) {

            String textJoke = args.getString(BUNDLE_JOKE_STRING);
            int imageId = args.getInt(BUNDLE_JOKE_IMAGE_ID, -1);

            if (textJoke == null) textJoke = "";
            mTextJoke.setText(textJoke);
            if (textJoke.equals(START_CLICK_STRING_RESOURCE)) {
                mImageJoke.setImageResource(getFrontImageId());
            } else {
                mImageJoke.setImageResource(getImageId(imageId));
            }
        }

        return rootView;
    }


    private int getImageId(int imageId) {
        if (imageId == -1 ) {
            return IMAGE_GRID_IDS[mRnd.nextInt(IMAGE_GRID_IDS.length)];
        }else {
            return imageId;
        }
    }

    private int getFrontImageId() {
        return IMAGE_FRONT_IDS[mRnd.nextInt(IMAGE_FRONT_IDS.length)];
    }

    public static Fragment newInstance(String s, int position) {
        Fragment fragment = new FragmentJoke();
        Bundle args = new Bundle();
        args.putString(BUNDLE_JOKE_STRING, s);
        args.putInt(BUNDLE_JOKE_IMAGE_ID, position);
        fragment.setArguments(args);
        return fragment;
    }

    public static int getSize() {
        return IMAGE_GRID_IDS.length;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
