package ru.vpcb.contentprovider.add;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import ru.vpcb.contentprovider.R;


public class MessageReceiver2 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            if(action.equals(context.getString(R.string.broadcast_update_started))) {
                Toast.makeText(context,"Broadcast message: update started", Toast.LENGTH_SHORT).show();

            }
            else if(action.equals(context.getString(R.string.broadcast_update_finished))){
                Toast.makeText(context,"Broadcast message: update finished", Toast.LENGTH_SHORT).show();
            }
            else if(action.equals(context.getString(R.string.broadcast_no_network))) {
                Toast.makeText(context,"Broadcast message: no network", Toast.LENGTH_SHORT).show();
            }else  {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }

    }
}
