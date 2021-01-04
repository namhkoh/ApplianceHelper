package com.unity3d.player;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aic.libnilu.nlu.ResponseObject;
//import com.company.MainManager;
//import com.company.ResponseObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


/**
 * This class is responsible for handling the UI Variant 2 task of this application.
 * UI variant 2 is where the user asks a question by pressing the red mike button.
 * If a question is successfully asked a list with the first step will be displayed
 * The user can say Previous, Next, Repeat to toggle around the steps.
 * The user then presses the UI Variant button to go to UI variant 6 in order to perform the instructions given.
 * The user may come back to uiVariant6 if they forgot what the instructions were and go back.
 * The status from uiVariant6 will be saved in uivariant1Bundle.
 */
public class uiVariant2 extends AppCompatActivity {

    //Remove after checking (not being used)
    int testingVal = 5;
    // Set to 0 for testing
    int delayVal = 3500;
    ArrayList<String> microwaveButtons = new ArrayList<String>(Arrays.asList("Clock", "Melt", "Start"));
    ArrayList<String> ovenButtons = new ArrayList<String>(Arrays.asList("Frozen", "Cook Time", "Oven Start", "Cancel"));
    ArrayList<String> utteranceMicrowave = new ArrayList<String>(Arrays.asList("Clock", "SOFTEN/MELT", "Start"));
    ArrayList<String> utteranceOven = new ArrayList<String>(Arrays.asList("FROZEN BAKE", "COOK TIME", "START", "CANCEL"));


    private ImageButton SpeechBtn;
    private ListView lvSteps;
    private int index = 0;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> tmpList = new ArrayList<>();
    private ArrayList<String> buttonList;
    private ArrayAdapter adapter;
    private static String utterance;
    private int max_index;
    private boolean success;
    private HashMap<String, String> tmpHash; //getData
    int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
    private String incoming_indexString;
    private TextToSpeech textToSpeech;
    private String speakText = "";
    private HashMap<String, String> intentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkPermission();

        initialize_task();

        /**
         * Initializing buttons
         */
        SpeechBtn = (ImageButton) findViewById(R.id.speechButton);
        final EditText editText = findViewById(R.id.editText);
        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> {
            enterFeedback();
        });
        next.setEnabled(false);

        Button task = findViewById(R.id.task);
        task.setOnClickListener(v -> {
            task();
        });

        final String TAG = "Speech Debug";

        /**
         * Speech Recognize
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
                Log.d(TAG,"onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG,"onBeginningofSpeech");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                Log.e(TAG, "this is on end of speech.");
            }

            @Override
            public void onError(int i) {
                Log.e(TAG, "on Error: " + i);
            }

            @Override
            public void onResults(Bundle bundle) {
                Log.e(TAG, "on Results");

                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.e("ALL MATCHES", matches.toString());

                utterance = matches.get(0);
                editText.setText(utterance);

                if (utterance.contains("previous")) {
                    if (index > 0) {
                        Log.e("previous", String.valueOf(index));
                        index--;
                        update_state(tmpList.get(index));
                    } else {
                        Log.e("previous", "Front of the line");
                    }
                    return;
                } else if (utterance.contains("next")) {
                    if (index < max_index - 1) {
                        index++;
                        Log.e("UI STEP", tmpList.get(index));
                        update_state(tmpList.get(index));
                        if (index == max_index - 1) {
                            Toast.makeText(getApplicationContext(), "Last Step", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("next", "End of the line");
                    }
                    return;
                } else if (utterance.contains("repeat")) {
                    initTTS(tmpList.get(index));
                    return;
                }

                //Code below is technically an else cause everything above has a return statement.

                String question = editText.getText().toString();

                ResponseObject response = Utilities.returnResponse(getApplicationContext(), question);

                if(Utilities.debug) {
                    if (!response.getDialog_command().equals("no_match")) {
                        System.out.println("----------------------------------------------------------");
                        System.out.println("Question: " + question);
                        System.out.println("Response Intent: " + response.getIntent());
                        System.out.println("Response Appliance Name: " + response.getAppliance_name());
                        //System.out.println("Actual Intent: " + intentList.get(incoming_indexString));
                        System.out.println("Task Name: " + tmpHash.get(incoming_indexString));
                        System.out.println("----------------------------------------------------------");
                    }
                }

                //Some sort of error happened in the NLU part
                if (response.getDialog_command().equals("no_match")) {
                    buttonList = new ArrayList<>();
                    clear(list);
                    success = false;

                    tmpList.add("No Match");
                    tmpList.add("Try again by pressing the red mike button");
                    buttonList.add("try_again");
                    buttonList.add("speech");

                    index = 0;
                    max_index = 2;

                    update(tmpList.get(index), true);

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
                    clear(list);
                    success = true;
                    buttonList = new ArrayList<>();
                    next.setEnabled(true);
                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        buttonList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    for (int i = 0; i < response.getSteps().size(); ++i) {
                        String data = response.getSteps().get(i).getText();
                        String button = response.getSteps().get(i).getButton_name();
                        buttonList.add(button);
                        tmpList.add(data);
                    }

                    index = 0;
                    max_index = response.getSteps().size();
                    initial_update(tmpList.get(index));


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
                        if (success == false & (index < max_index - 1)) {
                            index++;
                            update(tmpList.get(index), true);
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
//                item.setBackgroundColor(Color.parseColor("#f2f2f2"));
                if (success) {
                    item.setBackgroundColor(Color.parseColor("#f2f2f2"));
                } else {
                    item.setBackgroundColor(Color.parseColor("#ff0033"));
                }
                item.setAlpha(0.7f);
                return item;
            }
        };
        lvSteps = findViewById(R.id.lv_steps);
        lvSteps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) adapter.getItem(position);
                initTTS(item);
                for (int i = 0; i < lvSteps.getChildCount(); i++) {
                    if (position == i) {
                        lvSteps.getChildAt(i).setBackgroundColor(Color.parseColor("#8c8c8c"));
                    } else {
                        lvSteps.getChildAt(i).setBackgroundColor(Color.parseColor("#f2f2f2"));
                    }
                }
            }
        });
        lvSteps.setAdapter(adapter);
    }

    /**
     * Clear everything from the lists.
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

    /**
     * Is called when going to the next screen (UI Variant6, UI Variant6 Oven ex.)
     */
    private void enterFeedback() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        HashMap<String, String> tmpHash = getData();
        if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
            Intent intent = new Intent(this, uiVariant6Microwave.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list);
            intent.putExtra("variant", 2);
            startActivity(intent);
        } else if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("oven")) {
            Intent intent = new Intent(this, uiVariant6Oven.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list);
            intent.putExtra("variant", 2);
            startActivity(intent);
        } else {
            return;
        }
        //Log.e("entering feedback", "enter");
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

    //Used for error message
    void update(String s, final boolean forward) {
        Log.e("UI STEP ", s);
        System.out.println(index);
        Handler h = new Handler(getMainLooper());
        h.postDelayed(() -> {
            if (!forward) {
                System.out.println(index);
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
        }, 3000);
        index--;
    }

    /**
     * Used to display the first step in the screen
     * @param s The text to be updated
     */
    void initial_update(String s) {
        Log.e("UI STEP Initial", s);
        list.add(s);
        speakText = s;
        adapter.notifyDataSetChanged();
        initTTS(speakText);
    }

    /**
     * Update the instruction list to the next/previous step
     * Maybe wise to combine with initial_update
     * @param s The text to be updated
     */
    void update_state(String s) {
        list.clear();
        list.add(s);
        adapter.notifyDataSetChanged();
        initTTS(s);
    }

    /**
     * A voice reads the text given in the method.
     * @param selectedText The String text that is read.
     */
    private void initTTS(String selectedText) {
        //textToSpeech.setSpeechRate(testingVal);
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