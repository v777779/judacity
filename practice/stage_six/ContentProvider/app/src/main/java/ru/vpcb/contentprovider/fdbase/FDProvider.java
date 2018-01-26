package ru.vpcb.contentprovider.fdbase;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import timber.log.Timber;

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
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case COMPETITIONS:
                retCursor = db.query(FDContract.CpEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case COMPETITIONS_WITH_ID:
                retCursor = db.query(FDContract.CpEntry.TABLE_NAME,
                        projection,
                        selection,      // RecipeEntry._ID + " = ?",
                        selectionArgs,  // new String[] {String.valueOf(ContentUris.parseId(uri)) },
                        null,
                        null,
                        sortOrder);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
//        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
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

        final SQLiteDatabase mDb = mFDDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case COMPETITIONS:
                long id = mDb.insert(FDContract.CpEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FDContract.CpEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
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

        int match = sUriMatcher.match(uri);
        int nDeleted;

        switch (match) {
            case COMPETITIONS:
                nDeleted = db.delete(FDContract.CpEntry.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FDContract.CpEntry.TABLE_NAME + "'");
                break;

            case COMPETITIONS_WITH_ID:
                nDeleted = db.delete(FDContract.CpEntry.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FDContract.CpEntry.TABLE_NAME + "'");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
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
        int match = sUriMatcher.match(uri);
        int nUpdated;

        switch (match) {
            case COMPETITIONS_WITH_ID:
                nUpdated = db.update(FDContract.CpEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (nUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return nUpdated;
    }

    /**
     * Inserts a number  of RecipeItems into database
     *
     * @param uri           Uri address to store in database
     * @param contentValues ContentValues with RecipeItem data
     * @return int number of inserted records, number > 0 valid for successful operation
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] contentValues) {
        final SQLiteDatabase db = mFDDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        if (contentValues == null) return 0;

        switch (match) {
            case COMPETITIONS:
                db.beginTransaction();
                int numInserted = 0;
                try {
                    for (ContentValues value : contentValues) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(FDContract.CpEntry.TABLE_NAME, null, value);
                        } catch (SQLiteConstraintException e) {
                            Timber.d("Attempting to insert id:" +
                                    value.getAsString(FDContract.CpEntry.COLUMN_COMPETITION_ID) +
                                    " name: " +
                                    value.getAsString(FDContract.CpEntry.COLUMN_COMPETITION_CAPTION) +
                                    " but value is already in database.");
                        }
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    if (numInserted > 0) {
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }
                if (numInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            default:
                return super.bulkInsert(uri, contentValues);
        }
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
}
