package android.example.com.squawker.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 02-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */
public class SquawkMessagingService extends FirebaseMessagingService {
    private static final String TAG = SquawkMessagingService.class.getSimpleName();
    private static final int MAX_LENGTH = 30;
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATE = "date";
    private static final String KEY_AUTHOR_KEY = "authorKey";
    private final static int SQUAWK_PENDING_INTENT_ID = 1521;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> dataMap = remoteMessage.getData();

        if (dataMap.size() > 0) {
            Log.d(TAG, "Received message: " + dataMap);

            insertMessage(dataMap);
            notifyMessage(dataMap);

        }
    }

    private void insertMessage(final Map<String, String> dataMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SquawkContract.COLUMN_AUTHOR, dataMap.get(KEY_AUTHOR));
                contentValues.put(SquawkContract.COLUMN_MESSAGE, dataMap.get(KEY_MESSAGE).trim());
                contentValues.put(SquawkContract.COLUMN_DATE, dataMap.get(KEY_DATE));
                contentValues.put(SquawkContract.COLUMN_AUTHOR_KEY, dataMap.get(KEY_AUTHOR_KEY));
                getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, contentValues);
            }
        }).start();
    }



    private static PendingIntent contentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(context, SQUAWK_PENDING_INTENT_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void notifyMessage(Map<String, String> dataMap) {

        String messageTitle = "Message from " + dataMap.get(KEY_AUTHOR);
        String messageBody = dataMap.get(KEY_MESSAGE);
        if(messageBody.length() > MAX_LENGTH) {
            messageBody = messageBody.substring(0,MAX_LENGTH);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.ic_duck)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setDefaults(Notification.DEFAULT_SOUND);

        mBuilder.setContentIntent(contentIntent(this));             // add PendingIntent
        mBuilder.setAutoCancel(true);                               // auto cancel

        String release = Build.VERSION.RELEASE.substring(0, 3);     // first three symbols

        try {
            double version = Double.parseDouble(release);
            if (version > 4.3) {
                mBuilder.setPriority(Notification.PRIORITY_HIGH);
            }

        } catch (NumberFormatException e) {
            Log.v(TAG, " can't define version " + release);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SQUAWK_PENDING_INTENT_ID, mBuilder.build());

    }


}
