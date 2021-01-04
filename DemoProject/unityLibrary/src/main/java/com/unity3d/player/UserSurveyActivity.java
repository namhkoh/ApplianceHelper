package com.unity3d.player;

import android.content.Intent;
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
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashMap;

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
    APIInterface apiInterface;
    HashMap<String, String> questionList = new HashMap<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("ENTERED","UserSurveyActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_survey);
        apiInterface = APIClient.getClient().create(APIInterface.class);
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
            String sessionStartVal = String.valueOf(sessionStart);
            Log.e("sessionStart", sessionStartVal);

            String name = StartScreen.userDataBundle.getString("name");
            Log.e("name", name);

            long sessionEnd = Instant.now().getEpochSecond();
            String sessionEndVal = String.valueOf(sessionEnd);
            Log.e("sessionEnd", sessionEndVal);

            long totalTime = (sessionEnd - sessionStart);
            String totalTimeVal = String.valueOf(totalTime);

            String userConsent = "true";

            int buttonsCorrect = 10;
            int buttonsIncorrect = 2;

            //questionList = (HashMap<String, String>) uiVariant1.userQuestions.getSerializable("questions");
            questionList.put("hello","world");

            Gson gson = new Gson();
            JSONObject feedbackObject = new JSONObject();
            try {
                feedbackObject.put("NetScore", netScore);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                feedbackObject.put("CustomerScore", customerScore);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String feedbackValue = gson.toJson(feedbackObject);

            User user = new User(
                    testId,
                    name,
                    10,
                    9,
                    sessionStartVal,
                    sessionEndVal,
                    totalTimeVal,
                    userConsent,
                    feedbackValue,
                    questionList
            );
            Call<User> call1 = apiInterface.createUser(user);
            call1.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        Log.e("response", String.valueOf(response.code()));
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("Error", t.getMessage());
                }
            });
        });

        additionalFeedback.addTextChangedListener(submitTextWatcher);

//        Intent intent = new Intent(this, UserSurveyActivity.class);
//        startActivity(intent);
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