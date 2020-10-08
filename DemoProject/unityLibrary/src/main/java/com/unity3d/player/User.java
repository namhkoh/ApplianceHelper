package com.unity3d.player;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("testId")
    private Integer testId;
    @SerializedName("name")
    private String name;
    @SerializedName("buttonsCorrect")
    private Integer buttonsCorrect;
    @SerializedName("buttonsIncorrect")
    private Integer buttonsIncorrect;
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

    public User(Integer testId, String name, Integer buttonsCorrect, Integer buttonsIncorrect, String startSession, String endSession, String totalTime, String userConsent, String feedback) {
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
}
