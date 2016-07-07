package com.example.swetashinde.mytodo.custom;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swetashinde.mytodo.R;
import com.example.swetashinde.mytodo.data.TaskContract;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by swetashinde on 7/6/16.
 */
public class MyCursorAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;

    public MyCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    public void bindView(View view, Context context, Cursor cursor) {

        //bind task name
        TextView textViewName= (TextView) view.findViewById(R.id.textName);
        String name = cursor.getString( cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME) );
        textViewName.setText(StringUtils.capitalize(name.toLowerCase().trim()));


        //bind task priority
        TextView textViewPriority = (TextView) view.findViewById(R.id.textPriority);
        String priority = cursor.getString( cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY) );
        textViewPriority.setText(priority);
        if(priority.equalsIgnoreCase("HIGH")){
            textViewPriority.setTextColor(Color.RED);
        }else if(priority.equalsIgnoreCase("MEDIUM")){
            textViewPriority.setTextColor(Color.rgb(265,165,0));
        }else if(priority.equalsIgnoreCase("LOW")){
            textViewPriority.setTextColor(Color.YELLOW);
        }

        //bind task details or notes
        TextView textViewNotes= (TextView) view.findViewById(R.id.textNotes);
        String notes = cursor.getString( cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NOTES) );
        textViewNotes.setText(notes);


    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return cursorInflater.inflate(R.layout.my_cursor_adapter, parent, false);
    }
}
