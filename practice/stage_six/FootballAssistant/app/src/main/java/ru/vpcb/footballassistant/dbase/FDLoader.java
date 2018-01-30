package ru.vpcb.footballassistant.dbase;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 30-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FDLoader extends CursorLoader {
    public FDLoader(Context context, Uri uri, String sortOrder) {
        super(context, uri, null,null,null, sortOrder);
    }

    public static FDLoader getInstance(Context context, int id) {
    Uri uri = FDProvider.buildLoaderIdUri(context,id);
    String sortOrder = FDProvider.buildLoaderIdSortOrder(context, id);

        return new FDLoader(context,uri,sortOrder);
    }

}
