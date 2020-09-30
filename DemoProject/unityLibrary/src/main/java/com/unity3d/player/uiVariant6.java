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
import java.util.List;


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
    boolean reheat_pressed = false;

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
    boolean reheat_active = false;
    boolean defrost_active = false;
    boolean pizza_active = false;
    boolean melt_active = false;
    boolean popcorn_active = false;

    boolean clock_working = false;
    boolean timer_working = false;
    boolean food_working = false;
    boolean defrost_working = false;
    boolean reheat_working = false;
    boolean pizza_pressed = false;
    boolean popcorn_pressed = false;

    boolean previous_numberpad = false;
    CountDownTimer t;

    HashMap<String, Boolean> next_button;
    HashMap<String, Boolean> active_button;
    ArrayList<String> myList;
    Animation anim;
    ArrayList<String> tmpList;

    int current_state;
    Button current_button;
    String string_button;
    String previous_state;
    String current_task;
    int number_of_steps;
    int reheat_category = 0;
    //String[] time = new String[4];
    String[] time = {" ", " ", " ", " "};
    int time_position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant6);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize the time on the screen with the current time.
        TextView lcd = findViewById(R.id.lcd_text);
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

        Button reheat = findViewById(R.id.microwave_reheat);
        reheat.setOnClickListener(v -> {
            reheat();
        });

        Button defrost = findViewById(R.id.microwave_defrost);
        defrost.setOnClickListener(v -> {
            defrost();
        });

        Button pizza = findViewById(R.id.microwave_pizza);
        pizza.setOnClickListener(v -> {
            pizza();
        });

        Button popcorn = findViewById(R.id.microwave_popcorn);
        popcorn.setOnClickListener(v -> {
            popcorn();
        });

        Button open = findViewById(R.id.open);
        open.setOnClickListener(v -> {
            open_microwave();
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

        //Set next into disabled
        next.setEnabled(false);

        //Currently don't have an onclick listener
        Button potato = findViewById(R.id.microwave_potato);
        Button cook = findViewById(R.id.microwave_cook);
        Button cooktime = findViewById(R.id.microwave_cooktime);
        Button cookpower = findViewById(R.id.microwave_cookpower);
        Button add30 = findViewById(R.id.microwave_30sec);

        ArrayList<Button> allButtons = new ArrayList<Button>(Arrays.asList(clock, start, cancel, soften, m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m0,
                popcorn, potato, pizza, cook, reheat, cooktime, cookpower, defrost,
                timer, add30));

        //What is this set alpha for
        for (int i = 0; i < allButtons.size(); i++) {
            allButtons.get(i).setAlpha(0);
        }

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        //Get data from uiVaraiant 1,2,3,4
        myList = (ArrayList<String>) getIntent().getSerializableExtra("button");

        tmpList = new ArrayList<String>();

        //Initialize an hashmap that is used to track the button that used to be pressed next.
//        next_button = new HashMap<String, Boolean>();
//        next_button.put("clock", false);
//        next_button.put("start", false);
//        next_button.put("cancel", false);
//        next_button.put("timer", false);
//        next_button.put("reheat", false);
//        next_button.put("defrost", false);
//        next_button.put("pizza", false);
//        next_button.put("pork", false);
//        active_button = new HashMap<String, Boolean>();
//        active_button.put("clock", false);
//        active_button.put("start", false);
//        active_button.put("cancel", false);
//        active_button.put("timer", false);
//        active_button.put("reheat", false);
//        active_button.put("defrost", false);
//        active_button.put("pizza", false);
//        active_button.put("pork", false);

        //Initialize hashmap
        List<String> list = Arrays.asList("clock", "start", "cancel", "timer", "reheat", "defrost", "pizza", "pork", "no button", "number pad","open");
        next_button = new HashMap<String, Boolean>();
        active_button = new HashMap<String, Boolean>();
        for (String i : list) {
            next_button.put(i, false);
            active_button.put(i, false);
        }

        //Initial Step
        current_state = 0;
        string_button = myList.get(current_state);
        number_of_steps = myList.size();
        next_step(string_button);

    }

    private void next_step(String string_button) {
        if (string_button.equals("clock")) {
            clock_active = true;
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("reheat")) {
            reheat_active = true;
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("defrost")) {
            defrost_active = true;
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("pizza")) {
            pizza_active = true;
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("popcorn")) {
            popcorn_active = true;
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("timer")) {
            timer_active = true;
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("start")) {
            start_active = true;
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("no button")) {
            start_active = true;
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
            finish_task();
        }

        if (string_button.equals("open")) {
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("number pad")) {
            //Because number pad doesn't have a specific button to press in order to go to the next step.
            numberpad_active = true;
            //next_button.put(string_button,true);
            active_button.put(string_button, true);
            previous_state = string_button;
            previous_numberpad = true;

            //Go to the next element in the list. This button would be used to identify the movement in state.
            current_state += 1;
            string_button = myList.get(current_state);
            if (string_button.equals("clock")) {
                clock_active = true;
                active_button.put(string_button, true);
            } else {
                start_active = true;
                active_button.put(string_button, true);
            }
            //Activate the next button without deactivating the current button.
            next_button.put(string_button, true);
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

    //Simulates Opening the microwave.
    private void open_microwave() {
        if(active_button.get("open")) {
            TextView lcd = findViewById(R.id.lcd_text);
            lcd.clearAnimation();
            lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            numberpad_toggle();
            current_state++;
            manage_next();
        }
    }

    private void press(String number) {
        if (food_working == true) {
            lcdString = number;
            tmpList = new ArrayList<>();
            tmpList.add(number);
            update_number(number);
        } else {
            if(lcdString.length() < 4) {
                lcdString += number;
                tmpList.add(number);
            }
            if (time_position < 4) {
                time[time_position] = number;
                time_position++;
            }
            if (clock_working | timer_working) {
                update(lcdString);
            }
            if (food_working | defrost_working | reheat_working) {
                update_number(lcdString);
            }
            if (current_task == "Reheat") {
                reheat_active = false;
            }
        }
    }

    private void press0() {
        if (active_button.get("number pad")) {
            zeroPressed = true;
            press("0");
            Log.e("Button Pressed (Active)", "0");
        } else {
            Log.e("Button Pressed (Inactive)", "0");
        }
    }

    private void press1() {
        if (active_button.get("number pad")) {
            onePressed = true;
            press("1");
            Log.e("Button Pressed (Active)", "1");
        } else {
            Log.e("Button Pressed (Inactive)", "1");
        }
    }

    private void press2() {
        if (active_button.get("number pad")) {
            twoPressed = true;
            press("2");
            Log.e("Button Pressed (Active)", "2");
        } else {
            Log.e("Button Pressed (Inactive)", "2");
        }
    }

    private void press3() {
        if (active_button.get("number pad")) {
            threePressed = true;
            press("3");
            Log.e("Button Pressed (Active)", "3");
        } else {
            Log.e("Button Pressed (Inactive)", "3");
        }
    }

    private void press4() {
        if (active_button.get("number pad")) {
            fourPressed = true;
            press("4");
            Log.e("Button Pressed (Active)", "4");
        } else {
            Log.e("Button Pressed (Inactive)", "4");
        }
    }

    private void press5() {
        if (active_button.get("number pad")) {
            fivePressed = true;
            press("5");
            Log.e("Button Pressed (Active)", "5");
        } else {
            Log.e("Button Pressed (Inactive)", "5");
        }
    }

    private void press6() {
        if (active_button.get("number pad")) {
            sixPressed = true;
            press("6");
            Log.e("Button Pressed (Active)", "6");
        } else {
            Log.e("Button Pressed (Inactive)", "6");
        }
    }

    private void press7() {
        if (active_button.get("number pad")) {
            sevenPressed = true;
            press("7");
            Log.e("Button Pressed (Active)", "7");
        } else {
            Log.e("Button Pressed (Inactive)", "7");
        }
    }

    private void press8() {
        if (active_button.get("number pad")) {
            eightPressed = true;
            press("8");
            Log.e("Button Pressed (Active)", "8");
        } else {
            Log.e("Button Pressed (Inactive)", "8");
        }
    }

    private void press9() {
        if (active_button.get("number pad")) {
            ninePressed = true;
            press("9");
            Log.e("Button Pressed (Active)", "9");
        } else {
            Log.e("Button Pressed (Inactive)", "9");
        }
    }

    private void manage_next() {
        TextView lcd = findViewById(R.id.lcd_text);
        if (current_state >= myList.size()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lcdString = "";
            lcd.clearAnimation();
            lcdString = "Ready!";
            lcd.setText(lcdString);
            Button next = findViewById(R.id.Next);
            next.setEnabled(true);
        } else {
            string_button = myList.get(current_state);
            next_step(string_button);
        }
    }

    private void finish_task() {
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

    private void microwaveClock() {
        if (active_button.get("clock") == true) {
            //Is this really needed?
            clockPressed = true;
            clearClock();
            Log.e("Button Pressed (Active)", "Clock");
            if (next_button.get("clock") == true) {
                TextView lcd = findViewById(R.id.lcd_text);
                next_button.put("clock", false);
                lcdString = "  :  ";
                lcd.setText(lcdString);
                //What was this for again?
                clock_active = false;
                clock_working = true;
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        } else {
            Log.e("Button Pressed (Inactive)", "Clock");
        }
    }

    private void timer() {
        if (active_button.get("timer") == true) {
            current_task = "Timer";
            timerPressed = true;
            clearScreen();
            //lcdString = "";
            Log.e("Button Pressed (Active)", "Timer");
            if (next_button.get("timer") == true) {
                Log.e("Button", "Timer deactivate");
                next_button.put("timer", false);
                active_button.put("timer",false);
                timer_active = false;
                timer_working = true;
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        } else {
            Log.e("Button Pressed (Inactive)", "Timer");
        }
    }


    public void pizza() {
        if (active_button.get("pizza")) {
            pizza_pressed = true;
            food_working = true;
            clearScreen();

            Log.e("Button Pressed (Active)", "Pizza");
            if (next_button.get("pizza") == true) {
                TextView lcd = findViewById(R.id.lcd_text);
                lcd.setText("0.0 QTY");
                current_task = "Pizza";
                next_button.put("defrost", false);
                Log.e("Button", "Pizza deactivate");
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        } else {
            Log.e("Button Pressed (Deactivate)", "Pizza");
        }
    }

    ;

    public void popcorn() {
        if (active_button.get("popcorn")) {
            popcorn_pressed = true;
            food_working = true;
            clearScreen();
            Log.e("Button Pressed (Active)", "Popcorn");
            if (next_button.get("popcorn") == true) {
                TextView lcd = findViewById(R.id.lcd_text);
                lcd.setText("0.0 oz");
                current_task = "Popcorn";
                next_button.put("popcorn", false);
                Log.e("Button", "Popcorn deactivate");
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        } else {
            Log.e("Button Pressed (Deactivate)", "Popcorn");
        }
    }

    ;

    private void defrost() {
        if (active_button.get("defrost")) {
            clearScreen();
            defrost_working = true;
            Log.e("Button Pressed (Active)", "Defrost");
            Log.e("Next Button", String.valueOf(next_button.get("Defrost")));
            if (next_button.get("defrost") == true) {
                current_task = "Defrost";
                lcdString = "";
                next_button.put("defrost", false);
                defrost_active = false;
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        } else {
            Log.e("Button Pressed (Inactive)", "Defrost");
        }
    }

    private void reheat() {
        if (active_button.get("reheat")) {
            reheatScreen();
            Log.e("Button Pressed (Active)", "Reheat");
            if (next_button.get("reheat") == true) {
                reheat_working = true;
                // Not for reheat
                // next_button.put("reheat",false);
                // reheat_active = false;
                current_task = "Reheat";
                lcdString = "";
                if (reheat_pressed == false) {
                    current_state++;
                }
                reheat_pressed = true;
                manage_next();
            }
        } else {
            Log.e("Button Pressed (Inactive)", "Reheat");
        }
    }

    private void numberpad_toggle() {
        numberpad_active = false;
        previous_numberpad = false;
    }

    private void microwaveStart() {
        if (active_button.get("start")) {
            if (next_button.get("start") == true) {
                Log.e("Button", "Clock deactivate");
                next_button.put("start", false);
                active_button.put("start",false);
                start_active = false;
                numberpad_toggle();
                System.out.println(previous_state);
                if (current_task == "Reheat") {
                    countdown(lcdString);
                }
                if ((current_task == "Pizza") | (current_task == "Popcorn") | (current_task == "Defrost")) {
                    countdown("10");
                }
                current_state++;
                if (timerPressed == true) {
                    countdown(lcdString);
                }

                if (current_state >= myList.size()) {
                    finish_task();
                } else {
                    string_button = myList.get(current_state);
                    next_step(string_button);
                }
            }
            Log.e("Button Pressed (Active)", "Start");
        } else {
            Log.e("Button Pressed (Inactive)", "Start");
        }
    }

    private void microwaveSoften() {
        meltPressed = true;
        Log.e("Button Pressed", "Soften/Melt");
        if (melt_active = true) {
            Log.e("Button Pressed (Active)", "Cancel");
            melt_active = false;
        } else {
            Log.e("Button Pressed (Inactive)", "Cancel");
        }
    }

    public void cancel(String string_button) {
        if (current_task == "Reheat" | current_task == "Timer") {
            stopTimer();
            return;
        }
        if (string_button.equals("clock")) {
            clock_active = false;
        }
        if (numberpad_active = true) {
            numberpad_active = false;
        }

    }

    private void microwaveCancel() {
        cancel(string_button);
        //refreshTime();
        TextView lcd = findViewById(R.id.lcd_text);
        lcd.clearAnimation();
        Button next = findViewById(R.id.Next);
        next.setEnabled(false);
        //lcdString = DateTimeHandler.getCurrentTime("hh:mm");
        lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));
        time = new String[]{" ", " ", " ", " "};
        time_position = 0;
        current_state = 0;
        string_button = myList.get(current_state);
        next_step(string_button);
    }

    public void startTimer(final long finish, long tick) {
        TextView lcd = findViewById(R.id.lcd_text);
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

    private void update_number(String number) {
        System.out.println("Update Number");
        TextView lcd = findViewById(R.id.lcd_text);
        if(pizza_pressed) {
            lcd.setText(number + ".0 QTY");
        } else if(popcorn_pressed){
            lcd.setText(number + ".0 oz");
        } else{
            lcd.setText(number);
        }
    }

    private void update(String buttonValue) {
        Log.e("update", buttonValue);
        lcdString = "";
        lcdString = buttonValue;
        TextView lcd = findViewById(R.id.lcd_text);
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


        if (meltPressed) {
            meltButter(buttonValue);
        } else if (meltButterBool) {
            setButterQty(buttonValue);
        }
    }

    private void clearClock() {
        TextView alt = findViewById(R.id.alt_txt);
        TextView lcd = findViewById(R.id.lcd_text);
        alt.setAlpha(1);
        lcdString = "";
        tmpList.clear();
        if (clockPressed) {
            // blink animation and allow user to alter the time
            altString = "   ";
            alt.setText(altString);
            lcd.setText(lcdString);
        }
    }

    private void clearScreen() {
        TextView alt = findViewById(R.id.alt_txt);
        TextView lcd = findViewById(R.id.lcd_text);
        alt.setAlpha(1);
        lcdString = "";
        altString = "";
        alt.setText(altString);
        lcd.setText(lcdString);

    }

    private void reheatScreen() {
        TextView alt = findViewById(R.id.alt_txt);
        TextView lcd = findViewById(R.id.lcd_text);
        alt.setAlpha(1);
        lcdString = " ";
        altString = " ";
        alt.setText(altString);
        lcd.setText(lcdString);
        if (reheat_category % 3 == 0) {
            lcd.setText("Plate of food");
        } else if (reheat_category % 3 == 1) {
            lcd.setText("Casserole");
        } else {
            lcd.setText("Pasta");
        }
        reheat_category++;

    }

    private void setClock() {
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