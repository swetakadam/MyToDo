package com.example.swetashinde.mytodo.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by swetashinde on 6/30/16.
 */
public class TestTaskProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestTaskProvider.class.getSimpleName();


    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                TaskContract.TaskEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                TaskContract.TaskEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Tasks table during delete", 0, cursor.getCount());
        cursor.close();

    }


    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.

     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // TaskProvider class
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                TaskProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: TaskProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " +TaskContract.CONTENT_AUTHORITY,
                    providerInfo.authority, TaskContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: TaskProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.

         */
    public void testGetType() {
        // content://com.example.swetashinde.mytodo/task/
        String type = mContext.getContentResolver().getType(TaskContract.TaskEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.swetashinde.mytodo/task
        assertEquals("Error: the TaskEntry CONTENT_URI should return TaskEntry.CONTENT_TYPE",
                TaskContract.TaskEntry.CONTENT_TYPE, type);

        long id = 1l;
        // content://com.example.swetashinde.mytodo/task/1
        type = mContext.getContentResolver().getType(
                TaskContract.TaskEntry.buildTaskUri(id));
        // vnd.android.cursorT.dir/com.example.swetashinde.mytodo/task/1
        assertEquals("Error: the TaskEntry CONTENT_URI with task should return TaskEntry.CONTENT_TYPE",
                TaskContract.TaskEntry.CONTENT_ITEM_TYPE, type);


    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.
     */
    public void testBasicTaskQuery() {
        // insert our test records into the database
        TaskDbHelper dbHelper = new TaskDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createTestTaskValues();
        long taskRowId = TestUtilities.insertTestTaskValues(mContext);

        db.close();

        // Test the basic content provider query
        Cursor taskCursor = mContext.getContentResolver().query(
               TaskContract.TaskEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicTasjQuery", taskCursor, testValues);

        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Task Query did not properly set NotificationUri",
                    taskCursor.getNotificationUri(), TaskContract.TaskEntry.CONTENT_URI);
        }
    }


    /*
        This test uses the provider to insert and then update the data.
     */
    public void testUpdateTask() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createTestTaskValues();

        Uri taskUri = mContext.getContentResolver().
                insert(TaskContract.TaskEntry.CONTENT_URI, values);
        long taskRowId = ContentUris.parseId(taskUri);

        // Verify we got a row back.
        assertTrue(taskRowId != -1);
        Log.d(LOG_TAG, "New row id: " + taskRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(TaskContract.TaskEntry._ID, taskRowId);
        updatedValues.put(TaskContract.TaskEntry.COLUMN_NAME,"Buy Soy Milk");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor taskCursor = mContext.getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        taskCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                TaskContract.TaskEntry.CONTENT_URI, updatedValues, TaskContract.TaskEntry._ID + "= ?",
                new String[] { Long.toString(taskRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        taskCursor.unregisterContentObserver(tco);
        taskCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                TaskContract.TaskEntry.CONTENT_URI,
                null,   // projection
                TaskContract.TaskEntry._ID + " = " + taskRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateTask.  Error validating Task entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createTestTaskValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TaskContract.TaskEntry.CONTENT_URI, true, tco);
        Uri taskUri = mContext.getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, testValues);

        // Did our content observer get called?   If this fails, your insert task
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long taskRowId = ContentUris.parseId(taskUri);

        // Verify we got a row back.
        assertTrue(taskRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                TaskContract.TaskEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating TaskEntry.",
                cursor, testValues);


    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our task delete
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TaskContract.TaskEntry.CONTENT_URI, true, taskObserver);

        deleteAllRecordsFromProvider();

        // If  fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        taskObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(taskObserver);

    }



}
