package com.unity3d.player;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import androidx.constraintlayout.motion.widget.Debug;
import androidx.core.content.ContextCompat;

import com.aic.libnilu.nlu.MainManager;
import com.aic.libnilu.nlu.ResponseObject;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * this class will contain the microwave panel with the speech button.
 */

public class uiVariant4WithMicrowave extends AppCompatActivity implements View.OnTouchListener {

    private float dX;
    private float dY;
    private int lastAction;
    private ImageButton SpeechBtn;
    private ListView lvSteps;
    private int index = 0;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> tmpList = new ArrayList<>();
    ArrayList<String> buttonList;
    private ArrayAdapter adapter;
    private static String utterance;
    private FirebaseAnalytics mFirebaseAnalytics;
    HashMap<String, String> intentList;
    private Button next;
    private boolean sucess = false;
    private BottomNavigationView BottomNav;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_variant4_with_microwave);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //View draggableView = findViewById(R.id.speechButton);
        //draggableView.setOnTouchListener(this);
        SpeechBtn = findViewById(R.id.speechButton);

        final EditText editText = findViewById(R.id.editText);
        next = findViewById(R.id.nextActivity);
        next.setEnabled(false);
        next.setOnClickListener(v -> {
            enterFeedback();
        });

//        Button task = findViewById(R.id.task);
//        task.setOnClickListener(v -> {
//            task();
//        });

        BottomNav = findViewById(R.id.BottomNavigation);
        BottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nextActivity) {
                System.out.println("1");
                return true;
            } else if (itemId == R.id.speechButton) {
                System.out.println("2");
                return true;
            } else if (itemId == R.id.task) {
                System.out.println("3");
                return true;
            }
            return false;
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

                System.out.println(tmpList.size());

                //Some sort of error happened in the NLU part
                if (response.getDialog_command().equals("no_match")) {

                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        //adapter.notifyDataSetChanged();
                    }

                    list.add("No Match");
                    list.add("Try again by pressing the red mike button");
                    initTTS("No Match");


                } else if (!response.getIntent().equals(intentList.get(incoming_indexString))) {

                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        //adapter.notifyDataSetChanged();
                    }

                    list.add("Wrong Intent. " + "The current intent is " + response.getIntent());
                    list.add("Try again by pressing the red mike button");
                    initTTS("Wrong Intent");


                } else if (!Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains(response.getAppliance_name().toLowerCase())) {

                    if (list != null) {
                        list.clear();
                        tmpList.clear();
                        //adapter.notifyDataSetChanged();
                    }

                    list.add("Wrong appliance. " + "The current appliance is " + response.getAppliance_name());
                    list.add("Try again by pressing the red mike button");
                    initTTS("Wrong appliance");

                } else {
                    sucess = true;
                    if (list != null) {
                        list.clear();
                        tmpList.clear();

                        //adapter.notifyDataSetChanged();
                    }

                    buttonList = new ArrayList<>();

                    for (int i = 0; i < response.getSteps().size(); ++i) {
                        String data = response.getSteps().get(i).getText();
                        String button = response.getSteps().get(i).getButton_name();
                        buttonList.add(button);
                        list.add(data);
                        showToast(data);
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
//        SpeechBtn.setOnTouchListener((View v, MotionEvent event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_UP:
//                    mSpeechRecognizer.stopListening();
//                    editText.setHint("You will see input here");
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    lastAction = MotionEvent.ACTION_MOVE;
//                    v.setX(event.getRawX() + dX);
//                    v.setY(event.getRawY() + dY);
//                    break;
//                case MotionEvent.ACTION_DOWN:
//                    lastAction = MotionEvent.ACTION_DOWN;
//                    dX = v.getX() - event.getRawX();
//                    dY = v.getY() - event.getRawY();
//                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
//                    editText.setText("");
//                    editText.setHint("Listening...");
//                    break;
//            }
//            return false;
//        });
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
                        if (index < tmpList.size()) {
                            update(tmpList.get(index), true);
                        }
                        Log.d("Speak", String.valueOf(index));

                        index++;

                        if (index == list.size() & sucess == true) {
                            Log.d("Here we go", "Done");
                            SpeechBtn.setEnabled(false);

                            Handler h = new Handler(getMainLooper());

                            h.postDelayed(() -> {

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //lvSteps.setAdapter(null);
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


        // TOAST it up
//        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list) {
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                TextView item = (TextView) super.getView(position, convertView, parent);
//                item.setTextColor(Color.parseColor("#000000"));
//                item.setTypeface(item.getTypeface(), Typeface.BOLD);
//                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
//                if (sucess) {
//                    item.setBackgroundColor(Color.parseColor("#f2f2f2"));
//                } else {
//                    item.setBackgroundColor(Color.parseColor("#ff0033"));
//                }
//                item.setAlpha(0.7f);
//                return item;
//            }
//        };
//        lvSteps = findViewById(R.id.lv_steps);
//        lvSteps.setOnItemClickListener((parent, view, position, id) -> {
//            String item = (String) adapter.getItem(position);
//            initTTS(item);
//            for (int i = 0; i < lvSteps.getChildCount(); i++) {
//                if (position == i) {
//                    lvSteps.getChildAt(i).setBackgroundColor(Color.parseColor("#8c8c8c"));
//                } else {
//                    lvSteps.getChildAt(i).setBackgroundColor(Color.parseColor("#f2f2f2"));
//                }
//            }
//        });
//        lvSteps.setAdapter(adapter);
        // toast it up
    }

    private void showToast(String inputText) {
        Toast.makeText(this, inputText, Toast.LENGTH_LONG).show();
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

    private void task() {
        HashMap<String, String> tmpHash = getData();
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
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

    private void initTTS(String selectedText) {
        //textToSpeech.setSpeechRate(testingVal);
        int speechStatus = textToSpeech.speak(selectedText, TextToSpeech.QUEUE_ADD, null, "1");
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}