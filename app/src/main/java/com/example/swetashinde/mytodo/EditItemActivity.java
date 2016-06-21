package com.example.swetashinde.mytodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditItemActivity extends AppCompatActivity {

    private int position;
    private String dataItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        dataItem = getIntent().getExtras().getString("dataItem");
        position = getIntent().getExtras().getInt("position");

        // populate the multiline edit text with original list item value
        EditText editText = (EditText) findViewById(R.id.editTextItem);
        editText.setText(dataItem);
        editText.setSelection(editText.getText().length());

        // need to set up a listener for button click ...

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
}
