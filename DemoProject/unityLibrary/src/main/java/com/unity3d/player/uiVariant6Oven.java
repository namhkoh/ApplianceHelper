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
import java.util.List;

public class uiVariant6Oven extends AppCompatActivity {

    private String lcdString = " ";
    private String current_task = "";
    private String button_lowercase;
    private String temp_task = "";
    private String string_button;
    private String previous_state;
    private String info_button = "nothing";
    private String[] time = {" ", " ", " ", " "};
    private String arr1[];

    boolean isTemp = false;
    boolean isCode = false;
    boolean numberpad_active = false;
    boolean previous_numberpad = false;
    boolean settings_clock_working = false;
    boolean sound_working = false;
    boolean settings_temperature_working = false;
    boolean timerPressed = false;
    boolean clock_active;
    boolean start_active;

    private int time_position = 0;
    private int number_of_steps;
    private int current_state;
    private int pressed_wrong;

    private ArrayList<String> tmpList = new ArrayList<String>();
    private ArrayList<String> myList;
    private ArrayList<Button> allButtons;
    private List<String> list;

    private HashMap<String, Boolean> next_button;
    private HashMap<String, Boolean> button_active;
    private Button hint;

    private CountDownTimer t;
    private Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant6_oven);

        TextView lcd = findViewById(R.id.oven_panel_text);
        lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));

        /**
         * Oven panel 1
         */
        Button bakeBtn = findViewById(R.id.oven_bake);
        bakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                broil();
            }
        });

        Button convectBtn = findViewById(R.id.oven_convect);
        convectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convect_modes();
            }
        });

        Button keepWarmBtn = findViewById(R.id.oven_warm);
        keepWarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keepWarm();
            }
        });

        Button selfCleanBtn = findViewById(R.id.oven_clean);
        selfCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oven_clean();
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
                delay_start();
            }
        });

        Button preheatBtn = findViewById(R.id.oven_preheat);
        preheatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rapid_predheat();
            }
        });

        Button settingsBtn = findViewById(R.id.oven_settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings_clock();
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

        hint = findViewById(R.id.hint);
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint();
            }
        });

        Button open = findViewById(R.id.open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });

        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> nextActivity());

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
                on_off();
            }
        });

        Button confirm = findViewById(R.id.oven_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });

        //Setting alpha value
        allButtons = new ArrayList<Button>(Arrays.asList(
                frozenBakeBtn, cookTimeBtn, o0, o1, o2, o3, o4, o5, o6, o7, o8, o9, startOvenbtn, cancelOvenBtn,
                bakeBtn, broilBtn, convectBtn, keepWarmBtn, selfCleanBtn, delayStartBtn, preheatBtn, settingsBtn,
                timerBtn, on_offBtn, confirm
                ));

        setAlphaValue(0, allButtons);

        //Blinking Animation
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        //List gotten for buttons
        myList = (ArrayList<String>) getIntent().getSerializableExtra("button");

        //How many buttons are there in the button array.
        number_of_steps = myList.size();
        pressed_wrong = 0;

        //Button Hashmap for state
        list = Arrays.asList("settings/clock", "six", "one", "two","three","four","five","nine","seven","eight","zero","start","bake",
                "cook time","broil","keep warm", "timer","frozen bake","convect modes","oven clean","delay start", "rapid preheat",
                "on off", "confirm");
        next_button = new HashMap<String, Boolean>();
        button_active = new HashMap<String, Boolean>();
        for (String i : list) {
            next_button.put(i, false);
            button_active.put(i, false);
        }

        //Current Button
        current_state = 0;
        string_button = myList.get(current_state);

        //Initial Step
        next_step(string_button);

    }

    private void next_step(String string_button) {

        //info_button is for extra information for the settings/clock fields
        arr1 = string_button.split(",");
        if(arr1.length == 1){
            string_button = arr1[0];
        }else{
            string_button = arr1[0];
            info_button = arr1[1];
        }

        if (string_button.equals("no button")) {
            current_state++;
            string_button = myList.get(current_state);
            next_step(string_button);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if(string_button.equals("number pad")){
            lcdString = "";
            numberpad_active = true;
            previous_state = string_button;
            current_state+=1;
            previous_numberpad = true;
            string_button = myList.get(current_state);
            debug_next_step_log(string_button);
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

//        else{
//            next_step_helper(string_button);
//        }

        if (string_button.equals("settings/clock")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("six")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("timer")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("broil")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("keep warm")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("one")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("cook time")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("bake")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("two")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("three")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("four")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("five")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }

        if (string_button.equals("start")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }
    }

    private void next_step_helper(String button){
        next_button.put(string_button, true);
        button_active.put(string_button, true);
        debug_next_step_log(string_button);
    }

    private void debug_next_step_log(String string_button){
        Log.d("Debug (Next Step)", string_button);
    }

    private void active_inactive_log(boolean active, String msg){
        if(active == true){
            Log.i("Button Pressed (Active)", msg);
            pressed_wrong = 0;
            hint.setEnabled(false);
        } else{
            Log.i("Button Pressed (Inactive)", msg);
            pressed_wrong++;
        }
        if(pressed_wrong > 5){
            hint.setEnabled(true);
        }
    }

    private void core_button_support(String button, String lcd_text){
        button_lowercase = button.toLowerCase();
        if(button_active.get(button_lowercase) == true) {
            clearClock();
            active_inactive_log(true, button);
            current_task = button_lowercase;
            if(next_button.get(button_lowercase) == true){
                TextView lcd = findViewById(R.id.oven_panel_text);
                next_button.put(button_lowercase,false);
                button_active.put(button_lowercase,false);
                if(lcd_text != null) {
                    lcdString = lcd_text;
                    lcd.setText(lcdString);
                    lcdString = "";
                }
                if(button_lowercase.equals("settings/clock")){
                    settings_clock_working = true;
                    settings_temperature_working = true;
                }
                if(button_lowercase.equals("timer")){
                    timerPressed = true;
                }
                if(button_lowercase.equals("bake")){
                    temp_task = button_lowercase;
                    previous_state = button_lowercase;
                }
                if(button_lowercase.equals("cook time")){
                    previous_state = "cooktime";
                    time = new String[]{" ", " ", " ", " "};
                    time_position=0;
                }
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        }
        else{
            active_inactive_log(false, button);
        }

    }

    private void timer(){
        core_button_support("Timer", null);
    }

    private void settings_clock(){
        core_button_support("settings/clock", "Settings Clock");
    }

    private void keepWarm(){
        core_button_support("Keep Warm", "Keep warm");
    }

    private void broil(){
        core_button_support("Broil", "Broil");
    }

    private void bake(){
        core_button_support("Bake", "Bake");
    }

    private void convect_modes() { core_button_support("Convect Modes", null); }

    private void oven_clean(){core_button_support("Oven Clean", null); };

    private void frozenBake() {
        core_button_support("Frozen Bake",null);
//        TextView ovenLcd = findViewById(R.id.oven_panel_text);
//        Handler h = new Handler(getMainLooper());
//        lcdString = "Frozen Bake";
//        ovenLcd.setTextSize(30);
//        ovenLcd.setText(lcdString);
//        h.postDelayed(() -> {
//            lcdString = "";
//            lcdString = "Enter food code:";
//            ovenLcd.setText(lcdString);
//        }, 2000);
//        Log.e("Button pressed", "frozenBake");
    }

    private void delay_start(){core_button_support("Delay Start", null);};

    private void rapid_predheat(){core_button_support("Rapid Preheat", null);};

    private void on_off(){core_button_support("On Off", null);};

    private void confirm(){core_button_support("Confirm", null);};

    private void cookTime() {
        core_button_support("Cook Time", "Cook Time");
    }

    private void hint(){
        active_inactive_log(false,"Hint");
    }

    private void open(){
        active_inactive_log(false,"Open");
    }

    private void numberpad_toggle(){
        numberpad_active = false;
        previous_numberpad = false;
    }

    private void manage_next(){
        TextView lcd = findViewById(R.id.oven_panel_text);
        if(current_state >= myList.size()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lcd.clearAnimation();
            lcdString = "";
            lcdString = "Well Done!";
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lcdString = "";
            lcdString = "Press Next!";
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
        TextView lcd = findViewById(R.id.oven_panel_text);
        lcdString = "";
        if (true) {
            // blink animation and allow user to alter the time
            lcd.setText(lcdString);
        }
    }

    private void clearScreen() {
        TextView lcd = findViewById(R.id.oven_panel_text);
        lcdString = "  :  ";
        if (true) {
            // blink animation and allow user to alter the time
            lcd.setText(lcdString);
        }
    }

    private void update(String buttonValue) {
        lcdString += buttonValue;
        TextView lcd = findViewById(R.id.oven_panel_text);

        Log.d("Debug update", buttonValue);
        Log.d("Debug current size", String.valueOf(tmpList.size()));
        Log.d("Debug time", Arrays.toString(time));

        HashMap<Integer, String> time_map = new HashMap<Integer, String>();
        time_map.put(0, " ");
        time_map.put(1, " ");
        time_map.put(2, " ");
        time_map.put(3, " ");

        if(temp_task.equals("bake")){
            lcd.setText(lcdString);
        } else {
            int temp_position = time_position;
            for (int i = 0; i < time_position; i++) {
                time_map.put(3 - temp_position + 1, time[i]);
                temp_position--;
            }

            Log.d("Debug time", time_map.get(0) + time_map.get(1) + ":" + time_map.get(2) + time_map.get(3));
            lcd.setText(time_map.get(0) + time_map.get(1) + ":" + time_map.get(2) + time_map.get(3));
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
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t.cancel();
            }
        }.start();
    }

    public void stopTimer() {
        t.cancel();
    }


    private void countdown(String number) {
        Log.d("Debug Countdown",number);
        int time = Integer.parseInt(number);
        int seconds = time / 1000 * 60 * 60 + time / 100 * 60 + time % 100;
        startTimer(seconds * 1000 + 1100, 1000);
    }

    private void startOven() {
        if(button_active.get("start") == true) {
            Log.e("Button", "Start (Active)");
            if (next_button.get("start") == true) {
                Log.e("Button", "Start deactivate");
                next_button.put("start", false);
            }

            current_state++;

            if (timerPressed == true) {
                countdown(lcdString);
            }

            if(current_task.equals("keep warm")){
                countdown("10");
            }
            if(temp_task.equals("bake")){
                countdown("10");
            }

            if(current_task.equals("broil") | current_task.equals("broiling")){
                countdown("10");
            }

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

        System.out.println(current_state+" "+myList.size());

        System.out.println(string_button);

        if(current_state + 1>= myList.size() & string_button.equals("cancel")){
            System.out.println("946");
            stopTimer();
            finish_task();
        }
        else {
            TextView lcd = findViewById(R.id.oven_panel_text);

            if (current_task.equals("timer")) {
                stopTimer();
            }

            if (current_task.equals("broil") | current_task.equals("broiling")) {
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
    }

    private void press(String number){
        if (time_position < 4) {
            time[time_position] = number;
            time_position++;
        }
    }

    private void press_support(String number, String word){
        if(numberpad_active == true | button_active.get(word) == true) {
            active_inactive_log(true,number);
            if(button_active.get(word) ==  true){
                button_active.put(word,false);

                TextView lcd = findViewById(R.id.oven_panel_text);
                if(number.equals("1")){
                    if(current_task.equals("broil") | current_task.equals("broiling")){
                        lcd.setText("High");
                    }

                    if(info_button.equals("sound")){
                        lcd.setText("Sound on");
                    }

                    if(info_button.equals("temperature")){
                        lcd.setText("Temperature on");
                    }
                }
                if(number.equals("2")){
                    if(info_button.equals("sound")){
                        lcd.setText("Sound off");
                    }

                    if(info_button.equals("temperature")){
                        lcd.setText("Temperature off");
                    }
                }
                if(number.equals("3")){
                    if(info_button.equals("clock")){
                        System.out.println("AM");
                        lcd.setText("AM");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(number.equals("4")){
                    if(info_button.equals("clock")){
                        lcd.setText("Clock Settings");
                    }
                }

                if(number.equals("5")){
                    if(settings_temperature_working){
                        sound_working = true;
                        lcd.setText("Temperature Settings");
                    }
                }

                if(number.equals("6")){
                    if(settings_clock_working){
                        sound_working = true;
                        lcd.setText("Sound Settings");
                    }
                }

                numberpad_toggle();
                current_state++;
                manage_next();
            }
            else{
                press(number);
                update(number);
            }
        }
        else {
            active_inactive_log(false,number);
        }
    }

    private void press0() {
        press_support("0","zero");
    }

    private void press1() {
        press_support("1","one");
    }

    private void press2() {
        press_support("2","two");
    }

    private void press3() {
        press_support("3","three");
       }

    private void press4() {
        press_support("4","four");
    }

    private void press5() {
        press_support("5","five");
    }

    private void press6() {
        press_support("6","six");
    }

    private void press7() {
            press_support("7","seven");
        }

    private void press8() {
        press_support("8","eight");
    }

    private void press9() {
        press_support("9","nine");
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

    private void setAlphaValue(int alpha, ArrayList<Button> allButtons) {
        for (int i = 0; i < allButtons.size(); i++) {
            allButtons.get(i).setAlpha(alpha);
        }
    }


///////////////////////////////


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

}