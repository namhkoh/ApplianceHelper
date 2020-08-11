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
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
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


    public InferenceTask(Context c) {
        this.mModule=null;
        gContext=new WeakReference<>(c);
        activityManager = (ActivityManager) c.getSystemService(ACTIVITY_SERVICE);
        memInfo=new ActivityManager.MemoryInfo();
        //Loading model
        //Loading model
        //Timber.d("Model loading..");
        long startTime=System.currentTimeMillis();
        mModule=load();
        if(mModule==null)
            Timber.d("Model load : fail");
        else
            Timber.d("Model load: success");
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
            results.add(predict(l));
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
        for (long[] l:this.inputTokens)
            tasks.add(new predictTask(this.mModule,l));
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

    private synchronized float[] predict(long[] data)
    {
        // for (int i=0;i<20;i++) {
        if (mModule == null)
            mModule = load();
        final long[] shape = new long[]{1, data.length};
        final Tensor inputTensor = Tensor.fromBlob(data, shape);
        final IValue[] outputs = mModule.forward(IValue.from(inputTensor)).toTuple();
        float[] scores = new float[outputs.length];
        if(outputs.length>1) {
            for (IValue output : outputs
            ) {
                final Tensor oTensor = output.toTensor();
                scores = oTensor.getDataAsFloatArray();
            }
        }
        else
        {
            final Tensor oTensor = outputs[0].toTensor();
            scores = oTensor.getDataAsFloatArray();
        }
        return scores;
    }

    private synchronized Module load()
    {
        Module tModule;
        try {
            tModule = Module.load("/* Model File path  */ "); //add model file path either form assets or memory
        }
        catch(Exception e){
            Timber.d(e);
            return null;
        }
        return tModule;
    }
}
