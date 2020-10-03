package com.unity3d.player;

import com.google.gson.JsonObject;

import org.json.JSONObject;

public class User {

    private String testId;
    private String name;
    private int buttonsCorrect;
    private int buttonsIncorrect;
    private long startSession;
    private long endSession;
    private int totalTime;
    private Boolean userConsent;
    private JSONObject feedback;

    public User(String testId, String name, int buttonsCorrect, int buttonsIncorrect, long startSession, long endSession, int totalTime, Boolean userConsent, JSONObject feedback) {
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
}
