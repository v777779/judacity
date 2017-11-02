package android.example.com.squawker.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 02-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */
public class SquawkFCMIdService extends FirebaseInstanceIdService {
    private static final String TAG = SquawkFCMIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();  // получить токен
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String token) {  // ffu

    }

}
