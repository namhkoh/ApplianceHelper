package com.aic.libnilu.token;

public class WordTokenizer implements Tokenizer{

	@Override
	public TokenizeResult tokenize(String utterance) {

		TokenizeResult tokenizeResult = new TokenizeResult(utterance);

		for(String word: utterance.split(" ")) {
			tokenizeResult.addTokenizeResultUnit(new TokenizeResultUnit(word, word, 1, false));
		}

		return tokenizeResult;
	}

	@Override
	public TokenizeResult tokenize(TokenizeResult tokenizeResult) {
		return tokenizeResult;
	}
}
