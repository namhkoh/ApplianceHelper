package com.unity3d.player;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.StateSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.internal.$Gson$Preconditions;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;

public class UserSurveyActivity extends AppCompatActivity {
    RatingBar nps;
    RatingBar css;
    private EditText additionalFeedback;
    private String additionalFeedbackString;
    public static Bundle activityBundle = new Bundle();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_survey);
        additionalFeedback = findViewById(R.id.userFeedback);
        nps = findViewById(R.id.net_promoter_score);
        css = findViewById(R.id.customer_satisfaction_score);
        Button submit = findViewById(R.id.submitButton);
        submit.findViewById(R.id.submitButton);

        submit.setOnClickListener(v -> {
            String netScore = String.valueOf(nps.getRating());
            String customerScore = String.valueOf(css.getRating());
            showToast("Net score " + netScore);
            showToast("Customer score " + customerScore);
            // Store additional feedback here
            Log.e("Additional User Feedback", additionalFeedback.getText().toString());

            // preparing the data before creating JSON file.

            String testId = (String) StartScreen.userDataBundle.get("testId");
            Log.e("testId", testId);

            long sessionStart = StartScreen.userDataBundle.getLong("sessionStart");
            Log.e("sessionStart", String.valueOf(sessionStart));

            String name = StartScreen.userDataBundle.getString("name");
            Log.e("name", name);

            long sessionEnd = Instant.now().getEpochSecond();
            Log.e("sessionEnd", String.valueOf(sessionEnd));

            int totalTime = 000000;
            Boolean userConsent = true;
            int buttonsCorrect = 10;
            int buttonsIncorrect = 2;
            JSONObject feedback = new JSONObject();
            try {
                feedback.put("netScore", netScore);
                feedback.put("CustomerScore", customerScore);
            } catch (JSONException e) {
                e.printStackTrace();
            }


//            long sessionEnd = Instant.now().getEpochSecond();
//            final Bundle parameters = this.getIntent().getExtras();
//            String test = parameters.getString("testID");
//            Log.e("test",test);

        });

        additionalFeedback.addTextChangedListener(submitTextWatcher);
    }

    private TextWatcher submitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userIdInput = additionalFeedback.getText().toString();
//            acceptConsent.setEnabled(!userIdInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void showToast(String inputText) {
        Toast.makeText(this, inputText + " Star", Toast.LENGTH_SHORT).show();
    }
}