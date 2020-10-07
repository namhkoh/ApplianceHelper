package com.unity3d.player;

import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.$Gson$Preconditions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;

import javax.security.auth.login.LoginException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UserSurveyActivity extends AppCompatActivity {
    RatingBar nps;
    RatingBar css;
    private EditText additionalFeedback;
    private String additionalFeedbackString;
    public static Bundle activityBundle = new Bundle();
//    ApiService apiService;

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
            String userConsent = "true";
            int buttonsCorrect = 10;
            int buttonsIncorrect = 2;

//            JSONArray feedback = new JSONArray();
//            JSONObject feedbackScores = new JSONObject();
//            try {
//                feedbackScores.put("Net Score", netScore);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            try {
//                feedbackScores.put("Customer Score", customerScore);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            feedback.put(feedbackScores);
//            feedback.put(feedbackScores);

            User user = new User(
                    "1",
                    "jeff",
                    0,
                    0,
                    000000,
                    0000000,
                    000000,
                    "true",
                    "feedback"
            );



            sendNetworkRequest(user);
        });

        additionalFeedback.addTextChangedListener(submitTextWatcher);
    }


    private void sendNetworkRequest(User user) {
//        String awsUrl = "http://namho@ec2-18-217-40-32.us-east-2.compute.amazonaws.com:3030/api/v1/todos/";
//        String localUrl = "http://localhost:3030/api/v1/todos/";
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl("http://namho@ec2-18-217-40-32.us-east-2.compute.amazonaws.com:3030/api/v1/todos/")
//                .addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = builder.build();
//        TestClient client = retrofit.create(TestClient.class);

        /**
         * Retrofit retrofit = new Retrofit.Builder()
         *                 .baseUrl("http://namho@ec2-18-217-40-32.us-east-2.compute.amazonaws.com:3030/api/v1/todos/")
         *                 .addConverterFactory(GsonConverterFactory.create())
         *                 .build();
         *         TestClient client = retrofit.create(TestClient.class);
         */

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://namho@ec2-18-217-40-32.us-east-2.compute.amazonaws.com:3030/api/v1/todos/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TestClient client = retrofit.create(TestClient.class);

        Call<User> call = client.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                //Toast.makeText(UserSurveyActivity.this, "Success!" + response.body().getTestId(), Toast.LENGTH_SHORT).show();
                Toast.makeText(UserSurveyActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserSurveyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                t.getCause();
                t.printStackTrace();
            }
        });
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