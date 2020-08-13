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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;

public class InferenceTask implements Callable<InferenceTask.InferenceResult> {
    private ArrayList<long[]> inputTokens;
    private Module mModule;
    private WeakReference<Context> gContext;
    private ActivityManager.MemoryInfo memInfo;
    private ActivityManager activityManager;
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


//    public InferenceTask(Context c) {
//        this.mModule=null;
//        gContext=new WeakReference<>(c);
//        activityManager = (ActivityManager) c.getSystemService(ACTIVITY_SERVICE);
//        memInfo=new ActivityManager.MemoryInfo();
//        //Loading model
//        //Loading model
//        //Timber.d("Model loading..");
//        long startTime=System.currentTimeMillis();
//        mModule=load();
//        if(mModule==null)
//            Timber.d("Model load : fail");
//        else
//            Timber.d("Model load: success");
//        long endTime=System.currentTimeMillis();
//        load_time=endTime-startTime;
//    }

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
        System.out.println(mModule);
        long endTime=System.currentTimeMillis();
        load_time=endTime-startTime;
    }

    public void setInputData(final  ArrayList<long[]> iTokens)
    {
        this.inputTokens=iTokens;
        //prepare for other data structures
        this.latency=new long[iTokens.size()+1];
        this.memory=new long[iTokens.size()+1];
        this.latency[0]=load_time;
    }

    @Override
    public synchronized InferenceResult call() throws Exception {
        if(mModule==null)
            throw new Exception("Model load failed");
        return new InferenceResult(predictingLoop(),latency,memory);
        //return new InferenceResult(predictinParallel(),latency,memory);
    }

    private  ArrayList<float[]>  predictingLoop(){
        ArrayList<float[]> results=new ArrayList<>();
        int j=1;
        long startTime=System.currentTimeMillis();
        for (long[] l:this.inputTokens
        ) {
            final long s=System.currentTimeMillis();
            results.add(predict());
            final long e=System.currentTimeMillis();
            latency[j]=e-s;
            Timber.d("∫: %s", e-s);
            j++;
        }
        long endTime=System.currentTimeMillis();

        Timber.d("Total inference time: %s", endTime-startTime);
        return results;
    }

    private  ArrayList<float[]>  predictingParallel(){
        ArrayList<float[]> results=new ArrayList<>();
        ArrayList<predictTask> tasks=new ArrayList<>();
        long startTime=System.currentTimeMillis();
        for (long[] l:this.inputTokens) {
            tasks.add(new predictTask(this.mModule,l));
        }
        ExecutorService service= Executors.newFixedThreadPool(this.inputTokens.size());
        List<Future<float[]>> futures=new ArrayList<Future<float[]>>();
        try {
          futures=service.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Future<float[]> f:futures
             ) {
            try {
                results.add(f.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime=System.currentTimeMillis();
        Timber.d("Total inference time: %s", endTime-startTime);
        return results;
    }

    /**** Load Modules ******/

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

    public synchronized float[] predict()
    {
        // for (int i=0;i<20;i++) {
        Map<String,IValue> data_map = new HashMap<String, IValue>();
        long[] tokens = {101,  2097,  1045,  2022,  2583,  2000,  2224, 17767,  1999,  1996, 17428,   102};
        long[] segments = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        long[] selects = {1,  2,  3,  4,  5,  6,  7,  8,  9, 10};
        long[]  copies = {0, 1,  2,  3,  4,  5,  6,  7,  8,  9};
        long[]  masks = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        long[]  lengths = {10};

        final long[] shape = new long[]{1, 12};
        final long[] shape_10 = new long[]{1, 10};
        final long[] shape_1 = new long[]{1, 1};
        final Tensor tokens_inputTensor = Tensor.fromBlob(tokens, shape);
        final Tensor segments_inputTensor = Tensor.fromBlob(segments, shape);
        final Tensor selects_inputTensor = Tensor.fromBlob(selects, shape_10);
        final Tensor copies_inputTensor = Tensor.fromBlob(copies, shape_10);
        final Tensor masks_inputTensor = Tensor.fromBlob(masks, shape);
        final Tensor lengths_inputTensor = Tensor.fromBlob(lengths, shape_1);

        data_map.put("tokens",IValue.from(tokens_inputTensor));
        data_map.put("segments",IValue.from(segments_inputTensor));
        data_map.put("selects", IValue.from(selects_inputTensor));
        data_map.put("copies",IValue.from(copies_inputTensor));
        data_map.put("masks",IValue.from(masks_inputTensor));
        data_map.put("lengths",IValue.from(lengths_inputTensor));

        final IValue[] outputs = mModule.forward(IValue.dictStringKeyFrom(data_map)).toTuple();

        return null;

//        if (mModule == null)
//            mModule = load();
//        final long[] shape = new long[]{1, data.length};
//        final Tensor inputTensor = Tensor.fromBlob(data, shape);
//        final IValue[] outputs = mModule.forward(IValue.from(inputTensor)).toTuple();
//        float[] scores = new float[outputs.length];
//        if(outputs.length>1) {
//            for (IValue output : outputs
//            ) {
//                final Tensor oTensor = output.toTensor();
//                scores = oTensor.getDataAsFloatArray();
//            }
//        }
//        else
//        {
//            final Tensor oTensor = outputs[0].toTensor();
//            scores = oTensor.getDataAsFloatArray();
//        }
//        return scores;
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

    private synchronized Module load()
    {
        Module tModule;
        try {
            tModule = Module.load(""); //add model file path either form assets or memory
        }
        catch(Exception e){
            Timber.d(e);
            return null;
        }
        return tModule;
    }
}
