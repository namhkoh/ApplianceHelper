package com.unity3d.player;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aic.libnilu.NiluLibProcess;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class UserConsentActivity extends AppCompatActivity {

    private String testId;
    private EditText testIDInput;
    private Button acceptConsent;
    public static Bundle activityBundle = new Bundle();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_consent);
        testIDInput = findViewById(R.id.InputTestID);

        /**
         * Testing node server side data transfer
         */
        Button serverBtn = findViewById(R.id.server);
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://127.0.0.1/postdata"; // your URL
        queue.start();
        serverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String,String>();
                params.put("data", "test string from android"); // the entered data as the body.

                JsonObjectRequest jsObjRequest = new
                        JsonObjectRequest(Request.Method.POST,
                        url,
                        new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.e("response",response.getString("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error","That didn't work!");
                    }
                });
                queue.add(jsObjRequest);
            }
        });

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
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, testId);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "testID");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "entered_test_id");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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