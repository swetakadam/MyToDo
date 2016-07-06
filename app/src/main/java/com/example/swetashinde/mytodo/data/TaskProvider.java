package com.example.swetashinde.mytodo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by swetashinde on 6/30/16.
 */
public class TaskProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TaskDbHelper mOpenHelper;

    static final int TASK = 100;
    static final int TASK_WITH_ID = 101;


    @Override
    public boolean onCreate() {
        mOpenHelper = new TaskDbHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "task/#"
            case TASK_WITH_ID: {
                //get id from URI
                long id = TaskContract.TaskEntry.getIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TaskContract.TaskEntry.TABLE_NAME,
                        projection,
                        TaskContract.TaskEntry._ID + " = ?",
                        new String[]{Long.toString(id)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "task"
            case TASK: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TaskContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case TASK_WITH_ID:
                return TaskContract.TaskEntry.CONTENT_ITEM_TYPE;
            case TASK:
                return TaskContract.TaskEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(TaskContract.TaskEntry.COLUMN_DUE_DATE)) {
            long dateValue = values.getAsLong(TaskContract.TaskEntry.COLUMN_DUE_DATE);
            values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE, TaskContract.normalizeDate(dateValue));
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TASK: {
                //normalizeDate(values);
                long _id = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TaskContract.TaskEntry.buildTaskUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            // more cases go here  ... if any .. may be not for this app ...
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numOfDeletedRecords;

        selection = (selection == null) ? "1" : selection;

        // handle.  If it doesn't match these, throw an UnsupportedOperationException.
        switch (match) {
            case TASK: {
                numOfDeletedRecords = db.delete(TaskContract.TaskEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            // more cases for future based on  different uri's
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // return the actual rows deleted
        if(numOfDeletedRecords !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numOfDeletedRecords;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numOfUpdatedRecords;

        // handle.  If it doesn't match these, throw an UnsupportedOperationException.
        switch (match) {
            case TASK: {
                numOfUpdatedRecords = db.update(TaskContract.TaskEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            // more cases for future based on  different uri's
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // return the actual rows deleted
        if(numOfUpdatedRecords !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numOfUpdatedRecords;
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below
        final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority       = TaskContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        sURIMatcher.addURI(authority,TaskContract.PATH_TASK ,TASK);
        sURIMatcher.addURI(authority,TaskContract.PATH_TASK+"/#",TASK_WITH_ID);

        // 3) Return the new matcher!
        return sURIMatcher;
    }
}
