package com.unity3d.player;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aic.libnilu.nlu.ResponseObject;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * uiPanel supported list view instructions
 */
public class BaseLineActivityMicrowave extends AppCompatActivity {

    private String lcdString = " ";
    private String altString = " ";
    private int max_index;
    //private ArrayAdapter adapter;


    private Button clock, start, cancel, soften, timer, popcorn, pizza, reheat, defrost, open;
    private Button cook, cooktime, cookpower, potato, add30;
    private Button m0, m1, m2, m3, m4, m5, m6, m7, m8, m9;
    private ImageButton SpeechBtn;


    private int index = 0;
    ArrayList<String> list_ui1 = new ArrayList<>();
    ArrayList<String> tmpList_ui1 = new ArrayList<>();
    public static ArrayList<String> buttonList;
    private static String utterance;
    private FirebaseAnalytics mFirebaseAnalytics;
    HashMap<String, String> intentList;
    HashMap<String, String> commandList;
    Button next;
    private boolean success = false;

    /**
     * UI 6 buttons start
     */
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

    int current_state;
    int number_of_steps;
    int reheat_category = 0;
    int time_position = 0;
    int pressed_wrong;

    /**
     * UI 6 buttons end
     */

    // Data collection variables
    public static Bundle userQuestions = new Bundle();
    boolean is_first = false;
    HashMap<String, Integer> correctButtonManager;
    HashMap<String, Integer> incorrectButtonManager;
    int correct_press = 0;
    int incorrect_press = 0;
    public static Bundle buttonHandler = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_line_microwave);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkPermission();

        /**
         * Initializing buttons
         */
        next = findViewById(R.id.nextActivity);
        next.setEnabled(false);
        next.setOnClickListener(v -> {
            //enterFeedback();
            nextTask();
        });


        /**
         * Initialize the time on the screen with the current time.
         */
        TextView lcd = findViewById(R.id.lcd_text);
        lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));

        /**
         * Button Initialization start
         */

        this.addButtons();

        ArrayList<Button> allButtons = new ArrayList<Button>(Arrays.asList(clock, start, cancel, soften, m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m0,
                popcorn, potato, pizza, cook, reheat, cooktime, cookpower, defrost,
                timer, add30));


        setAlphaValue(0, allButtons);

        anim = setAnimation();

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
        this.spawnResponse();
        if (BaseLineActivityMicrowave.userQuestions.containsKey("Is First")) {
            Log.e("Is First", String.valueOf(BaseLineActivityMicrowave.userQuestions.getBoolean("Is First")));
            load_bundle();
            Log.d("Load Bundle", "Restored");
        } else {
            System.out.println("new hashmap!");
            is_first = true;
            // Stores the incorrect button count
            correctButtonManager = new HashMap<String, Integer>();
            incorrectButtonManager = new HashMap<String, Integer>();
        }

    }

    private void setAlphaValue(int alpha, ArrayList<Button> allButtons) {
        for (int i = 0; i < allButtons.size(); i++) {
            allButtons.get(i).setAlpha(alpha);
        }
    }

    private Animation setAnimation() {
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        return anim;
    }


    private void spawnResponse() {

        HashMap<String, String> tmpHash = getData();
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        String question = commandList.get(incoming_indexString);

        ResponseObject response = Utilities.returnResponse(getApplicationContext(), question);

        System.out.println(tmpList_ui1.size());

        //Some sort of error happened in the NLU part
        if (response.getDialog_command().equals("no_match")) {

            buttonList = new ArrayList<>();
            clear(list_ui1);
            success = false;

            tmpList_ui1.add("No Match");
            tmpList_ui1.add("Try again by pressing the red mike button");
            buttonList.add("try_again");
            buttonList.add("speech");

            index = 0;
            max_index = 2;

            update(tmpList_ui1.get(index), true);

        } else if (!response.getIntent().equals(intentList.get(incoming_indexString))) {

            clear(list_ui1);

            tmpList_ui1.add("Wrong Intent. " + "The current intent is " + response.getIntent());
            tmpList_ui1.add("Try again by pressing the red mike button");
            buttonList.add("try_again");
            buttonList.add("speech");

            index = 0;
            max_index = 2;

            update(tmpList_ui1.get(index), true);


        } else if (!Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains(response.getAppliance_name().toLowerCase())) {

            clear(list_ui1);

            tmpList_ui1.add("Wrong appliance. " + "The current appliance is " + response.getAppliance_name());
            tmpList_ui1.add("Try again by pressing the red mike button");
            buttonList.add("try_again");
            buttonList.add("speech");

            index = 0;
            max_index = 2;

            update(tmpList_ui1.get(index), true);

        } else {
            success = true;
            clear(list_ui1);
            buttonList = new ArrayList<>();
            next.setEnabled(true);

            for (int i = 0; i < response.getSteps().size(); ++i) {
                String data = response.getSteps().get(i).getText();
                String button = response.getSteps().get(i).getButton_name();
                buttonList.add(button);
                tmpList_ui1.add(data);
//                initTTS(data);
            }

            //tmpList.add(data);
            myList = buttonList;
            instructionList = list_ui1;


            index = 0;
            max_index = response.getSteps().size();

            // Commented due to repeated step - Check with Leo
            //initial_update(tmpList_ui1.get(index));

            initiate();

        }
        //update(tmpList.get(index), true);
        next.setEnabled(true);
        if (!success & (index < max_index - 1)) {
            index++;
            update(tmpList_ui1.get(index), true);
        }
    }


    private void addButtons() {
        clock = findViewById(R.id.microwave_clock);
        clock.setOnClickListener(v -> {
            microwaveClock();
        });

        start = findViewById(R.id.microwave_start);
        start.setOnClickListener(v -> {
            microwaveStart();
        });

        cancel = findViewById(R.id.microwave_cancel);
        cancel.setOnClickListener(v -> {
            microwaveCancel();
        });

        soften = findViewById(R.id.microwave_soften);
        soften.setOnClickListener(v -> {
            microwaveSoften();
        });

        timer = findViewById(R.id.microwave_timer);
        timer.setOnClickListener(v -> {
            timer();
        });

        reheat = findViewById(R.id.microwave_reheat);
        reheat.setOnClickListener(v -> {
            reheat();
        });

        defrost = findViewById(R.id.microwave_defrost);
        defrost.setOnClickListener(v -> {
            defrost();
        });

        pizza = findViewById(R.id.microwave_pizza);
        pizza.setOnClickListener(v -> {
            pizza();
        });

        popcorn = findViewById(R.id.microwave_popcorn);
        popcorn.setOnClickListener(v -> {
            popcorn();
        });

        open = findViewById(R.id.open);
        open.setOnClickListener(v -> {
            open_microwave();
        });

        potato = findViewById(R.id.microwave_potato);
        potato.setOnClickListener(v -> {
            potato();
        });

        cook = findViewById(R.id.microwave_cook);
        cook.setOnClickListener(v -> {
            cook();
        });

        cooktime = findViewById(R.id.microwave_cooktime);
        cooktime.setOnClickListener(v -> {
            cooktime();
        });

        cookpower = findViewById(R.id.microwave_cookpower);
        cookpower.setOnClickListener(v -> {
            cookpower();
        });

        add30 = findViewById(R.id.microwave_30sec);
        add30.setOnClickListener(v -> {
            add30();
        });

        this.addNumberPad();

    }

    private void addNumberPad() {
        m0 = findViewById(R.id.microwave_0);
        m0.setOnClickListener(v -> {
            press0();
        });

        m1 = findViewById(R.id.microwave_1);
        m1.setOnClickListener(v -> {
            press1();
        });

        m2 = findViewById(R.id.microwave_2);
        m2.setOnClickListener(v -> {
            press2();
        });

        m3 = findViewById(R.id.microwave_3);
        m3.setOnClickListener(v -> {
            press3();
        });

        m4 = findViewById(R.id.microwave_4);
        m4.setOnClickListener(v -> {
            press4();
        });

        m5 = findViewById(R.id.microwave_5);
        m5.setOnClickListener(v -> {
            press5();
        });

        m6 = findViewById(R.id.microwave_6);
        m6.setOnClickListener(v -> {
            press6();
        });

        m7 = findViewById(R.id.microwave_7);
        m7.setOnClickListener(v -> {
            press7();
        });

        m8 = findViewById(R.id.microwave_8);
        m8.setOnClickListener(v -> {
            press8();
        });

        m9 = findViewById(R.id.microwave_9);
        m9.setOnClickListener(v -> {
            press9();
        });
    }

    public void initiate() {
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

    /**
     * Clear everything from the lists.
     *
     * @param list_
     */
    public void clear(ArrayList<String> list_) {
        if (list_ != null) {
            list_ui1.clear();
            tmpList_ui1.clear();
        }
    }

    /**
     * Used to display the first step in the screen
     *
     * @param s The text to be updated
     */
    void initial_update(String s) {
        Log.e("UI STEP ", s);
        list_ui1.add(s);
        speakText = s;
        //initTTS(speakText);
    }

    void update_state(String s) {
        list_ui1.clear();
        list_ui1.add(s);
        //initTTS(s);
    }

    public void openDialog() {

        StringBuilder sb = new StringBuilder();

        for (String instruction : tmpList_ui1) {
            sb.append(instruction);
            sb.append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>();
        builder.setTitle("Instructions");
        builder.setMessage("Look at this dialog!");
        builder.setMessage(sb.toString());
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * This function will take the user to TaskUserActivity
     */
    private void nextTask() {
        System.out.println("im here boss");
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        HashMap<String, String> tmpHash = getData();
        ArrayList<String> instructionList = new ArrayList<>(tmpHash.values());
        Log.e("incoming_index ", String.valueOf(incoming_index));
        Log.e("instruction_list ", String.valueOf(instructionList.size()));
        Log.e("values ", String.valueOf(tmpHash.values()));
        if (incoming_index < instructionList.size() - 1) {
            Intent intent = new Intent(this, TaskInstructionActivity.class);
            intent.putExtra("newIndex", incoming_index);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, UserSurveyActivity.class);
            startActivity(intent);
        }
//        Intent intent = new Intent(this,DebugActivity.class);
//        startActivity(intent);
    }

    private HashMap<String, String> getData() {
        InputStream ls = getResources().openRawResource(R.raw.file2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ls, StandardCharsets.UTF_8));
        String line = "";
        HashMap<String, String> resultList = new HashMap<String, String>();
        intentList = new HashMap<String, String>();
        commandList = new HashMap<String, String>();
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                resultList.put(tokens[0], tokens[1]);
                intentList.put(tokens[0], tokens[2]);
                commandList.put(tokens[0], tokens[3]);
            }
        } catch (IOException e) {
            Log.wtf("TaskInstructionActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
        return resultList;
    }

    private TextToSpeech textToSpeech;
    String speakText = "";

    void update(String s, final boolean forward) {
        Log.e("UI STEP ", s);
        Handler h = new Handler(getMainLooper());
        h.postDelayed(() -> {
            if (!forward) {
                list_ui1.remove(tmpList_ui1.get(index));
                index--;
                speakText = tmpList_ui1.get(index);
            } else {
                list_ui1.add(s);
                speakText = s;
            }
            //initTTS(speakText);
            index++;
        }, 0);
    }


//    private void initTTS(String selectedText) {
//        //textToSpeech.setSpeechRate(testingVal);
//        int speechStatus = textToSpeech.speak(selectedText, TextToSpeech.QUEUE_ADD, null, "1");
//        if (speechStatus == TextToSpeech.ERROR) {
//            Log.e("TTS", "Error in converting Text to Speech!");
//        }
//    }

    private void checkPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) &&
                !(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                !(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        ) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    private void showToast(String inputText, int Length) {
        if (Length == 0) {
            Toast.makeText(this, inputText, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, inputText, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Button methods and logic
     */
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
            correct_press++;
            correctButtonManager.put("Button Pressed (Active) " + msg,correct_press);
        } else {
            Log.e("Button Pressed (Inactive)", msg);
            pressed_wrong++;
            incorrect_press++;
            incorrectButtonManager.put("Button Pressed (inactive) " + msg,incorrect_press);
        }

        buttonHandler.putSerializable("correct_button",correctButtonManager);
        buttonHandler.putSerializable("incorrect_button",incorrectButtonManager);
        System.out.println(pressed_wrong);

        if (pressed_wrong >= 5 & current_state < myList.size()) {
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
        next.setEnabled(true);
    }

    private void core_button_support(String button, String lcd_text) {
        button_lowercase = button.toLowerCase();
        if (active_button.get(button_lowercase) == true) {
            active_inactive_log(true, button);
            if (next_button.get(button_lowercase)) {
                Log.e("NEXT_BUTTON ",String.valueOf(next_button));
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
        //Button next = findViewById(R.id.Next);
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

    private void next_step(String string_button) {

        Set<String> accept = new HashSet<String>();
        accept.add("clock");
        accept.add("reheat");
        accept.add("defrost");
        accept.add("pizza");
        accept.add("popcorn");
        accept.add("timer");
        accept.add("start");
        accept.add("open");

        System.out.println("String Button: " + string_button);

        if (accept.contains(string_button)) {
            next_step_helper(string_button);
        }

        if (string_button.equals("no button")) {
            active_button.put(string_button, true);
            next_button.put(string_button, true);
            Log.e("Button", string_button);
            finish_task();
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

    private void next_step_helper(String string_button) {
        next_button.put(string_button, true);
        active_button.put(string_button, true);
        //debug_next_step_log(string_button); //Need to implement if wanted
    }

    private void load_bundle() {
        is_first = BaseLineActivityMicrowave.userQuestions.getBoolean("Is First");
        correctButtonManager = (HashMap<String, Integer>) BaseLineActivityMicrowave.buttonHandler.getSerializable("correct_button");
        incorrectButtonManager = (HashMap<String, Integer>) BaseLineActivityMicrowave.buttonHandler.getSerializable("incorrect_button");
    }

}