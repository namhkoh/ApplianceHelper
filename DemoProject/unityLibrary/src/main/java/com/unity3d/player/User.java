package com.unity3d.player;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    @SerializedName("testId")
    private String testId;
    @SerializedName("name")
    private String name;
    @SerializedName("startSession")
    private String startSession;
    @SerializedName("endSession")
    private String endSession;
    @SerializedName("totalTime")
    private String totalTime;
    @SerializedName("userConsent")
    private String userConsent;
    @SerializedName("feedback")
    private String feedback;
    @SerializedName("userSequence")
    private HashMap<String, String> userSequence;

    public User(String testId, String name, String startSession, String endSession, String totalTime, String userConsent, String feedback, HashMap<String, String> userSequence) {
        this.testId = testId;
        this.name = name;
        this.startSession = startSession;
        this.endSession = endSession;
        this.totalTime = totalTime;
        this.userConsent = userConsent;
        this.feedback = feedback;
        this.userSequence = userSequence;
    }
}
