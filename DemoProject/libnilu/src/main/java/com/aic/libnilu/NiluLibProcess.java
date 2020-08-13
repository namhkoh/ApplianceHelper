package com.aic.libnilu;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.aic.libnilu.nlu.MainManager;
import com.aic.libnilu.nlu.ResponseObject;

public class NiluLibProcess {


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void init(String filepath, String modelpath){
        // Start her for any lib init's and post settings.
        Log.d("NLU","NLU");
        ResponseObject response = MainManager.getAnswer("How do I set the clock on the microwave?", filepath);
        response.printResponseSolution();

        InferenceTask inference = new InferenceTask(modelpath);
        inference.predict();


    }

    public static String getIntent(){
        return null;
    }
}
