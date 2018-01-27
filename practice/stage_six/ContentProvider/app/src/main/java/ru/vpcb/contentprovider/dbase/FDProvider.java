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

    public static final int COMPETITIONS = 100;
    public static final int COMPETITIONS_WITH_ID = 101;
    public static final int COMPETITION_TEAMS = 150;
    public static final int COMPETITION_TEAMS_WITH_ID = 151;
    public static final int COMPETITION_FIXTURES = 170;
    public static final int COMPETITION_FIXTURES_WITH_ID = 171;
    public static final int TEAMS = 200;
    public static final int TEAMS_WITH_ID = 201;
    public static final int TEAMS_WITH_COMP_ID = 202;
    public static final int FIXTURES = 400;
    public static final int FIXTURES_WITH_TEAM_ID = 401;
    public static final int FIXTURES_WITH_COMP_ID = 402;
    public static final int TABLES = 500;
    public static final int TABLES_WITH_ID = 501;
    public static final int PLAYERS = 600;
    public static final int PLAYERS_WITH_TEAM_ID = 601;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FDDbHelper mFDDbHelper;

    /**
     * Returns UriMatcher object which recognizes bulk or alone records Uri
     *
     * @return UriMatcher object
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_COMPETITIONS, COMPETITIONS);
        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_COMPETITIONS + "/#", COMPETITIONS_WITH_ID);

        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_COMPETITION_TEAMS, COMPETITION_TEAMS);
        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_COMPETITION_TEAMS + "/#", COMPETITION_TEAMS_WITH_ID);

        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_COMPETITION_FIXTURES, COMPETITION_FIXTURES);
        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_COMPETITION_FIXTURES + "/#", COMPETITION_FIXTURES_WITH_ID);

        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_TEAMS, TEAMS);
        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_TEAMS + "/#", TEAMS_WITH_ID);

        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_FIXTURES, FIXTURES);
        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_FIXTURES + "/#", FIXTURES_WITH_TEAM_ID);
        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_FIXTURES + "/#", FIXTURES_WITH_COMP_ID);

        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_TABLES, TABLES);
        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_TABLES + "/#", TABLES_WITH_ID);

        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_PLAYERS, PLAYERS);
        uriMatcher.addURI(FDContract.CONTENT_AUTHORITY, FDContract.TABLE_PLAYERS + "/#", PLAYERS_WITH_TEAM_ID);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFDDbHelper = new FDDbHelper(context);
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

        Selection builder = getSelection(uri);

        Cursor cursor = db.query(builder.table,
                projection,
                selection,
                selectionArgs,
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
        Selection builder = getSelection(uri);

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
        Selection builder = getSelection(uri);
        int nDeleted = 0;
        if (selection == null) {
            nDeleted = db.delete(builder.table, builder.selection, builder.selectionArgs);
        }
        else {
            nDeleted = db.delete(builder.table, selection, selectionArgs);
        }

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
        Selection builder = getSelection(uri);
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


    public static Uri buildItemIdUri(String tableName, long id) {
        return FDContract.BASE_CONTENT_URI.buildUpon().appendPath(tableName).appendPath(Long.toString(id)).build();
    }


    private Selection getSelection(Uri uri) {
        int match = sUriMatcher.match(uri);                 // code for switch
        List<String> paths = uri.getPathSegments();
        String _id;
        switch (match) {
            case FDContract.CpEntry.TABLE_MATCHER:
                return new Selection(FDContract.CpEntry.TABLE_NAME,
                        null,
                        null);

            case FDContract.CpEntry.TABLE_ID_MATCHER:
                _id = paths.get(1);
                return new Selection(FDContract.CpEntry.TABLE_NAME,
                        FDContract.CpEntry.COLUMN_COMPETITION_ID + "=?",
                        new String[]{_id});


            case FDContract.TmEntry.TABLE_MATCHER:
                return new Selection(FDContract.TmEntry.TABLE_NAME,
                        null,
                        null);


            case FDContract.TmEntry.TABLE_ID_MATCHER:
                _id = paths.get(1);
                return new Selection(FDContract.TmEntry.TABLE_NAME,
                        FDContract.TmEntry.COLUMN_TEAM_ID + "=?",
                        new String[]{_id});
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
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
