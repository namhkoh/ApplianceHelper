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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.company.MainManager;
import com.company.ResponseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class uiVariant2 extends AppCompatActivity {
    private ImageButton SpeechBtn;
    int testingVal = 5;
    // Set to 0 for testing
    int delayVal = 3500;
    private ListView lvSteps;
    private int index = 0;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> tmpList = new ArrayList<>();
    private ArrayAdapter adapter;
    private static String utterance;
    ArrayList<String> microwaveButtons = new ArrayList<String>(Arrays.asList("Clock","Melt","Start"));
    ArrayList<String> ovenButtons = new ArrayList<String>(Arrays.asList("Frozen","Cook Time","Oven Start","Cancel"));

    ArrayList<String> utteranceMicrowave = new ArrayList<String>(Arrays.asList("Clock","SOFTEN/MELT","Start"));
    ArrayList<String> utteranceOven = new ArrayList<String>(Arrays.asList("FROZEN BAKE","COOK TIME","START","CANCEL"));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpeechBtn = (ImageButton) findViewById(R.id.speechButton);
        final EditText editText = findViewById(R.id.editText);
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
                update(tmpList.get(index), true);
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
        },3000);
    }

    private void initTTS(String selectedText){
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