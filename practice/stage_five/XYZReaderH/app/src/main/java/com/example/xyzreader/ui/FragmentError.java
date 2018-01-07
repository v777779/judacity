package com.example.xyzreader.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.xyzreader.R;

import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_IS_CURSOR_EMPTY;
import static com.example.xyzreader.remote.Config.BUNDLE_FRAGMENT_PARAMETERS;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_CLOSE;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_EXIT;
import static com.example.xyzreader.remote.Config.CALLBACK_FRAGMENT_RETRY;
import static com.example.xyzreader.remote.Config.FRAGMENT_INDEX_BUTTON1;
import static com.example.xyzreader.remote.Config.FRAGMENT_INDEX_BUTTON2;
import static com.example.xyzreader.remote.Config.FRAGMENT_INDEX_LAYOUT;
import static com.example.xyzreader.remote.Config.FRAGMENT_INDEX_LINE1;
import static com.example.xyzreader.remote.Config.FRAGMENT_INDEX_LINE2;
import static com.example.xyzreader.remote.Config.FRAGMENT_INDEX_TITLE;


/**
 * FragmentError is Dialog Fragment class
 * to show dialog when there is no connection to network
 */
public class FragmentError extends DialogFragment implements View.OnClickListener {


    private ICallback mCallback;
    private int[] mParams;

    /**
     * Constructor default
     * Sets default layout for MainActivity
     */
    public FragmentError() {

    }


    public static FragmentError newInstance(int[] params) {
        FragmentError fragment = new FragmentError();
        fragment.setStyle(R.style.dialog_title_style, R.style.CustomDialog);
        Bundle args = new Bundle();
        args.putIntArray(BUNDLE_FRAGMENT_PARAMETERS, params);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Creates FragmentError main View
     * Setup different layouts using mLayoutId field
     * Setup different titile for MainActivity and DetailsActivity
     *
     * @param inflater           LayoutInflater inflates layout
     * @param container          ViewGroup parent view
     * @param savedInstanceState Bundle savedInstanceState
     * @return View of dialog
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args == null) throw new IllegalArgumentException();

        mParams = args.getIntArray(BUNDLE_FRAGMENT_PARAMETERS);

        View v = inflater.inflate(mParams[FRAGMENT_INDEX_LAYOUT], null);
        getDialog().setTitle(getString(mParams[FRAGMENT_INDEX_TITLE]));

        TextView textLine1 = v.findViewById(R.id.text_line1);
        TextView textLine2 = v.findViewById(R.id.text_line2);
        Button button1 = v.findViewById(R.id.button1);
        Button button2 = v.findViewById(R.id.button2);

        textLine1.setText(getString(mParams[FRAGMENT_INDEX_LINE1]));
        textLine2.setText(getString(mParams[FRAGMENT_INDEX_LINE2]));
        button1.setText(getString(mParams[FRAGMENT_INDEX_BUTTON1]));
        button2.setText(getString(mParams[FRAGMENT_INDEX_BUTTON2]));
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        mCallback = (ICallback) getActivity();

        setCancelable(false);  // prevents click off the dialog when cursor is empty
        return v;
    }


    /**
     * OnClick method, recreate MainActivity on RETRY or finish() any activity on EXIT.
     *
     * @param v View object of button
     */
    public void onClick(View v) {
        String s = ((Button) v).getText().toString();
        if (s.equals(getString(R.string.button_retry))) {
            mCallback.onCallback(CALLBACK_FRAGMENT_RETRY);
        }
        if (s.equals(getString(R.string.button_close))) {
            mCallback.onCallback(CALLBACK_FRAGMENT_CLOSE);
        }
        if (s.equals(getString(R.string.button_exit))) {
            mCallback.onCallback(CALLBACK_FRAGMENT_EXIT);

        }
        dismiss();

    }


}
