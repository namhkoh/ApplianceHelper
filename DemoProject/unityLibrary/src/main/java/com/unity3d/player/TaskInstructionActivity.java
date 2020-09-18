package com.unity3d.player;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

public class TaskInstructionActivity extends AppCompatActivity {
    public static Bundle indexBundle = new Bundle();
    public static int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_instruction);
        Log.e("onCreate Index", String.valueOf(index));
        Button Enter = findViewById(R.id.Enter);
        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity();
            }
        });
        setInstructionText(index);
    }

    private void enterActivity() {
        String str = StartScreen.activityBundle.getString("activity");
        try {
            switch (str) {
                case "uiVariant1":
                    Log.e("taskInstruction", String.valueOf(index));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    index++;
                    Intent intent1 = new Intent(getApplicationContext(), uiVariant1.class);
                    startActivity(intent1);
                    break;
                case "uiVariant2":
                    Log.e("taskInstruction", String.valueOf(index));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    index++;
                    Intent intent2 = new Intent(getApplicationContext(), uiVariant2.class);
                    startActivity(intent2);
                    break;
                case "uiVariant3":
                    Log.e("taskInstruction", String.valueOf(index));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    index++;
                    Intent intent3 = new Intent(getApplicationContext(), uiVariant3.class);
                    startActivity(intent3);
                    break;
                case "uiVariant4":
                    Log.e("taskInstruction", String.valueOf(index));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    index++;
                    Intent intent4 = new Intent(getApplicationContext(), uiVariant4.class);
                    startActivity(intent4);
                    break;
                case "UnityPlayerActivity":
                    Log.e("taskInstruction", String.valueOf(index));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    index++;
                    Intent intent5 = new Intent(getApplicationContext(), UnityPlayerActivity.class);
                    startActivity(intent5);
                    break;
                case "uiVariant6":
                    Log.e("taskInstruction", String.valueOf(index));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    index++;
                    // Dynamic for all appliances.
//                    Intent intent6 = new Intent(getApplicationContext(), uiVariant6.class);
//                    startActivity(intent6);
                    enterFeedback();
                    break;
            }
        } catch (NullPointerException e) {
            //throw new RuntimeException("Please enter the correct userID!", e);
            Intent error = new Intent(getApplicationContext(), StartScreen.class);
            startActivity(error);
            showToast("Error");
        }
    }

    private void setInstructionText(int indexValue) {
        String indexValueString = String.valueOf(indexValue);
        TextView task1 = findViewById(R.id.Task);
        HashMap<String, String> tmp = getData();
        task1.setText(tmp.get(indexValueString));
    }

    private void enterFeedback() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        HashMap<String, String> tmpHash = getData();
        if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
            Intent intent = new Intent(this, uiVariant6.class);
            startActivity(intent);
        } else if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("oven")) {
            Intent intent = new Intent(this, uiVariant6Oven.class);
            startActivity(intent);
        } else {
            return;
        }
        Log.e("entering feedback", "enter");
    }

    private HashMap<String, String> getData() {
        InputStream ls = getResources().openRawResource(R.raw.file2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ls, StandardCharsets.UTF_8));
        String line = "";
        HashMap<String, String> resultList = new HashMap<String, String>();
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                resultList.put(tokens[0], tokens[1]);
            }
        } catch (IOException e) {
            Log.wtf("TaskInstructionActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
        return resultList;
    }

    private void showToast(String text) {
        Toast.makeText(TaskInstructionActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}