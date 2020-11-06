package com.unity3d.player;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BlendMode;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.aic.libnilu.nlu.MainManager;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * this class will contain the microwave panel with the speech button.
 */

public class uiVariant4WithMicrowave extends AppCompatActivity {

    private String lcdString = " ";
    private String altString = " ";

    private ImageButton SpeechBtn;
    private int index = 0;
    ArrayList<String> list_ui1 = new ArrayList<>();
    ArrayList<String> tmpList_ui1 = new ArrayList<>();
    public static ArrayList<String> buttonList;
    private static String utterance;
    private FirebaseAnalytics mFirebaseAnalytics;
    HashMap<String, String> intentList;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant4_with_microwave);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkPermission();
        SpeechBtn = (ImageButton) findViewById(R.id.speechButton);
        final EditText editText = findViewById(R.id.editText);
        next = findViewById(R.id.nextActivity);
        next.setEnabled(false);
        next.setOnClickListener(v -> {
            //enterFeedback();
            nextTask();
        });

        /**
         * Button Initialization start
         */

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

        /**
         * Button Initialization end
         */

        // SPEECH TO TEXT START
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                Log.e("onEndOfSpeech", "this is on end of speech.");
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {

                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.e("ALL MATCHES", matches.toString());

                utterance = matches.get(0);
                editText.setText(utterance);

                if (utterance.contains("previous")) {
                    Log.e("previous", String.valueOf(index));
                    index--;
                    update(list_ui1.get(index), false);
                    return;
                }

                String question = editText.getText().toString();

                //String assetName = "video_demo_data26.txt";
                String assetName = "appliance_test6.txt";
                String filePath = Utilities.assetFilePath(getApplicationContext(), assetName);
                Log.d("Data File path", filePath);

                String model_file = "model_tiny_9_5.pt";
                String file_name = Utilities.assetFilePath(getApplicationContext(), model_file);
                Log.d("Model File Path", file_name);

                String vocab_file = "vocab.txt";
                String vocab_path = Utilities.assetFilePath(getApplicationContext(), vocab_file);
                Log.d("Vocab File Path", vocab_path);

                String config_file = "config.json";
                String config_path = Utilities.assetFilePath(getApplicationContext(), config_file);
                Log.d("Config File Path", config_path);

                String vocab_class_file = "vocab1.class";
                String vocab_class_path = Utilities.assetFilePath(getApplicationContext(), vocab_class_file);
                Log.d("Vocab Class File", vocab_class_path);

                String vocab_slot_file = "vocab1.tag";
                String vocab_slot_path = Utilities.assetFilePath(getApplicationContext(), vocab_slot_file);
                Log.d("Vocab Tag Path", vocab_slot_path);

                ResponseObject response = MainManager.getAnswer(question, filePath, file_name, vocab_path, config_path, vocab_class_path, vocab_slot_path);

                //Current task from file2 here Please
                HashMap<String, String> tmpHash = getData();
                int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
                String incoming_indexString = String.valueOf(incoming_index);

                System.out.println(tmpList_ui1.size());

                //Some sort of error happened in the NLU part
                if (response.getDialog_command().equals("no_match")) {

                    if (list_ui1 != null) {
                        list_ui1.clear();
                        tmpList_ui1.clear();
                    }

                    list_ui1.add("No Match");
                    list_ui1.add("Try again by pressing the red mike button");
                    initTTS("No Match");


                } else if (!response.getIntent().equals(intentList.get(incoming_indexString))) {

                    if (list_ui1 != null) {
                        list_ui1.clear();
                        tmpList_ui1.clear();
                    }

                    list_ui1.add("Wrong Intent. " + "The current intent is " + response.getIntent());
                    list_ui1.add("Try again by pressing the red mike button");
                    initTTS("Wrong Intent");


                } else if (!Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains(response.getAppliance_name().toLowerCase())) {

                    if (list_ui1 != null) {
                        list_ui1.clear();
                        tmpList_ui1.clear();
                    }

                    list_ui1.add("Wrong appliance. " + "The current appliance is " + response.getAppliance_name());
                    list_ui1.add("Try again by pressing the red mike button");
                    initTTS("Wrong appliance");

                } else {
                    success = true;
                    if (list_ui1 != null) {
                        list_ui1.clear();
                        tmpList_ui1.clear();
                    }

                    buttonList = new ArrayList<>();

                    for (int i = 0; i < response.getSteps().size(); ++i) {
                        String data = response.getSteps().get(i).getText();
                        String button = response.getSteps().get(i).getButton_name();
                        buttonList.add(button);
                        list_ui1.add(data);
                        initTTS(data);

                        showToast(data, 1);

                        //tmpList.add(data);
                        myList = buttonList;
                        instructionList = list_ui1;
                        //Get the Next button
                        string_button = myList.get(current_state);
                        //How many instructions are there in total
                        number_of_steps = myList.size();
                        //Use the String value of the button to go to the next step.
                        next_step(string_button);
                    }
                    index = 0;
                }
                //update(tmpList.get(index), true);
                next.setEnabled(true);
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        SpeechBtn.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP:
                    mSpeechRecognizer.stopListening();
                    editText.setHint("You will see input here");
                    break;

                case MotionEvent.ACTION_DOWN:
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    editText.setText("");
                    editText.setHint("Listening...");
                    break;
            }
            return false;
        });
        // SPEECH TO TEXT END
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {

        });
        textToSpeech.setOnUtteranceProgressListener(
                new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        SpeechBtn.setEnabled(false);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (index < tmpList_ui1.size()) {
                            update(tmpList_ui1.get(index), true);
                        }


                        Log.d("Speak", String.valueOf(index));

                        index++;

                        if (index == list_ui1.size() & success == true) {
                            Log.d("Here we go", "Done");
                            SpeechBtn.setEnabled(false);

                            Handler h = new Handler(getMainLooper());

                            h.postDelayed(() -> {

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Press Next", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }, 3000);

                            Log.d("Here we go", "Done-2");
                        } else {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    SpeechBtn.setEnabled(true);
                                }
                            });

                        }
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });

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
//        myList = (ArrayList<String>) getIntent().getSerializableExtra("button");
//
//        instructionList = (ArrayList<String>) getIntent().getSerializableExtra("instructions");

//        System.out.println(myList);
//
//        System.out.println(instructionList);

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
//        //Get the Next button
//        string_button = myList.get(current_state);
//        //How many instructions are there in total
//        number_of_steps = myList.size();
//        //Use the String value of the button to go to the next step.
//        next_step(string_button);
    }


    /**
     * This function will take the user to TaskUserActivity
     */
    private void nextTask() {
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

    private void enterFeedback() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        HashMap<String, String> tmpHash = getData();
        if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
            Intent intent = new Intent(this, uiVariant6.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list_ui1);
            startActivity(intent);
        } else if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("oven")) {
            Intent intent = new Intent(this, uiVariant6Oven.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list_ui1);
            startActivity(intent);
        } else {
            return;
        }
        Log.e("entering feedback", "enter");
    }

    private HashMap<String, String> getData() {
        InputStream ls = getResources().openRawResource(R.raw.file2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ls, StandardCharsets.UTF_8));
        String line = "";
        HashMap<String, String> resultList = new HashMap<String, String>();
        intentList = new HashMap<String, String>();
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                resultList.put(tokens[0], tokens[1]);
                intentList.put(tokens[0], tokens[2]);
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
            initTTS(speakText);
            index++;
        }, 0);
    }


    private void initTTS(String selectedText) {
        //textToSpeech.setSpeechRate(testingVal);
        int speechStatus = textToSpeech.speak(selectedText, TextToSpeech.QUEUE_ADD, null, "1");
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }

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
        } else {
            Log.e("Button Pressed (Inactive)", msg);
            pressed_wrong++;
        }

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
}