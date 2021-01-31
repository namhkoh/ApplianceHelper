package com.unity3d.player;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aic.libnilu.nlu.ResponseObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class uiVariant4WithOven extends AppCompatActivity {


    /**
     * Buttons
     */
    private Button bakeBtn;
    private Button settingsBtn;
    private ImageButton SpeechBtn;
    private EditText editText;
    private Button o1, o2, o3, o4, o5, o6, o7, o8, o9, o0; //Keypad
    private Button open, startOvenbtn, cancelOvenBtn, timerBtn, on_offBtn, confirm; //oven3
    private Button cookTimeBtn, delayStartBtn, preheatBtn; //oven2
    private Button broilBtn, convectBtn, keepWarmBtn, selfCleanBtn, frozenBakeBtn; //oven1


    private int index = 0;
    ArrayList<String> list_ui1 = new ArrayList<>();
    ArrayList<String> tmpList_ui1 = new ArrayList<>();
    public static ArrayList<String> buttonList;
    private static String utterance;
    private FirebaseAnalytics mFirebaseAnalytics;
    HashMap<String, String> intentList;
    Button next;
    private boolean success = false;
    private int max_index;

    private TextToSpeech textToSpeech;
    String speakText = "";

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
    private List<String> button_hashmap_list;

    private HashMap<String, Boolean> next_button;
    private HashMap<String, Boolean> button_active;

    private CountDownTimer t;
    private Animation anim;


    public static Bundle userQuestions = new Bundle();
    private HashMap<String, String> userSequence = new HashMap<>();
    HashMap<String, String> tmpQuestions;
    private HashMap<String, String> inputQuestions = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant4_with_oven);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView lcd = findViewById(R.id.oven_panel_text);
        lcd.setText(DateTimeHandler.getCurrentTime("hh:mm"));

        this.addButtons();
        this.initialize_speaker();

        //Setting alpha value
        allButtons = getAllButtons();

        setAlphaValue(0, allButtons);

        //Blinking Animation
        anim = setAnimation();

        //inputQuestion List
        tmpQuestions = (HashMap<String, String>) getIntent().getSerializableExtra("questions");
        Log.e("ENTERING 4 OVEN", String.valueOf(tmpQuestions));

        //Button Hashmap for button state disable (true/false)
        initialize_button_list_for_hashmap();

        FloatingActionButton fabInstructions = findViewById(R.id.instructionHelp);
        fabInstructions.setOnClickListener(v -> {
            //openDialog();
            openListViewDialog();
        });

        next.setEnabled(false);
        open.setEnabled(false);
        if (uiVariant4WithOven.userQuestions.containsKey("Is First")) {
            Log.e("Is First", String.valueOf(uiVariant4WithOven.userQuestions.getBoolean("Is First")));
            load_bundle();
            Log.d("Load Bundle", "Restored");
        } else {
            System.out.println("new hashmap!");
            is_first = true;
            inputQuestions = (HashMap<String, String>) getIntent().getSerializableExtra("questions");
        }

    }

    private void initialize_button_list_for_hashmap() {
        button_hashmap_list = Arrays.asList("settings/clock", "six", "one", "two", "three", "four", "five", "nine", "seven", "eight", "zero", "start", "bake",
                "cook time", "broil", "keep warm", "timer", "frozen bake", "convect modes", "oven clean", "delay start", "rapid preheat",
                "on off", "confirm");
    }

    private ArrayList<Button> getAllButtons() {
        allButtons = new ArrayList<Button>(Arrays.asList(
                frozenBakeBtn, cookTimeBtn, o0, o1, o2, o3, o4, o5, o6, o7, o8, o9, startOvenbtn, cancelOvenBtn,
                bakeBtn, broilBtn, convectBtn, keepWarmBtn, selfCleanBtn, delayStartBtn, preheatBtn, settingsBtn,
                timerBtn, on_offBtn, confirm
        ));
        return allButtons;
    }

    private Animation setAnimation() {
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        return anim;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addButtons() {
        SpeechBtn = (ImageButton) findViewById(R.id.speechButton);
        editText = findViewById(R.id.editText);
        /**
         * Oven Panels
         */
        addOvenPanel1();
        addOvenPanel2();
        addOvenPanel3();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addOvenPanel1() {
        bakeBtn = findViewById(R.id.oven_bake);
        bakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bake();
            }
        });

        frozenBakeBtn = findViewById(R.id.oven_frozen);
        frozenBakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frozenBake();
            }
        });

        broilBtn = findViewById(R.id.oven_broil);
        broilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                broil();
            }
        });

        convectBtn = findViewById(R.id.oven_convect);
        convectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convect_modes();
            }
        });

        keepWarmBtn = findViewById(R.id.oven_warm);
        keepWarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keepWarm();
            }
        });

        selfCleanBtn = findViewById(R.id.oven_clean);
        selfCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oven_clean();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addOvenPanel2() {
        settingsBtn = findViewById(R.id.oven_settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings_clock();
            }
        });

        cookTimeBtn = findViewById(R.id.oven_cooktime);
        cookTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookTime();
            }
        });

        delayStartBtn = findViewById(R.id.oven_delay);
        delayStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delay_start();
            }
        });

        preheatBtn = findViewById(R.id.oven_preheat);
        preheatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rapid_predheat();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addOvenPanel3() {
        this.addNumberPad();
        open = findViewById(R.id.open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });

        next = findViewById(R.id.next);
        next.setOnClickListener(v -> nextActivity());

        startOvenbtn = findViewById(R.id.oven_start);
        startOvenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOven();
            }
        });

        cancelOvenBtn = findViewById(R.id.oven_cancel);
        cancelOvenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOven();
            }
        });

        timerBtn = findViewById(R.id.oven_timer);
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Timer set/off");
                timer();
            }
        });

        on_offBtn = findViewById(R.id.oven_onoff);
        on_offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                on_off();
            }
        });

        confirm = findViewById(R.id.oven_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNumberPad() {
        o0 = findViewById(R.id.oven_0);
        o0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press0();
            }
        });
        o1 = findViewById(R.id.oven_1);
        o1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press1();
            }
        });
        o2 = findViewById(R.id.oven_2);
        o2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press2();
            }
        });
        o3 = findViewById(R.id.oven_3);
        o3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press3();
            }
        });
        o4 = findViewById(R.id.oven_4);
        o4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press4();
            }
        });
        o5 = findViewById(R.id.oven_5);
        o5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press5();
            }
        });
        o6 = findViewById(R.id.oven_6);
        o6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press6();
            }
        });
        o7 = findViewById(R.id.oven_7);
        o7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press7();
            }
        });
        o8 = findViewById(R.id.oven_8);
        o8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press8();
            }
        });
        o9 = findViewById(R.id.oven_9);
        o9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press9();
            }
        });
    }

    private void initiate() {
        //How many buttons are there in the button array.
        number_of_steps = myList.size();
        pressed_wrong = 0;

        next_button = new HashMap<String, Boolean>();
        button_active = new HashMap<String, Boolean>();
        for (String i : button_hashmap_list) {
            next_button.put(i, false);
            button_active.put(i, false);
        }

        //Current Button
        current_state = 0;
        string_button = myList.get(current_state);

        //Initial Step
        next_step(string_button);
    }

    private void initialize_speaker() {
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

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResults(Bundle bundle) {

                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.e("ALL MATCHES", matches.toString());
                inputQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "User voice input: " + matches.toString());
                userQuestions.putBoolean("Is First", is_first);
                userQuestions.putSerializable("questions", inputQuestions);
                Log.e("1", " value stored");

                utterance = matches.get(0);
                editText.setText(utterance);

                if (utterance.contains("previous")) {
                    if (index > 0) {
                        Log.e("previous", String.valueOf(index));
                        index--;
                        update_state(tmpList_ui1.get(index));
                    } else {
                        Log.e("previous", "Front of the line");
                    }
                    return;
                } else if (utterance.contains("next")) {
                    if (index < max_index - 1) {
                        index++;
                        Log.e("next", String.valueOf(index));
                        update_state(tmpList_ui1.get(index));
                        if (index == max_index - 1) {
                            Toast.makeText(getApplicationContext(), "Last Step", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("next", "End of the line");
                    }
                    return;
                } else if (utterance.contains("repeat")) {
                    initTTS(tmpList_ui1.get(index));
                    return;
                }

                //Code below is technically an else cause everything above has a return statement.

                String question = utterance;

                ResponseObject response = Utilities.returnResponse(getApplicationContext(), question);

                //Current task from file2 here Please
                HashMap<String, String> tmpHash = getData();
                int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
                String incoming_indexString = String.valueOf(incoming_index);

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
                    buttonList = new ArrayList<>();

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
                    }

                    myList = buttonList;
                    instructionList = list_ui1;


                    index = 0;
                    max_index = response.getSteps().size();
                    initial_update(tmpList_ui1.get(index));

                    initiate();
                    System.out.println("question! " + question);
                    System.out.println("utterance! " + utterance);
                    uiVariant4WithMicrowave.userQuestions.putBoolean("Is First", is_first);
                }
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
                        //SpeechBtn.setEnabled(false);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (success == false & (index < max_index - 1)) {
                            index++;
                            update(tmpList_ui1.get(index), true);
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
    }

    private void next_step(String string_button) {

        Set<String> accept = new HashSet<String>();
        accept.add("settings/clock");
        accept.add("six");
        accept.add("timer");
        accept.add("broil");
        accept.add("keep warm");
        accept.add("one");
        accept.add("cook time");
        accept.add("bake");
        accept.add("two");
        accept.add("three");
        accept.add("four");
        accept.add("five");
        accept.add("start");

        System.out.println("String Button: " + string_button);


        //info_button is for extra information for the settings/clock fields
        arr1 = string_button.split(",");
        if (arr1.length == 1) {
            string_button = arr1[0];
        } else {
            string_button = arr1[0];
            info_button = arr1[1];
        }

        //Turn on specific buttons.
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
            string_button = myList.get(current_state);
            debug_next_step_log(string_button);
            if (info_button.equals("clock")) {

            }
            if (string_button.equals("clock")) {
                clock_active = true;
            } else {
                start_active = true;
            }
            next_button.put(string_button, true);
            manage_next();
        } else if (accept.contains(string_button)) {
            next_step_helper(string_button);
        }

    }

    private void next_step_helper(String string_button) {
        next_button.put(string_button, true);
        button_active.put(string_button, true);
        debug_next_step_log(string_button);
    }

    private void debug_next_step_log(String string_button) {
        Log.d("Debug (Next Step)", string_button);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void active_inactive_log(boolean active, String msg) {
        if (success) {
            if (active == true) {
                Log.i("Button Pressed (Active)", msg);
                pressed_wrong = 0;
                tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), " Button Pressed (Active) " + msg);
                System.out.println(Instant.now().getEpochSecond());
                System.out.println(tmpQuestions);
                System.out.println(tmpQuestions.size());
            } else {
                Log.i("Button Pressed (Inactive)", msg);
                pressed_wrong++;
                tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), " Button Pressed (Inactive) " + msg);
                System.out.println(Instant.now().getEpochSecond());
                System.out.println(tmpQuestions);
                System.out.println(tmpQuestions.size());
            }
            userQuestions.putSerializable("questions", tmpQuestions);
            if (pressed_wrong > 5 & current_state < myList.size()) {
            }
        }
    }

    public void openDialog() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= index; i++) {
            sb.append(tmpList_ui1.get(i));
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openListViewDialog() {
        inputQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "Button Pressed (Active) Helper Button");
        //userQuestions.putSerializable("questions", inputQuestions);
        String[] list_view = new String[index + 1];
        for (int i = 0; i <= index; i++) {
            list_view[i] = tmpList_ui1.get(i);
        }

        ContextThemeWrapper themedContext = new ContextThemeWrapper(this, android.R.style.Theme_Holo_NoActionBar);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
            }
        });

        final AlertDialog dialog = builder.setTitle("Instructions").setItems(list_view, null).create();

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(tmpList_ui1.get(i));
                initTTS(tmpList_ui1.get(i - 1));
            }
        };

        dialog.getListView().setOnItemClickListener(listener);

        ListView listView = dialog.getListView();
        listView.addHeaderView(new View(this));
        //listView.addFooterView(new View(this));
        //listView.setHeaderDividersEnabled(true);
        listView.setDivider(new ColorDrawable(Color.BLACK));
        listView.setDividerHeight(1);

        dialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void core_button_support(String button, String lcd_text) {
        if (success) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void frozenBake() {
        core_button_support("Frozen Bake", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void delay_start() {
        core_button_support("Delay Start", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void rapid_predheat() {
        core_button_support("Rapid Preheat", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void on_off() {
        core_button_support("On Off", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void confirm() {
        core_button_support("Confirm", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cookTime() {
        if (bake_pressed) {
            core_button_support("Cook Time", "Cook Time");
        } else {
            active_inactive_log(false, "Cook Time");
        }
    }

    void update_state(String s) {
        list_ui1.clear();
        list_ui1.add(s);
        initTTS(s);
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

    /**
     * Used to display the first step in the screen
     *
     * @param s The text to be updated
     */
    void initial_update(String s) {
        Log.e("UI STEP ", s);
        list_ui1.add(s);
        speakText = s;
        initTTS(speakText);
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
            if (time_position < 4) {
                bake_pressed = true;
                lcd.setText(lcdString + "°F");
            }

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
            t = null;
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
        if (success) {
            if (button_active.get("start") == true) {
                active_inactive_log(true, "Start Button");
                if (next_button.get("start") == true) {
                    next_button.put("start", false);
                }
                next_button.put("start", false);
                button_active.put("start", false);
                current_state++;

                if (timerPressed == true) {
                    //countdown(lcdString);
                    countdown("3");
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
    }

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

        if (success) {

            //Not good very limited conditions (If time try to change)
            // Bug may be fixed by putting & !current_task.equals("cook time")
            if (current_state + 1 == myList.size() & string_button.equals("cancel")) {
                current_state++;
                System.out.println("946");
                stopTimer();
                finish_task();
            } else {
                System.out.println("636");
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
                for (String i : button_hashmap_list) {
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
    }

    private void press(String number) {
        if (time_position < 4) {
            time[time_position] = number;
            time_position++;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void press_support(String number, String word) {
        if (success) {
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
            Intent intent = new Intent(this, TaskInstructionActivity.class);
            intent.putExtra("questions", tmpQuestions);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, UserSurveyActivity.class);
            intent.putExtra("questions", tmpQuestions);
            startActivity(intent);
        }
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

    private void setAlphaValue(int alpha, ArrayList<Button> allButtons) {
        for (int i = 0; i < allButtons.size(); i++) {
            allButtons.get(i).setAlpha(alpha);
        }
    }

    private void initTTS(String selectedText) {
        //textToSpeech.setSpeechRate(testingVal);
        int speechStatus = textToSpeech.speak(selectedText, TextToSpeech.QUEUE_ADD, null, "1");
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }

    //    private void load_bundle() {
//        is_first = uiVariant4WithOven.userQuestions.getBoolean("Is First");
//        questionList = (HashMap<String, String>) uiVariant4WithOven.userQuestions.getSerializable("questions");
//        correctButtonManager = (HashMap<String, Integer>) uiVariant4WithOven.buttonHandler.getSerializable("correct_button");
//        incorrectButtonManager = (HashMap<String, Integer>) uiVariant4WithOven.buttonHandler.getSerializable("incorrect_button");
//    }
    private void load_bundle() {
        is_first = uiVariant4WithOven.userQuestions.getBoolean("Is First");
        //tmpQuestions = (HashMap<String, String>) uiVariant4WithMicrowave.userQuestions.getSerializable("questions");
        inputQuestions = (HashMap<String, String>) getIntent().getSerializableExtra("questions");
//        correctButtonManager = (HashMap<String, Integer>) uiVariant4WithMicrowave.buttonHandler.getSerializable("correct_button");
//        incorrectButtonManager = (HashMap<String, Integer>) uiVariant4WithMicrowave.buttonHandler.getSerializable("incorrect_button");
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

}