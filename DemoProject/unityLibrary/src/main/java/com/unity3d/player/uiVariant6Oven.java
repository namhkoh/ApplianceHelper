package com.unity3d.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class uiVariant6Oven extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant6_oven);

        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> nextActivity());

        Button frozenBake = findViewById(R.id.oven_frozen);
        frozenBake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button cookTime = findViewById(R.id.oven_cooktime);
        cookTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o0 = findViewById(R.id.oven_0);
        o0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o1 = findViewById(R.id.oven_1);
        o1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o2 = findViewById(R.id.oven_2);
        o2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o3 = findViewById(R.id.oven_3);
        o3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o4 = findViewById(R.id.oven_4);
        o4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o5 = findViewById(R.id.oven_5);
        o5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o6 = findViewById(R.id.oven_6);
        o6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o7 = findViewById(R.id.oven_7);
        o7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o8 = findViewById(R.id.oven_8);
        o8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

        Button o9 = findViewById(R.id.oven_9);
        o9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert method
            }
        });

    }

    private void nextActivity() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        HashMap<String, String> tmpHash = getData();
        ArrayList<String> instructionList = new ArrayList<>(tmpHash.values());
        if (incoming_index < instructionList.size() - 1) {
            Intent intent = new Intent(this, TaskInstructionActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, UserSurveyActivity.class);
            startActivity(intent);
        }
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
}