package com.aic.libnilu.token;

import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;

public class RunTokenizer {

    ClassLoader classLoader = getClass().getClassLoader();
    TokenizeWrapper tokenizer;
    Vocab vocab;
    private ArrayList<Integer> tokens;
    private ArrayList<String> original_tokens;
    private ArrayList<String> split_tokens;
    public RunTokenizer(String configFile, String vocabFile)
    {
        Config config = ConfigUtils.loadConfig(configFile);
        assert config != null;
        tokenizer = new TokenizeWrapper(config.getTokenizer(), vocabFile);
        vocab = new Vocab(vocabFile);
    }


    public void tokenize(String sentence)
    {
        TokenizeResult tokenized = tokenizer.tokenize(sentence.toLowerCase());
        ArrayList<String> inputIds = new ArrayList<String>();
        for (TokenizeResultUnit tokenizeResultUnit: tokenized.getTokenizeResultUnits()){
            inputIds.add(tokenizeResultUnit.getToken());
        }
        //Original tokens
        this.original_tokens=(ArrayList<String>)tokenized.getOrgTokenList();
        this.split_tokens= inputIds;
        this.tokens=vocab.convertTokensToIds(inputIds);
    }

    public long[] getSelect(String sentence){
        int sentence_length = sentence.split(" ").length;
        long[] select = new long[sentence_length];
        int position = 0;
        for(int i = 0; i < split_tokens.size(); i ++){
            if(split_tokens.get(i).charAt(0) != '#'){
                select[position] = Long.valueOf(i+1);
                position++;
            }
        }
        System.out.println(Arrays.toString(select));
        return select;
    }

    public long[] getInput(){
        long[] input = new long[split_tokens.size()+2];
        for(int i = 0; i < split_tokens.size()+2; i++){
            input[i] = Long.valueOf(1);
        }
        System.out.println(Arrays.toString(input));
        return input;
    }

    public long[] getSegment(){
        long[] segment = new long[split_tokens.size()+2];
        for(int i = 0; i < split_tokens.size()+2; i++){
            segment[i] = Long.valueOf(1);
        }
        System.out.println(Arrays.toString(segment));
        return segment;
    }

    public long[] getCopies(String sentence){
        int sentence_length = sentence.split(" ").length;
        long[] select = new long[sentence_length];
        for(int i = 0; i < sentence_length; i ++){
            select[i] = Long.valueOf(i);
        }
        System.out.println(Arrays.toString(select));
        return select;
    }


    public ArrayList<Integer> getTokens() {
        return tokens;
    }

    public ArrayList<String> getOriginal_tokens() {
        return original_tokens;
    }

    public ArrayList<String> getSplit_tokens() {
        return split_tokens;
    }

    public void words()
    {
        String example= "Welcome to the ##EDGE Virtual Event Thu, May 2020 5:31";
        TokenizeResult tokenized=tokenizer.tokenize(example.toLowerCase());
        for (TokenizeResultUnit tokenizeResultUnit: tokenized.getTokenizeResultUnits()){
            Timber.d(".1 %s",tokenizeResultUnit.getOrgToken());
            Timber.d(".1 %s",tokenizeResultUnit.getToken());
        }
        Timber.d(".1 %s",tokenized.getOrgTokenList());
        Timber.d(".2 %s",tokenized.getTokenList());
    }
}
