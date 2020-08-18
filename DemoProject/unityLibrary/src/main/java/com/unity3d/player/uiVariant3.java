package com.unity3d.player;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.company.MainManager;
import com.company.ResponseObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class uiVariant3 extends AppCompatActivity {
    private ImageButton SpeechBtn;
    private int index = 0;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> tmpList = new ArrayList<>();
    private ArrayAdapter adapter;
    private static String utterance;

    ImageView iv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkPermission();
        SpeechBtn = (ImageButton) findViewById(R.id.speechButton);
        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> enterFeedback());
        final EditText editText = findViewById(R.id.editText);
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

                editText.setText(matches.get(0));

                if (matches.get(0).contains("previous")) {
                    Log.e("previous", String.valueOf(index));
                    index--;
                    update(list.get(index), false);
                    return;
                } else if (matches.get(0).toLowerCase().contains("next")) {
                    nextStep(tmpList.get(index));
                    return;
                } else {
                    utterance = matches.get(0);
                }
                String question = utterance;
                String assetName = "video_demo_data.txt";
                String filePath = Utilities.assetFilePath(getApplicationContext(), assetName);
                ResponseObject response = MainManager.getAnswer(question, filePath);
                if (list != null) {
                    list.clear();
                    tmpList.clear();
                    adapter.notifyDataSetChanged();
                }
                for (int i = 0; i < response.getSteps().size(); ++i) {
                    String data = response.getSteps().get(i).getText();
                    tmpList.add(data);
                }
                index = 0;
                //update(tmpList.get(index), true);
                //nextStep(tmpList.get(index));
                runSteps(tmpList.get(index));
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
                        if (index < tmpList.size()) {
                            runSteps(tmpList.get(index));
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

    private void enterFeedback() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        HashMap<String, String> tmpHash = getData();
        if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
            Intent intent = new Intent(this, uiVariant6.class);
            startActivity(intent);
        } else if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("oven")){
            Intent intent = new Intent(this, uiVariant6Oven.class);
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
        TextView step = findViewById(R.id.stepOutput);
        Handler h = new Handler(getMainLooper());
        h.postDelayed(() -> {
            if (!forward) {
                list.remove(tmpList.get(index));
                index--;
                speakText = tmpList.get(index);
            } else {
                list.add(s);
                step.setText(s);
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
    }

    public void runSteps(String s) {
        Handler h1 = new Handler(getMainLooper());
        TextView step = findViewById(R.id.stepOutput);
        h1.postDelayed(() -> {
            step.setText(s);
            initTTS(s);
            displayPanels();
            index++;
        }, 3000);
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
                iv1.setImageResource(R.drawable.oven_3_button);
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