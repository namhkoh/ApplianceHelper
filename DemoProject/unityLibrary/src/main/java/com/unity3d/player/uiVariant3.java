package com.unity3d.player;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.aic.libnilu.nlu.ResponseObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * This class is responsible for handling the UI Variant 23task of this application.
 * UI variant 3 is where the user asks a question by pressing the red mike button.
 * If a question is successfully asked a list with the first step will be displayed along with an image of the button
 * This UI Variant is very much similar to uiVariant2
 * The user can say Previous, Next, Repeat to toggle around the steps.
 * The user then presses the UI Variant button to go to UI variant 6 in order to perform the instructions given.
 * The user may come back to uiVariant6 if they forgot what the instructions were and go back.
 * The status from uiVariant6 will be saved in uivariant1Bundle.
 */
public class uiVariant3 extends AppCompatActivity {

    private ImageButton SpeechBtn;
    private int index = 0;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> tmpList = new ArrayList<>();
    HashMap<String, String> intentList;
    private ArrayAdapter adapter;
    private static String utterance;
    ArrayList<String> buttonList;
    private String current_appliance;
    private int max_index;
    private boolean sucess = false;
    ImageView iv1;

    private HashMap<String, String> tmpHash; //getData
    private String incoming_indexString;
    private int incoming_index;

    private TextToSpeech textToSpeech;
    String speakText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkPermission();

        initialize_task();

        /**
         * Initialize buttons
         */
        SpeechBtn = (ImageButton) findViewById(R.id.speechButton);
        final EditText editText = findViewById(R.id.editText);
        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> enterFeedback());
        next.setEnabled(false);

        Button task = findViewById(R.id.task);
        task.setOnClickListener(v -> {
            task();
        });

        /**
         * SPEECH TO TEXT START
         */
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
                    if(index > 0) {
                        Log.e("previous", String.valueOf(index));
                        index--;
                        update_state(tmpList.get(index));
                    }else{
                        Log.e("previous", "Beginning of the instructions");
                    }
                    return;
                } else if (utterance.toLowerCase().contains("next")) {
                    if(index < max_index - 1) {
                        index++;
                        Log.e("next", String.valueOf(index));
                        System.out.println(list);
                        update_state(tmpList.get(index));
                    }else{
                        Log.e("next","End of the instructions");
                    }
                    return;
                } else if (utterance.contains("repeat")){
                    initTTS(tmpList.get(index));
                    return;
                }

                //Technically the code below is an else statement.
                //Due to the return statements from the code above.

                String question = utterance;

                ResponseObject response = Utilities.returnResponse(getApplicationContext(),question);

                current_appliance = response.getAppliance_name();

                //Current task from file2 here Please
                HashMap<String, String> tmpHash = getData();
                int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
                String incoming_indexString = String.valueOf(incoming_index);

                System.out.println(intentList.get(incoming_indexString));

                buttonList = new ArrayList<>();

                if(!response.getDialog_command().equals("no_match")){
                    System.out.println("----------------------------------------------------------");
                    System.out.println("Question: " + question);
                    System.out.println("Response Intent: "+response.getIntent());
                    System.out.println("Response Appliance Name: "+response.getAppliance_name());
                    System.out.println("Actual Intent: " + intentList.get(incoming_indexString));
                    System.out.println("Task Name: " + tmpHash.get(incoming_indexString));
                    System.out.println("----------------------------------------------------------");
                }


                //Some sort of error happened in the NLU part
                if (response.getDialog_command().equals("no_match")) {
                    sucess = false;
                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        buttonList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    list.add("No Match");
                    list.add("Try again by pressing the red mike button");
                    tmpList.add("No Match");
                    tmpList.add("Try again by pressing the red mike button");
                    buttonList.add("try_again");
                    buttonList.add("speech");

                    max_index = 2;

                    index = 0;

                    update_state(buttonList.get(index));


                } else if (!response.getIntent().equals(intentList.get(incoming_indexString))) {

                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        adapter.notifyDataSetChanged();
                        buttonList.clear();
                    }

                    list.add("Wrong Intent. " + "The current intent is " + response.getIntent());
                    list.add("Try again by pressing the red mike button");
                    tmpList.add("Wrong Intent. " + "The current intent is " + response.getIntent());
                    tmpList.add("Try again by pressing the red mike button");
                    buttonList.add("try_again");
                    buttonList.add("speech");

                    index = 0;

                    update_state(buttonList.get(index));


                } else if (!Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains(response.getAppliance_name().toLowerCase())) {

                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        adapter.notifyDataSetChanged();
                        buttonList.clear();
                    }

                    list.add("Wrong appliance. " + "The current appliance is " + response.getAppliance_name());
                    list.add("Try again by pressing the red mike button");
                    tmpList.add("Wrong appliance. " + "The current appliance is " + response.getAppliance_name());
                    tmpList.add("Try again by pressing the red mike button");
                    buttonList.add("try_again");
                    buttonList.add("speech");

                    index = 0;

                    update_state(buttonList.get(index));

                } else  {
                    next.setEnabled(true);
                    sucess = true;
                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        buttonList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    buttonList = new ArrayList<>();

                    for (int i = 0; i < response.getSteps().size(); ++i) {
                        String data = response.getSteps().get(i).getText();
                        String button = response.getSteps().get(i).getButton_name();
                        buttonList.add(button);
                        list.add(data);
                        tmpList.add(data);
                        //tmpList.add(data);
                    }
                    index = 0;
                    max_index = response.getSteps().size();

                    initial_update(buttonList.get(index), tmpList.get(index));
                }

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
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
        textToSpeech.setOnUtteranceProgressListener(
                new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.e("Debug","On Done");

                        if(sucess==false & (index < max_index - 1)){
                            index++;
                            Handler h = new Handler(getMainLooper());
                            h.postDelayed(() -> {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    update_state(tmpList.get(index));
                                }
                            });
                            }, 3000);
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView item = (TextView) super.getView(position, convertView, parent);
                item.setTextColor(Color.parseColor("#000000"));
                item.setTypeface(item.getTypeface(), Typeface.BOLD);
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                item.setBackgroundColor(Color.parseColor("#f2f2f2"));
                item.setAlpha(0.7f);
                return item;
            }
        };
    }

    /**
     * Is called when going to the next screen (UI Variant6, UI Variant6 Oven ex.)
     */
    private void enterFeedback() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        HashMap<String, String> tmpHash = getData();
        if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
            Intent intent = new Intent(this, uiVariant6.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list);
            intent.putExtra("variant",3);
            startActivity(intent);
        } else if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("oven")){
            Intent intent = new Intent(this, uiVariant6Oven.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list);
            intent.putExtra("variant",3);
            startActivity(intent);
        } else {
            return;
        }
        Log.e("entering feedback", "enter");
    }

    /**
     * Get the data from the task file file2.tsv.
     * @return Could get rid of this since the objective is to populate resultList and intentList and not just resultList.
     */
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

    /**
     * Initializing (Extracting) information from file2.tsv
     * tmpHash: result list. Technically no need to return it to tmpHash as the method getData() initializes everything we want.
     * incoming_indexString: Index value of current task. Used to extract corresponding intents and instructions.
     */
    private void initialize_task() {
        tmpHash = getData();
        incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        incoming_indexString = String.valueOf(incoming_index);
    }

    /**
     * Reminds the user of the current task (What they need to ask to the application)
     */
    private void task() {
        Toast.makeText(getApplicationContext(), tmpHash.get(incoming_indexString), Toast.LENGTH_SHORT).show();
    }

    void update(String s, final boolean forward) {
        TextView step = findViewById(R.id.stepOutput);
        Handler h = new Handler(getMainLooper());
        System.out.println("Index: " + index);
        h.postDelayed(() -> {
            if (!forward) {
                list.remove(tmpList.get(index));
                index--;
                speakText = tmpList.get(index);
                System.out.println(speakText);
                System.out.println(buttonList.get(index));
                step.setText(speakText);
                displayPanels2(buttonList.get(index));
            } else {
                list.add(s);
                step.setText(s);
                displayPanels2(buttonList.get(index));
                speakText = s;
            }
            adapter.notifyDataSetChanged();
            //runAR();
            int speechStatus = textToSpeech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, "1");
            textToSpeech.setSpeechRate(3);
            if (speechStatus == TextToSpeech.ERROR) {
                Log.e("TTS", "Error in converting Text to Speech!");
            }
            //3000
            index++;
        }, 0);
        index--;
        System.out.println(index);
    }

    void update_state(String s){
        TextView step = findViewById(R.id.stepOutput);
        list.clear();
        list.add(s);
        step.setText(s);
        displayPanels2(buttonList.get(index));
        adapter.notifyDataSetChanged();
        initTTS(s);
    }

    void initial_update(String s, String k){
        TextView step = findViewById(R.id.stepOutput);
        step.setText(k);
        initTTS(k);
        displayPanels2(s);
        adapter.notifyDataSetChanged();
        initTTS(s);
    }

    public void runSteps(String s, String k) {
        Handler h1 = new Handler(getMainLooper());
        TextView step = findViewById(R.id.stepOutput);
        h1.postDelayed(() -> {
            step.setText(k);
            initTTS(k);
            displayPanels2(s);
        }, 3000);
    }

    /**
     * Used to display the button images.
     * @param button The name of the button.
     */
    private void displayPanels2(String button){
        iv1 = (ImageView) findViewById(R.id.appliance_image);
        System.out.println(button.toLowerCase());
        System.out.println(current_appliance);

        if(current_appliance.toLowerCase().equals("oven")) {
            if(button.toLowerCase().equals("speech")){
                iv1.setImageResource(R.drawable.speech);
            } else if(button.toLowerCase().equals("try_again")){
                iv1.setImageResource(R.drawable.try_again);
            } else if(button.toLowerCase().equals("two")){
                iv1.setImageResource(R.drawable.oven_panel_button_2);
            }else if(button.toLowerCase().equals("three")){
                iv1.setImageResource(R.drawable.oven_panel_button_3);
            }else if(button.toLowerCase().equals("six")){
                iv1.setImageResource(R.drawable.oven_panel_button_6);
            }else if(button.toLowerCase().equals("four")){
                iv1.setImageResource(R.drawable.oven_panel_button_4);
            }else if(button.toLowerCase().equals("start")){
                iv1.setImageResource(R.drawable.oven_start_button);
            }else if(button.toLowerCase().equals("cook time")){
                iv1.setImageResource(R.drawable.oven_panel_cook_time);
            }else if(button.toLowerCase().equals("number pad")){
                iv1.setImageResource(R.drawable.oven_panel_number_pad);
            }else if(button.toLowerCase().equals("bake")){
                iv1.setImageResource(R.drawable.oven_panel_bake);
            }else if(button.toLowerCase().equals("settings/clock,sound")){
                iv1.setImageResource(R.drawable.oven_panel_settings_clock);
            }else if(button.toLowerCase().equals("settings/clock,clock")){
                iv1.setImageResource(R.drawable.oven_panel_settings_clock);
            } else if(button.toLowerCase().equals("cancel")){
                iv1.setImageResource(R.drawable.oven_cancel_button);
            } else if(button.toLowerCase().equals("open")){
                iv1.setImageResource(R.drawable.oven_open);
            } else{
                iv1.setImageResource(R.drawable.no_image_available);
            }
        } else{
            if (button.toLowerCase().equals("clock")) {
                iv1.setImageResource(R.drawable.microwave_clock_button);
            } else if (button.toLowerCase().equals("number pad")) {
                iv1.setImageResource(R.drawable.microwave_number_pad);
            } else if(button.toLowerCase().equals("timer")){
                iv1.setImageResource(R.drawable.microwave_timer);
            } else if(button.toLowerCase().equals("reheat")){
                iv1.setImageResource(R.drawable.microwave_reheat);
            } else if(button.toLowerCase().equals("speech")){
                iv1.setImageResource(R.drawable.speech);
            } else if(button.toLowerCase().equals("try_again")){
                iv1.setImageResource(R.drawable.try_again);
            } else if(button.toLowerCase().equals("start")){
                iv1.setImageResource(R.drawable.microwave_start_button);
            }else{
                iv1.setImageResource(R.drawable.no_image_available);
            }
        }
    }

    private void displayPanels() {
        iv1 = (ImageView) findViewById(R.id.appliance_image);
        if (utterance.contains("set") && utterance.contains("microwave") && utterance.contains("clock")) {
            if (index == 0) {
                iv1.setImageResource(R.drawable.microwave_clock_button);
            } else if (index == 2) {
                iv1.setImageResource(R.drawable.microwave_start_button);
            }
        } else if (utterance.contains("melt") && utterance.contains("microwave") && utterance.contains("butter")) {
            if (index == 0) {
                iv1.setImageResource(R.drawable.microwave_melt_button);
            } else if (index == 1) {
                iv1.setImageResource(R.drawable.microwave_5_button);
            } else if (index == 3) {
                iv1.setImageResource(R.drawable.microwave_start_button);
            }
        } else if (utterance.contains("cook") && utterance.contains("oven") && utterance.contains("fries")) {
            if (index == 1) {
                iv1.setImageResource(R.drawable.oven_frozen_button);
            } else if (index == 2) {
                iv1.setImageResource(R.drawable.oven_panel_button_3);
            } else if (index == 4) {
                iv1.setImageResource(R.drawable.oven_cooktime_button);
            } else if (index == 6) {
                iv1.setImageResource(R.drawable.oven_start_button);
            } else if (index == 7) {
                iv1.setImageResource(R.drawable.oven_cancel_button);
            }
        }
    }

    public void nextStep(String s) {
        TextView step = findViewById(R.id.stepOutput);
        step.setText(s);
        initTTS(s);
        index++;
    }

    private void initTTS(String selectedText) {
        //textToSpeech.setSpeechRate(3);
        int speechStatus = textToSpeech.speak(selectedText, TextToSpeech.QUEUE_FLUSH, null, "1");
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
}