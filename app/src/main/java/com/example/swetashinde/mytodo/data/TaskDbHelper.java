package com.example.swetashinde.mytodo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by swetashinde on 6/30/16.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "mytodo.db";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create database tables here ...
        // we have only one table task
        final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TaskContract.TaskEntry.COLUMN_NAME + " TEXT NOT NULL, "+
                TaskContract.TaskEntry.COLUMN_NOTES + " TEXT NOT NULL, "+
                TaskContract.TaskEntry.COLUMN_PRIORITY + " TEXT NOT NULL, "+
                TaskContract.TaskEntry.COLUMN_DUE_DATE + " TEXT UNIQUE NOT NULL, "+
                TaskContract.TaskEntry.COLUMN_STATUS + " TEXT NOT NULL "+
                ");";

        //create the table task
        sqLiteDatabase.execSQL(SQL_CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Note that this only fires if you change the version number for your database.
        // Need to do more work here based on the change in the schema
        onCreate(sqLiteDatabase);
    }
}
