package com.example.android.mygarden;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by V1 on 08-Nov-17.
 */

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this);
    }
}
