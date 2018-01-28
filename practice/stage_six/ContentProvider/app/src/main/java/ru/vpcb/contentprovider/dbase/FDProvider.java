package ru.vpcb.contentprovider.dbase;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ru.vpcb.contentprovider.utils.FDUtils.buildItemIdUri;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * Recipe Content Provider Class
 */
public class FDProvider extends ContentProvider {

    private UriMatcher mUriMatcher;
    private FDDbHelper mFDDbHelper;

    /**
     * Returns UriMatcher object which recognizes bulk or alone records Uri
     *
     * @return UriMatcher object
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        for (FDContract.FDParams p : FDContract.MATCH_PARAMETERS) {
            uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, p.tableName, p.tableMatcher);
            uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, p.tableName + "/#", p.tableIdMatcher);
        }
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFDDbHelper = new FDDbHelper(context);
// test!!!  // possible static final ???
        mUriMatcher = buildUriMatcher();
        return true;
    }

    /**
     * Returns  Cursor object with RecipeItems loaded from database
     *
     * @param uri           URI   address of RecipeItems
     * @param projection
     * @param selection     String  selection query template
     * @param selectionArgs String[] selection arguments for selection template
     * @param sortOrder     String sorting template
     * @return Cursor object with RecipeItems
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mFDDbHelper.getReadableDatabase();

        Selection builder = getSelection(uri, selection, selectionArgs);

        Cursor cursor = db.query(builder.table,
                projection,
                builder.selection,
                builder.selectionArgs,
                null,
                null,
                sortOrder);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Inserts RecipeItem into database
     * RecipeItem object converted to JSON format and store as string
     *
     * @param uri           Uri address to store in database
     * @param contentValues ContentValues with RecipeItem data
     * @return Uri of inserted record, Uri is valid for successful operation
     */

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mFDDbHelper.getWritableDatabase();
        Selection builder = getSelection(uri, null, null);

        long id = db.insertOrThrow(builder.table, null, contentValues);
        if (id != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return buildItemIdUri(builder.table, id);
    }


    /**
     * Deletes RecipeItem from database
     * RecipeItem object converted to JSON format and store as string
     *
     * @param uri           Uri address to store in database
     * @param selection     String  selection query template
     * @param selectionArgs String[] selection arguments for selection template
     * @return int number of deleted record, number > 0 valid for successful operation
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFDDbHelper.getWritableDatabase();
        Selection builder = getSelection(uri, selection, selectionArgs);
        int nDeleted = db.delete(builder.table, builder.selection, builder.selectionArgs);

        if (nDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return nDeleted;
    }

    /**
     * Updates RecipeItem in database
     * RecipeItem object converted to JSON format and store as string
     *
     * @param uri           Uri address to store in database
     * @param contentValues ContentValues with RecipeItem data
     * @param selection     String  selection query template
     * @param selectionArgs String[] selection arguments for selection template
     * @return int number of updated record, number > 0 valid for successful operation
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFDDbHelper.getReadableDatabase();
        Selection builder = getSelection(uri, selection, selectionArgs);
        int nUpdated = db.update(builder.table, contentValues, builder.selection, builder.selectionArgs);

        if (nUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return nUpdated;
    }


    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mFDDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }



    private Selection getSelection(Uri uri, String selection, String[] selectionArgs) {
        int match = mUriMatcher.match(uri);                             // code for switch
        for (FDContract.FDParams p : FDContract.MATCH_PARAMETERS) {
            if (match == p.tableMatcher) {
                return new Selection(FDContract.CpEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
            }
            if (match == p.tableIdMatcher) {
                List<String> paths = uri.getPathSegments();
                String _id = paths.get(1);
                return new Selection(FDContract.CpEntry.TABLE_NAME,
                        FDContract.CpEntry.COLUMN_COMPETITION_ID + "=?",
                        new String[]{_id});
            }
        }
        throw new UnsupportedOperationException("Unknown uri: " + uri);

    }

    private class Selection {
        String table;
        String selection;
        String[] selectionArgs;

        public Selection(String table, String selection, String[] selectionArgs) {
            this.table = table;
            this.selection = selection;
            this.selectionArgs = selectionArgs;
        }
    }

}
