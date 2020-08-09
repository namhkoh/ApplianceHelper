package com.unity3d.player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class uiVariant6 extends AppCompatActivity {
    private String lcdString = " ";
    private String altString = " ";
    private int index = 0;
    public static Bundle uiVariant6Bundle = new Bundle();

    boolean meltPressed = false;
    boolean meltButterBool = false;
    boolean startPressed = false;
    boolean clockPressed = false;
    boolean cancelPressed = false;

    boolean zeroPressed = false;
    boolean onePressed = false;
    boolean twoPressed = false;
    boolean threePressed = false;
    boolean fourPressed = false;
    boolean fivePressed = false;
    boolean sixPressed = false;
    boolean sevenPressed = false;
    boolean eightPressed = false;
    boolean ninePressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant6);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //refreshTime();
        TextView lcd = findViewById(R.id.lcd_text);
        //lcdString = DateTimeHandler.getCurrentTime("hh:mm");
        lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));

        Button clock = findViewById(R.id.microwave_clock);
        clock.setOnClickListener(v -> {
            microwaveClock();
        });

        Button start = findViewById(R.id.microwave_start);
        start.setOnClickListener(v -> {
            microwaveStart();
        });

        Button cancel = findViewById(R.id.microwave_cancel);
        cancel.setOnClickListener(v -> {
            microwaveCancel();
        });

        Button soften = findViewById(R.id.microwave_soften);
        soften.setOnClickListener(v -> {
            microwaveSoften();
        });

        Button m0 = findViewById(R.id.microwave_0);
        m0.setOnClickListener(v -> {
            press0();
        });

        Button m1 = findViewById(R.id.microwave_1);
        m1.setOnClickListener(v -> {
            press1();
        });

        Button m2 = findViewById(R.id.microwave_2);
        m2.setOnClickListener(v -> {
            press2();
        });

        Button m3 = findViewById(R.id.microwave_3);
        m3.setOnClickListener(v -> {
            press3();
        });

        Button m4 = findViewById(R.id.microwave_4);
        m4.setOnClickListener(v -> {
            press4();
        });

        Button m5 = findViewById(R.id.microwave_5);
        m5.setOnClickListener(v -> {
            press5();
        });

        Button m6 = findViewById(R.id.microwave_6);
        m6.setOnClickListener(v -> {
            press6();
        });

        Button m7 = findViewById(R.id.microwave_7);
        m7.setOnClickListener(v -> {
            press7();
        });

        Button m8 = findViewById(R.id.microwave_8);
        m8.setOnClickListener(v -> {
            press8();
        });

        Button m9 = findViewById(R.id.microwave_9);
        m9.setOnClickListener(v -> {
            press9();
        });

        Button next = findViewById(R.id.Next);
        next.setOnClickListener(v -> {
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
        });

        Button popcorn = findViewById(R.id.microwave_popcorn);
        Button potato = findViewById(R.id.microwave_potato);
        Button pizza = findViewById(R.id.microwave_pizza);
        Button cook = findViewById(R.id.microwave_cook);
        Button reheat = findViewById(R.id.microwave_reheat);
        Button cooktime = findViewById(R.id.microwave_cooktime);
        Button cookpower = findViewById(R.id.microwave_cookpower);
        Button defrost = findViewById(R.id.microwave_defrost);
        Button timer = findViewById(R.id.microwave_timer);
        Button add30 = findViewById(R.id.microwave_30sec);
        ArrayList<Button> allButtons = new ArrayList<Button>(Arrays.asList(clock, start, cancel, soften, m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m0,
                popcorn, potato, pizza, cook, reheat, cooktime, cookpower, defrost,
                timer, add30));
        for (int i = 0; i < allButtons.size(); i++) {
            allButtons.get(i).setAlpha(0);
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

    private void microwaveClock() {
        clockPressed = true;
        clearClock();
        Log.e("Button Pressed", "Clock");
    }

    private void microwaveStart() {
        startPressed = true;
        TextView lcd = findViewById(R.id.lcd_text);
        if (clockPressed) {
            setClock();
        } else if (meltButterBool) {
            lcdString = "";
            lcdString = "Ready!";
            lcd.setText(lcdString);
        } else {
            return;
        }
        Log.e("Button Pressed", "Start");
    }

    private void microwaveSoften() {
        meltPressed = true;
        meltSelection();
        Log.e("Button Pressed", "Soften/Melt");
    }

    private void microwaveCancel() {
        TextView lcd = findViewById(R.id.lcd_text);
        tmpList.clear();
        cancelPressed = true;
        lcdString = "";
        lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));
        Log.e("Button Pressed", "Cancel");
    }

    private void press0() {
        zeroPressed = true;
        lcdString += 0;
        tmpList.add("0");
        update(lcdString);
        Log.e("Button Pressed", "0");
    }

    private void press1() {
        onePressed = true;
        lcdString += "1";
        tmpList.add("1");
        update(lcdString);
        Log.e("Button Pressed", "1");
    }

    private void press2() {
        twoPressed = true;
        lcdString += "2";
        tmpList.add("2");
        update(lcdString);
        Log.e("Button Pressed", "2");
    }

    private void press3() {
        threePressed = true;
        lcdString += "3";
        tmpList.add("3");
        update(lcdString);
        Log.e("Button Pressed", "3");
    }

    private void press4() {
        fourPressed = true;
        lcdString += "4";
        tmpList.add("4");
        update(lcdString);
        Log.e("Button Pressed", "4");
    }

    private void press5() {
        fivePressed = true;
        lcdString = "5";
        tmpList.add("5");
        update(lcdString);
        Log.e("Button Pressed", "5");
    }

    private void press6() {
        sixPressed = true;
        lcdString += "6";
        tmpList.add("6");
        update(lcdString);
        Log.e("Button Pressed", "6");
    }

    private void press7() {
        sevenPressed = true;
        lcdString += "7";
        tmpList.add("7");
        update(lcdString);
        Log.e("Button Pressed", "7");
    }

    private void press8() {
        eightPressed = true;
        lcdString += "8";
        tmpList.add("8");
        update(lcdString);
        Log.e("Button Pressed", "8");
    }

    private void press9() {
        ninePressed = true;
        lcdString += "9";
        tmpList.add("9");
        update(lcdString);
        Log.e("Button Pressed", "9");
    }

    private void update(String buttonValue) {
        Log.e("update", buttonValue);
        lcdString = "";
        lcdString = buttonValue;
        TextView lcd = findViewById(R.id.lcd_text);
        Log.e("current size", String.valueOf(tmpList.size()));
        lcd.setText(lcdString);
        if (meltPressed) {
            meltButter(buttonValue);
        } else if (meltButterBool) {
            setButterQty(buttonValue);
        }
    }

    ArrayList<String> tmpList = new ArrayList<String>();

    private void clearClock() {
        TextView alt = findViewById(R.id.alt_txt);
        TextView lcd = findViewById(R.id.lcd_text);
        alt.setAlpha(1);
        lcdString = " ";
        tmpList.clear();
        if (clockPressed) {
            // blink animation and allow user to alter the time
            altString = " : ";
            alt.setText(altString);
            lcd.setText(lcdString);
        }
    }

    private void setClock(){
        TextView alt = findViewById(R.id.alt_txt);
        TextView lcd = findViewById(R.id.lcd_text);
        altString = "";
        alt.setText(altString);
        StringBuilder hh = new StringBuilder();
        StringBuilder mm = new StringBuilder();
        Log.e("First", tmpList.get(0));
        Log.e("Second", tmpList.get(1));
        Log.e("Third", tmpList.get(2));
        Log.e("Fourth", tmpList.get(3));
        System.out.println(tmpList.size());
        hh.append(tmpList.get(0)).append(tmpList.get(1));
        mm.append(tmpList.get(2)).append(tmpList.get(3));
        update(lcdString);
        String newTime = DateTimeHandler.setNewTime(hh.toString(), mm.toString());
        lcd.setText(newTime);
        clockPressed = false;
    }

    // This method will set the microwave panel to 0 and wait for an incoming string
    private void meltSelection() {
        TextView lcd = findViewById(R.id.lcd_text);
        lcdString = "";
        lcdString = "0";
        lcd.setText(lcdString);
    }

    // If the user inputs "5", it will set the panel to "0.0 QTY";
    private void meltButter(String buttonValue) {
        Log.e("button value", buttonValue);
        Handler h = new Handler(getMainLooper());
        TextView lcd = findViewById(R.id.lcd_text);
        TextView alt = findViewById(R.id.alt_txt);
        if (buttonValue.equals("5")) {
            lcdString = "";
            lcdString += "5";
            lcd.setText(lcdString);
            h.postDelayed(() -> {
                lcdString = "";
                altString = "";
                //lcdString += "0.0 QTY";
                altString += "0.0 QTY";
                lcd.setText(lcdString);
                lcd.setAlpha(0);
                alt.setText(altString);
            }, 1000);
            meltButterBool = true;
            meltPressed = false;
        }
    }

    private void setButterQty(String qty) {
        TextView lcd = findViewById(R.id.lcd_text);
        TextView alt = findViewById(R.id.alt_txt);
        alt.setAlpha(0);
        lcd.setAlpha(1);
        Log.e("butter qty", qty);
        switch (qty) {
            case "1":
                lcdString = "1.0 QTY";
                lcd.setText(lcdString);
                break;
            case "2":
                lcdString = "2.0 QTY";
                lcd.setText(lcdString);
                break;
            case "3":
                lcdString = "3.0 QTY";
                lcd.setText(lcdString);
                break;
            case "4":
                lcdString = "4.0 QTY";
                lcd.setText(lcdString);
                break;
            case "5":
                lcdString = "5.0 QTY";
                lcd.setText(lcdString);
                break;
            case "6":
                lcdString = "6.0 QTY";
                lcd.setText(lcdString);
                break;
            case "7":
                lcdString = "7.0 QTY";
                lcd.setText(lcdString);
                break;
            case "8":
                lcdString = "8.0 QTY";
                lcd.setText(lcdString);
                break;
            case "9":
                lcdString = "9.0 QTY";
                lcd.setText(lcdString);
                break;
        }
    }

}