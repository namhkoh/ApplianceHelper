package com.aic.libnilu.token;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern ENGLISH_WORD_PATTERN = Pattern.compile("[a-zA-Z]+");
    private static final Pattern ENGLISH_PATTERN = Pattern.compile(".*[a-zA-Z]+.*");
    private static final Pattern NUMBER_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern MULTIPLE_SPACE_PATTERN = Pattern.compile(" +");
    private static final Pattern IGNORE_CHARS_PATTERN = Pattern.compile("[.,!~\\^]");
    private static final Pattern WHITESPACE_CHARS_PATTERN = Pattern.compile("[\t\r\n\u00A0]");
    private static final Pattern ZERO_WIDTH_CHARS_PATTERN = Pattern.compile("[\u200B]");
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[\\p{P}\\u0021-\\u002F\\u003A-\\u0040\\u005B-\\u0060\\u007B-\\u007E]");

    static public String normalizeWhiteSpace(String utterance){
        return ZERO_WIDTH_CHARS_PATTERN.matcher(WHITESPACE_CHARS_PATTERN.matcher(utterance).replaceAll(" ")).replaceAll("");
    }

    static public String replaceIgnoreCharset(String utterance, String replaceTo){
        return ZERO_WIDTH_CHARS_PATTERN.matcher(IGNORE_CHARS_PATTERN.matcher(utterance).replaceAll(replaceTo)).replaceAll("");
    }

    static public String normalizeConsecutiveWhiteSpace(String utterance){
        return MULTIPLE_SPACE_PATTERN.matcher(utterance).replaceAll(" ").trim();
    }

    static public List <String> tokenize(String utterance, String delimiter) {
        return Arrays.asList(utterance.split(delimiter));
    }

    static public boolean containNumber(String str) {
        return NUMBER_PATTERN.matcher(str).matches();
    }

    static public boolean containEnglish(String str){
        return ENGLISH_PATTERN.matcher(str).matches();
    }

    static public boolean isDigit(String str){
        return DIGIT_PATTERN.matcher(str).matches();
    }

    static public boolean isSpecialToken(String token){
        return token.equals("[PAD]") || token.equals("[CLS]") || token.equals("[SEP]");
    }
    static public boolean isPunctuation(String token){
        return PUNCTUATION_PATTERN.matcher(token).matches();
    }
    static public String splitPunctuation(String utterance){
        return PUNCTUATION_PATTERN.matcher(utterance).replaceAll(" $0 ");
    }
    static public String splitNumber(String utterance){
        return DIGIT_PATTERN.matcher(utterance).replaceAll(" $0 ");
    }
    static public String splitEnglish(String utterance){
        return ENGLISH_WORD_PATTERN.matcher(utterance).replaceAll(" $0 ");
    }
}
