package com.example.swetashinde.mytodo;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;


import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.swetashinde.mytodo.custom.MyCursorAdapter;
import com.example.swetashinde.mytodo.data.TaskContract;
import com.example.swetashinde.mytodo.data.TaskProvider;
import com.example.swetashinde.mytodo.model.Task;

import org.apache.commons.io.FileUtils;
import org.parceler.Parcel;
import org.parceler.Parcels;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TodoActivity extends AppCompatActivity {

    ListView lvItems;
    ArrayAdapter<String> itemsAdapter;
    //SimpleCursorAdapter taskCursorAdapter;
    MyCursorAdapter customAdapter;
    Cursor taskCursor;
    ArrayList<String> items;
    private final int REQUEST_CODE = 200;
    private final int ADD_TASK_REQUEST_CODE = 400;

    private final String LOGTAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //readItems();
        // read now from database AND SET THE LIST VIEW USING SIMPLE CURSOR ADAPTER
        //using simple cursor adapter :)

          taskCursor = readAll();
          lvItems = (ListView) findViewById(R.id.lvItems);
//        String[] columns = {TaskContract.TaskEntry.COLUMN_NAME,TaskContract.TaskEntry.COLUMN_NOTES};
//        int[] ids = {android.R.id.text1,android.R.id.text2};
//        taskCursorAdapter  = new SimpleCursorAdapter(this,android.R.layout.two_line_list_item,taskCursor,columns,ids,0);
//        lvItems.setAdapter(taskCursorAdapter);


        //using custom cursor adapter

        setupListViewListener();

        // on background thread -- must be replaced by Cursor Loader
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                customAdapter = new MyCursorAdapter(
                        getApplicationContext(),
                        taskCursor,
                        0);

                lvItems.setAdapter(customAdapter);
            }

        });

    }


    public void launchEditItemView(Task dataItem){
        Intent i = new Intent(this,com.example.swetashinde.mytodo.EditItemActivity.class);
        //i.putExtra("position",position);
        i.putExtra("dataItem",Parcels.wrap(dataItem));
        startActivityForResult(i,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){

            String deleteTaskId = data.getStringExtra("taskId");

            if(deleteTaskId != null ){
                //delete the item  ...
                ContentResolver resolver = getContentResolver();
                resolver.delete(TaskContract.TaskEntry.buildTaskUri(Long.parseLong(deleteTaskId)),null,null);
                taskCursor = readAll();
                customAdapter.changeCursor(taskCursor);

            }else {

                Task task = (Task) Parcels.unwrap(data.getParcelableExtra("task"));

                // need to update here :) ... but it should happen on another background thread ...
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(TaskContract.TaskEntry.COLUMN_NAME, task.getName());
                values.put(TaskContract.TaskEntry.COLUMN_NOTES, task.getNotes());
                values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, task.getPriority());
                values.put(TaskContract.TaskEntry.COLUMN_STATUS, task.getStatus());
                values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE, task.getDueDate());

                resolver.update(TaskContract.TaskEntry.buildTaskUri(task.getId()), values, null, null);

                //update the Cursor
                taskCursor = readAll();
                customAdapter.changeCursor(taskCursor);
            }


        } else if(resultCode == RESULT_OK && requestCode == ADD_TASK_REQUEST_CODE){

            //should be done on background thread
            Task newTask = (Task) Parcels.unwrap(data.getParcelableExtra("task"));
            // need to store in database here :) ... but it should happen on another background thread ...
            ContentResolver resolver = getContentResolver();
            ContentValues values     = new ContentValues();
            values.put(TaskContract.TaskEntry.COLUMN_NAME,newTask.getName());
            values.put(TaskContract.TaskEntry.COLUMN_NOTES,newTask.getNotes());
            values.put(TaskContract.TaskEntry.COLUMN_PRIORITY,newTask.getPriority());
            values.put(TaskContract.TaskEntry.COLUMN_STATUS,newTask.getStatus());
            values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE,newTask.getDueDate());

            Uri taskUri = resolver.insert(TaskContract.TaskEntry.CONTENT_URI,values);
            long taskRowId = ContentUris.parseId(taskUri);
            Log.v(LOGTAG,"Task created in database with id"+taskRowId);

            //now update the adapter  .. should we query table ???
            taskCursor = readAll();
            customAdapter.changeCursor(taskCursor);

        }

    }


    private void setupListViewListener(){
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //items.remove(position);
                //itemsAdapter.notifyDataSetChanged();
                //saveItems();
                return true;
            }
        });

        lvItems.setOnItemClickListener( new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) customAdapter.getItem(position);
                //make a Task obj .. send it to Edit item view  ... it will send back the Task object ... update that into database ..
                // read all from database and reassign new cursor

                Long   task_id       = cursor.getLong(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
                String name      = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME));
                String notes     = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NOTES));
                String priority  = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY));
                String status    = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_STATUS));
                String dueDate   = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DUE_DATE));


                Task t = new Task(task_id,name,notes,priority,status,dueDate);

               // Log.v(LOGTAG,"the task id selected to edit is "+task.getColumnNames());
                launchEditItemView(t);
            }
        });
    }



    //read from database using content provider
    private Cursor readAll(){
        ContentResolver resolver = getContentResolver();
        Cursor taskCursor  = resolver.query(TaskContract.TaskEntry.CONTENT_URI,null,null,null,null);
        return taskCursor;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_task){
            //start a new activty here  ...
            Intent i = new Intent(this,com.example.swetashinde.mytodo.AddTaskActivity.class);


            startActivityForResult(i,ADD_TASK_REQUEST_CODE);
            return true ;
        }

        return super.onOptionsItemSelected(item);
    }



}
