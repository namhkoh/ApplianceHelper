package com.unity3d.player;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.JsonUtils;

import org.w3c.dom.ls.LSOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

import kotlin.text.UStringsKt;

public class uiVariant6Oven extends AppCompatActivity {

    private String lcdString = " ";
    private String current_task = "";
    private String button_lowercase;
    private String temp_task = "";
    private String string_button;
    private String previous_state;
    private String info_button = "nothing";
    private String task = "";
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
    boolean is_first = false;
    private boolean bake_pressed;

    private int time_position = 0;
    private int number_of_steps;
    private int current_state;
    private int pressed_wrong;

    private ArrayList<String> tmpList = new ArrayList<String>();
    private ArrayList<String> myList;
    private ArrayList<Button> allButtons;
    ArrayList<String> instructionList;
    private List<String> list;

    private HashMap<String, Boolean> next_button;
    private HashMap<String, Boolean> button_active;
    private Button hint;

    private CountDownTimer t;
    private Animation anim;

    // Data collection variables
    int correct_press;
    int incorrect_press;
    HashMap<String, Integer> correctButtonManager;
    HashMap<String, Integer> incorrectButtonManager;
    public static Bundle buttonHandler = new Bundle();
    Intent intent;

    /**
     * Proper data collection pipeline
     */
    public static Bundle userQuestions = new Bundle();
    HashMap<String, String> tmpQuestions;
    HashMap<String, String> primeQUestions;
    boolean is_first_oven = true;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant6_oven);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        hint = findViewById(R.id.task);
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

        //Instructions List
        instructionList = (ArrayList<String>) getIntent().getSerializableExtra("instructions");

        //inputQuestion List
        tmpQuestions = (HashMap<String, String>) getIntent().getSerializableExtra("questions");


        //Button Hashmap for state
        list = Arrays.asList("settings/clock", "six", "one", "two", "three", "four", "five", "nine", "seven", "eight", "zero", "start", "bake",
                "cook time", "broil", "keep warm", "timer", "frozen bake", "convect modes", "oven clean", "delay start", "rapid preheat",
                "on off", "confirm");

        next.setEnabled(false);
        open.setEnabled(false);
        hint.setEnabled(false);

        // Not first time
        if (uiVariant1.uivariant1Bundle.containsKey("Is First")) {
            System.out.println("Is First: " + uiVariant1.uivariant1Bundle.getBoolean("Is First"));
            System.out.println("Number Pad: " + uiVariant1.uivariant1Bundle.getBoolean("NumberPad"));
            load_bundle();
            save_button_press();
            Log.d("Load Bundle", "Restored");
        } else {
            is_first = true;
            //How many buttons are there in the button array.
            number_of_steps = myList.size();
            pressed_wrong = 0;

            next_button = new HashMap<String, Boolean>();
            button_active = new HashMap<String, Boolean>();
            // Stores the incorrect button count
            correctButtonManager = new HashMap<String, Integer>();
            incorrectButtonManager = new HashMap<String, Integer>();
            tmpQuestions = (HashMap<String, String>) getIntent().getSerializableExtra("questions");

            //tmpQuestions = new HashMap<String, String>();

            Log.e("tmpQuestions_uiVariant6Oven", String.valueOf(tmpQuestions));
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

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e("Event Captured: ", "back button pressed");
                Log.e("tmpQuestions_check", String.valueOf(tmpQuestions));
                //Intent intent = new Intent();
                //intent.putExtra("questions", tmpQuestions);
                //setResult(RESULT_OK, intent);
                //Toast.makeText(getApplicationContext(),"Back button clicked",Toast.LENGTH_SHORT).show();
                save_bundle();
                finish();
                //return true;
                //startActivity(intent);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void save_bundle() {
        uiVariant1.uivariant1Bundle.clear();
        Log.v("Saved", "Save Instance State");
        TextView lcd = findViewById(R.id.oven_panel_text);
        uiVariant1.uivariant1Bundle.putBoolean("Is First", is_first);
        uiVariant1.uivariant1Bundle.putSerializable("NextButton", next_button);
        uiVariant1.uivariant1Bundle.putSerializable("ActiveButton", button_active);
        uiVariant1.uivariant1Bundle.putInt("CurrentState", current_state);
        uiVariant1.uivariant1Bundle.putInt("PressedWrong", pressed_wrong);
        uiVariant1.uivariant1Bundle.putString("CurrentTask", current_task);
        uiVariant1.uivariant1Bundle.putString("lcdstring", lcdString);
        uiVariant1.uivariant1Bundle.putString("OnScreen", lcd.getText().toString());
        uiVariant1.uivariant1Bundle.putStringArray("time", time);
        uiVariant1.uivariant1Bundle.putInt("timeposition", time_position);
        uiVariant1.uivariant1Bundle.putString("StringButton", string_button);
        uiVariant1.uivariant1Bundle.putString("InfoButton", info_button);
        uiVariant1.uivariant1Bundle.putString("TempTask", temp_task);
        uiVariant1.uivariant1Bundle.putString("Task", task);
        uiVariant1.uivariant1Bundle.putBoolean("NumberPad", numberpad_active);
        uiVariant1.uivariant1Bundle.putBoolean("BakePressed", bake_pressed);
        uiVariant1.uivariant1Bundle.putBoolean("SettingsClockWorking", settings_clock_working);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void load_bundle() {
        Log.v("Loaded", "Loaded Instance State");
        TextView lcd = findViewById(R.id.oven_panel_text);
        task = uiVariant1.uivariant1Bundle.getString("Task");
        settings_clock_working = uiVariant1.uivariant1Bundle.getBoolean("SettingsClockWorking");
        temp_task = uiVariant1.uivariant1Bundle.getString("TempTask");
        is_first = uiVariant1.uivariant1Bundle.getBoolean("Is First");
        bake_pressed = uiVariant1.uivariant1Bundle.getBoolean("BakePressed");
        next_button = (HashMap<String, Boolean>) uiVariant1.uivariant1Bundle.getSerializable("NextButton");
        button_active = (HashMap<String, Boolean>) uiVariant1.uivariant1Bundle.getSerializable("ActiveButton");
        current_state = uiVariant1.uivariant1Bundle.getInt("CurrentState");
        pressed_wrong = uiVariant1.uivariant1Bundle.getInt("PressedWrong");
        current_task = uiVariant1.uivariant1Bundle.getString("CurrentTask");
        lcdString = uiVariant1.uivariant1Bundle.getString("lcdstring");
        time = uiVariant1.uivariant1Bundle.getStringArray("time");
        time_position = uiVariant1.uivariant1Bundle.getInt("timeposition");
        string_button = uiVariant1.uivariant1Bundle.getString("StringButton");
        info_button = uiVariant1.uivariant1Bundle.getString("InfoButton");
        numberpad_active = uiVariant1.uivariant1Bundle.getBoolean("NumberPad");
        lcd.setText(uiVariant1.uivariant1Bundle.getString("OnScreen"));
        uiVariant1.uivariant1Bundle.clear();
        print_bundle();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void save_button_press() {
        is_first = uiVariant1.userQuestions.getBoolean("Is First");
        tmpQuestions = (HashMap<String, String>) getIntent().getSerializableExtra("questions");
    }

    private void print_bundle() {
        System.out.println("Task: " + task);
        System.out.println("Is First " + is_first);
        System.out.println("Next Button: " + next_button);
        System.out.println("Next Button: " + button_active);
        System.out.println("Current State: " + current_state);
        System.out.println("Pressed Wrong: " + pressed_wrong);
        System.out.println("Current Task: " + current_task);
        System.out.println("lcdString: " + lcdString);
        System.out.println("Time :" + time);
        System.out.println("Time position: " + time_position);
        System.out.println("String Button: " + string_button);
        System.out.println("Info Button: " + info_button);
        System.out.println("Numberpad active: " + numberpad_active);
    }

    private void next_step(String string_button) {

        //info_button is for extra information for the settings/clock fields
        arr1 = string_button.split(",");
        if (arr1.length == 1) {
            string_button = arr1[0];
        } else {
            string_button = arr1[0];
            info_button = arr1[1];
        }

        if (string_button.equals("no button")) {
            current_state++;
            string_button = myList.get(current_state);
            next_step(string_button);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("number pad")) {
            lcdString = "";
            numberpad_active = true;
            previous_state = string_button;
            current_state += 1;
            previous_numberpad = true;
            debug_next_step_log(string_button);
            string_button = myList.get(current_state);
            if (info_button.equals("clock")) {

            }
            if (string_button.equals("clock")) {
                clock_active = true;
            } else {
                start_active = true;
            }
            next_button.put(string_button, true);
            manage_next();
        } else if (string_button.equals("settings/clock")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("six")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("timer")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("broil")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("keep warm")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("one")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("cook time")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("bake")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("two")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("three")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("four")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("five")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        } else if (string_button.equals("start")) {
            next_button.put(string_button, true);
            button_active.put(string_button, true);
            debug_next_step_log(string_button);
        }
    }

    private void next_step_helper(String button) {
        next_button.put(string_button, true);
        button_active.put(string_button, true);
        debug_next_step_log(string_button);
    }

    private void debug_next_step_log(String string_button) {
        Log.d("Debug (Next Step)", string_button);
    }

    private static String randomStringGenerator(int length, String seedChars) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        Random rand = new Random();
        while (i < length) {
            sb.append(seedChars.charAt(rand.nextInt(seedChars.length())));
            i++;
        }
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void active_inactive_log(boolean active, String msg) {
        String asciiUpperCase = "ABCDEFGHIJKLMNOPQRSTUWXYZ";
        String asciiLowerCase = asciiUpperCase.toLowerCase();
        String digits = "0123456789";
        String asciiChars = asciiUpperCase + asciiLowerCase + digits;
        // Generating random string for
        if (active == true) {
            Log.i("Button Pressed (Active)", msg);
            pressed_wrong = 0;
            correct_press++;

            //tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()) + " " + randomStringGenerator(3, asciiChars), " Button Pressed (Active) " + msg);
            tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), " Button Pressed (Active) " + msg);
            System.out.println(Instant.now().getEpochSecond());
            System.out.println(randomStringGenerator(3, asciiChars));
            System.out.println(tmpQuestions);
            System.out.println(tmpQuestions.size());
            hint.setEnabled(false);
        } else {
            Log.i("Button Pressed (Inactive)", msg);
            pressed_wrong++;
            incorrect_press++;
            //tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), " " + randomStringGenerator(3, asciiChars) + " Button Pressed (Inactive) " + msg);
            tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), " Button Pressed (Inactive) " + msg);
            System.out.println(Instant.now().getEpochSecond());
            System.out.println(randomStringGenerator(3, asciiChars));
            System.out.println(tmpQuestions);
            System.out.println(tmpQuestions.size());
        }
        userQuestions.putSerializable("questions", tmpQuestions);
        buttonHandler.putSerializable("correct_button", correctButtonManager);
        buttonHandler.putSerializable("incorrect_button", incorrectButtonManager);
        if (pressed_wrong > 5 & current_state < myList.size()) {
            hint.setEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void core_button_support(String button, String lcd_text) {
        button_lowercase = button.toLowerCase();
        if (button_active.get(button_lowercase) == true) {
            clearClock();
            active_inactive_log(true, button);
            current_task = button_lowercase;
            if (next_button.get(button_lowercase) == true) {
                TextView lcd = findViewById(R.id.oven_panel_text);
                next_button.put(button_lowercase, false);
                button_active.put(button_lowercase, false);
                if (lcd_text != null) {
                    lcdString = lcd_text;
                    lcd.setText(lcdString);
                    lcdString = "";
                }
                if (button_lowercase.equals("settings/clock")) {
                    settings_clock_working = true;
                    settings_temperature_working = true;
                }
                if (button_lowercase.equals("timer")) {
                    timerPressed = true;
                }
                if (button_lowercase.equals("bake")) {
                    temp_task = button_lowercase;
                    task = button_lowercase;
                    previous_state = button_lowercase;
                }
                if (button_lowercase.equals("cook time")) {
                    temp_task = "";
                    previous_state = "cooktime";
                    time = new String[]{" ", " ", " ", " "};
                    time_position = 0;
                }
                numberpad_toggle();
                current_state++;
                manage_next();
            }
        } else {
            active_inactive_log(false, button);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void timer() {
        core_button_support("Timer", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void settings_clock() {
        core_button_support("settings/clock", "Settings Clock");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void keepWarm() {
        core_button_support("Keep Warm", "Keep warm");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void broil() {
        core_button_support("Broil", "Broil");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void bake() {
        core_button_support("Bake", "Bake");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void convect_modes() {
        core_button_support("Convect Modes", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void oven_clean() {
        core_button_support("Oven Clean", null);
    }

    ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void frozenBake() {

        core_button_support("Frozen Bake", null);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void delay_start() {
        core_button_support("Delay Start", null);
    }

    ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void rapid_predheat() {
        core_button_support("Rapid Preheat", null);
    }

    ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void on_off() {
        core_button_support("On Off", null);
    }

    ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void confirm() {
        core_button_support("Confirm", null);
    }

    ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cookTime() {
        if (bake_pressed) {
            core_button_support("Cook Time", "Cook Time");
        } else {
            active_inactive_log(false, "Cook Time");
        }
    }

    private void hint() {
        Toast toast = Toast.makeText(getApplicationContext(), instructionList.get(current_state), Toast.LENGTH_SHORT);
        toast.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void open() {
        active_inactive_log(false, "Open");
    }

    private void numberpad_toggle() {
        numberpad_active = false;
        previous_numberpad = false;
    }

    private void manage_next() {
        TextView lcd = findViewById(R.id.oven_panel_text);
        if (current_state >= myList.size()) {
            finish_task();
        } else {
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

        Log.d("Debug lcdString", lcdString);
        Log.d("Debug time position", String.valueOf(time_position));
        Log.d("Debug time", Arrays.toString(time));

        HashMap<Integer, String> time_map = new HashMap<Integer, String>();
        time_map.put(0, " ");
        time_map.put(1, " ");
        time_map.put(2, " ");
        time_map.put(3, " ");

        if (temp_task.equals("bake")) {
            bake_pressed = true;
            if (time_position < 4) {
                lcd.setText(lcdString + "°F");
            }

            // Time (not bake at the moment)
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
                System.out.println(millisUntilFinished + " " + finish);
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
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                t.cancel();
            }
        }.start();
    }

    public void stopTimer() {
        if (t != null) {
            t.cancel();
        }
    }


    private void countdown(String number) {
        Log.d("Debug Countdown", number);
        int time = Integer.parseInt(number);
        int seconds = time / 1000 * 60 * 60 + time / 100 * 60 + time % 100;
        startTimer(seconds * 1000 + 1100, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startOven() {
        if (button_active.get("start") == true) {
            active_inactive_log(true, "Start Button");
            if (next_button.get("start") == true) {
                next_button.put("start", false);
            }
            next_button.put("start", false);
            button_active.put("start", false);
            current_state++;

            if (timerPressed == true) {
                countdown(lcdString);
            }

            if (current_task.equals("keep warm")) {
                countdown("3");
            }
            if (task.equals("bake")) {
                countdown("3");
            }

            if (current_task.equals("broil") | current_task.equals("broiling")) {
                countdown("3");
            }

            ///////////////////////////

            if (current_state >= myList.size()) {
                finish_task();
            } else {
                string_button = myList.get(current_state);
                next_step(string_button);
            }


        } else {
            active_inactive_log(false, "Start Button");
        }
    }


    private void finish_task() {

        Handler h = new Handler(getMainLooper());
        Handler h1 = new Handler(getMainLooper());
        TextView lcd = findViewById(R.id.oven_panel_text);
        lcd.clearAnimation();
        h.postDelayed(() -> {
            isCode = true;
            lcdString = "";
            lcdString = "Well Done!";
            lcd.setText(lcdString);
            h1.postDelayed(() -> {
                lcdString = "";
                lcdString = "Press Next";
                //isTemp = true;
                lcd.setText(lcdString);
            }, 2000);
        }, 2000);
        Button next = findViewById(R.id.next);
        next.setEnabled(true);
    }


    private void cancelOven() {


        //Not good very limited conditions (If time try to change)
        // Bug may be fixed by putting & !current_task.equals("cook time")
        if (current_state + 1 == myList.size() & string_button.equals("cancel")) {
            current_state++;
            stopTimer();
            finish_task();
        } else {
            TextView lcd = findViewById(R.id.oven_panel_text);

            stopTimer();

            if (current_task.equals("timer")) {
                stopTimer();
            }

            if (current_task.equals("broil") | current_task.equals("broiling")) {
                stopTimer();
            }

            lcd.clearAnimation();

            //Reset
            Button next = findViewById(R.id.next);
            next.setEnabled(false);
            lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));
            time = new String[]{" ", " ", " ", " "};
            time_position = 0;

            next_button = new HashMap<String, Boolean>();
            button_active = new HashMap<String, Boolean>();
            for (String i : list) {
                next_button.put(i, false);
                button_active.put(i, false);
            }

            isTemp = false;
            isCode = false;
            numberpad_active = false;
            previous_numberpad = false;
            settings_clock_working = false;
            sound_working = false;
            settings_temperature_working = false;
            timerPressed = false;

            lcdString = " ";
            current_task = "";
            temp_task = "";
            info_button = "nothing";

            current_state = 0;
            string_button = myList.get(current_state);
            next_step(string_button);
        }
    }

    private void press(String number) {
        if (time_position < 4) {
            time[time_position] = number;
            time_position++;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press_support(String number, String word) {
        if (numberpad_active == true | button_active.get(word) == true) {
            active_inactive_log(true, number);
            if (button_active.get(word) == true) {
                button_active.put(word, false);

                TextView lcd = findViewById(R.id.oven_panel_text);
                if (number.equals("1")) {
                    if (current_task.equals("broil") | current_task.equals("broiling")) {
                        lcd.setText("High");
                    }

                    if (info_button.equals("sound")) {
                        lcd.setText("Sound on");
                    }

                    if (info_button.equals("temperature")) {
                        lcd.setText("Temperature on");
                    }
                }
                if (number.equals("2")) {
                    if (info_button.equals("sound")) {
                        lcd.setText("Sound off");
                    }

                    if (info_button.equals("temperature")) {
                        lcd.setText("Temperature off");
                    }
                }
                if (number.equals("3")) {
                    if (info_button.equals("clock")) {
                        lcd.setText("AM");
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
                if (number.equals("4")) {
                    if (info_button.equals("clock")) {
                        lcd.setText("Clock Settings");
                    }
                }

                if (number.equals("5")) {
                    if (settings_temperature_working) {
                        sound_working = true;
                        lcd.setText("Temperature Settings");
                    }
                }

                if (number.equals("6")) {
                    if (settings_clock_working) {
                        sound_working = true;
                        lcd.setText("Sound Settings");
                    }
                }

                numberpad_toggle();
                current_state++;
                manage_next();
            } else {
                press(number);
                update(number);
            }
        } else {
            active_inactive_log(false, number);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press0() {
        press_support("0", "zero");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press1() {
        press_support("1", "one");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press2() {
        press_support("2", "two");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press3() {
        press_support("3", "three");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press4() {
        press_support("4", "four");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press5() {
        press_support("5", "five");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press6() {
        press_support("6", "six");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press7() {
        press_support("7", "seven");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press8() {
        press_support("8", "eight");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press9() {
        press_support("9", "nine");
    }

    private void nextActivity() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        HashMap<String, String> tmpQuestions = (HashMap<String, String>) userQuestions.getSerializable("questions");
        HashMap<String, String> tmpHash = getData();
        ArrayList<String> instructionList = new ArrayList<>(tmpHash.values());
        if (incoming_index < instructionList.size() - 1) {
            intent = new Intent(this, TaskInstructionActivity.class);
            intent.putExtra("questions", tmpQuestions);
            Log.e("ui6Oven", String.valueOf(tmpQuestions));
            startActivity(intent);
        } else {
            intent = new Intent(this, UserSurveyActivity.class);
            intent.putExtra("questions", tmpQuestions);
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


//////////////////////////////////////////////////////////////////////////////////////////////////


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