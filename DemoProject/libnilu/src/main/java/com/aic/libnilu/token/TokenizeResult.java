package com.aic.libnilu.token;

import java.util.ArrayList;
import java.util.List;

public class TokenizeResult {
	private String utterance;
	private List<TokenizeResultUnit> tokenizeResultUnits;

	public TokenizeResult(){
		this.tokenizeResultUnits = new ArrayList<TokenizeResultUnit>();
	}

	public TokenizeResult(TokenizeResultUnit tokenizeResultUnit){
		this.tokenizeResultUnits = new ArrayList<TokenizeResultUnit>();
		this.tokenizeResultUnits.add(tokenizeResultUnit);
	}

	public TokenizeResult(String utterance){
		this.utterance = utterance;
		this.tokenizeResultUnits = new ArrayList<TokenizeResultUnit>();
	}

	public void addTokenizeResult(TokenizeResult tokenizeResult){
		this.tokenizeResultUnits.addAll(tokenizeResult.getTokenizeResultUnits());
	}

	public void addTokenizeResultUnit(TokenizeResultUnit tokenizeResultUnit){
		tokenizeResultUnits.add(tokenizeResultUnit);
	}

	public void fixLastSpaceInfo(int value){
		tokenizeResultUnits.get(tokenizeResultUnits.size()-1).setSpaceInfo(value);
	}

	public List<TokenizeResultUnit> getTokenizeResultUnits(){
		return tokenizeResultUnits;
	}

	public TokenizeResultUnit getTokenizeResultUnit(int n){
		return tokenizeResultUnits.get(n);
	}

	public List<String> getTokenList(){
		List<String> allTokens = new ArrayList<String>();
		for (TokenizeResultUnit tokenizeResultUnit: tokenizeResultUnits){
			allTokens.add(tokenizeResultUnit.getToken());
		}
		return allTokens;
	}

	public List<String> getOrgTokenList(){
		List<String> allOrgTokens = new ArrayList<String>();
		for (TokenizeResultUnit tokenizeResultUnit: tokenizeResultUnits){
			allOrgTokens.add(tokenizeResultUnit.getOrgToken());
		}
		return allOrgTokens;
	}

	public List<Integer> getSpaceInfoList(){
		List<Integer> allSpaceInfo = new ArrayList<Integer>();
		for (TokenizeResultUnit tokenizeResultUnit: tokenizeResultUnits){
			allSpaceInfo.add(tokenizeResultUnit.getSpaceInfo());
		}
		return allSpaceInfo;
	}

	public int getSize(){
		return tokenizeResultUnits.size();
	}

	public String getUtterance(){
		return this.utterance;
	}

	public void setUtterance(String utterance){
		this.utterance = utterance;
	}
}
