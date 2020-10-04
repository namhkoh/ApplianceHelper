package com.unity3d.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {

    private String testId;
    private String name;
    private int buttonsCorrect;
    private int buttonsIncorrect;
    private long startSession;
    private long endSession;
    private int totalTime;
    private String userConsent;
    private JSONArray feedback;

    public User(String testId, String name, int buttonsCorrect, int buttonsIncorrect, long startSession, long endSession, int totalTime, String userConsent, JSONArray feedback) {
        this.testId = testId;
        this.name = name;
        this.buttonsCorrect = buttonsCorrect;
        this.buttonsIncorrect = buttonsIncorrect;
        this.startSession = startSession;
        this.endSession = endSession;
        this.totalTime = totalTime;
        this.userConsent = userConsent;
        this.feedback = feedback;
    }

    public String getTestId() {
        return testId;
    }

    public String getName() {
        return name;
    }

    public int getButtonsCorrect() {
        return buttonsCorrect;
    }

    public int getButtonsIncorrect() {
        return buttonsIncorrect;
    }

    public long getStartSession() {
        return startSession;
    }

    public long getEndSession() {
        return endSession;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public String getUserConsent() {
        return userConsent;
    }

    public JSONArray getFeedback() {
        return feedback;
    }
}
