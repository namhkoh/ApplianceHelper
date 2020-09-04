package com.aic.libnilu.nlu;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class extracts the slot of intent values of the question provided.
 * The class will be replaced with a sophisticated slot-intent tagger.
 */
public class NLU {
    // A sentence split as a list by a space
    String modelpath;
    String vocabpath;
    String configpath;
    String intentpath;
    String slotpath;

    public NLU(String modelpath, String vocabpath, String configpath, String intentpath, String slotpath){
        this.modelpath = modelpath;
        this.vocabpath = vocabpath;
        this.configpath = configpath;
        this.intentpath = intentpath;
        this.slotpath = slotpath;
    }

    private static List sentence_list;

    public String Appliance(){
        return null;

    }

    /**
     * Extracts the slot of the sentence.
     * @param knowledge The hashmap that serves as a database.
     * @param question The question that was given as input.
     * @return The value of the slot
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getAppliance(KnowledgeBase knowledge, String question) throws Exception {
        String appliance = "Appliance not found";
        try {
            sentence_list = preprocess_sentence(question);
            appliance = knowledge.getAppliances().stream().filter(key -> sentence_list.contains(key)).findAny().get();
        }catch(Exception e){
            throw new Exception("Appliance not found from question: " + question);
        }
        return appliance;
    }

    /**
     * Extracts the intent of the sentence.
     * @param knowledge The hashmap that serves as a database.
     * @param question The question that was given as input.
     * @return The value of the intent
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getIntent(KnowledgeBase knowledge, String question) throws Exception {
        String intent;
        try {
        sentence_list = preprocess_sentence(question);
        intent = knowledge.getIntents().stream().filter(key -> sentence_list.contains(key)).findAny().get();
        }catch(Exception e){
            return "None";
            //throw new Exception("Intent not found from question: " + question);
        }
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getIntent_Steps(KnowledgeBase knowledge, String question) throws Exception {
        String intent;
        try {
            sentence_list = preprocess_sentence(question);
            intent = knowledge.getIntents_step().stream().filter(key -> sentence_list.contains(key)).findAny().get();
        }catch(Exception e){
            return "None";
            //throw new Exception("Intent not found from question: " + question);
        }
        return intent;
    }

    /**
     * Extracts the slot of the sentence.
     * @param knowledge The hashmap that serves as a database.
     * @param question The question that was given as input.
     * @return The value of the slot
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> getSlot(KnowledgeBase knowledge, String question) throws Exception{
        List<String> slot;
        try {
            sentence_list = preprocess_sentence(question);
            slot = knowledge.getSlots().keySet().stream().filter(key -> sentence_list.contains(key)).map(key -> knowledge.getSlots().get(key)).collect(Collectors.toList());
        }catch(Exception e){
            throw new Exception("Slots not found from question: " + question);
        }
        return slot;
    }

    /**
     * Extracts the slot of the sentence.
     * @param knowledge The hashmap that serves as a database.
     * @param question The question that was given as input.
     * @return The value of the slot
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> getSlotName(KnowledgeBase knowledge, String question) throws Exception {
        List<String> slot_name;
        try {
        sentence_list = preprocess_sentence(question);
        slot_name = knowledge.getSlots().keySet().stream().filter(key -> sentence_list.contains(key)).collect(Collectors.toList());
        }catch(Exception e){
            throw new Exception("Slot name not found from question: " + question);
        }
        return slot_name;
    }

    /**
     * Breaks the words of a sentence to a list
     * @param sentence The qeustion that was given as input
     * @return A list of words in the sentence.
     */
    private static List preprocess_sentence(String sentence){
        //Preprocess sentence
        sentence = sentence.toLowerCase();
        sentence = sentence.replaceAll("[?.\"]", "");
        String[] sentence_token = sentence.split("\\s");
        List sentence_list = Arrays.asList(sentence_token);
        return sentence_list;
    }




}