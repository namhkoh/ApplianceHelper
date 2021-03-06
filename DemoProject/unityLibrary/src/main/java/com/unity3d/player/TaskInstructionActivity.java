package com.unity3d.player;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;

public class TaskInstructionActivity extends AppCompatActivity {
    public static Bundle indexBundle = new Bundle();
    public static int index = 0;
    /**
     * Proper data collection pipeline
     */
    public static Bundle userQuestions = new Bundle();
    //HashMap<String, String> tmpQuestions;
    Multimap<String, String> tmpQuestions = ArrayListMultimap.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_instruction);

        tmpQuestions = (Multimap<String, String>) getIntent().getSerializableExtra("questions");
        Log.e("TASK_INSTRUCTION_ONCREATE", String.valueOf(tmpQuestions));

        Log.e("onCreate Index", String.valueOf(index));
        Button Enter = findViewById(R.id.Enter);
        Enter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                enterActivity();
            }
        });
        setInstructionText(index);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterActivity() {
        String str = StartScreen.activityBundle.getString("activity");
        try {
            switch (str) {
                case "uiVariant1":
                    Log.e("taskInstruction", String.valueOf(index));
                    tmpQuestions = (Multimap<String, String>) getIntent().getSerializableExtra("questions");
                    System.out.println(Instant.now().getEpochSecond());
                    tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "Entering uiVariant1");
                    Log.e("TASK_INSTRUCTION_ENTER_ACTIVITY", String.valueOf(tmpQuestions));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    index++;
                    Intent intent1 = new Intent(getApplicationContext(), uiVariant1.class);
                    intent1.putExtra("questions", (Serializable) tmpQuestions);
                    startActivity(intent1);
                    break;
                case "uiVariant2":
                    Log.e("taskInstruction", String.valueOf(index));
                    tmpQuestions = (Multimap<String, String>) getIntent().getSerializableExtra("questions");
                    System.out.println(Instant.now().getEpochSecond());
                    tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "Entering uiVariant2");
                    Log.e("TASK_INSTRUCTION_ENTER_ACTIVITY", String.valueOf(tmpQuestions));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    index++;
                    Intent intent2 = new Intent(getApplicationContext(), uiVariant2.class);
                    intent2.putExtra("questions", (Serializable) tmpQuestions);
                    startActivity(intent2);
                    break;
                case "uiVariant3":
                    Log.e("taskInstruction", String.valueOf(index));
                    tmpQuestions = (Multimap<String, String>) getIntent().getSerializableExtra("questions");
                    System.out.println(Instant.now().getEpochSecond());
                    tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "Entering uiVariant3");
                    Log.e("TASK_INSTRUCTION_ENTER_ACTIVITY", String.valueOf(tmpQuestions));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    index++;
                    Intent intent3 = new Intent(getApplicationContext(), uiVariant3.class);
                    intent3.putExtra("questions", (Serializable) tmpQuestions);
                    startActivity(intent3);
                    break;
                /**
                 * if user inputs User4, they will be directed to uiVariant4.
                 */
                case "uiVariant4":
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    String incoming_indexString = String.valueOf(index);
                    HashMap<String, String> tmpHash = getData();
                    tmpQuestions = (Multimap<String, String>) getIntent().getSerializableExtra("questions");
                    System.out.println(Instant.now().getEpochSecond());
                    tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "Entering uiVariant4");
                    Log.e("TASK_INSTRUCTION_ENTER_ACTIVITY", String.valueOf(tmpQuestions));
                    index++;
                    if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
                        Intent intent4 = new Intent(getApplicationContext(), uiVariant4WithMicrowave.class);
                        intent4.putExtra("questions", (Serializable) tmpQuestions);
                        startActivity(intent4);
                    } else if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("oven")) {
                        Intent intent = new Intent(this, uiVariant4WithOven.class);
                        intent.putExtra("questions", (Serializable) tmpQuestions);
                        startActivity(intent);
                    } else {
                        return;
                    }
                    break;
                case "uiVariant5":
                    Log.e("taskInstruction", String.valueOf(index));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    String incoming_indexString5 = String.valueOf(index);
                    HashMap<String, String> tmpHash5 = getData();
                    tmpQuestions = (Multimap<String, String>) getIntent().getSerializableExtra("questions");
                    System.out.println(Instant.now().getEpochSecond());
                    tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "Entering uiVariant5");
                    Log.e("TASK_INSTRUCTION_ENTER_ACTIVITY", String.valueOf(tmpQuestions));
                    index++;
                    if (Objects.requireNonNull(tmpHash5.get(incoming_indexString5)).toLowerCase().contains("microwave")) {
                        Intent intent4 = new Intent(getApplicationContext(), uiVariant5WithMicrowave.class);
                        intent4.putExtra("questions", (Serializable) tmpQuestions);
                        startActivity(intent4);
                    } else if (Objects.requireNonNull(tmpHash5.get(incoming_indexString5)).toLowerCase().contains("oven")) {
                        Intent intent = new Intent(this, uiVariant5WithOven.class);
                        intent.putExtra("questions", (Serializable) tmpQuestions);
                        startActivity(intent);
                    } else {
                        return;
                    }
                    break;
                case "uiVariant6":
                    Log.e("taskInstruction", String.valueOf(index));
                    TaskInstructionActivity.indexBundle.putInt("index", index);
                    String incoming_indexString6 = String.valueOf(index);
                    HashMap<String, String> tmpHash6 = getData();
                    tmpQuestions = (Multimap<String, String>) getIntent().getSerializableExtra("questions");
                    System.out.println(Instant.now().getEpochSecond());
                    tmpQuestions.put(String.valueOf(Instant.now().getEpochSecond()), "Entering BaseLineUI");
                    Log.e("TASK_INSTRUCTION_ENTER_ACTIVITY", String.valueOf(tmpQuestions));
                    index++;
                    if (Objects.requireNonNull(tmpHash6.get(incoming_indexString6)).toLowerCase().contains("microwave")) {
                        Intent baseLineIntent1 = new Intent(getApplicationContext(), BaseLineActivityMicrowave.class);
                        baseLineIntent1.putExtra("questions", (Serializable) tmpQuestions);
                        startActivity(baseLineIntent1);
                    } else if (Objects.requireNonNull(tmpHash6.get(incoming_indexString6)).toLowerCase().contains("oven")) {
                        Intent baseLineIntent2 = new Intent(this, BaseLineActivityOven.class);
                        baseLineIntent2.putExtra("questions", (Serializable) tmpQuestions);
                        startActivity(baseLineIntent2);
                    } else {
                        return;
                    }
                    break;
            }
        } catch (NullPointerException e) {
            //throw new RuntimeException("Please enter the correct userID!", e);
            Intent error = new Intent(getApplicationContext(), StartScreen.class);
            startActivity(error);
            showToast("Error");
        }
    }

    private void setInstructionText(int indexValue) {
        String uiVariant = StartScreen.activityBundle.getString("activity");
        String indexValueString = String.valueOf(indexValue);
        TextView task1 = findViewById(R.id.Task);
        HashMap<String, String> tmp = getData();
        task1.setText(tmp.get(indexValueString));
    }

    private void enterApplianceUI() {
        int incoming_index = TaskInstructionActivity.indexBundle.getInt("index");
        String incoming_indexString = String.valueOf(incoming_index);
        HashMap<String, String> tmpHash = getData();
        if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("microwave")) {
            Intent intent = new Intent(this, uiVariant6Microwave.class);
            startActivity(intent);
        } else if (Objects.requireNonNull(tmpHash.get(incoming_indexString)).toLowerCase().contains("oven")) {
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

    private void showToast(String text) {
        Toast.makeText(TaskInstructionActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}