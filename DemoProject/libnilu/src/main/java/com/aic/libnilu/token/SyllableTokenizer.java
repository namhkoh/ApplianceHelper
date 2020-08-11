package com.aic.libnilu.token;

public class SyllableTokenizer implements Tokenizer {

	@Override
	public TokenizeResult tokenize(String utterance) {

		TokenizeResult tResult = new TokenizeResult();

		String subs;

		for(int index = 0; index < utterance.length(); index++){
			subs = utterance.substring(index, index+1);
			if(subs.equals(" ")){
				tResult.fixLastSpaceInfo(1);
			}
			else {
				tResult.addTokenizeResultUnit(new TokenizeResultUnit(subs, subs, 0, false));
			}
		}
		tResult.fixLastSpaceInfo(1);

		return tResult;
	}

	@Override
	public TokenizeResult tokenize(TokenizeResult tokenizeResult) {

		TokenizeResult tResult = new TokenizeResult();

		for (TokenizeResultUnit tokenizeResultUnit: tokenizeResult.getTokenizeResultUnits()){
			if (tokenizeResultUnit.getNeverSplit()){
				tResult.addTokenizeResultUnit(tokenizeResultUnit);
			}
			else{
				TokenizeResult syllableResult = this.tokenize(tokenizeResultUnit.getToken());
				syllableResult.fixLastSpaceInfo(tokenizeResultUnit.getSpaceInfo());
				tResult.addTokenizeResult(syllableResult);
			}

		}
		return tResult;
	}
}
