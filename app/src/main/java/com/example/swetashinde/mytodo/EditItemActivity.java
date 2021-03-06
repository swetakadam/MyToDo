package com.example.swetashinde.mytodo;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.swetashinde.mytodo.model.Task;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.text.DateFormatSymbols;

public class EditItemActivity extends AppCompatActivity implements DatePickerFragment.OnFragmentInteractionListener {

    private int position;
    private Task dataItem;
    private final String LOGTAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_with_menu);

        //programatically add delete button :)

        dataItem = (Task) Parcels.unwrap(getIntent().getParcelableExtra("dataItem"));
        //position = getIntent().getExtras().getInt("position");

        // populate every view ...
        EditText editText = (EditText) findViewById(R.id.taskNameEditText);
        editText.setText(dataItem.getName());
        editText.setSelection(editText.getText().length());

        EditText editTextNotes = (EditText) findViewById(R.id.taskNotesEditText);
        editTextNotes.setText(dataItem.getNotes());

        EditText editTextDueDate = (EditText) findViewById(R.id.due_date_edit_text);
        editTextDueDate.setText(dataItem.getDueDate());

        initializePrioritySpinner();
        initializeStatusSpinner();

        // need to set up a listener for button click ...

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save_task){
            saveEditedTaskData();
            return true ;
        }else if (id == R.id.action_delete_task){
            deleteTask();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveEditedTaskData(){
        // parcel task data back to the main activity ...
        RelativeLayout addTaskLayout          = (RelativeLayout) findViewById(R.id.add_task_layout);
        EditText editTextTaskName      = (EditText) addTaskLayout.findViewById(R.id.taskNameEditText);
        EditText editTextTaskNotes     = (EditText) addTaskLayout.findViewById(R.id.taskNotesEditText);
        EditText editTextDueDate       = (EditText) addTaskLayout.findViewById(R.id.due_date_edit_text);
        Spinner  prioritySpinner       = (Spinner)  addTaskLayout.findViewById(R.id.prioritySpinner);
        Spinner  statusSpinner         = (Spinner)  addTaskLayout.findViewById(R.id.statusSpinner);

        String name                    = editTextTaskName.getText().toString();
        String notes                   = editTextTaskNotes.getText().toString();
        String priority                = prioritySpinner.getSelectedItem().toString();
        String status                  = statusSpinner.getSelectedItem().toString();
        String dueDate                 = editTextDueDate.getText().toString();

        // return the task object to main screen .. where it will be saved in database
        Task newTask = new Task(dataItem.getId(),name,notes,priority,status,dueDate);

        Log.v(LOGTAG,"Task details are name - "+name +" notes : "+notes+" priority : "+priority+" status: "+status + " duedate "+ dueDate);

        //send back new Task obj back to calling activity
        Intent mIntent = new Intent();
        mIntent.putExtra("task", Parcels.wrap(newTask));
        mIntent.putExtra("code",200);
        setResult(RESULT_OK,mIntent);
        finish();

    }

    private void deleteTask(){

        //send back the ID to be deleted ..
        Long id = dataItem.getId();
        Intent mIntent = new Intent();
        mIntent.putExtra("taskId", String.valueOf(id));
        setResult(RESULT_OK,mIntent);
        finish();

    }

    private void initializePrioritySpinner(){
        int priorityIndex = 0;

        // this should not be hardcoded ..
        //how to set a spinner to a value ??
        if(dataItem.getPriority().equalsIgnoreCase("High")){
            priorityIndex = 0;
        }else if(dataItem.getPriority().equalsIgnoreCase("Medium")){
            priorityIndex = 1;
        }else if(dataItem.getPriority().equalsIgnoreCase("Low")){
            priorityIndex = 2;
        }
        //set spinner style
        // Get reference of widgets from XML layout
        Spinner spinner = (Spinner) findViewById(R.id.prioritySpinner);

        // Initializing a String Array
        String[] priorityOptions  = getResources().getStringArray(R.array.priority_options);

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,priorityOptions
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        //set it to right selection
        spinner.setSelection(priorityIndex);

    }

    private void initializeStatusSpinner(){
        int statusIndex = 0;
        if(dataItem.getStatus().equalsIgnoreCase("ToDo")){
            statusIndex = 0;
        }else{
            statusIndex = 1;
        }

        Spinner statusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        String[] statusOptions  = getResources().getStringArray(R.array.status);

        // Initializing an ArrayAdapter
        ArrayAdapter<String> statusSpinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,statusOptions
        );
        statusSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        statusSpinner.setAdapter(statusSpinnerArrayAdapter);
        statusSpinner.setSelection(statusIndex);
    }

    public void onSubmit(View v){
        EditText editTextItem = (EditText) findViewById(R.id.editTextItem);
        //prepare data intent  ... like a blank intent just to send data back
        Intent result = new Intent();
        result.putExtra("position",position);
        result.putExtra("editedDataItem", editTextItem.getText().toString());
        result.putExtra("code",200);
        setResult(RESULT_OK,result);
        finish();

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onFragmentInteraction(int year,int month ,int day) {
        // this is the date from fragment
        Log.v("ADD TASK ACTIVITY","date is "+year + " " +month+" "+day);
        // convert that into format  jan 16 2016
        String monthText = getMonth(month+1); // month is indexed from 0 so 5 means June
        EditText dueDate = (EditText) findViewById(R.id.due_date_edit_text);
        dueDate.setText(monthText+ " " +day+" "+year);
    }

    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
}
