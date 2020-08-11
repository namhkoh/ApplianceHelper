package com.aic.libnilu.token;

/*
 * Implement Syllable / Word / SPM Tokenizer
 * - excluding POSTagger for now  
 * 
 * */
public interface Tokenizer {
	TokenizeResult tokenize(String utterance);
	TokenizeResult tokenize(TokenizeResult tokenizeResult);
}
