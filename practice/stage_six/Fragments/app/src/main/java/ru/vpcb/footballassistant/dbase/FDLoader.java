package ru.vpcb.footballassistant.dbase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 30-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDLoader extends CursorLoader {
    public FDLoader(Context context, Uri uri, String sortOrder,String selection) {
        super(context, uri, null, selection, null, sortOrder);
    }

    public static FDLoader getInstance(Context context, int id, Bundle args) {
        Uri uri = FDProvider.buildLoaderIdUri(context, id);
        String sortOrder = FDProvider.buildLoaderIdSortOrder(context, id);
        String selection = null;
        if (args != null) {
            String dateBefore = args.getString("bundle_date_before");
            String dateAfter = args.getString("bundle_date_after");
            selection = FDContract.FxEntry.COLUMN_FIXTURE_DATE + " BETWEEN '" + dateBefore + "' AND '" + dateAfter+"'";
        }


        return new FDLoader(context, uri, sortOrder, selection);
    }

}
