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
    public static void init(String filepath, String modelpath){
        // Start her for any lib init's and post settings.
        Log.d("NLU","NLU");
        ResponseObject response = MainManager.getAnswer("How do I set the clock on the microwave?", filepath);
        response.printResponseSolution();

//        byte[] ascii = "will i be able to use porcelain in the oven".getBytes(StandardCharsets.US_ASCII);
//        System.out.println("ascii"+ascii);
//        int[] arr = new int[ascii.length];
//        for(int i = 0; i < ascii.length; i++){
//            arr[i] = (int)ascii[i];
//        }
//
//        System.out.println(Arrays.toString(arr));

//        String asciiString = Arrays.toString(ascii);
//        System.out.println("String"+asciiString);
        //int[] arr = Stream.of(asciiString).mapToInt(Integer::parseInt).toArray();
        //System.out.println("Int Array" + Arrays.toString(arr));


        InferenceTask inference = new InferenceTask(modelpath);
        inference.predict();


    }

    public static String getIntent(){
        return null;
    }
}
