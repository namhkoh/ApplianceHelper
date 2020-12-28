/*
 * Copyright (c) 2020.
 * Author: Sandeep Nama
 * Company: Samsung Research America, AIC, DI/NLU
 * Email: s.nama@samsung.com
 * { Project - MARS (MDC2020) }
 */

package com.aic.libnilu;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;


import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;

public class InferenceTask{
    private ArrayList<long[]> inputTokens;
    private Module mModule;
    private WeakReference<Context> gContext;
    private ActivityManager.MemoryInfo memInfo;
    private ActivityManager activityManager;
    private Map<Integer,String> intent_idx = new HashMap();
    private HashMap<Integer,String> slot_idx = new HashMap();
    private String[] slot_solution;
    private int maxAt;
    private String intent;

    public String[] getSlot_solution() {
        return slot_solution;
    }

    public int getMaxAt() {
        return maxAt;
    }

    public String getIntent() {
        return intent;
    }

    public int[] getMax_slot() {
        return max_slot;
    }

    private int[] max_slot;

    private long[] memory;
    private long[] latency;
    private static final long  MEGABYTE = 1024L * 1024L;
    private long load_time;
    public static class InferenceResult {
        private ArrayList<float[]> results;
        private long[] processTimes;
        private long[] memoryUsage;


        public InferenceResult(ArrayList<float[]> results, long[] processTimes, long[] memoryUsage) {
            this.results = results;
            this.processTimes = processTimes;
            this.memoryUsage = memoryUsage;
        }

        public ArrayList<float[]> getResults() {
            return results;
        }
        public long[] getProcessTimes() {
            return processTimes;
        }
        public long[] getMemoryUsage() {
            return memoryUsage;
        }
    }


    public InferenceTask(String a){

        long startTime=System.currentTimeMillis();
        mModule=load(a);
        if(mModule==null) {
            Timber.d("Model load : fail");
            Log.d("0","Model load: fail");
        }
        else {
            Timber.d("Model load: success");
            Log.d("1","Model load: success");
        }
        //System.out.println(mModule);
        long endTime=System.currentTimeMillis();
        load_time=endTime-startTime;
    }


    /**** Load Modules ******/

    public void setVocab(String intentpath, String slotpath){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    intentpath));
            String line = reader.readLine();
            String line_nospace;
            String[] split;
            while (line != null) {
                //System.out.println(line);
                // read next line
                line_nospace = line.replaceAll("\\s+", "");
                //System.out.println(line_nospace);
                split = line_nospace.split(":");
                //System.out.println(Arrays.asList(line_nospace.split(":")));
                line = reader.readLine();
                intent_idx.put(new Integer(split[1]),split[0]);
            }


            Set<Map.Entry<Integer, String>> entries = intent_idx.entrySet();

            //Intent Key Value
//            for (Map.Entry<Integer, String> entry : entries) {
//                System.out.print("key: "+ entry.getKey());
//                System.out.println(", Value: "+ entry.getValue());
//            }

            reader.close();

            reader = new BufferedReader(new FileReader(
                    slotpath));
            line = reader.readLine();
            while (line != null) {
                // read next line
                line_nospace = line.replaceAll("\\s+", "");
                split = line_nospace.split(":");
                line = reader.readLine();
                slot_idx.put(new Integer(split[1]),split[0]);

//                System.out.println(line);
//                System.out.println(line_nospace);
//                System.out.println(Arrays.asList(line_nospace.split(":")));
            }


            Set<Map.Entry<Integer, String>> entries_slot = intent_idx.entrySet();

            //Slot print
//            for (Map.Entry<Integer, String> entry : entries_slot) {
//                System.out.print("key: "+ entry.getKey());
//                System.out.println(", Value: "+ entry.getValue());
//            }
            reader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class predictTask implements Callable<float[]> {
        long[] data;
        Module module;
        public predictTask(Module m,long[] d) {
            this.data = d;
            this.module=m;
        }
        @Override
        public float[] call() throws Exception {
            final long s=System.currentTimeMillis();
            final long[] shape = new long[]{1, data.length};
            final Tensor inputTensor = Tensor.fromBlob(data, shape);
            final IValue[] outputs = this.module.forward(IValue.from(inputTensor)).toTuple();
            float[] scores = new float[outputs.length];
            if (outputs.length > 1) {
                for (IValue output : outputs
                ) {
                    final Tensor oTensor = output.toTensor();
                    scores = oTensor.getDataAsFloatArray();
                }
            } else {
                final Tensor oTensor = outputs[0].toTensor();
                scores = oTensor.getDataAsFloatArray();
            }
            final long e=System.currentTimeMillis();
            Timber.d("∫: %s", e-s);
            return scores;
        }
    }

    public synchronized float[] predict(long[] tokens, long[] selects, long[] mask, long[] segments, long[] copies){

        String question;
        byte[] ascii = "will i be able to use porcelain in the oven".getBytes(StandardCharsets.US_ASCII);
        //byte[] ascii = "how do I cook the fries on the oven".getBytes(StandardCharsets.US_ASCII);
        //System.out.println("ascii"+ascii);
        int[] data = new int[ascii.length];
        for(int i = 0; i < ascii.length; i++){
            data[i] = (int)ascii[i];
        }

        //System.out.println(Arrays.toString(data));

        final long[] shape = new long[]{1, data.length};
        final Tensor inputTensor = Tensor.fromBlob(data, shape);

        long[] temporary = {700};

        final long[] temp_shape = new long[]{1, temporary.length};
        final Tensor tempTensor = Tensor.fromBlob(temporary, temp_shape);

        final long[] tokens_shape = new long[]{1, tokens.length};
        final Tensor tokensTensor = Tensor.fromBlob(tokens, tokens_shape);

        final long[] mask_shape = new long[]{1, mask.length};
        final Tensor maskTensor = Tensor.fromBlob(mask, mask_shape);

        final long[] segments_shape = new long[]{1, segments.length};
        final Tensor segmentsTensor = Tensor.fromBlob(segments, segments_shape);

        final long[] selects_shape = new long[]{1, selects.length};
        final Tensor selectsTensor = Tensor.fromBlob(selects, selects_shape);

        final long[] copies_shape = new long[]{1, copies.length};
        final Tensor copiesTensor = Tensor.fromBlob(copies, copies_shape);

        final long[] lengths = {selects.length};
        //System.out.println("selects: " + selects.length);
        final long[] lengths_shape = new long[]{1, lengths.length};
        final Tensor lengthsTensor = Tensor.fromBlob(lengths, lengths_shape);

        final IValue[] outputs = mModule.forward(IValue.from(inputTensor), IValue.from(tempTensor), IValue.from(lengthsTensor), IValue.from(maskTensor), IValue.from(segmentsTensor), IValue.from(tokensTensor), IValue.from(selectsTensor), IValue.from(copiesTensor)).toTuple();

        float[] scores = new float[outputs.length];
        if(outputs.length>1) {
            Boolean class_predict;
            int p = 0;
            for (IValue output : outputs
            ) {
                final Tensor oTensor = output.toTensor();
                p++;
                scores = oTensor.getDataAsFloatArray();
//                System.out.println("Scores"+scores);
//                System.out.println(scores.length);
                maxAt = 0;
//                for(int i=0; i < scores.length; i++){
//                    System.out.print(scores[i] + " ");
//                }
//                System.out.println();
                if(p == 1){
                    //int[] max_slot = {0,0,0,0,0,0,0,0,0,0};
                    max_slot = new int[copies.length];
                    Arrays.fill(max_slot, 0);
                    float[][] slot_2d = new float[copies.length][25];
                    for(int i=0; i < scores.length; i++){
                        slot_2d[i / 25][i % 25] = scores[i];
                    }
                    for(int i = 0; i<copies.length; i++)
                    {
                        for(int j = 0; j<25; j++)
                        {
                            //Print Slot Values From 2D array
                            //System.out.print(slot_2d[i][j]);
                            max_slot[i] = slot_2d[i][j] > slot_2d[i][max_slot[i]] ? j : max_slot[i];
                        }
                        //Print Slot Values From 2D array
                        //System.out.println();
                    }
                    System.out.println("Process Tensor: "+Arrays.toString(max_slot));
                    slot_solution = new String[copies.length];
                    for(int i = 0; i < slot_solution.length;i++){
                        slot_solution[i] = slot_idx.get(max_slot[i]);
                    }
                    System.out.println("Tensor to BOI: "+Arrays.asList(slot_solution));

                }

                if(p == 2){
                    for(int i=0; i < scores.length; i++){
                        maxAt = scores[i] > scores[maxAt] ? i : maxAt;
                    }
                    System.out.println("Intent Number: "+maxAt);
                    System.out.println("Intent Value: "+ intent_idx.get(maxAt));
                    intent = intent_idx.get(maxAt);
                }
                System.out.println();
            }
        }



        return null;
    }

    private synchronized Module load(String load)
    {
        Module tModule;
        try {
            tModule = Module.load(load); //add model file path either form assets or memory
        }
        catch(Exception e){
            Timber.d(e);
            return null;
        }
        return tModule;
    }

}
