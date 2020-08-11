package com.aic.libnilu.token;

import java.util.ArrayList;
import java.util.List;

public class SPMTokenizer implements Tokenizer {

	private static final String spmPrefix = "##";
	private Vocab vocab;

	public void initSPMTokenizer(String spmVocabPath){

		vocab = new Vocab(spmVocabPath);
	}

	@Override
	public TokenizeResult tokenize(String utterance) {

		TokenizeResult tResult = new TokenizeResult();

		List<String> whitespaceTokens = StringUtils.tokenize(utterance, " ");
		ArrayList<String> subTokens = new ArrayList<String>();

		String substring;
		String curSubstr = "";

		int tokLen;

		boolean isBad;
		boolean isSubstr;
		int start, end;

		for (String token : whitespaceTokens){

			isBad = false;
			start = 0;
			subTokens.clear();
			tokLen = token.length();

			while (start < tokLen) {

				isSubstr = false;
				end = tokLen;

				while (start < end){

					substring = token.substring(start, end);
					if (start > 0){
						substring = spmPrefix.concat(substring);
					}
					if (vocab.invVocabList.containsKey(substring)){
						curSubstr = substring;
						isSubstr = true;
						break;
					}
					end -= 1;
				}
				if (!isSubstr){
					isBad = true;
					break;
				}
				subTokens.add(curSubstr);
				start = end;
			}
			if (isBad) {
				tResult.addTokenizeResultUnit(new TokenizeResultUnit("[UNK]", token, 1, true));
			}
			else {
				for (String subToken : subTokens) {
					tResult.addTokenizeResultUnit(new TokenizeResultUnit(subToken, subToken.replace(spmPrefix, ""), 0, true));
				}
				tResult.fixLastSpaceInfo(1);
			}
		}
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
				TokenizeResult spmResult = this.tokenize(tokenizeResultUnit.getToken());
				spmResult.fixLastSpaceInfo(tokenizeResultUnit.getSpaceInfo());
				tResult.addTokenizeResult(spmResult);
			}

		}
		return tResult;
	}
}
