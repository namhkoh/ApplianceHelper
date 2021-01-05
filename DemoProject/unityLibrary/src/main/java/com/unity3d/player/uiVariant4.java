package com.unity3d.player;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.company.MainManager;
import com.company.ResponseObject;

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

public class uiVariant4 extends Activity implements IUnityPlayerLifecycleEvents {

    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    private ImageButton SpeechBtn;
    private ListView lvSteps;
    private int index = 0;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> tmpList = new ArrayList<>();
    private ArrayAdapter adapter;
    private static String utterance;
    ArrayList<String> microwaveButtons = new ArrayList<String>(Arrays.asList("Clock", "Melt", "Start"));
    ArrayList<String> ovenButtons = new ArrayList<String>(Arrays.asList("Frozen", "Cook Time", "Oven Start", "Cancel"));

    ArrayList<String> utteranceMicrowave = new ArrayList<String>(Arrays.asList("Clock", "SOFTEN/MELT", "Start"));
    ArrayList<String> utteranceOven = new ArrayList<String>(Arrays.asList("FROZEN BAKE", "COOK TIME", "START", "CANCEL"));


    // Override this in your custom UnityPlayerActivity to tweak the command line arguments passed to the Unity Android Player
    // The command line arguments are passed as a string, separated by spaces
    // UnityPlayerActivity calls this from 'onCreate'
    // Supported: -force-gles20, -force-gles30, -force-gles31, -force-gles31aep, -force-gles32, -force-gles, -force-vulkan
    // See https://docs.unity3d.com/Manual/CommandLineArguments.html
    // @param cmdLine the current command line arguments, may be null
    // @return the modified command line string or null
    protected String updateUnityCommandLineArguments(String cmdLine) {
        return cmdLine;
    }

    // Setup activity layout
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        checkPermission();
        String cmdLine = updateUnityCommandLineArguments(getIntent().getStringExtra("unity"));
        getIntent().putExtra("unity", cmdLine);

        mUnityPlayer = new UnityPlayer(this, this);
        setContentView(mUnityPlayer);
        setContentView(R.layout.activity_ui_variant4);
        RelativeLayout unityLayout = findViewById(R.id.unityView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        unityLayout.addView(mUnityPlayer.getView(), 0, lp);
        mUnityPlayer.requestFocus();

        SpeechBtn = (ImageButton) findViewById(R.id.speechButton);

        Button exit = (Button) findViewById(R.id.exit);
        exit.setOnClickListener(v -> quitUnityActivity());
        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> enterFeedback());

        final EditText editText = findViewById(R.id.editText);
        ViewCompat.animate(SpeechBtn ).setStartDelay(3500).alpha(1).setDuration(4000).setInterpolator(new DecelerateInterpolator(1.2f)).start();
        ViewCompat.animate(editText ).setStartDelay(3500).alpha(1).setDuration(4000).setInterpolator(new DecelerateInterpolator(1.2f)).start();
        ViewCompat.animate(exit).setStartDelay(3500).alpha(1).setDuration(4000).setInterpolator(new DecelerateInterpolator(1.2f)).start();
        ViewCompat.animate(next).setStartDelay(3500).alpha(1).setDuration(4000).setInterpolator(new DecelerateInterpolator(1.2f)).start();
        // Testing params
//        ViewCompat.animate(SpeechBtn).setStartDelay(0).alpha(1).setDuration(4000).setInterpolator(new DecelerateInterpolator(1.2f)).start();
//        ViewCompat.animate(editText).setStartDelay(0).alpha(1).setDuration(4000).setInterpolator(new DecelerateInterpolator(1.2f)).start();
//        ViewCompat.animate(exit).setStartDelay(0).alpha(1).setDuration(4000).setInterpolator(new DecelerateInterpolator(1.2f)).start();
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
                    // nextStep(list.get(index));
//                    index++;
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
                    removeMicrowaveButtons();
                    removeOvenButtons();
                    adapter.notifyDataSetChanged();
                }
                for (int i = 0; i < response.getSteps().size(); ++i) {
                    String data = response.getSteps().get(i).getText();
                    tmpList.add(data);
                }
                index = 0;
                //update(tmpList.get(index), true);
                nextStep(tmpList.get(index));
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
            refreshAR(item, "Microwave");
            refreshAR(item, "Oven");
        });

        lvSteps.setAdapter(adapter);
    }

    private void enterFeedback() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        HashMap<String, String> tmpHash = getData();
        if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
            Intent intent = new Intent(this, uiVariant6Microwave.class);
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

    private void refreshAR(String item, String appliance) {
        Handler h1 = new Handler(getMainLooper());
        if (appliance == "Microwave") {
            for (int i = 0; i < utteranceMicrowave.size(); i++) {
                if (item.contains(utteranceMicrowave.get(i))) {
                    removeMicrowaveLabel(microwaveButtons.get(i));
                    int finalI = i;
                    h1.postDelayed(() -> {
                        sendMicrowaveLabel(microwaveButtons.get(finalI));
                    }, 3000);
                }
            }
        } else if (appliance == "Oven") {
            for (int i = 0; i < utteranceOven.size(); i++) {
                if (item.contains(utteranceOven.get(i))) {
                    removeOvenLabel(ovenButtons.get(i));
                    int finalI = i;
                    h1.postDelayed(() -> {
                        sendOvenLabel(ovenButtons.get(finalI));
                    }, 3000);
                }
            }
        }
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

    public void nextStep(String s) {
        TextView step = findViewById(R.id.stepOutput);
        step.setText(s);
        runAR();
        initTTS(s);
        index++;
    }

    public void quitUnityActivity() {
        Log.v("Unity", "Unity Activity Exited!");
        this.mUnityPlayer.quit();
        UnityPlayer.currentActivity.finish();
    }

    private void runAR() {
        if (utterance.contains("set") && utterance.contains("microwave") && utterance.contains("clock")) {
            if (index == 0) {
                sendMicrowaveLabel("Clock");
            } else if (index == 2) {
                sendMicrowaveLabel("Start");
            }
        } else if (utterance.contains("melt") && utterance.contains("microwave") && utterance.contains("butter")) {
            if (index == 0) {
                sendMicrowaveLabel("Melt");
            } else if (index == 3) {
                sendMicrowaveLabel("Start");
            }
        } else if (utterance.contains("cook") && utterance.contains("oven") && utterance.contains("fries")) {
            if (index == 1) {
                sendOvenLabel("Frozen");
            } else if (index == 4) {
                sendOvenLabel("Cook Time");
            } else if (index == 6) {
                sendOvenLabel("Oven Start");
            } else if (index == 7) {
                removeOvenLabel("Oven Start");
                sendOvenLabel("Cancel");
            }
        }
    }

    private void removeMicrowaveButtons() {
        for (int i = 0; i < microwaveButtons.size(); i++) {
            removeMicrowaveLabel(microwaveButtons.get(i));
        }
    }

    private void removeOvenButtons() {
        for (int i = 0; i < ovenButtons.size(); i++) {
            removeMicrowaveLabel(ovenButtons.get(i));
        }
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

    private void sendMicrowaveLabel(String label) {
        UnityPlayer.UnitySendMessage("LabelManager", "SendMicrowaveLabel", label);
    }

    private void sendOvenLabel(String label) {
        UnityPlayer.UnitySendMessage("LabelManager", "SendOvenLabel", label);
    }

    private void removeMicrowaveLabel(String removeLabel) {
        UnityPlayer.UnitySendMessage("LabelManager", "SetMicrowaveInactive", removeLabel);
    }

    private void removeOvenLabel(String removeLabel) {
        UnityPlayer.UnitySendMessage("LabelManager", "SetOvenInactive", removeLabel);
    }

    // When Unity player unloaded move task to background
    @Override
    public void onUnityPlayerUnloaded() {
        moveTaskToBack(true);
    }

    // When Unity player quited kill process
    @Override
    public void onUnityPlayerQuitted() {
        Process.killProcess(Process.myPid());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
        mUnityPlayer.newIntent(intent);
    }

    // Quit Unity
    @Override
    protected void onDestroy() {
        mUnityPlayer.destroy();
        super.onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }

    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }
}