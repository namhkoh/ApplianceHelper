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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.$Gson$Preconditions;

import org.checkerframework.common.reflection.qual.GetConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    //HashMap<String, String> questionList;
    Multimap<String, String> questionList = ArrayListMultimap.create();
    Multimap<String, String> sorted = MultimapBuilder.treeKeys(Ordering.arbitrary()).arrayListValues().build();
    Intent intent;

    public static Bundle userQuestions = new Bundle();
    Boolean is_first;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("ENTERED", "UserSurveyActivity");
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
//            showToast("Net score " + netScore);
//            showToast("Customer score " + customerScore);
            // Store additional feedback here
            Log.e("Additional User Feedback", additionalFeedback.getText().toString());

            // Test id
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

            // Extract values from each uiVariant
            String activityType = StartScreen.activityBundle.getString("activity");
            switch (activityType) {
                case "uiVariant1":
                    Log.e("Data extracted! ", activityType + " user data collected");
                    intent = getIntent();
                    questionList = (Multimap<String, String>) intent.getSerializableExtra("questions");
//                    sorted = (Multimap<String, String>) intent.getSerializableExtra("questions");
                    Log.e("uiVariant1 ", String.valueOf(questionList));
                    Log.e("uiVariant1_length ", String.valueOf(questionList.size()));
                    break;
                case "uiVariant2":
                    Log.e("Data extracted! ", activityType + " user data collected");
                    intent = getIntent();
                    questionList = (Multimap<String, String>) intent.getSerializableExtra("questions");
                    Log.e("uiVariant2 ", String.valueOf(questionList));
                    Log.e("uiVariant2_length ", String.valueOf(questionList.size()));
                    break;
                case "uiVariant3":
                    Log.e("Data extracted! ", activityType + " user data collected");
                    questionList = (Multimap<String, String>) uiVariant3.userQuestions.getSerializable("questions");
                    Log.e("uiVariant3 ", String.valueOf(questionList));
                    Log.e("uiVariant3_length ", String.valueOf(questionList.size()));
                    break;
                case "uiVariant4":
                    Log.e("Data extracted! ", activityType + " user data collected");
                    intent = getIntent();
                    questionList = (Multimap<String, String>) intent.getSerializableExtra("questions");
                    Log.e("uiVariant4", String.valueOf(questionList));
                    break;
                case "uiVariant5":
                    Log.e("Data extracted! ", activityType + " user data collected");
                    intent = getIntent();
                    questionList = (Multimap<String, String>) intent.getSerializableExtra("questions");
                    Log.e("uiVariant5", String.valueOf(questionList));
                    break;
                case "uiVariant6":
                    Log.e("Data extracted! ", activityType + " user data collected");
                    intent = getIntent();
                    questionList = (Multimap<String, String>) intent.getSerializableExtra("questions");
                    Log.e("BaseLineUI", String.valueOf(questionList));
                    break;
            }


            String moreFeedback = additionalFeedback.getText().toString();

            Gson gson = new Gson();
            JSONObject feedbackObject = new JSONObject();
            JSONObject userObject = new JSONObject();
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
            try {
                feedbackObject.put("AdditionalFeedback", moreFeedback);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                userObject.put("userSequence",  questionList.asMap());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String feedbackValue = gson.toJson(feedbackObject);
            String userValue = gson.toJson(userObject);

            JsonParser jp = new JsonParser();
            JsonObject jo = (JsonObject)jp.parse(userObject.toString());


            User user = new User(
                    testId,
                    name,
                    sessionStartVal,
                    sessionEndVal,
                    totalTimeVal,
                    userConsent,
                    feedbackValue,
                    jo
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

            Intent end = new Intent(getApplicationContext(), EndActivityScreen.class);
            startActivity(end);
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