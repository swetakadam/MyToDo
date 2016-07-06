package com.example.swetashinde.mytodo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by swetashinde on 7/5/16.
 */
@org.parceler.Parcel
public class Task{

    private Long id;
    private String name;
    private String notes;
    private String priority; // later change to enum
    private String status;
    private String dueDate;

    public Task(){

    }

    public Task(Long id,String name,String notes,String priority,String status,String dueDate){
        this.id       = id;
        this.name     = name;
        this.notes    = notes;
        this.priority = priority;
        this.status   = status;
        this.dueDate  = dueDate;
    }



}