package ru.vpcb.footballassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import static ru.vpcb.footballassistant.utils.Config.CALENDAR_DIALOG_ACTION_APPLY;


/**
 * FragmentError is Dialog Fragment class
 * to show dialog when there is no connection to network
 */
public class CalendarDialog extends DialogFragment implements View.OnClickListener {

    private ICallback mCallback;
    private CalendarView mCalendarView;
    private TextView mTextViewYear;
    private TextView mTextViewDate;
    private Calendar mCalendar;

    public static Fragment newInstance(ICallback callback, Calendar calendar) {
        CalendarDialog fragment = new CalendarDialog();
        fragment.setStyle(R.style.Dialog_Title, R.style.Calendar_Dialog);
        fragment.mCallback = callback;
        fragment.mCalendar = calendar;
        return fragment;
    }

    /**
     * Constructor default
     * Sets default layout for MainActivity
     */
    public CalendarDialog() {

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

        View v = inflater.inflate(R.layout.fragment_calendar, null);
        v.findViewById(R.id.btn_ok).setOnClickListener(this);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        getDialog().setTitle(getString(R.string.calendar_title));


        mCalendarView = v.findViewById(R.id.calendar_view);
        mTextViewYear = v.findViewById(R.id.calendar_year);
        mTextViewDate = v.findViewById(R.id.calendar_date);

        if (mCalendar == null) mCalendar = Calendar.getInstance();


        mCalendarView.setDate(mCalendar.getTimeInMillis());
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                mCalendar.set(year, month, dayOfMonth);
                setCalendarHead();
            }
        });

        setCalendarHead();
        getDialog().setCanceledOnTouchOutside(true);

        return v;
    }


    /**
     * OnClick method, recreate MainActivity on RETRY or finish() any activity on EXIT.
     *
     * @param v View object of button
     */
    public void onClick(View v) {
        String s = ((Button) v).getText().toString();

        if (s.equals(getString(android.R.string.ok))) {
            mCallback.onComplete(CALENDAR_DIALOG_ACTION_APPLY, mCalendar);
        }
        if (s.equals(getString(android.R.string.cancel))) {

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


    private void setCalendarHead() {
        mTextViewYear.setText(getString(R.string.calendar_text_year, mCalendar.get(Calendar.YEAR)));

        mTextViewDate.setText(getString(R.string.calendar_text_date,
                mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH),
                mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH),
                mCalendar.get(Calendar.DAY_OF_MONTH)));
    }
}
