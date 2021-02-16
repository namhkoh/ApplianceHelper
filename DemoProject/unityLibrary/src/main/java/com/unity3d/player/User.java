package com.unity3d.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    private JsonObject userSequence;
    //private HashMultimap<String, String> userSequence;

    public User(String testId, String name, String startSession, String endSession, String totalTime, String userConsent, String feedback, JsonObject userSequence) {
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
