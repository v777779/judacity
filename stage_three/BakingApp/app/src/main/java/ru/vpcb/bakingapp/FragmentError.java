package ru.vpcb.bakingapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by V1 on 29-Nov-17.
 */

public class FragmentError extends DialogFragment implements View.OnClickListener {
    final String LOG_TAG = "myLogs";
    private boolean isFinish = false;

    private int mLayoutId;

    public FragmentError() {
        this.mLayoutId = R.layout.fragment_error;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(mLayoutId, null);
        v.findViewById(R.id.error_exit).setOnClickListener(this);
        if(mLayoutId  == R.layout.fragment_error) {
            getDialog().setTitle(getString(R.string.error_title));
            v.findViewById(R.id.error_try).setOnClickListener(this);
            v.findViewById(R.id.error_close).setOnClickListener(this);
        }else {
            getDialog().setTitle(getString(R.string.empty_title));
        }


        return v;
    }

    public void setLayoutId(int mLayoutId) {
        this.mLayoutId = mLayoutId;
    }

    public void onClick(View v) {
        Log.d(LOG_TAG, "Dialog 1: " + ((Button) v).getText());
        String s = ((Button) v).getText().toString();
        if(s.equals(getString(R.string.error_retry))){
            recreate();

        }
        if(s.equals(getString(R.string.error_close))) {

        }
        if(s.equals(getString(R.string.error_exit))) {
            getActivity().finish();

        }
dismiss();


    }


    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Dialog 1: onDismiss*");


    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 1: onCancel");
        isFinish = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void recreate() {
        Activity parent = getActivity();
        if (android.os.Build.VERSION.SDK_INT >= 11){
//Code for recreate
            parent.recreate();

        }else{
//Code for Intent
            Intent intent = parent.getIntent();
            parent.finish();
            startActivity(intent);
        }
    }
}
