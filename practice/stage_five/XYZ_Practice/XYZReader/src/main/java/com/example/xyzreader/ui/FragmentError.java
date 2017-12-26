package com.example.xyzreader.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.toolbox.StringRequest;
import com.example.xyzreader.R;


/**
 * FragmentError is Dialog Fragment class
 * to show dialog when there is no connection to network
 */
public class FragmentError extends DialogFragment implements View.OnClickListener {
    public static final String BUNDLE_FRAGMENT_IS_CURSOR_EMPTY = "bundle_fragment_is_cursor_empty";
    private IFragmentHelper mCallback;
    private int mLayoutId;
    private boolean mIsCursorEmpty;

    /**
     * Constructor default
     * Sets default layout for MainActivity
     */
    public FragmentError() {
        this.mLayoutId = R.layout.fragment_error;
    }

    public static FragmentError newInstance() {
        FragmentError fragmentError = new FragmentError();
        fragmentError.setStyle(R.style.dialog_title_style, R.style.CustomDialog);
        return fragmentError;
    }

    public static FragmentError newInstance(boolean isCursorEmpty) {
        FragmentError fragmentError = new FragmentError();
        fragmentError.setStyle(R.style.dialog_title_style, R.style.CustomDialog);
        Bundle args = new Bundle();
        args.putBoolean(BUNDLE_FRAGMENT_IS_CURSOR_EMPTY, isCursorEmpty);
        fragmentError.setArguments(args);
        return fragmentError;
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

        View v = inflater.inflate(mLayoutId, null);
        v.findViewById(R.id.error_exit).setOnClickListener(this);
        if (mLayoutId == R.layout.fragment_error) {
            getDialog().setTitle(getString(R.string.error_title));
            v.findViewById(R.id.error_retry).setOnClickListener(this);
            v.findViewById(R.id.error_close).setOnClickListener(this);
        } else {
            getDialog().setTitle(getString(R.string.empty_title));
        }

        mCallback = (IFragmentHelper) getActivity();

        Bundle args = getArguments();
        if (args != null) {
            mIsCursorEmpty = args.getBoolean(BUNDLE_FRAGMENT_IS_CURSOR_EMPTY, false);
        }

        if (!mIsCursorEmpty) {
            v.findViewById(R.id.error_exit).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.error_close).setVisibility(View.VISIBLE);
        }

        return v;
    }

    /**
     * Set  layout ID of dialog
     * Called from MainActivity and DetailActivity to inflate different layouts
     *
     * @param mLayoutId
     */
    public void setLayoutId(int mLayoutId) {
        this.mLayoutId = mLayoutId;
    }

    /**
     *  Set callback IFragmentHelper object
     *  Used to call showError() method in MainActivity onClick()
     *
     //  * @param mCallback IFragmentHelper callback object  // DOES NOT WORK HERE AFTER ROTATION
     */
//    public void setCallback() {
//        this.mCallback = mCallback;
//    }

    /**
     * OnClick method, recreate MainActivity on RETRY or finish() any activity on EXIT.
     *
     * @param v View object of button
     */
    public void onClick(View v) {
        String s = ((Button) v).getText().toString();
        if (s.equals(getString(R.string.error_retry))) {
            mCallback.onRetry();
        }
        if (s.equals(getString(R.string.error_close))) {
            mCallback.onClose();
        }
        if (s.equals(getString(R.string.error_exit))) {
            mCallback.onExit();

        }
        dismiss();

    }


    /**
     * Recreates activity with SDK version support.
     */
    private void recreate() {
        Activity parent = getActivity();
        if (android.os.Build.VERSION.SDK_INT >= 11) {
//Code for recreate
            parent.recreate();

        } else {
//Code for Intent
            Intent intent = parent.getIntent();
            parent.finish();
            startActivity(intent);
        }
    }
}