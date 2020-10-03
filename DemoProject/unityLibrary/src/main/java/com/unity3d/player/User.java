package com.unity3d.player;

import com.google.gson.JsonObject;

public class User {

    private String testId;
    private String name;
    private int buttonsCorrect;
    private int startSession;
    private int endSession;
    private int totalTime;
    private Boolean userConsent;
    private JsonObject feedback;

    public User(String testId, String name, int buttonsCorrect, int startSession, int endSession, int totalTime, Boolean userConsent, JsonObject feedback) {
        this.testId = testId;
        this.name = name;
        this.buttonsCorrect = buttonsCorrect;
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
