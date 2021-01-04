package com.unity3d.player;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;

public class StartScreen extends AppCompatActivity {
    private String name;
    private EditText nameInput;
    private Button submit;
    public static Bundle testBundle = new Bundle();
    public static Bundle activityBundle = new Bundle();
    public static Bundle userDataBundle = new Bundle();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        nameInput = findViewById(R.id.InputName);
        submit = findViewById(R.id.Submit);

        submit.setOnClickListener(v -> {
            name = nameInput.getText().toString();
            String str = StartScreen.activityBundle.getString("testId");
            long sessionStart = Instant.now().getEpochSecond();
            StartScreen.userDataBundle.putLong("sessionStart",sessionStart);
            StartScreen.userDataBundle.putString("name",name);


//            Bundle userData = new Bundle();
//            Intent i = new Intent();
//            userData.putString("name",name);
//            userData.putLong("sessionStart",sessionStart);
//            userData.putString("testId",str);
//            i.putExtras(userData);


            startUserActivity(str);
        });

        TextView instructionalText = findViewById(R.id.instructions);
        CharSequence instruction1 = "You will be asked to perform 3 distinct tasks on two kitchen appliances through the designated UI variant.";
        CharSequence instruction2 = "Each task will be described before the action.";
        CharSequence instruction3 = "Once you have completed the task, please move to the replication phase.";
        CharSequence instruction4 = "After the replication, head towards the survey and provide feedback and ratings.";
        CharSequence bl = BulletTextUtil.makeBulletList(35, instruction1, instruction2, instruction3, instruction4);
        instructionalText.setText(bl);

        nameInput.addTextChangedListener(submitTextWatcher);
    }

    private void enterInstructions(){
        Intent intent = new Intent(this, TaskInstructionActivity.class);
        startActivity(intent);
    }

    private void startUserActivity(String id) {
        HashMap<String, String> tmp = getData();
        StartScreen.activityBundle.putString("activity", tmp.get(id));
        Intent intent = new Intent(this, TaskInstructionActivity.class);
        startActivity(intent);
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
            Log.wtf("StartScreen", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
        return resultList;
    }

    private void findUI(String id) {
        HashMap<String, String> tmp = getData();
        try {
            switch (Objects.requireNonNull(tmp.get(id))) {
                case "uiVariant1":
                    Intent intent1 = new Intent(getApplicationContext(), uiVariant1.class);
                    startActivity(intent1);
                    break;
                case "uiVariant2":
                    Intent intent2 = new Intent(getApplicationContext(), uiVariant2.class);
                    startActivity(intent2);
                    break;
                case "uiVariant3":
                    Intent intent3 = new Intent(getApplicationContext(), uiVariant3.class);
                    startActivity(intent3);
                    break;
                case "uiVariant4":
                    Intent intent4 = new Intent(getApplicationContext(), uiVariant4.class);
                    startActivity(intent4);
                    break;
                case "UnityPlayerActivity":
                    Intent intent5 = new Intent(getApplicationContext(), UnityPlayerActivity.class);
                    startActivity(intent5);
                    break;
                case "uiVariant6":
                    Intent intent6 = new Intent(getApplicationContext(), uiVariant6Microwave.class);
                    startActivity(intent6);
                    break;
            }
        } catch (NullPointerException e) {
            //throw new RuntimeException("Please enter the correct userID!", e);
            Intent error = new Intent(getApplicationContext(), StartScreen.class);
            startActivity(error);
            showToast("Please enter the correct userID!");
        }

    }

    private TextWatcher submitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userNameInput = nameInput.getText().toString().trim();

            submit.setEnabled(!userNameInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public String createRandomString(int n) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        StartScreen.testBundle.putString("userID", sb.toString());
        //userText.setText("UserID: " + sb.toString());
        return sb.toString();
    }

    public void openUIMenu() {
        Intent intent = new Intent(this, uiMenu.class);
        startActivity(intent);
    }

    private void showToast(String text) {
        Toast.makeText(StartScreen.this, text, Toast.LENGTH_SHORT).show();
    }
}