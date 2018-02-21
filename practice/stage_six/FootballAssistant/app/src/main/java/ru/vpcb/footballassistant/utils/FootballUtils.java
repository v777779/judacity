package ru.vpcb.footballassistant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ru.vpcb.footballassistant.ICallback;
import ru.vpcb.footballassistant.R;
import ru.vpcb.footballassistant.data.FDFixture;
import timber.log.Timber;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DATE;
import static ru.vpcb.footballassistant.utils.Config.IMAGE_IDS;
import static ru.vpcb.footballassistant.utils.Config.MESSAGE_TOAST_SUPER_LONG;
import static ru.vpcb.footballassistant.utils.Config.MESSAGE_TOAST_TICK;
import static ru.vpcb.footballassistant.utils.Config.SHOW_MESSAGE_INFINITE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FootballUtils {
    public static Random rnd = new Random();

    // strings
    public static String formatString(int value) {
        return String.format(Locale.ENGLISH, "%d", value);
    }

    public static String formatStringWide(int value) {
        return String.format(Locale.ENGLISH, "%4d", value);
    }

    public static String formatStringInt(String s) {
        return s.substring(s.lastIndexOf("/") + 1);
    }

//    public static String formatStringDate(Date date) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//
//        String day = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.DAY_OF_MONTH));
//        String month = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.MONTH) + 1);
//        String year = String.format(Locale.ENGLISH, "%04d", c.get(Calendar.YEAR));
//        return "" + day + "/" + month + "/" + year.substring(2, year.length()) + "";
//    }

    public static String formatStringDate(Date date, String delim) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        String day = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.DAY_OF_MONTH));
        String month = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.MONTH) + 1);
        String year = String.format(Locale.ENGLISH, "%04d", c.get(Calendar.YEAR));
        return "" + day + delim + month + delim + year.substring(2, year.length()) + "";
    }

    public static String formatStringDate(Context context, Calendar c) {

        if (c == null) return EMPTY_LONG_DATE;

        return context.getString(R.string.notification_time,
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH),
                c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    public static boolean isWebViewAction(Context context) {
        boolean default_value = context.getString(R.string.pref_webview_action_default).equals("true");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_webview_action_key), default_value);
    }


    private static boolean isSnackbarStyle(Context context) {
        boolean default_value = context.getString(R.string.pref_snackbar_default).equals("true");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_snackbar_key), default_value);
    }

    public static void showMessageThread(Context context, String s, boolean isInfinite) {
        new MessageAsyncTask(context,s,isInfinite).execute();

    }

    public static void showMessageThread(Context context, String s) {
        new MessageAsyncTask(context,s,!SHOW_MESSAGE_INFINITE).execute();

    }

    public static void showMessage(Context context, String s) {
        if (!(context instanceof Activity) || s == null || s.isEmpty()) return;

        try {
            if (isSnackbarStyle(context)) {
                Snackbar.make(((Activity) context).getWindow().getDecorView(), s, Snackbar.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            Timber.d(context.getString(R.string.notification_empty_activity_exception, e.getMessage()));
        }
    }

    private static Toast mToast;
    private static Snackbar mSnackbar;

    public static void showMessageLong(final Context context, final String s) {
        if (!(context instanceof Activity) || s == null || s.isEmpty()) return;

        try {
            if (isSnackbarStyle(context)) {
                mSnackbar = Snackbar.make(((Activity) context).getWindow().getDecorView(), s, Snackbar.LENGTH_INDEFINITE);
                mSnackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSnackbar.dismiss();
                    }
                });
                mSnackbar.setActionTextColor(ContextCompat.getColor(context,R.color.message_snackbar_action));
                mSnackbar.show();
            } else {
                mToast = Toast.makeText(context, s, Toast.LENGTH_LONG);
                new CountDownTimer(MESSAGE_TOAST_SUPER_LONG, MESSAGE_TOAST_TICK) {
                    @Override
                    public void onTick(long l) {
                        if (mToast != null) mToast.cancel();
                        mToast = Toast.makeText(context, s, Toast.LENGTH_LONG);
                        mToast.show();
                    }

                    @Override
                    public void onFinish() {
                        if (mToast != null) mToast.cancel();
                        mToast = Toast.makeText(context, s, Toast.LENGTH_LONG);
                        mToast.show();
                    }
                }.start();

            }
        } catch (NullPointerException e) {
            Timber.d(context.getString(R.string.notification_empty_activity_exception, e.getMessage()));
        }
    }


    // images

    public static int getImageBackId() {
        return IMAGE_IDS[rnd.nextInt(IMAGE_IDS.length)];
    }

    // connection

    /**
     * Returns status of connection to network
     *
     * @param context Context of calling activity
     * @return boolean status of connection, true if connected, false if not
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


// test!!!

    /**
     * Placeholder
     *
     * @param context
     * @return
     */
    public static boolean isCursorEmpty(Context context) {

        return true;
    }



    private static class MessageAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> weakContext;
        private String message;
        private boolean isInfinite;


        MessageAsyncTask(Context context, String message, boolean isInfinite) {
            this.weakContext = new WeakReference<>(context);
            this.message = message;

        }

        @Override
        protected Void doInBackground(Void... params) {
            Context context = weakContext.get();
            if (context == null || message == null || message.isEmpty()) return null;
            if(isInfinite) {
                showMessageLong(context, message);
            }else {
                showMessage(context,message);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void value) {

        }
    }
}
