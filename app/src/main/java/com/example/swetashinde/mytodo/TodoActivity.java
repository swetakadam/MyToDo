package com.example.swetashinde.mytodo;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    ArrayList<String> items;
    private final int REQUEST_CODE = 200;
    private final int ADD_TASK_REQUEST_CODE = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        readItems();

        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();

    }

    public void addTodoItem(View v){

        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        itemsAdapter.add(etNewItem.getText().toString());
        etNewItem.setText("");
        saveItems();


    }

    public void launchEditItemView(String dataItem,int position){
        Intent i = new Intent(this,com.example.swetashinde.mytodo.EditItemActivity.class);
        i.putExtra("position",position);
        i.putExtra("dataItem",dataItem);

        startActivityForResult(i,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){

            String editedText = data.getExtras().getString("editedDataItem");
            int    position   = data.getExtras().getInt("position");

            items.set(position,editedText);
            itemsAdapter.notifyDataSetChanged();
            saveItems();


        } else if(resultCode == RESULT_OK && requestCode == ADD_TASK_REQUEST_CODE){

            Task newTask = (Task) Parcels.unwrap(data.getParcelableExtra("taskNew"));
            // need to store in database here :) ... but it should happen on another background thread ...

        }



    }


    private void setupListViewListener(){
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                saveItems();
                return true;
            }
        });

        lvItems.setOnItemClickListener( new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dataItem = items.get(position);
                launchEditItemView(dataItem,position);
            }
        });
    }

    //reading and writing from files
    private void readItems(){

        File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"mytodo.txt");
        try{
            String encoding = null; // platform default
            items = new ArrayList<String>(FileUtils.readLines(todoFile,encoding));
        }catch(IOException ex){
            items = new ArrayList<String>();
            ex.printStackTrace();
        }

    }

    private void saveItems(){

        File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"mytodo.txt");
        try{
            FileUtils.writeLines(todoFile,items,true);

        }catch(IOException ex){
            items = new ArrayList<String>();
            ex.printStackTrace();
        }

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
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_add_task){
            //start a new activty here  ...
            Intent i = new Intent(this,com.example.swetashinde.mytodo.AddTaskActivity.class);
            //i.putExtra("position",position);
            //i.putExtra("dataItem",dataItem);

            //startActivityForResult(i,REQUEST_CODE);
            startActivityForResult(i,ADD_TASK_REQUEST_CODE);
            return true ;
        }

        return super.onOptionsItemSelected(item);
    }



}
