package com.unity3d.player;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.aic.libnilu.*;

public class UserConsentActivity extends AppCompatActivity {

    private String testId;
    private EditText testIDInput;
    private Button acceptConsent;
    public static Bundle activityBundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_consent);
        testIDInput = findViewById(R.id.InputTestID);

        Log.d("Start","Hello");
        String assetName = "video_demo_data.txt";
        String filePath = Utilities.assetFilePath(getApplicationContext(), assetName);
        Log.d("1",filePath);

        String model_file = "model_tiny_ascii.pt";
        String file_name = Utilities.assetFilePath(getApplicationContext(), model_file);
        Log.d("2",file_name);

        NiluLibProcess.init(filePath,file_name);

        acceptConsent = findViewById(R.id.acceptConsent);
        acceptConsent.setOnClickListener(v -> {
            testId = testIDInput.getText().toString();
            StartScreen.activityBundle.putString("testId", testId);
            getActivity(testId);
        });

        CheckBox userCheck = findViewById(R.id.accept_box);
        userCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                acceptConsent.setEnabled(true);
            } else {
                acceptConsent.setEnabled(false);
            }
        });

        testIDInput.addTextChangedListener(submitTextWatcher);

    }

    private void getActivity(String id) {
        // verify if check box has been checked
        HashMap<String, String> tmpHash = getData();
        String value = tmpHash.get(id);
        if (value != null) {
            HashMap<String, String> tmp = getData();
            UserConsentActivity.activityBundle.putString("activity", tmp.get(id));
            Intent intent = new Intent(this, StartScreen.class);
            startActivity(intent);
        } else {
            showToast("Please enter the correct testID!");
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

            //acceptConsent.setEnabled(!userIdInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void showToast(String text) {
        Toast.makeText(UserConsentActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}