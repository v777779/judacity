package ru.vpcb.footballassistant.dbase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

import ru.vpcb.footballassistant.utils.FDUtils;

import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_DATE_AFTER;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_DATE_BEFORE;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_ITEM_ID;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_ITEM_ID2;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_REQUEST;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_REQUEST_DATES;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_REQUEST_FIXTURES;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_REQUEST_ID;
import static ru.vpcb.footballassistant.utils.Config.BUNDLE_LOADER_REQUEST_TEAMS;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 30-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDLoader extends CursorLoader {
    public FDLoader(Context context, Uri uri, String sortOrder, String selection) {
        super(context, uri, null, selection, null, sortOrder);
    }

    public static FDLoader getInstance(Context context, int id, Bundle args) {
        Uri uri = FDProvider.buildLoaderIdUri(context, id);
        String sortOrder = FDProvider.buildLoaderIdSortOrder(context, id);
        String selection = null;

        if (args != null) {
            int type = args.getInt(BUNDLE_LOADER_REQUEST); // type of request
            if (type == BUNDLE_LOADER_REQUEST_DATES) {
                String dateBefore = args.getString(BUNDLE_LOADER_DATE_BEFORE);
                String dateAfter = args.getString(BUNDLE_LOADER_DATE_AFTER);
                selection = FDContract.FxEntry.COLUMN_FIXTURE_DATE + " BETWEEN '" + dateBefore + "' AND '" + dateAfter + "'";

            } else if (type == BUNDLE_LOADER_REQUEST_FIXTURES) {

            } else if (type == BUNDLE_LOADER_REQUEST_TEAMS) {

            }
        }

        return new FDLoader(context, uri, sortOrder, selection);
    }



}
