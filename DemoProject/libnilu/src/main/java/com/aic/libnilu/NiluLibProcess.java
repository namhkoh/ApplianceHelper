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
    public static void init(String filepath, String modelpath, String vocabpath, String configpath){
        // Start her for any lib init's and post settings.
        Log.d("NLU","NLU");
        ResponseObject response = MainManager.getAnswer("How do I set the clock on the microwave?", filepath);
        response.printResponseSolution();

        InferenceTask inference = new InferenceTask(modelpath);
        inference.predict();

        String question = "will i be able to use procelain in the oven";
        RunTokenizer tokenizer = new RunTokenizer(configpath, vocabpath);
        tokenizer.tokenize(question);
        System.out.println(tokenizer.getTokens());


    }

    public static String getIntent(){
        return null;
    }
}
