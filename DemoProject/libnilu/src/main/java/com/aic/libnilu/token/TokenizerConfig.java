package com.aic.libnilu.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TokenizerConfig {
    private String method;
    private String language;
    private boolean doLowerCase;
    private boolean doNormalizeNumber;
    private boolean doTokenizeEnglish;
    private boolean doSplitPunctuation;

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(String method) {
        this.method = method.toLowerCase();
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language.toLowerCase();
    }

    @JsonProperty("do_lower_case")
    public boolean getDoLowerCase() {
        return doLowerCase;
    }

    @JsonProperty("do_lower_case")
    public void setDoLowerCase(boolean doLowerCase) {
        this.doLowerCase = doLowerCase;
    }

    @JsonProperty("do_normalize_number")
    public boolean getDoNormalizeNumber() {
        return doNormalizeNumber;
    }

    @JsonProperty("do_normalize_number")
    public void setDoNormalizeNumber(boolean doNormalizeNumber) {
        this.doNormalizeNumber = doNormalizeNumber;
    }

    @JsonProperty("do_tokenize_english")
    public boolean getDoTokenizeEnglish() {
        return doTokenizeEnglish;
    }

    @JsonProperty("do_tokenize_english")
    public void setDoTokenizeEnglish(boolean doTokenizeEnglish) {
        this.doTokenizeEnglish = doTokenizeEnglish;
    }

    @JsonProperty("do_split_punctuation")
    public boolean getDoSplitPunctuation() {
        return doSplitPunctuation;
    }

    @JsonProperty("do_split_punctuation")
    public void setDoSplitPunctuation(boolean doSplitPunctuation) {
        this.doSplitPunctuation = doSplitPunctuation;
    }

}