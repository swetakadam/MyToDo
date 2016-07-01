package com.example.swetashinde.mytodo;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class AddTaskActivity extends AppCompatActivity implements DatePickerFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        //showDatePickerDialog();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new DatePickerFragment(),"datePicker")
                    .commit();
        }

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onFragmentInteraction(int year,int month ,int day) {
            // this is the date from fragment
        Log.v("ADD TASK ACTIVITY","date is "+year + " " +month+" "+day);
    }


}
