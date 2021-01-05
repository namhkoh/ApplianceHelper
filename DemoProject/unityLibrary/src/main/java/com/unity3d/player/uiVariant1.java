package com.unity3d.player;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.prefs.AbstractPreferences;


/**
 * This class is responsible for handling the UI Variant 1 task of this application.
 * UI variant 1 is where the user asks a question by pressing the red mike button.
 * If a question is successfully asked a list will be displayed containing such instructions.
 * The user then presses the UI Variant button to go to UI variant 6 in order to perform the instructions given.
 * The user may come back to uiVariant6 if they forgot what the instructions were and go back.
 * The status from uiVariant6 will be saved in uivariant1Bundle.
 */
public class uiVariant1 extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private ArrayAdapter adapter;
    public static Bundle uivariant1Bundle = new Bundle();
    private ImageButton SpeechBtn;
    private ListView lvSteps; //The list showing the instructions.

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> tmpList = new ArrayList<>();
    private ArrayList<String> buttonList;

    private HashMap<String, String> intentList;
    private HashMap<String, String> tmpHash; //getData
    private HashMap<String, String> questionList = new HashMap<>();

    Button next;
    Intent intent;
    Button task;

    int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
    private int index = 0;
    private boolean sucess = false;
    private String incoming_indexString;
    private static String utterance;

    private TextToSpeech textToSpeech;
    private String speakText = "";

    public static Bundle userQuestions = new Bundle();
    boolean is_first = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkPermission();
        initialize_task();

        firebase_instance();

        //Set tasks to buttons. SpeechButton, Task button, UI Panel Button (next)
        SpeechBtn = (ImageButton) findViewById(R.id.speechButton);
        final EditText editText = findViewById(R.id.editText);
        next = findViewById(R.id.nextActivity);
        next.setEnabled(false);
        next.setOnClickListener(v -> {
            enterFeedback();
        });
        task = findViewById(R.id.task);
        task.setOnClickListener(v -> {
            task();
        });

        if (uiVariant1.userQuestions.containsKey("Is First")) {
            Log.e("Is First", String.valueOf(uiVariant1.uivariant1Bundle.getBoolean("Is First")));
            load_bundle();
            Log.d("Load Bundle", "Restored");
        } else {
            System.out.println("new hashmap!");
            is_first = true;
            questionList = new HashMap<>();
        }


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

                String question = editText.getText().toString();
                questionList.put(tmpHash.get(incoming_indexString), question);

                // Data collection
                // Need to store the questions in sequence.
//                System.out.println("question! " + question);
//                System.out.println("utterance! " + utterance);
//                questionList.put(tmpHash.get(incoming_indexString), utterance);
//                userQuestions.putBoolean("Is First", is_first);
//                userQuestions.putSerializable("questions", questionList);

                ResponseObject response = Utilities.returnResponse(getApplicationContext(), question);


                if (!response.getDialog_command().equals("no_match")) {
                    System.out.println("----------------------------------------------------------");
                    System.out.println("Question: " + question);
                    System.out.println("Response Intent: " + response.getIntent());
                    System.out.println("Response Appliance Name: " + response.getAppliance_name());
                    System.out.println("Actual Intent: " + intentList.get(incoming_indexString));
                    System.out.println("Task Name: " + tmpHash.get(incoming_indexString));
                    System.out.println("----------------------------------------------------------");
                    questionList.put(tmpHash.get(incoming_indexString), question);
                }

                //Some sort of error happened in the NLU part
                if (response.getDialog_command().equals("no_match")) {

                    clear(list);

                    list.add("No Match");
                    list.add("Try again by pressing the red mike button");
                    initTTS("No Match");

                    //Adapter is what creates the list on the screen using lv_steps and list.

                    //Intent list contains the intent values.
                    //So if the incoming_indexString is 0 the intentList will get me the corresponding intent.
                } else if (!response.getIntent().equals(intentList.get(incoming_indexString))) {

                    clear(list);

                    list.add("Wrong Intent. " + "The current intent is " + response.getIntent());
                    list.add("Try again by pressing the red mike button");
                    initTTS("Wrong Intent");

                } else if (!Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains(response.getAppliance_name().toLowerCase())) {

                    clear(list);
                    list.add("Wrong appliance. " + "The current appliance is " + response.getAppliance_name());
                    list.add("Try again by pressing the red mike button");
                    initTTS("Wrong appliance");

                } else {
                    SpeechBtn.setEnabled(false);
                    clear(list);
                    sucess = true;
                    buttonList = new ArrayList<>();

                    for (int i = 0; i < response.getSteps().size(); ++i) {
                        String data = response.getSteps().get(i).getText();
                        String button = response.getSteps().get(i).getButton_name();
                        buttonList.add(button);
                        list.add(data);
                        initTTS(data);
                    }
                    index = 0;
                    next.setEnabled(true);
                    System.out.println("question! " + question);
                    System.out.println("utterance! " + utterance);
                    userQuestions.putBoolean("Is First", is_first);
                    userQuestions.putSerializable("questions", questionList);
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
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {

        });

        //Results of pressing the speech button.
        textToSpeech.setOnUtteranceProgressListener(
                new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        SpeechBtn.setEnabled(false);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        index++;
                        if (index == list.size() & sucess == true) {
                            Log.d("Log", "Speach button deactivated");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    SpeechBtn.setEnabled(false);
                                }
                            });
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


        //Used to automatically update the list on the screen.
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView item = (TextView) super.getView(position, convertView, parent);
                item.setTextColor(Color.parseColor("#000000"));
                item.setTypeface(item.getTypeface(), Typeface.BOLD);
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                if (sucess) {
                    item.setBackgroundColor(Color.parseColor("#f2f2f2"));
                } else {
                    item.setBackgroundColor(Color.parseColor("#ff0033"));
                }
                item.setAlpha(0.7f);
                return item;
            }
        };
        lvSteps = findViewById(R.id.lv_steps);
        lvSteps.setOnItemClickListener((parent, view, position, id) -> {
            String item = (String) adapter.getItem(position);
            initTTS(item);
            for (int i = 0; i < lvSteps.getChildCount(); i++) {
                if (position == i) {
                    lvSteps.getChildAt(i).setBackgroundColor(Color.parseColor("#8c8c8c"));
                } else {
                    lvSteps.getChildAt(i).setBackgroundColor(Color.parseColor("#f2f2f2"));
                }
            }
        });
        lvSteps.setAdapter(adapter);
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
     * Obtain the FirebaseAnalytics instance.
     */
    private void firebase_instance() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "testID");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "testName");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, dateFormat.format(date));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /**
     * Clear everything from the lists.
     *
     * @param list_
     */
    public void clear(ArrayList<String> list_) {
        if (list_ != null) {
            list.clear();
            tmpList.clear();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Used to get rid of the lvSteps (Instructions)
     * Not currently used. Was used when the instructions were given a timer.
     */
    private void stop_screen() {
        lvSteps.setAdapter(null);
    }

    /**
     * Reminds the user of the current task (What they need to ask to the application)
     */
    private void task() {
        Toast.makeText(getApplicationContext(), tmpHash.get(incoming_indexString), Toast.LENGTH_SHORT).show();
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

    /**
     * Is called when going to the next screen (UI Variant6, UI Variant6 Oven ex.)
     */
    private void enterFeedback() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        HashMap<String, String> tmpHash = getData();
        if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
            intent = new Intent(this, uiVariant6.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list);
            intent.putExtra("variant", 1);
            startActivity(intent);
        } else if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("oven")) {
            Intent intent = new Intent(this, uiVariant6Oven.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list);
            intent.putExtra("variant", 1);
            startActivity(intent);
        } else {
            return;
        }
        Log.e("entering feedback", "enter");
    }

    /**
     * Get the data from the task file file2.tsv.
     *
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

//  Remove if no errors found. No reason for this to be down here.
//    private TextToSpeech textToSpeech;
//    String speakText = "";

    //This method doesn't really seem to be used in uiVariant1
    //But not removed because it might be used.
    void update(String s, final boolean forward) {
        Log.e("UI STEP ", s);
        Handler h = new Handler(getMainLooper());
        h.postDelayed(() -> {
            if (!forward) {
                list.remove(tmpList.get(index));
                index--;
                speakText = tmpList.get(index);
            } else {
                list.add(s);
                speakText = s;
            }
            adapter.notifyDataSetChanged();
            initTTS(speakText);
            index++;
        }, 0);
    }

    /**
     * A voice reads the text given in the method.
     *
     * @param selectedText The String text that is read.
     */
    private void initTTS(String selectedText) {
        //textToSpeech.setSpeechRate(testingVal);
        int speechStatus = textToSpeech.speak(selectedText, TextToSpeech.QUEUE_ADD, null, "1");
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }

    /**
     * Checking permission with the Device. (Probably. Check with Namho)
     */
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

    /**
     * Loading the userQustion bundle
     */
    private void load_bundle() {
        is_first = uiVariant1.userQuestions.getBoolean("Is First");
        questionList = (HashMap<String, String>) uiVariant1.userQuestions.getSerializable("questions");
    }
}