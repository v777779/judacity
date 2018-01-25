package ru.vpcb.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import timber.log.Timber;

import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.CONTENT_URI;
import static ru.vpcb.bakingapp.data.RecipeContract.RecipeEntry.TABLE_NAME;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */

/**
 * Recipe Content Provider Class
 */
public class RecipeContentProvider extends ContentProvider {

    public static final int RECIPES = 100;
    public static final int RECIPES_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = RecipeContentProvider.class.getSimpleName();

    /**
     * Returns UriMatcher object which recognizes bulk or alone records Uri
     * @return UriMatcher object
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES + "/#", RECIPES_WITH_ID);
        return uriMatcher;
    }


    private RecipeDbHelper mRecipesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mRecipesDbHelper = new RecipeDbHelper(context);
        return true;
    }

    /**
     *  Returns  Cursor object with RecipeItems loaded from database
     *
     * @param uri URI   address of RecipeItems
     * @param projection
     * @param selection  String  selection query template
     * @param selectionArgs String[] selection arguments for selection template
     * @param sortOrder String sorting template
     * @return Cursor object with RecipeItems
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mRecipesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case RECIPES:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case RECIPES_WITH_ID:
                retCursor = db.query(TABLE_NAME,
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
     *  Inserts RecipeItem into database
     *  RecipeItem object converted to JSON format and store as string
     *
     * @param uri Uri address to store in database
     * @param contentValues ContentValues with RecipeItem data
     * @return Uri of inserted record, Uri is valid for successful operation
     */

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase mDb = mRecipesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RECIPES:
                long id = mDb.insert(TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
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
     *  Deletes RecipeItem from database
     *  RecipeItem object converted to JSON format and store as string
     *
     * @param uri Uri address to store in database
     * @param selection  String  selection query template
     * @param selectionArgs String[] selection arguments for selection template
     * @return int number of deleted record, number > 0 valid for successful operation
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mRecipesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int nDeleted;

        switch (match) {
            case RECIPES:
                nDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + RecipeEntry.TABLE_NAME + "'");
                break;

            case RECIPES_WITH_ID:
                nDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + RecipeEntry.TABLE_NAME + "'");
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
     *  Updates RecipeItem in database
     *  RecipeItem object converted to JSON format and store as string
     *
     * @param uri Uri address to store in database
     * @param contentValues ContentValues with RecipeItem data
     * @param selection  String  selection query template
     * @param selectionArgs String[] selection arguments for selection template
     * @return int number of updated record, number > 0 valid for successful operation
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mRecipesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        int nUpdated;

        switch (match) {
            case RECIPES_WITH_ID:
                nUpdated = db.update(TABLE_NAME, contentValues, selection, selectionArgs);
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
     * @param uri Uri address to store in database
     * @param contentValues ContentValues with RecipeItem data
     * @return int number of inserted records, number > 0 valid for successful operation
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] contentValues) {
        final SQLiteDatabase db = mRecipesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        if (contentValues == null) return 0;

        switch (match) {
            case RECIPES:
                db.beginTransaction();
                int numInserted = 0;
                try {
                    for (ContentValues value : contentValues) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(RecipeEntry.TABLE_NAME, null, value);
                        } catch (SQLiteConstraintException e) {
                            Timber.d(TAG+ "Attempting to insert " +
                                    value.getAsString(RecipeEntry.COLUMN_RECIPE_NAME)
                                    + " but value is already in database.");
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
}
