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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.aic.libnilu.nlu.MainManager;
import com.aic.libnilu.nlu.ResponseObject;
import com.google.firebase.analytics.FirebaseAnalytics;
//import com.company.MainManager;
//import com.company.ResponseObject;

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

public class uiVariant1 extends AppCompatActivity {
    private ImageButton SpeechBtn;
    private ListView lvSteps;
    private int index = 0;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> tmpList = new ArrayList<>();
    ArrayList<String> buttonList;
    private ArrayAdapter adapter;
    private static String utterance;
    private FirebaseAnalytics mFirebaseAnalytics;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "testID");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "testName");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, dateFormat.format(date));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        setContentView(R.layout.activity_ui_variant1);
        checkPermission();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpeechBtn = (ImageButton) findViewById(R.id.speechButton);
        final EditText editText = findViewById(R.id.editText);
        next = findViewById(R.id.nextActivity);
        next.setEnabled(false);
        next.setOnClickListener(v -> {
            enterFeedback();
        });
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
                    update(list.get(index), false);
                    return;
                }

                String question = editText.getText().toString();

                //String assetName = "video_demo_data26.txt";
                String assetName = "appliance_test6.txt";
                String filePath = Utilities.assetFilePath(getApplicationContext(), assetName);
                Log.d("1", filePath);

                String model_file = "model_tiny_9_5.pt";
                String file_name = Utilities.assetFilePath(getApplicationContext(), model_file);
                Log.d("2", file_name);

                String vocab_file = "vocab.txt";
                String vocab_path = Utilities.assetFilePath(getApplicationContext(), vocab_file);
                Log.d("3", vocab_path);

                String config_file = "config.json";
                String config_path = Utilities.assetFilePath(getApplicationContext(), config_file);
                Log.d("3", config_path);

                String vocab_class_file = "vocab1.class";
                String vocab_class_path = Utilities.assetFilePath(getApplicationContext(), vocab_class_file);
                Log.d("3", vocab_class_path);

                String vocab_slot_file = "vocab1.tag";
                String vocab_slot_path = Utilities.assetFilePath(getApplicationContext(), vocab_slot_file);
                Log.d("3", vocab_slot_path);


                ResponseObject response = MainManager.getAnswer(question, filePath, file_name, vocab_path, config_path, vocab_class_path, vocab_slot_path);

                //Current task from file2 here Please
                HashMap<String, String> tmpHash = getData();
                int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
                String incoming_indexString = String.valueOf(incoming_index);
                System.out.println(tmpHash.get(incoming_indexString));

                if (response.getDialog_command().equals("no_match")) {

                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    list.add("No Match");
                    initTTS("No Match");


                } else {

                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    buttonList = new ArrayList<>();

                    for (int i = 0; i < response.getSteps().size(); ++i) {
                        String data = response.getSteps().get(i).getText();
                        String button = response.getSteps().get(i).getButton_name();
                        buttonList.add(button);
                        list.add(data);
                        initTTS(data);
                        //tmpList.add(data);
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

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (index < tmpList.size()) {
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
                item.setBackgroundColor(Color.parseColor("#f2f2f2"));
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

    private void enterFeedback() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        HashMap<String, String> tmpHash = getData();
        if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
            Intent intent = new Intent(this, uiVariant6.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list);
            startActivity(intent);
        } else if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("oven")) {
            Intent intent = new Intent(this, uiVariant6Oven.class);
            intent.putExtra("button", buttonList);
            intent.putExtra("instructions", list);
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

    private TextToSpeech textToSpeech;
    String speakText = "";

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

}