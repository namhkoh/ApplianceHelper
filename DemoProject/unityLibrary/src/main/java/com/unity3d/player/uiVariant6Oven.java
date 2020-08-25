package com.unity3d.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class uiVariant6Oven extends AppCompatActivity {

    private String lcdString = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant6_oven);

        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> nextActivity());

        Button frozenBakeBtn = findViewById(R.id.oven_frozen);
        frozenBakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frozenBake();
            }
        });

        Button cookTimeBtn = findViewById(R.id.oven_cooktime);
        cookTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookTime();
            }
        });

        Button o0 = findViewById(R.id.oven_0);
        o0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press0();
            }
        });

        Button o1 = findViewById(R.id.oven_1);
        o1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press1();
            }
        });

        Button o2 = findViewById(R.id.oven_2);
        o2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press2();
            }
        });

        Button o3 = findViewById(R.id.oven_3);
        o3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press3();
            }
        });

        Button o4 = findViewById(R.id.oven_4);
        o4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press4();
            }
        });

        Button o5 = findViewById(R.id.oven_5);
        o5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press5();
            }
        });

        Button o6 = findViewById(R.id.oven_6);
        o6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press6();
            }
        });

        Button o7 = findViewById(R.id.oven_7);
        o7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press7();
            }
        });

        Button o8 = findViewById(R.id.oven_8);
        o8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press8();
            }
        });

        Button o9 = findViewById(R.id.oven_9);
        o9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press9();
            }
        });

        Button startOvenbtn = findViewById(R.id.oven_start);
        startOvenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOven();
            }
        });

        Button cancelOvenBtn = findViewById(R.id.oven_cancel);
        cancelOvenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOven();
            }
        });

        ArrayList<Button> allButtons = new ArrayList<Button>(Arrays.asList(
                frozenBakeBtn, cookTimeBtn, o0, o1, o2, o3, o4, o5, o6, o7, o8, o9, startOvenbtn, cancelOvenBtn
        ));
        for (int i = 0; i < allButtons.size(); i++) {
            allButtons.get(i).setAlpha(0);
        }
    }

    private void update(String buttonValue){
        //°F
        Log.e("Button added",buttonValue);
        TextView ovenLcd = findViewById(R.id.oven_panel_text);
        lcdString += buttonValue;
        ovenLcd.setText(lcdString);
    }

    private void frozenBake() {
        TextView ovenLcd = findViewById(R.id.oven_panel_text);
        Handler h = new Handler(getMainLooper());
        lcdString = "Frozen Bake";
        ovenLcd.setTextSize(30);
        ovenLcd.setText(lcdString);
        h.postDelayed(() -> {
            lcdString = "";
            lcdString = "Enter food code";
            ovenLcd.setText(lcdString);
        }, 3000);
        Log.e("Button pressed", "frozenBake");
    }

    private void stringChecker(String val) {
        TextView ovenLcd = findViewById(R.id.oven_panel_text);
        if (ovenLcd.getText().toString().contains("Enter temperature:")) {
            // run method to listen for temperature
            lcdString = "";
            lcdString = val;
            ovenLcd.setText(val);
        }
    }

    private void cookTime() {
        Log.e("Button pressed", "cookTime");
    }

    private void startOven() {
        Log.e("Button pressed", "startOven");
    }

    private void cancelOven() {
        TextView lcd = findViewById(R.id.oven_panel_text);
        lcdString = "";
        lcd.setText(lcdString);
        Log.e("Button pressed", "cancelOven");
    }

    private void press0() {
        Log.e("Button pressed", "0");
    }

    private void press1() {
        update("1");
        Log.e("Button pressed", "1");
    }

    private void press2() {
        Log.e("Button pressed", "2");

    }

    private void press3() {
        Log.e("Button pressed", "3");

    }

    private void press4() {
        Log.e("Button pressed", "4");
    }

    private void press5() {
        Log.e("Button pressed", "5");
    }

    private void press6() {
        Log.e("Button pressed", "6");
    }

    private void press7() {
        Log.e("Button pressed", "7");
    }

    private void press8() {
        Log.e("Button pressed", "8");
    }

    private void press9() {
        Log.e("Button pressed", "9");
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

    private void setAlphaValue(int alpha) {
        ArrayList<Button> allButtons = new ArrayList<Button>(Arrays.asList());
        for (int i = 0; i < allButtons.size(); i++) {
            allButtons.get(i).setAlpha(0);
        }
    }
}