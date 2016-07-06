package com.example.swetashinde.mytodo;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.swetashinde.mytodo.model.Task;

import org.parceler.Parcels;

import java.text.DateFormatSymbols;


public class AddTaskActivity extends AppCompatActivity implements DatePickerFragment.OnFragmentInteractionListener, AdapterView.OnItemSelectedListener {


    private final String LOGTAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_with_menu);
        //showDatePickerDialog();
       /* if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new DatePickerFragment(),"datePicker")
                    .commit();
        }*/


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



        Spinner statusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        String[] statusOptions  = getResources().getStringArray(R.array.status);

        // Initializing an ArrayAdapter
        ArrayAdapter<String> statusSpinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,statusOptions
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        statusSpinner.setAdapter(statusSpinnerArrayAdapter);

        addListenerOnPrioritySpinnerItemSelection();

        //register both spinners to itemselectlistener

        spinner.setOnItemSelectedListener(this);
        statusSpinner.setOnItemSelectedListener(this);


    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void addListenerOnPrioritySpinnerItemSelection() {
        Spinner spinner1 = (Spinner) findViewById(R.id.prioritySpinner);
        //spinner1.setOnItemSelectedListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_create_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_create_task){
            processTaskData();
            return true ;
        }

        return super.onOptionsItemSelected(item);
    }

    private void processTaskData(){
        // get the root element
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
        Task newTask = new Task(null,name,notes,priority,status,dueDate);

        Log.v(LOGTAG,"Task details are name - "+name +" notes : "+notes+" priority : "+priority+" status: "+status + " duedate "+ dueDate);

        //send back new Task obj back to calling activity
        Intent mIntent = new Intent();
        mIntent.putExtra("task", Parcels.wrap(newTask));
        setResult(RESULT_OK,mIntent);
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
