package com.aic.libnilu;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.aic.libnilu.nlu.MainManager;
import com.aic.libnilu.nlu.ResponseObject;
import com.aic.libnilu.token.RunTokenizer;
import com.aic.libnilu.token.Tokenizer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

public class NiluLibProcess {


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void init(String filepath, String modelpath, String vocabpath, String configpath, String intentpath, String slotpath){
        // Start her for any lib init's and post settings.
//        Log.d("NLU","NLU");
        String question;
        //question = "How do I set the clock on the microwave";
        question = "how can i buy a wirerack for my oven";
        question = "How can I clean the cooktop seal on my oven";
        //question = "what does c-F0 code mean on my oven";
        //question = "how do i cook a hamburger in the oven";
        //question = "how do i set the broiling feature in the oven";
        //question = "how do i use the reheat feature in the microwave";
        question = "how do i turn on the wifi in my oven";
        ResponseObject response = MainManager.getAnswer(question, filepath, modelpath, vocabpath, configpath, intentpath, slotpath);
        response.printResponseSolution();

//        String question = "will i be able to use procelain in the oven";
//        question = "how do I set the clock on the microwave";
//        RunTokenizer tokenizer = new RunTokenizer(configpath, vocabpath);
//        tokenizer.tokenize(question);
//        System.out.println(tokenizer.getTokens());
//        System.out.println(tokenizer.getOriginal_tokens());
//        System.out.println(tokenizer.getSplit_tokens());
//
//        Object[] object_tokens = tokenizer.getTokens().toArray();
//
//        long[] tokens = new long[object_tokens.length+2];
//        tokens[0] = Long.valueOf(101);
//        tokens[object_tokens.length+1] = Long.valueOf(102);
//
//        for(int i = 1; i < object_tokens.length+1; i++){
//            tokens[i] = Long.valueOf((Integer)object_tokens[i-1]);
//        }
//
//        long[] select = tokenizer.getSelect(question);
//        long[] input = tokenizer.getInput();
//        long[] segment = tokenizer.getSegment();
//        long[] copies = tokenizer.getCopies(question);
//
//        System.out.println(Arrays.toString(tokens));
//
//        InferenceTask inference = new InferenceTask(modelpath);
//        inference.setVocab(intentpath,slotpath);
//        inference.predict_2(tokens, select, input, segment, copies);

    }

    public static String getIntent(){
        return null;
    }
}
