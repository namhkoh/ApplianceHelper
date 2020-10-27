package com.unity3d.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * this class will contain the microwave panel with the speech button.
 */

public class uiVariant4WithMicrowave extends AppCompatActivity {

    private String lcdString = " ";
    private String altString = " ";
    public static Bundle uiVariant6Bundle = new Bundle();

    //For toggling among states
    boolean reheat_pressed = false;

    //Not currently in any real use
    boolean melt_active = false;

    //What task the user is currently on.
    boolean food_working = false;

    //If the previous step was a numberpad used for Reheat
    boolean previous_numberpad = false;

    CountDownTimer t;

    HashMap<String, Boolean> next_button;
    HashMap<String, Boolean> active_button;
    HashMap<String, Boolean> working_button;

    ArrayList<String> myList;
    ArrayList<String> instructionList;
    ArrayList<String> tmpList;

    Animation anim;

    String string_button;
    String button_lowercase;
    String previous_state;
    String current_task;
    String[] time = {" ", " ", " ", " "};

    List<String> list;
    Button hint;

    int current_state;
    int number_of_steps;
    int reheat_category = 0;
    int time_position = 0;
    int pressed_wrong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant4_with_microwave);

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

        //Buttons that don't really have anytthing
        Button potato = findViewById(R.id.microwave_potato);
        potato.setOnClickListener(v -> {
            potato();
        });

        Button cook = findViewById(R.id.microwave_cook);
        cook.setOnClickListener(v -> {
            cook();
        });

        Button cooktime = findViewById(R.id.microwave_cooktime);
        cooktime.setOnClickListener(v -> {
            cooktime();
        });

        Button cookpower = findViewById(R.id.microwave_cookpower);
        cookpower.setOnClickListener(v -> {
            cookpower();
        });

        Button add30 = findViewById(R.id.microwave_30sec);
        add30.setOnClickListener(v -> {
            add30();
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

        hint = findViewById((R.id.task));
        hint.setOnClickListener(v -> {
            hint();
        });

        //Set next into disabled
        next.setEnabled(false);
        hint.setEnabled(false);

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

        instructionList = (ArrayList<String>) getIntent().getSerializableExtra("instructions");

        System.out.println(myList);

        System.out.println(instructionList);

        tmpList = new ArrayList<String>();

        //Initialize hashmap
        list = Arrays.asList("clock", "start", "cancel", "timer", "reheat", "defrost", "pizza", "pork",
                "no button", "number pad", "open", "popcorn", "soften",
                "potato", "cook", "add30", "cook time", "cook power");
        //Next Button is the button I have to Press Next
        next_button = new HashMap<String, Boolean>();
        //Active Button is to define what buttons are active.
        active_button = new HashMap<String, Boolean>();
        //Working Button is to define features (not buttons) are active.
        working_button = new HashMap<String, Boolean>();
        //Initialize everything to false
        for (String i : list) {
            next_button.put(i, false);
            active_button.put(i, false);
            working_button.put(i, false);
        }

        pressed_wrong = 0;

        //Initial Step
        current_state = 0;
        //Get the Next button
        string_button = myList.get(current_state);
        //How many instructions are there in total
        number_of_steps = myList.size();
        //Use the String value of the button to go to the next step.
        next_step(string_button);
    }

    private void next_step(String string_button) {
        if (string_button.equals("clock")) {
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("reheat")) {
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("defrost")) {
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("pizza")) {
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("popcorn")) {
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("timer")) {
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("start")) {
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
        }

        if (string_button.equals("no button")) {
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
            //next_button.put(string_button,true);
            active_button.put(string_button, true);
            previous_state = string_button;
            previous_numberpad = true;

            //Go to the next element in the list. This button would be used to identify the movement in state.
            current_state += 1;
            string_button = myList.get(current_state);
            if (string_button.equals("clock")) {
                active_button.put(string_button, true);
            } else {
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

    private void potato() {
        core_button_support("Potato", null);
    }

    private void cook() {
        core_button_support("Cook", null);
    }

    private void cooktime() {
        core_button_support("Cook Time", null);
    }

    private void cookpower() {
        core_button_support("Cook Power", null);
    }

    private void add30() {
        core_button_support("add30", null);
    }


    //Simulates Opening the microwave.
    private void open_microwave() {
        if (active_button.get("open")) {
            active_inactive_log(true, "Open");
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
        } else {
            active_inactive_log(false, "Open");
        }
    }

    private void press(String number) {
        if (food_working | working_button.get("reheat")) {
            lcdString = number;
            tmpList = new ArrayList<>();
            tmpList.add(number);
            update_number(number);
        } else {
            if (lcdString.length() < 4) {
                lcdString += number;
                tmpList.add(number);
            }
            if (time_position < 4) {
                time[time_position] = number;
                time_position++;
            }
            if (working_button.get("clock") | working_button.get("timer") | working_button.get("defrost")) {
                update(lcdString);
            }
            if (food_working | working_button.get("reheat")) {
                update_number(lcdString);
            }
            if (current_task == "Reheat") {
                active_button.put("reheat", false);
            }
        }
    }

    private void active_inactive_log(boolean active, String msg) {
        if (active == true) {
            Log.e("Button Pressed (Active)", msg);
            pressed_wrong = 0;
            hint.setEnabled(false);
        } else {
            Log.e("Button Pressed (Inactive)", msg);
            pressed_wrong++;
        }

        System.out.println(pressed_wrong);

        if (pressed_wrong >= 5 & current_state < myList.size()) {
            hint.setEnabled(true);
        }
    }

    private void press_helper(String number) {
        if (active_button.get("number pad")) {
            press(number);
            active_inactive_log(true, number);
        } else {
            active_inactive_log(false, number);
        }
    }

    private void press0() {
        press_helper("0");
    }

    private void press1() {
        press_helper("1");
    }

    private void press2() {
        press_helper("2");
    }

    private void press3() {
        press_helper("3");
    }

    private void press4() {
        press_helper("4");
    }

    private void press5() {
        press_helper("5");
    }

    private void press6() {
        press_helper("6");
    }

    private void press7() {
        press_helper("7");
    }

    private void press8() {
        press_helper("8");
    }

    private void press9() {
        press_helper("9");
    }

    private void hint() {
        Log.e("Button Pressed", "Hint");
        Toast toast = Toast.makeText(getApplicationContext(), instructionList.get(current_state), Toast.LENGTH_SHORT);
        toast.show();
    }

    //Disable back button
    @Override
    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        } else {
            Log.d("Debug", "Back Button Pressed");
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

            if (working_button.get("clock")) {
                working_button.put("clock", false);
            }

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

    private void core_button_support(String button, String lcd_text) {
        button_lowercase = button.toLowerCase();
        if (active_button.get(button_lowercase) == true) {
            active_inactive_log(true, button);
            if (next_button.get(button_lowercase)) {
                TextView lcd = findViewById(R.id.lcd_text);
                if (button_lowercase.equals("clock")) {
                    clearClock();
                }
                if (button_lowercase.equals("timer") | button_lowercase.equals("pizza") | button_lowercase.equals("popcorn") | button_lowercase.equals("defrost")) {
                    clearScreen();
                }
                if (button_lowercase.equals("pizza") | button_lowercase.equals("popcorn")) {
                    food_working = true;
                }
                current_task = button;
                //The current feature is now working
                working_button.put(button_lowercase, true);
                //As this is the next button deactivate the two buttons.
                next_button.put(button_lowercase, false);
                active_button.put(button_lowercase, false);
                //Set the Screen like that (Can remove in the future)
                if (lcd_text != null) {
                    lcdString = lcd_text;
                    lcd.setText(lcdString);
                    lcdString = "";
                }
                //To determine what type of update it should do.
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        } else {
            active_inactive_log(false, button);
        }
    }

    private void microwaveClock() {
        core_button_support("Clock", "  :  ");
    }

    private void timer() {
        core_button_support("Timer", "  :  ");
    }

    public void pizza() {
        core_button_support("Pizza", "0.0 QTY");
    }

    public void popcorn() {
        core_button_support("Popcorn", "0.0 oz");
    }

    private void defrost() {
        core_button_support("Defrost", "  :  ");
    }

    private void microwaveSoften() {
        core_button_support("soften", null);
    }

    private void reheat() {
        if (active_button.get("reheat")) {
            reheatScreen();
            active_inactive_log(true, "Reheat");
            if (next_button.get("reheat") == true) {
                working_button.put("reheat", true);
                // Not for reheat because reheat can be pressed multiple times to toggle
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
            active_inactive_log(false, "Reheat");
        }
    }

    private void numberpad_toggle() {
        previous_numberpad = false;
        active_button.put("number pad", false);

    }

    private void microwaveStart() {
        if (active_button.get("start")) {
            if (next_button.get("start") == true) {
                Log.e("Button", "Clock deactivate");
                next_button.put("start", false);
                active_button.put("start", false);
                numberpad_toggle();
                System.out.println(previous_state);
                if (current_task == "Reheat") {
                    countdown("10");
                }
                if ((current_task == "Pizza") | (current_task == "Popcorn") | (current_task == "Defrost")) {
                    countdown("10");
                }
                current_state++;
                if (working_button.get("timer")) {
                    countdown(lcdString);
                }

                if (active_button.get("reheat")) {
                    active_button.put("reheat", false);
                    next_button.put("reheat", false);
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

    public void cancel(String string_button) {
        if (current_task == "Reheat" | current_task == "Timer") {
            stopTimer();
            return;
        }
        if (string_button.equals("clock")) {
            active_button.put("clock", false);
        }

        if (active_button.get("number pad") == true) {
            active_button.put("number pad", false);
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
        reheat_pressed = false;
        time_position = 0;
        current_state = 0;
        reheat_category = 0;
        pressed_wrong = 0;
        string_button = myList.get(current_state);
        System.out.println(string_button);

        //Next Button is the button I have to Press Next
        next_button = new HashMap<String, Boolean>();
        //Active Button is to define what buttons are active.
        active_button = new HashMap<String, Boolean>();
        //Working Button is to define features (not buttons) are active.
        working_button = new HashMap<String, Boolean>();
        //Initialize everything to false
        for (String i : list) {
            next_button.put(i, false);
            active_button.put(i, false);
            working_button.put(i, false);
        }

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
        int time = Integer.parseInt(number);
        int seconds = time / 1000 * 60 * 60 + time / 100 * 60 + time % 100;
        startTimer(seconds * 1000 + 1100, 1000);
    }

    private void update_number(String number) {
        System.out.println("Update Number");
        TextView lcd = findViewById(R.id.lcd_text);
        if (current_task == "Pizza") {
            lcd.setText(number + ".0 QTY");
        } else if (current_task == "Popcorn") {
            lcd.setText(number + ".0 oz");
        } else if (current_task == "Reheat") {
            lcd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            lcd.setText(number + ".0 servings");
            lcd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        } else {
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

    }

    private void clearClock() {
        TextView alt = findViewById(R.id.alt_txt);
        TextView lcd = findViewById(R.id.lcd_text);
        alt.setAlpha(1);
        lcdString = "";
        tmpList.clear();
        if (working_button.get("clock")) {
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
            lcd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            lcd.setText("Plate of food");
            lcd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        } else if (reheat_category % 3 == 1) {
            lcd.setText("Casserole");
        } else {
            lcd.setText("Pasta");
        }
        reheat_category++;
    }

    /**
     * Speech handling
     */

}