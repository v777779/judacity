package com.example.android.mygarden;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.mygarden.provider.PlantContract.PATH_PLANTS;
import static com.example.android.mygarden.provider.PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME;

/**
 * Created by V1 on 08-Nov-17.
 */

public class PlantWateringService extends IntentService {
    public static final String ACTION_WATER_PLANTS =
            "com.example.android.mygarden.action.water_plants";

    public PlantWateringService() {
        super(PlantWateringService.class.getSimpleName());
    }

    public static void startActionWaterPlants(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANTS);
        context.startService(intent);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if(action == ACTION_WATER_PLANTS) {
                handleActionWaterPlants();
            }
        }
    }

    private void handleActionWaterPlants() {   // в Background делается update Database
        final Uri PLANTS_URI = BASE_CONTENT_URI.buildUpon()  // просто добавляем к Uri еще путь "plants"
                .appendPath(PATH_PLANTS)
                .build();

        ContentValues contentValues = new ContentValues();
        long currentTime = System.currentTimeMillis();
        contentValues.put(COLUMN_LAST_WATERED_TIME,currentTime);
        getContentResolver().update(
                PLANTS_URI,
                contentValues,
                COLUMN_LAST_WATERED_TIME+">?",
                new String[]{ String.valueOf(currentTime - PlantUtils.MAX_AGE_WITHOUT_WATER)}
        );

    }
}
