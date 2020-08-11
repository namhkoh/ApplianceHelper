package com.aic.libnilu.token;


import java.nio.file.Paths;

public class TokenizeWrapper {
    private String method;
    private String language;
    private boolean doLowerCase;
    private boolean doTokenizeEnglish;
    private boolean doNormalizeNum;
    private boolean doSplitPunctuation;
    private WordTokenizer wordTokenizer;
    private Tokenizer tokenizer;
    private SPMTokenizer englishTokenizer;
    private String modelDir;
    private String vocabFilePath;
    private static final String SPMResourceKoKR = "o_spm.ko-KR.50K.vocab";
    private static final String SPMResourceEnUS = "en_vocab_for_spm.txt";
    private static final String spmPrefix = "##";

    public TokenizeWrapper(TokenizerConfig config, String vocabfile){
        this.modelDir = modelDir;
        this.vocabFilePath=vocabfile;
        this.method = config.getMethod();
        this.language = config.getLanguage();
        this.doLowerCase = config.getDoLowerCase();
        this.doTokenizeEnglish = (config.getDoTokenizeEnglish() && this.language.equals("ko-kr"));
        this.doNormalizeNum = config.getDoNormalizeNumber();
        this.doSplitPunctuation = config.getDoSplitPunctuation();
        this.tokenizer = getTokenizer();

        if (doTokenizeEnglish){
            this.englishTokenizer = new SPMTokenizer();
            //this.englishTokenizer.initSPMTokenizer(Paths.get(modelDir, SPMResourceEnUS).toString());
            this.englishTokenizer.initSPMTokenizer(this.vocabFilePath);
        }
        this.wordTokenizer = new WordTokenizer();
    }

    public Tokenizer getTokenizer(){
        if (this.method.equals("syllable")){
            return new SyllableTokenizer();
        }
        else if (this.method.equals("word")){
            return new WordTokenizer();
        }
        else if (this.method.equals("spm")){
            SPMTokenizer tokenizer = new SPMTokenizer();
            if (this.language.equals("ko-kr")){
                tokenizer.initSPMTokenizer(Paths.get(modelDir, SPMResourceKoKR).toString());
            }
            else if (this.language.equals("en-us")){
               //tokenizer.initSPMTokenizer(Paths.get(modelDir, SPMResourceEnUS).toString());
                tokenizer.initSPMTokenizer(this.vocabFilePath);
            }
            return tokenizer;
        }
        else{
            System.err.println("Use default(syllable) tokenizer");
            return new SyllableTokenizer();
        }
    }

    public TokenizeResult applySPM(TokenizeResultUnit tokenizeResultUnit) {

        boolean neverSplit = tokenizeResultUnit.getNeverSplit();
        Integer spaceInfo = tokenizeResultUnit.getSpaceInfo();

        TokenizeResult applySPMResult = new TokenizeResult();
        String englishSpacedToken = StringUtils.splitEnglish(tokenizeResultUnit.getToken()).trim();

        int start = 0;
        int end;

        for (String token : englishSpacedToken.split(" ")) {
            if (StringUtils.containEnglish(token)) {
                TokenizeResult spmResult = englishTokenizer.tokenize(token);

                // save original token
                for (TokenizeResultUnit spmResultUnit: spmResult.getTokenizeResultUnits()){
                    end = start + spmResultUnit.getOrgToken().length();
                    spmResultUnit.setOrgToken(tokenizeResultUnit.getOrgToken().substring(start, end));
                    start = end;
                }

                applySPMResult.addTokenizeResult(spmResult);
                applySPMResult.fixLastSpaceInfo(0);
            } else {
                end = start+token.length();
                applySPMResult.addTokenizeResultUnit(
                        new TokenizeResultUnit(token, tokenizeResultUnit.getOrgToken().substring(start, end), 0, neverSplit));
                start = end;
            }
        }
        applySPMResult.fixLastSpaceInfo(spaceInfo);

        return applySPMResult;
    }

    public TokenizeResult tokenizeEnglish(TokenizeResult tokenizeResult){
        /*
         *  twg안녕twg => t ##wg 안녕 t ##wg
         *      1         0  0    0  0  1
         *
         * */
        TokenizeResult tokenizeEnglishResult = new TokenizeResult();

        for (TokenizeResultUnit tokenizeResultUnit: tokenizeResult.getTokenizeResultUnits()){

            if (tokenizeResultUnit.getNeverSplit()){
                tokenizeEnglishResult.addTokenizeResultUnit(tokenizeResultUnit);
            }
            else {
                TokenizeResult tokenizedResult = applySPM(tokenizeResultUnit);
                tokenizeEnglishResult.addTokenizeResult(tokenizedResult);
            }
        }

        return tokenizeEnglishResult;
    }

    public TokenizeResult normalizeNumber(TokenizeResultUnit tokenizeResultUnit){

        boolean neverSplit = tokenizeResultUnit.getNeverSplit();
        Integer spaceInfo = tokenizeResultUnit.getSpaceInfo();

        TokenizeResult normalizeNumberResult = new TokenizeResult();
        String numberSpacedToken = StringUtils.splitNumber(tokenizeResultUnit.getToken()).trim();

        int start = 0;
        int end;

        for (String token: numberSpacedToken.split(" ")){

            if (token.contains(spmPrefix)) {
                end = start + token.length()-spmPrefix.length();
            }
            else{
                end = start + token.length();
            }

            if (StringUtils.isDigit(token)){
                normalizeNumberResult.addTokenizeResultUnit(
                        new TokenizeResultUnit("[NUM]", tokenizeResultUnit.getOrgToken().substring(start, end), 0, true));
            }
            else{
                normalizeNumberResult.addTokenizeResultUnit(
                        new TokenizeResultUnit(token, tokenizeResultUnit.getOrgToken().substring(start, end), 0, neverSplit));
            }
            start = end;
        }
        normalizeNumberResult.fixLastSpaceInfo(spaceInfo);

        return normalizeNumberResult;
    }

    public TokenizeResult tokenizeNumberTokens(TokenizeResult inputTokenizeResult) {
        /*
         * 123twg => [NUM] twg
         *   1         0    1
         *
         * 123twg => [NUM] twg
         *   0         0    0
         * */

        TokenizeResult normalizeNumberResult = new TokenizeResult();

        for (TokenizeResultUnit tokenizeResultUnit: inputTokenizeResult.getTokenizeResultUnits()){

            if (tokenizeResultUnit.getNeverSplit()){
                normalizeNumberResult.addTokenizeResultUnit(tokenizeResultUnit);
            }
            else {
                TokenizeResult normalizedResult = normalizeNumber(tokenizeResultUnit);
                normalizeNumberResult.addTokenizeResult(normalizedResult);
            }
        }

        return normalizeNumberResult;
    }

    public TokenizeResult splitPunctuation(TokenizeResultUnit tokenizeResultUnit){
        /*
         * 123.345 => 123 . 345
         *    1        0  0  1
         * */

        boolean neverSplit = tokenizeResultUnit.getNeverSplit();
        if (neverSplit) {
            return new TokenizeResult(tokenizeResultUnit);
        }
        Integer spaceInfo = tokenizeResultUnit.getSpaceInfo();

        TokenizeResult punctuationResult = new TokenizeResult();
        String punctuationSpacedToken = StringUtils.normalizeConsecutiveWhiteSpace(
                StringUtils.splitPunctuation(tokenizeResultUnit.getToken()));

        int start = 0;

        for (String token: punctuationSpacedToken.split(" ")){
            punctuationResult.addTokenizeResultUnit(
                    new TokenizeResultUnit(token, tokenizeResultUnit.getOrgToken().substring(start, start + token.length()), 0, StringUtils.isPunctuation(token) || neverSplit));
            start += token.length();
        }
        punctuationResult.fixLastSpaceInfo(spaceInfo);

        return punctuationResult;
    }

    public TokenizeResult tokenize(String utterance) {

        utterance = StringUtils.normalizeWhiteSpace(utterance);
        utterance = StringUtils.normalizeConsecutiveWhiteSpace(utterance);

        TokenizeResult wordTokens = wordTokenizer.tokenize(utterance);
        TokenizeResult tokenizeResult = new TokenizeResult();

        TokenizeResult tempResult;

        for (TokenizeResultUnit wordTokenUnit: wordTokens.getTokenizeResultUnits()){

            tempResult = new TokenizeResult(wordTokenUnit);

            if (this.doLowerCase) {
                wordTokenUnit.setToken(wordTokenUnit.getToken().toLowerCase());
            }
            // note : if you want to change order of below functions, you must consider orgToken

            if (this.doSplitPunctuation) {
                tempResult = splitPunctuation(wordTokenUnit);
            }

            if (this.doTokenizeEnglish){
                tempResult = tokenizeEnglish(tempResult);
            }

            if (this.doNormalizeNum){
                tempResult = tokenizeNumberTokens(tempResult);
            }

            tokenizeResult.addTokenizeResult(tempResult);
        }

        if (!this.method.equals("word")){
            tokenizeResult = this.tokenizer.tokenize(tokenizeResult);
        }
        tokenizeResult.setUtterance(utterance);

        return tokenizeResult;
    }
}