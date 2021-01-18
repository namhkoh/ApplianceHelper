package com.unity3d.player;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.aic.libnilu.NiluLibProcess;
import com.google.firebase.analytics.FirebaseAnalytics;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class UserConsentActivity extends AppCompatActivity {

    private String testId;
    private EditText testIDInput;
    private Button acceptConsent;
    public static Bundle activityBundle = new Bundle();
    public static Bundle userDataBundle = new Bundle();
    private FirebaseAnalytics mFirebaseAnalytics;

    // Data collection pipeline
    public static Bundle userQuestions = new Bundle();
    private HashMap<String, String> inputQuestions = new HashMap<>();
    boolean is_first = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_consent);
        testIDInput = findViewById(R.id.InputTestID);

        Log.d("Start", "Hello");
        String assetName = "video_demo_data23.txt";
        String filePath = Utilities.assetFilePath(getApplicationContext(), assetName);
        Log.d("1", filePath);

        String model_file = "model_tiny_9_2.pt";
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
        //Log.d("3",vocab_class_path);

        String vocab_slot_file = "vocab1.tag";
        String vocab_slot_path = Utilities.assetFilePath(getApplicationContext(), vocab_slot_file);
        Log.d("3", vocab_slot_path);

        // Commented for run project
        //NiluLibProcess.init(filePath,file_name, vocab_path, config_path, vocab_class_path, vocab_slot_path);


        // Commented due to error.
        //NiluLibProcess.init(filePath,file_name);

        acceptConsent = findViewById(R.id.acceptConsent);
        acceptConsent.setOnClickListener(v -> {
            testId = testIDInput.getText().toString();
            StartScreen.activityBundle.putString("testId", testId);
            StartScreen.userDataBundle.putString("testId", testId);
            getActivity(testId);
        });

        CheckBox userCheck = findViewById(R.id.accept_box);
        userCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // User activity press check
                inputQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "User checks box");
                userQuestions.putSerializable("questions", inputQuestions);
                acceptConsent.setEnabled(true);
            } else {
                inputQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "User un-checks box");
                userQuestions.putSerializable("questions", inputQuestions);
                acceptConsent.setEnabled(false);
            }
        });

//        if (UserConsentActivity.userQuestions.containsKey("Is First")) {
//            Log.e("Is First", String.valueOf(UserConsentActivity.userQuestions.getBoolean("Is First")));
//            load_bundle();
//            Log.d("Load Bundle", "Restored");
//        } else {
//            System.out.println("new hashmap!");
//            is_first = true;
//            inputQuestions = new HashMap<>();
//        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getActivity(String id) {
        // verify if check box has been checked
        HashMap<String, String> tmpHash = getData();
        String value = tmpHash.get(id);
        if (value != null) {
            HashMap<String, String> tmp = getData();
            UserConsentActivity.activityBundle.putString("activity", tmp.get(id));
            Intent intent = new Intent(this, StartScreen.class);
            inputQuestions.put(String.valueOf(Instant.now().getEpochSecond())," " + testId + "consent accepted");
            userQuestions.putSerializable("questions", inputQuestions);
            startActivity(intent);
        } else {
            showToast("Please enter the correct testID!");
            inputQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "Wrong testID entered");
            userQuestions.putSerializable("questions", inputQuestions);
            Intent intent = new Intent(this, UserConsentActivity.class);
            startActivity(intent);
        }
    }


    private HashMap<String, String> getData() {
        InputStream ls = getResources().openRawResource(R.raw.file1);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ls, StandardCharsets.UTF_8));
        String line = "";
        HashMap<String, String> resultList = new HashMap<String, String>();
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                resultList.put(tokens[0], tokens[1]);
            }
        } catch (IOException e) {
            Log.wtf("UserConsentActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
        return resultList;
    }

    private TextWatcher submitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userIdInput = testIDInput.getText().toString().trim();
//            System.out.println(userIdInput);
            //acceptConsent.setEnabled(!userIdInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
//            System.out.println(s);
        }
    };

    private void showToast(String text) {
        Toast.makeText(UserConsentActivity.this, text, Toast.LENGTH_SHORT).show();
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void load_bundle() {
//        is_first = UserConsentActivity.userQuestions.getBoolean("Is First");
//        inputQuestions = (HashMap<String, String>) uiVariant1.userQuestions.getSerializable("questions");
//    }

}