package com.unity3d.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

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
    boolean isTemp = false;
    boolean isCode = false;
    boolean isCookTime = false;

    String current_task;

    boolean numberpad_active = false;
    boolean previous_numberpad = false;
    boolean six_active = false;
    boolean one_active = false;
    boolean settings_clock_working = false;
    boolean sound_working = false;
    boolean settings_temperature_working = false;
    boolean timerPressed = false;
    CountDownTimer t;
    Animation anim;
    ArrayList<String> tmpList = new ArrayList<String>();

    String[] time = {" ", " ", " ", " "};
    int time_position = 0;

    HashMap<String, Boolean> next_button;
    HashMap<String, Boolean> button_active;
    ArrayList<String> myList;
    int number_of_steps;
    int current_state;
    String string_button;
    String previous_state;
    String info_button = "nothing";

    boolean clock_active;
    boolean start_active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant6_oven);
        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> nextActivity());
        TextView lcd = findViewById(R.id.oven_panel_text);
        lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));

        /**
         * Oven panel 1
         */
        Button bakeBtn = findViewById(R.id.oven_bake);
        bakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Bake button");
                bake();
            }
        });

        Button frozenBakeBtn = findViewById(R.id.oven_frozen);
        frozenBakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frozenBake();
            }
        });

        Button broilBtn = findViewById(R.id.oven_broil);
        broilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Broil button");
                broil();
            }
        });

        Button convectBtn = findViewById(R.id.oven_convect);
        convectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Convect Modes button");
            }
        });

        Button keepWarmBtn = findViewById(R.id.oven_warm);
        keepWarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Keep warm button");
                keepWarm();
            }
        });

        Button selfCleanBtn = findViewById(R.id.oven_clean);
        selfCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Self Clean button");
            }
        });

        /**
         * Oven panel 2
         */
        Button cookTimeBtn = findViewById(R.id.oven_cooktime);
        cookTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookTime();
            }
        });

        Button delayStartBtn = findViewById(R.id.oven_delay);
        delayStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Delay start button");
            }
        });

        Button preheatBtn = findViewById(R.id.oven_preheat);
        preheatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "rapid preheat button");
            }
        });

        Button settingsBtn = findViewById(R.id.oven_settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings_clock();
                Log.e("Clicked", "Settings/Clock button");
            }
        });

        /**
         * Oven panel 3
         */

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

        Button timerBtn = findViewById(R.id.oven_timer);
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Timer set/off");
                timer();
            }
        });

        Button on_offBtn = findViewById(R.id.oven_onoff);
        on_offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "On/Off clicked");
            }
        });

        Button confirm = findViewById(R.id.oven_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Confirm Button clicked");
            }
        });

        ArrayList<Button> allButtons = new ArrayList<Button>(Arrays.asList(
                frozenBakeBtn, cookTimeBtn, o0, o1, o2, o3, o4, o5, o6, o7, o8, o9, startOvenbtn, cancelOvenBtn,
                bakeBtn, broilBtn, convectBtn, keepWarmBtn, selfCleanBtn, delayStartBtn, preheatBtn, settingsBtn,
                timerBtn, on_offBtn, confirm
                ));

        for (int i = 0; i < allButtons.size(); i++) {
            allButtons.get(i).setAlpha(0);
        }

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        //List gotten for buttons
        myList = (ArrayList<String>) getIntent().getSerializableExtra("button");
        Log.d("Debug",Arrays.toString(myList.toArray()));
        Log.d("Debug", String.valueOf(myList.size()));

        //How many buttons are there in the button array.
        number_of_steps = myList.size();

        //Button Hashmap for state
        next_button = new HashMap<String, Boolean>();
        button_active = new HashMap<String, Boolean>();
        next_button.put("settings/clock", false);
        button_active.put("settings/clock",false);
        next_button.put("six", false);
        button_active.put("six",false);
        next_button.put("one", false);
        button_active.put("one",false);
        next_button.put("two", false);
        button_active.put("two",false);
        next_button.put("three", false);
        button_active.put("three",false);
        next_button.put("four", false);
        button_active.put("four",false);
        next_button.put("five", false);
        button_active.put("five",false);
        next_button.put("nine", false);
        button_active.put("nine",false);
        next_button.put("seven", false);
        button_active.put("seven",false);
        next_button.put("eight", false);
        button_active.put("eight",false);
        next_button.put("zero", false);
        button_active.put("zero",false);
        next_button.put("start", false);
        button_active.put("start",false);
        next_button.put("bake", false);
        button_active.put("bake",false);
        next_button.put("cook time", false);
        button_active.put("cook time",false);
        next_button.put("broil", false);
        button_active.put("broil",false);
        next_button.put("keep warm", false);
        button_active.put("keep warm",false);
        next_button.put("timer", false);
        button_active.put("timer",false);


        //Current Button
        current_state = 0;
        string_button = myList.get(current_state);

        //Initial Step
        next_step(string_button);

    }

    private void next_step(String string_button) {

        //
        String arr1[] = string_button.split(",");
        if(arr1.length == 1){
            string_button = arr1[0];
        }else{
            string_button = arr1[0];
            info_button = arr1[1];
            System.out.println(info_button);
        }

        if (string_button.equals("settings/clock")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("six")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("timer")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("broil")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("keep warm")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("one")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("cook time")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("bake")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("two")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("three")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("four")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("five")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if (string_button.equals("no button")) {
//            next_button.put(string_button, true);
//            button_active.put(string_button, true);
//            Log.e("Debug", string_button);
            current_state++;
            string_button = myList.get(current_state);
            next_step(string_button);
        }

        if (string_button.equals("start")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            Log.e("Debug", string_button);
        }

        if(string_button.equals("number pad")){
//            TextView lcd = findViewById(R.id.oven_panel_text);
            lcdString = "";
//            lcd.setText(lcdString);
            numberpad_active = true;
            //next_button.put(string_button,true);
            previous_state = string_button;
            current_state+=1;
            previous_numberpad = true;
            string_button = myList.get(current_state);

            if(info_button.equals("clock")){

            }

            if(string_button.equals("clock")){
                clock_active = true;
            }
            else{
                start_active = true;
            }

            next_button.put(string_button,true);
            manage_next();

        }
    }

    private void settings_clock(){
        if(button_active.get("settings/clock") == true) {
            clearClock();
            Log.e("Button Pressed (Active)", "Clock");
            if(next_button.get("settings/clock") == true){
                Log.e("Button","Next Button Clock");
                TextView lcd = findViewById(R.id.oven_panel_text);
                next_button.put("settings/clock",false);
                button_active.put("settings/clock",false);
                lcdString = "Settings Clock";
                lcd.setText(lcdString);
                //clock_active = false;
                settings_clock_working = true;
                settings_temperature_working = true;
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        }
        else{
            Log.e("Button Pressed (Inactive)", "Clock");
        }
    }

    private void keepWarm(){
        if(button_active.get("keep warm") == true) {
            clearClock();
            Log.e("Button Pressed (Active)", "Keep Warm");
            if(next_button.get("keep warm") == true){
                Log.e("Button","Next Button Keep Warm");
                TextView lcd = findViewById(R.id.oven_panel_text);
                next_button.put("keep warm",false);
                button_active.put("keep warm",false);
                lcdString = "Keep warm";
                lcd.setText(lcdString);
                lcdString = "";
                //clock_active = false;
                //settings_clock_working = true;
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        }
        else{
            Log.e("Button Pressed (Inactive)", "Clock");
        }
    }

    private void broil(){
        if(button_active.get("broil") == true) {
            clearClock();
            Log.e("Button Pressed (Active)", "Broil");
            if(next_button.get("broil") == true){
                Log.e("Button","Next Button Broil");
                TextView lcd = findViewById(R.id.oven_panel_text);
                next_button.put("broil",false);
                button_active.put("broil",false);
                lcdString = "Broil";
                lcd.setText(lcdString);
                lcdString = "";
                //clock_active = false;
                //settings_clock_working = true;
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        }
        else{
            Log.e("Button Pressed (Inactive)", "Clock");
        }
    }

    private void bake(){
        if(button_active.get("bake") == true) {
            clearClock();
            Log.e("Button Pressed (Active)", "Bake");
            if(next_button.get("bake") == true){
                Log.e("Button","Next Button Bake");
                TextView lcd = findViewById(R.id.oven_panel_text);
                next_button.put("bake",false);
                button_active.put("bake",false);
                lcdString = "Bake";
                lcd.setText(lcdString);
                lcdString = "";
                //clock_active = false;
                //settings_clock_working = true;
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        }
        else{
            Log.e("Button Pressed (Inactive)", "Clock");
        }
    }

    private void numberpad_toggle(){
        numberpad_active = false;
        previous_numberpad = false;
    }

    private void manage_next(){
        TextView lcd = findViewById(R.id.oven_panel_text);
        System.out.println(current_state + " " + myList.size());
        //System.out.println(myList.get(current_state));
        if(current_state >= myList.size()){
            System.out.println("Inside");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lcd.clearAnimation();
            lcdString = "";
            lcdString = "Ready!";
            lcd.setText(lcdString);
            Button next = findViewById(R.id.next);
            next.setEnabled(true);
        }
        else {
            string_button = myList.get(current_state);
            next_step(string_button);
        }
    }

    private void clearClock() {
        //TextView alt = findViewById(R.id.alt_txt);
        TextView lcd = findViewById(R.id.oven_panel_text);
        //alt.setAlpha(1);
        lcdString = "";
        //tmpList.clear();
        if (true) {
            // blink animation and allow user to alter the time
            lcd.setText(lcdString);
        }
    }

    private void timer(){
        if (button_active.get("timer") == true) {
            current_task = "Timer";
            timerPressed = true;
            clearClock();
            //lcdString = "";
            Log.e("Button Pressed (Active)", "Timer");
            if (next_button.get("timer") == true) {
                Log.e("Button", "Timer deactivate");
                next_button.put("timer", false);
                button_active.put("timer",false);
//                timer_active = false;
//                timer_working = true;
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        } else {
            Log.e("Button Pressed (Inactive)", "Timer");
        }
    }

    private void update(String buttonValue) {
        Log.e("update", buttonValue);
        lcdString += buttonValue;
        TextView lcd = findViewById(R.id.oven_panel_text);
        Log.e("current size", String.valueOf(tmpList.size()));
        //lcd.setText(lcdString);

        Log.e("time", Arrays.toString(time));
        String first = " ";
        String second = " ";
        String third = " ";
        String fourth = " ";
        HashMap<Integer, String> time_map = new HashMap<Integer, String>();
        time_map.put(0, " ");
        time_map.put(1, " ");
        time_map.put(2, " ");
        time_map.put(3, " ");


        int temp_position = time_position;
        System.out.println(time_position);
        for (int i = 0; i < time_position; i++) {
            System.out.println(time[i]);
            time_map.put(3 - temp_position + 1, time[i]);
            temp_position--;
        }

        Log.e("time", time_map.get(0) + time_map.get(1) + ":" + time_map.get(2) + time_map.get(3));
        lcd.setText(time_map.get(0) + time_map.get(1) + ":" + time_map.get(2) + time_map.get(3));

    }


//    private void update(String buttonValue) {
//        Log.e("Button added", buttonValue);
//        TextView ovenLcd = findViewById(R.id.oven_panel_text);
//        lcdString += buttonValue;
//        ovenLcd.setText(lcdString);
//        if (!isCode) {
//            stringChecker(buttonValue);
//        } else if (!isTemp) {
//            Log.e("reached", "entering temperature");
//            ovenLcd.setText(lcdString + "°F");
//        } else if (!isCookTime) {
//            Log.e("reached", "entering time");
//            ovenLcd.setText(lcdString + "mins");
//        }
//    }

    private void stringChecker(String val) {
        Log.e("Checking string ...", val);
        Handler h = new Handler(getMainLooper());
        Handler h1 = new Handler(getMainLooper());
        TextView ovenLcd = findViewById(R.id.oven_panel_text);
        if (val.equals("3")) {
            h.postDelayed(() -> {
                isCode = true;
                lcdString = "";
                lcdString = "Nuggets/Fries";
                ovenLcd.setText(lcdString);
                h1.postDelayed(() -> {
                    lcdString = "";
                    lcdString = "Enter temperature:";
                    //isTemp = true;
                    ovenLcd.setText(lcdString);
                }, 2000);
            }, 2000);
        } else {
            isTemp = false;
            isCode = false;
            Log.e("reached else statement", "check");
        }
    }

    private void enterTemperature(String val) {
        //°F
        Log.e("Entering temperature", val);
        TextView ovenLcd = findViewById(R.id.oven_panel_text);
        lcdString += val;
        ovenLcd.setText(lcdString + "°F");
    }

    private void frozenBake() {
        TextView ovenLcd = findViewById(R.id.oven_panel_text);
        Handler h = new Handler(getMainLooper());
        lcdString = "Frozen Bake";
        ovenLcd.setTextSize(30);
        ovenLcd.setText(lcdString);
        h.postDelayed(() -> {
            lcdString = "";
            lcdString = "Enter food code:";
            ovenLcd.setText(lcdString);
        }, 2000);
        Log.e("Button pressed", "frozenBake");
    }

    private void cookTime() {
//        Log.e("Button pressed", "cookTime");
//        isTemp = false;
//        isCode = false;
//        isCookTime = true;
//        TextView lcd = findViewById(R.id.oven_panel_text);
//        lcdString = "";
//        lcdString = "Enter cook time:";
//        lcd.setText(lcdString);
        if(button_active.get("cook time") == true) {
            clearClock();
            Log.e("Button Pressed (Active)", "Cook time");
            if(next_button.get("cook time") == true){
                Log.e("Button","Next Button Cook time");
                TextView lcd = findViewById(R.id.oven_panel_text);
                next_button.put("cook time",false);
                button_active.put("cook time",false);
                lcdString = "Cook Time";
                lcd.setText(lcdString);
                lcdString = "";
                //clock_active = false;
                //settings_clock_working = true;
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        }
        else{
            Log.e("Button Pressed (Inactive)", "Cook Time");
        }
    }


    public void startTimer(final long finish, long tick) {
        TextView lcd = findViewById(R.id.oven_panel_text);
        t = new CountDownTimer(finish, tick) {


            public void onTick(long millisUntilFinished) {
                System.out.println(millisUntilFinished);
                System.out.println(finish);
                long remainedSecs = millisUntilFinished / 1000;
                long hh = (remainedSecs / 60);
                long mm = (remainedSecs % 60);
                String hour;
                String minute;
                if (hh / 10 == 0) {
                    hour = "0" + hh;
                } else {
                    hour = String.valueOf(hh);
                }
                if (mm / 10 == 0) {
                    minute = "0" + mm;
                } else {
                    minute = String.valueOf(mm);
                }
                if (finish - 101 > millisUntilFinished) {
                    lcd.setText("" + hour + ":" + minute);// manage it accordign to you
                }
            }

            public void onFinish() {
                lcd.setText("00:00");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lcd.setText("End");
                lcd.startAnimation(anim);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cancel();
            }
        }.start();
    }

    public void stopTimer() {
        t.cancel();
    }


    private void countdown(String number) {
        System.out.println(number);
        System.out.println("%" + number.substring(1) + "%");
        int time = Integer.parseInt(number);
        int seconds = time / 1000 * 60 * 60 + time / 100 * 60 + time % 100;
        startTimer(seconds * 1000 + 1100, 1000);
    }

    private void startOven() {
//        Log.e("Button pressed", "startOven");
//        TextView lcd = findViewById(R.id.oven_panel_text);
//        lcdString = "";
//        lcdString = "Cooking start";
//        lcd.setText(lcdString);
//
        if(button_active.get("start") == true) {
            Log.e("Button", "Start (Active)");
            if (next_button.get("start") == true) {
                Log.e("Button", "Start deactivate");
                next_button.put("start", false);
            }

            current_state++;
            if (timerPressed == true) {
                System.out.println("lcdString" + lcdString);
                countdown(lcdString);
            }

            System.out.println(current_state + ":" + myList.size());

            if(current_state >= myList.size()){
                finish_task();
            }
            else{
                string_button = myList.get(current_state);
                next_step(string_button);
            }

        }else{
            Log.e("Button", "Start (Inactive)");
        }
    }


    private void finish_task(){

        TextView lcd = findViewById(R.id.oven_panel_text);
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
        Button next = findViewById(R.id.next);
        next.setEnabled(true);
    }


    private void cancelOven() {
        TextView lcd = findViewById(R.id.oven_panel_text);

        if(current_task.equals("Timer")){
            stopTimer();
        }

        lcd.clearAnimation();
        Button next = findViewById(R.id.oven_cancel);
        next.setEnabled(false);
        //lcdString = DateTimeHandler.getCurrentTime("hh:mm");
        lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));
        time = new String[]{" ", " ", " ", " "};
        time_position = 0;
        current_state = 0;
        string_button = myList.get(current_state);
        next_step(string_button);

//        isTemp = false;
//        isCode = false;
//        isCookTime = false;
//        lcdString = "";
//        lcdString = "Cancelling ...";
//        lcd.setText(lcdString);
//        Log.e("Button pressed", "cancelOven");
//        current_state++;
//        manage_next();

    }

    private void press(String number){
        if (time_position < 4) {
            time[time_position] = number;
            time_position++;
        }
    }

    private void press0() {
        if(numberpad_active == true | button_active.get("zero") == true) {
            Log.e("Button Pressed (Active)", "0");
            if(button_active.get("zero") ==  true){
                button_active.put("zero",false);
                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press("0");
                update("0");
            }
        }
        else {
            Log.e("Button Pressed (Inactive)", "0");
        }}


    private void press1() {
        if(numberpad_active == true | button_active.get("one") == true) {
            TextView lcd = findViewById(R.id.oven_panel_text);
            Log.e("Button Pressed (Active)", "1");
            if(button_active.get("one") ==  true){
                System.out.println("button_active one is true");
                button_active.put("one",false);

                if(info_button.equals("sound")){
                    lcd.setText("Sound on");
                }

                if(info_button.equals("temperature")){
                    lcd.setText("Temperature on");
                }


                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press("1");
                update("1");
            }
        }
        else {
            Log.e("Button Pressed (Inactive)", "1");
        }}

    private void press2() {
        if(numberpad_active == true | button_active.get("two") == true) {
            TextView lcd = findViewById(R.id.oven_panel_text);
            Log.e("Button Pressed (Active)", "2");
            if(button_active.get("two") ==  true){
                button_active.put("two",false);

                if(info_button.equals("sound")){
                    lcd.setText("Sound off");
                }

                if(info_button.equals("temperature")){
                    lcd.setText("Temperature off");
                }

                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press("2");
                update("2");
            }
        }
        else {
            Log.e("Button Pressed (Inactive)", "2");
        }}

    private void press3() {
        if(numberpad_active == true | button_active.get("three") == true) {
            TextView lcd = findViewById(R.id.oven_panel_text);
            Log.e("Button Pressed (Active)", "3");
            if(button_active.get("three") ==  true){
                button_active.put("three",false);

                if(info_button.equals("clock")){
                    System.out.println("AM");
                    lcd.setText("AM");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press("3");
                update("3");
            }
        }
        else {
            Log.e("Button Pressed (Inactive)", "3");
        }}

    private void press4() {
        if(numberpad_active == true | button_active.get("four") == true) {
            TextView lcd = findViewById(R.id.oven_panel_text);
            Log.e("Button Pressed (Active)", "4");
            if(button_active.get("four") ==  true){
                button_active.put("four",false);

                if(info_button.equals("clock")){
                    lcd.setText("Clock Settings");
                }

                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press("4");
                update("4");
            }
        }
        else {
            Log.e("Button Pressed (Inactive)", "4");
        }}

    private void press5() {
        if(numberpad_active == true | button_active.get("five") == true) {
            Log.e("Button Pressed (Active)", "5");
            TextView lcd = findViewById(R.id.oven_panel_text);
            if(button_active.get("five") ==  true){
                button_active.put("five",false);
                numberpad_toggle();
                current_state++;
                manage_next();

                if(settings_temperature_working){
                    sound_working = true;
                    lcd.setText("Temperature Settings");
                }

            }
            else{
                press("5");
                update("5");
            }
        }
        else {
            Log.e("Button Pressed (Inactive)", "5");
        }}

    private void press6() {
        if(numberpad_active == true | button_active.get("six") == true) {
            TextView lcd = findViewById(R.id.oven_panel_text);
            Log.e("Button Pressed (Active)", "6");
            if(button_active.get("six") ==  true){
                button_active.put("six",false);

                if(settings_clock_working){
                    sound_working = true;
                    lcd.setText("Sound Settings");
                }

                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press("6");
                update("6");
            }
        }
        else {
        Log.e("Button Pressed (Inactive)", "6");
    }}


    private void press7() {
        if(numberpad_active == true | button_active.get("seven") == true) {
            Log.e("Button Pressed (Active)", "7");
            if(button_active.get("seven") ==  true){
                button_active.put("seven",false);
                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press("7");
                update("7");
            }
        }
        else {
            Log.e("Button Pressed (Inactive)", "7");
        }}

    private void press8() {
        if(numberpad_active == true | button_active.get("eight") == true) {
            Log.e("Button Pressed (Active)", "8");
            if(button_active.get("eight") ==  true){
                button_active.put("eight",false);
                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press("8");
                update("8");
            }
        }
        else {
            Log.e("Button Pressed (Inactive)", "8");
        }}

    private void press9() {
        if(numberpad_active == true | button_active.get("nine") == true) {
            Log.e("Button Pressed (Active)", "9");
            if(button_active.get("nine") ==  true){
                button_active.put("nine",false);
                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press("9");
                update("9");
            }
        }
        else {
            Log.e("Button Pressed (Inactive)", "9");
        }}

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