package com.unity3d.player;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class uiMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView card_view_1 = (CardView) findViewById(R.id.ui_card_1);
        card_view_1.setOnClickListener(v -> openUIVariant1());

        CardView card_view_2 = (CardView) findViewById(R.id.ui_card_2);
        card_view_2.setOnClickListener(v -> openUIVariant2());

        CardView card_view_3 = (CardView) findViewById(R.id.ui_card_3);
        card_view_3.setOnClickListener(v -> openUIVariant3());

        CardView card_view_4 = (CardView) findViewById(R.id.ui_card_4);
        card_view_4.setOnClickListener(v -> openUIVariant4());

        CardView card_view_5 = (CardView) findViewById(R.id.ui_card_5);
        card_view_5.setOnClickListener(v -> openUIVariant5());

        CardView card_view_6 = (CardView) findViewById(R.id.ui_card_6);
        card_view_6.setOnClickListener(v -> openUIVariant6());

        TextView userID = (TextView) findViewById(R.id.userID);
        String str = StartScreen.testBundle.getString("userID");
        userID.setText("UserID: " + str);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        TextView userID = findViewById(R.id.userID);
        String str = StartScreen.testBundle.getString("userID");
        userID.setText("UserID: " + str);
        savedInstanceState.putString("UserID", str);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TextView userID = (TextView) findViewById(R.id.userID);
        String myString = savedInstanceState.getString("UserID");
        userID.setText("UserID: " + myString);
    }

    public void setCardColour(CardView cv){
        cv.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
    }

    public void openUIVariant1(){
        CardView card_view_1 = (CardView) findViewById(R.id.ui_card_1);
        Intent intent = new Intent(this,uiVariant1.class);
        setCardColour(card_view_1);
        startActivity(intent);
    }

    public void openUIVariant2() {
        CardView card_view_2 = (CardView) findViewById(R.id.ui_card_2);
        Intent intent = new Intent(this, uiVariant2.class);
        setCardColour(card_view_2);
        startActivity(intent);
    }

    public void openUIVariant3() {
        CardView card_view_3 = (CardView) findViewById(R.id.ui_card_3);
        Intent intent = new Intent(this, uiVariant3.class);
        setCardColour(card_view_3);
        startActivity(intent);
    }
    public void openUIVariant4(){
        CardView card_view_4 = (CardView) findViewById(R.id.ui_card_4);
        Intent intent = new Intent(this, uiVariant4.class);
        setCardColour(card_view_4);
        startActivity(intent);
    }
    public void openUIVariant5(){
        CardView card_view_5 = (CardView) findViewById(R.id.ui_card_5);
        setCardColour(card_view_5);
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        startActivity(intent);
    }

    public void openUIVariant6() {
        CardView card_view_6 = (CardView) findViewById(R.id.ui_card_6);
        setCardColour(card_view_6);
        Intent intent = new Intent(this, uiVariant6.class);
        startActivity(intent);
    }
}