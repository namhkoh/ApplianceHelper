package com.aic.libnilu.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Config {
    // tokenizer Config
    private TokenizerConfig tokenizer;
    @JsonProperty("tokenizer")
    public TokenizerConfig getTokenizer() {
        return tokenizer;
    }
    @JsonProperty("tokenizer")
    public void setTokenizer(TokenizerConfig tokenizer) {
        this.tokenizer = tokenizer;
    }
}
