package com.aic.libnilu.token;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vocab {
	// (Vocab, Index) pair
	public Map<String, Integer> invVocabList;

	// Vocab list
	public List<String> vocabList;

	void loadVocab(String vocabPath){

		BufferedReader bufferedReader = null;

		try {

			InputStreamReader inputStreamReader = new InputStreamReader(
					new FileInputStream(vocabPath), StandardCharsets.UTF_8);
			bufferedReader = new BufferedReader(inputStreamReader);

			do {
				String line = bufferedReader.readLine();
				if (line == null) {
					break;
				}
				vocabList.add(line);
				invVocabList.put(line, vocabList.size() - 1);
			} while (true);

		} catch(IOException e){
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	public Vocab() {
		invVocabList = new HashMap<String, Integer>();
		vocabList = new ArrayList<String>();
	}

	public Vocab(String vocabPath) {
		invVocabList = new HashMap<String, Integer>();
		vocabList = new ArrayList<String>();

		loadVocab(vocabPath);
	}

	public ArrayList<Integer> convertTokensToIds(List<String> tokens){
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (String token : tokens) {
			// special 토큰 처리
			if (token.equals("[NUM]")) {
				ids.add(invVocabList.get("1"));
				continue;
			}
			if (invVocabList.containsKey(token)) {
				ids.add(invVocabList.get(token));
			} else {
				ids.add(invVocabList.get("[UNK]"));
			}
		}
		return ids;
	}

	public Integer convertTokenToId(String token) {
		// special 토큰 처리
		if (token.equals("[NUM]")) {
			return invVocabList.get("1");
		}
		if (invVocabList.containsKey(token)) {
			return invVocabList.get(token);
		} else {
			return invVocabList.get("[UNK]");
		}
	}

	public ArrayList<String> convertIdsToTokens(List<Integer> ids) {
		ArrayList<String> tokens = new ArrayList<String>();
		for (int id : ids) {
			try {
				tokens.add(vocabList.get(id));
			} catch (ArrayIndexOutOfBoundsException exception) {
				System.err.println("id does not exist in vocab");
			}
		}
		return tokens;
	}

	public String convertIdToToken(int id) {
		try {
			return vocabList.get(id);
		} catch (ArrayIndexOutOfBoundsException exception) {
			System.err.println("id does not exist in vocab");
			return "[UNK]";
		}
	}
}
