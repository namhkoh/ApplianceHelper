package com.unity3d.player;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import java.util.ArrayList;


public class Pop extends Activity {

    ArrayList<String> instructionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .5), (int) (height * .5));

        instructionList = (ArrayList<String>) getIntent().getSerializableExtra("instructions");
        for (int i = 0; i < instructionList.size(); i++) {
            TextView tv = findViewById(R.id.sampleText);
            tv.setText(instructionList.get(i));
        }
    }
}
