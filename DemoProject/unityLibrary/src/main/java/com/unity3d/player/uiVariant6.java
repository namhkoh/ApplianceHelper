package com.unity3d.player;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
    boolean timerPressed = false;


    boolean numberpad_active = false;
    boolean start_active = false;
    boolean cancel_active = true;
    boolean clock_active = false;
    boolean timer_active = false;

    HashMap<String, Boolean> next_button;
    ArrayList<String> myList;

    int current_state;
    Button current_button;
    String string_button;
    int number_of_steps;


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

        Button timer = findViewById(R.id.microwave_timer);
        timer.setOnClickListener(v -> {
            timer();
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

        next.setEnabled(false);

        Button popcorn = findViewById(R.id.microwave_popcorn);
        Button potato = findViewById(R.id.microwave_potato);
        Button pizza = findViewById(R.id.microwave_pizza);
        Button cook = findViewById(R.id.microwave_cook);
        Button reheat = findViewById(R.id.microwave_reheat);
        Button cooktime = findViewById(R.id.microwave_cooktime);
        Button cookpower = findViewById(R.id.microwave_cookpower);
        Button defrost = findViewById(R.id.microwave_defrost);
        Button add30 = findViewById(R.id.microwave_30sec);
        ArrayList<Button> allButtons = new ArrayList<Button>(Arrays.asList(clock, start, cancel, soften, m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m0,
                popcorn, potato, pizza, cook, reheat, cooktime, cookpower, defrost,
                timer, add30));
        for (int i = 0; i < allButtons.size(); i++) {
            allButtons.get(i).setAlpha(0);
        }

        HashMap<String, Button> button_string = new HashMap<String, Button>();
        HashMap<String, Boolean> button_boolean = new HashMap<String, Boolean>();
        myList = (ArrayList<String>) getIntent().getSerializableExtra("button");

        current_state = 0;
        string_button = myList.get(current_state);
        Log.i("Button",Arrays.toString(myList.toArray()));
        Log.i("Button", String.valueOf(myList.size()));
        number_of_steps = myList.size();


        //The next button that needs to be pushed before going to the next stage
        //Eventually need to add all the buttons on the screen
        next_button = new HashMap<String, Boolean>();
        next_button.put("clock", false);
        next_button.put("start", false);
        next_button.put("cancel", false);
        next_button.put("timer", false);

        button_string.put("clock", clock);
        button_boolean.put("clock",clock_active);
        Log.e("current",string_button);
        //Log.e("current", button_string.get(string_button));

        //Initial Step
        next_step(string_button);

    }

    private void next_step(String string_button){
        if(string_button.equals("clock")) {
            clock_active = true;
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if(string_button.equals("timer")) {
            timer_active = true;
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if(string_button.equals("start")) {
            start_active = true;
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if(string_button.equals("number pad")){
            numberpad_active = true;
            //next_button.put(string_button,true);
            current_state+=1;

            string_button = myList.get(current_state);
            if(string_button.equals("clock")){
                clock_active = true;
            }
            else{
                start_active = true;
            }
            next_button.put(string_button,true);

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

    private void finish_task(){

        TextView lcd = findViewById(R.id.lcd_text);
        lcd.clearAnimation();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lcdString = "";
        lcdString = "Ready!";
        lcd.setText(lcdString);
        lcd.clearAnimation();
        Button next = findViewById(R.id.Next);
        next.setEnabled(true);
    }

    private void timer(){
        Log.e("Button", "Timer Pressed");
        if(timer_active == true) {
            timerPressed = true;
            clearScreen();
            Log.e("Button Pressed (Active)", "Timer");
            Log.e("Next Button", String.valueOf(next_button.get("timer")));
            if(next_button.get("timer") == true){
                Log.e("Button","Timer deactivate");
                next_button.put("timer",false);
                timer_active = false;
                current_state++;
                if(current_state >= myList.size()){
                    TextView lcd = findViewById(R.id.lcd_text);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lcd.clearAnimation();
                    lcdString = "";
                    lcdString = "Ready!";
                    lcd.setText(lcdString);
                    Button next = findViewById(R.id.Next);
                    next.setEnabled(true);
                }
                else {
                    string_button = myList.get(current_state);
                    Log.e("Button 286", string_button);
                    next_step(string_button);
                }

            }
        }
        else{
            Log.e("Button Pressed (Inactive)", "Timer");
        }

    }

    private void microwaveClock() {
        Log.e("Button", String.valueOf(clock_active));
        if(clock_active == true) {
            clockPressed = true;
            clearClock();
            Log.e("Button Pressed (Active)", "Clock");
            Log.e("Next Button", String.valueOf(next_button.get("clock")));
            if(next_button.get("clock") == true){
                Log.e("Button","Clock deactivate");
                next_button.put("clock",false);
                clock_active = false;
                if(current_state >= myList.size()){
                    TextView lcd = findViewById(R.id.lcd_text);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lcdString = "";
                    lcdString = "Ready!";
                    lcd.setText(lcdString);
                    Button next = findViewById(R.id.Next);
                    next.setEnabled(true);
                }
                else {
                    string_button = myList.get(++current_state);
                    next_step(string_button);
                }

            }
        }
        else{
            Log.e("Button Pressed (Inactive)", "Clock");
        }
    }

    private void microwaveStart() {
        if(start_active == true) {
            if(next_button.get("start") == true) {
                Log.e("Button","Clock deactivate");
                next_button.put("start",false);
                start_active = false;
                current_state++;

                if(timerPressed == true){
                    TextView lcd = findViewById(R.id.lcd_text);


//                    for(int i = Integer.parseInt(tmpList.get(0)); i >= 0; i--){
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        Log.e("Button", String.valueOf(i));
//                        lcdString = "";
//                        lcdString = String.valueOf(i);
//                        Log.e("Button",lcdString);
//                        lcd.setText(lcdString);
//                    }

                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(50); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);

                    new CountDownTimer(7000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.e("Button",String.valueOf(millisUntilFinished));
                            Log.e("Button",String.valueOf(millisUntilFinished / 1000));
                            if((millisUntilFinished / 1000) < Integer.parseInt(tmpList.get(0))) {
                                lcd.setText(String.valueOf(millisUntilFinished / 1000));
                            }
                        }
                        @Override
                        public void onFinish() {
                            lcd.setText("End");
                            lcd.startAnimation(anim);
                        }
                    }.start();

//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    lcdString = "";
//                    lcdString = "End";
//                    lcd.setText(lcdString);

                }

                if(current_state >= myList.size()){
                    finish_task();
                }
                else{
                    string_button = myList.get(current_state);
                    next_step(string_button);
                }
            }


//            startPressed = true;
//            TextView lcd = findViewById(R.id.lcd_text);
//            if (clockPressed) {
//                setClock();
//            } else if (meltButterBool) {
//                lcdString = "";
//                lcdString = "Ready!";
//                lcd.setText(lcdString);
//            } else {
//                return;
//            }
            Log.e("Button Pressed (Active)", "Start");
        }
        else{
            Log.e("Button Pressed (Inactive)", "Start");
        }
    }

    private void microwaveSoften() {
        meltPressed = true;
        meltSelection();
        Log.e("Button Pressed", "Soften/Melt");
    }

    private void microwaveCancel() {
        if(cancel_active == true) {
            TextView lcd = findViewById(R.id.lcd_text);
            tmpList.clear();
            cancelPressed = true;
            lcdString = "";
            lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));
            Log.e("Button Pressed (Active)", "Cancel");
        }
        else{
            Log.e("Button Pressed (Inactive)", "Cancel");
        }
    }

    private void press0() {
        if(numberpad_active == true){
        zeroPressed = true;
        lcdString += 0;
        tmpList.add("0");
        update(lcdString);
        Log.e("Button Pressed (Active)", "0");
        }
        else {
            Log.e("Button Pressed (Inactive)", "0");
        }
    }

    private void press1() {
        if(numberpad_active == true) {
            onePressed = true;
            lcdString += "1";
            tmpList.add("1");
            update(lcdString);
            Log.e("Button Pressed (Active)", "1");
        }
        else {
            Log.e("Button Pressed (Inactive)", "1");
        }
    }

    private void press2() {
        if(numberpad_active == true) {
        twoPressed = true;
        lcdString += "2";
        tmpList.add("2");
        update(lcdString);
        Log.e("Button Pressed", "2");
        }
        else {
            Log.e("Button Pressed (Inactive)", "2");
        }
    }

    private void press3() {
        if(numberpad_active == true) {
        threePressed = true;
        lcdString += "3";
        tmpList.add("3");
        update(lcdString);
        Log.e("Button Pressed", "3");
        }
        else {
            Log.e("Button Pressed (Inactive)", "3");
        }
    }

    private void press4() {
        if(numberpad_active == true) {
        fourPressed = true;
        lcdString += "4";
        tmpList.add("4");
        update(lcdString);
        Log.e("Button Pressed", "4");
        }
        else {
            Log.e("Button Pressed (Inactive)", "4");
        }
    }

    private void press5() {
        if(numberpad_active == true) {
        fivePressed = true;
        lcdString += "5";
        tmpList.add("5");
        update(lcdString);
        Log.e("Button Pressed", "5");
        }
        else {
            Log.e("Button Pressed (Inactive)", "5");
        }
    }

    private void press6() {
        if(numberpad_active == true) {
        sixPressed = true;
        lcdString += "6";
        tmpList.add("6");
        update(lcdString);
        Log.e("Button Pressed", "6");
        }
        else {
            Log.e("Button Pressed (Inactive)", "6");
        }
    }

    private void press7() {
        if(numberpad_active == true) {
        sevenPressed = true;
        lcdString += "7";
        tmpList.add("7");
        update(lcdString);
        Log.e("Button Pressed", "7");
        }
        else {
            Log.e("Button Pressed (Inactive)", "7");
        }
    }

    private void press8() {
        if(numberpad_active == true) {
        eightPressed = true;
        lcdString += "8";
        tmpList.add("8");
        update(lcdString);
        Log.e("Button Pressed", "8");
        }
        else {
            Log.e("Button Pressed (Inactive)", "8");
        }
    }

    private void press9() {
        if(numberpad_active == true) {
        ninePressed = true;
        lcdString += "9";
        tmpList.add("9");
        update(lcdString);
        Log.e("Button Pressed", "9");
        }
        else {
            Log.e("Button Pressed (Inactive)", "9");
        }
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

    private void clearScreen(){
        TextView alt = findViewById(R.id.alt_txt);
        TextView lcd = findViewById(R.id.lcd_text);
        alt.setAlpha(1);
        lcdString = " ";
        altString = " ";
        alt.setText(altString);
        lcd.setText(lcdString);

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