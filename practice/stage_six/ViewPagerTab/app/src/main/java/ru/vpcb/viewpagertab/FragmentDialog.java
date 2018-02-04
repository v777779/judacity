package ru.vpcb.viewpagertab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * FragmentError is Dialog Fragment class
 * to show dialog when there is no connection to network
 */
public class FragmentDialog extends DialogFragment implements View.OnClickListener {
    public static final int FRAGMENT_DIALOG_ACTION_0 = 0;
    public static final int FRAGMENT_DIALOG_ACTION_1 = 1;

    private IFragment mCallback;
    private int mLayoutId;


    public static Fragment newInstance(IFragment callback, int layoutId) {
        FragmentDialog fragment = new FragmentDialog();
        fragment.setStyle(R.style.Dialog_Title, R.style.Calendar_Dialog);

        fragment.mLayoutId = layoutId;
        fragment.mCallback = callback;

        return fragment;
    }

    /**
     * Constructor default
     * Sets default layout for MainActivity
     */
    public FragmentDialog() {

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
        v.findViewById(R.id.btn_ok).setOnClickListener(this);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        if (mLayoutId == R.layout.fragment_calendar) {
            getDialog().setTitle(getString(R.string.calendar_title));
        } else {
            getDialog().setTitle(getString(R.string.common_title));
        }
        return v;
    }


    /**
     * OnClick method, recreate MainActivity on RETRY or finish() any activity on EXIT.
     *
     * @param v View object of button
     */
    public void onClick(View v) {
        String s = ((Button) v).getText().toString();

        if (s.equals(getString(R.string.calendar_btn_ok))) {
            mCallback.onComplete(FRAGMENT_DIALOG_ACTION_0);
        }
        if (s.equals(getString(R.string.calendar_btn_cancel))) {
            mCallback.onComplete(FRAGMENT_DIALOG_ACTION_1);
        }
        dismiss();
    }


    /**
     * Recreates activity with SDK version support.
     */
    private void recreate() {
        Activity parent = getActivity();
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            parent.recreate();
        } else {
            Intent intent = parent.getIntent();
            parent.finish();
            startActivity(intent);
        }
    }
}
