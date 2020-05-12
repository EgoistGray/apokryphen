package com.violetgarden.projectuncharted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    NumberPicker hour, minute, seconds;
    EditText name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        name = findViewById(R.id.taskName);
        Button createButton = findViewById(R.id.createButton);

        hour = findViewById(R.id.hour_pick);
        hour.setMinValue(0);
        hour.setMaxValue(9);

        minute = findViewById(R.id.minute_pick);
        minute.setMinValue(0);
        minute.setMaxValue(59);

        seconds = findViewById(R.id.second_pick);
        seconds.setMinValue(0);
        seconds.setMaxValue(59);

        name.requestFocus();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hour.getValue() == 0 && minute.getValue() == 0 && seconds.getValue() == 0){
                    Toast.makeText(AddTaskActivity.this, "Duration cannot be all zero", Toast.LENGTH_LONG).show();
                    return;
                }
                if(name.getText().toString().trim().length() <= 0){
                    Toast.makeText(AddTaskActivity.this, "Please set task name", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent returningIntent = new Intent();
                returningIntent.putExtra("TASK_NAME", name.getText().toString());
                returningIntent.putExtra("TASK_DURATION", hour.getValue() * 3600 + minute.getValue() * 60 + seconds.getValue());

                setResult(RESULT_OK, returningIntent);
                finish();
            }
        });

    }
}
