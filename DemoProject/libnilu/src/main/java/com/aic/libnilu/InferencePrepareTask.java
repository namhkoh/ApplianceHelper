/*
 * Copyright (c) 2020.
 * Author: Sandeep Nama
 * Company: Samsung Research America, AIC, DI/NLU
 * Email: s.nama@samsung.com
 * { Project - MARS (MDC2020) }
 */

package com.aic.libnilu;

import android.content.Context;

import com.aic.libnilu.token.RunTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pytorch.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;

public class InferencePrepareTask {
    private static Module model=null;
    public static JSONObject prepare(Context context, final InferenceTask iTask )
    {
        long start= System.currentTimeMillis();
        //Tokenizer support files
        /***
         * Enable both files in tokenizer configuration
         */
        String cFile= "";//Utils.assetFilePath(context, Settings.TOKENIZER_CONFIG_FILE);
        String vFile= "";//Utils.assetFilePath(context,Settings.TOKENIZER_VOCOB_FILE);

        RunTokenizer tokenizer=new RunTokenizer(cFile,vFile);
        ArrayList<String> sentences= new ArrayList<String>(){
            {
                add(" sentence 1");
                add(" sentence 2");
                add(" sentence 3");
            }
        };
        ArrayList<long[]> tokenSentences=new ArrayList<>();
        ArrayList<String[]> original_tokens=new ArrayList<>();
        ArrayList<String[]> predicated_tags=new ArrayList<>();

        for ( String sentence: sentences) {
            tokenizer.tokenize(sentence);
            String[] word_tokens =tokenizer.getOriginal_tokens().toArray(new String[0]);
            tokenSentences.add(Integer2long(tokenizer.getTokens()));
            original_tokens.add(word_tokens);
        }

        ExecutorService inferExecutor= Executors.newSingleThreadExecutor();
        Future<InferenceTask.InferenceResult> inferTaskResult;
        iTask.setInputData(tokenSentences);
        try {
            inferTaskResult = inferExecutor.submit(iTask);
        }catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }

        if(inferTaskResult.isCancelled())
            return null;

        try {
            for (float[] f:inferTaskResult.get().getResults()) {
                   predicated_tags.add(processResult(f));
            }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        final ArrayList<String> relevantTokens =new ArrayList<String>(){{
            add("B-TITLE");add("I-TITLE");add("B-LOC");add("I-LOC"); add("B-TIME"); add("I-TIME"); add("B-PRICE"); add("I-PRICE");}};

        HashMap<String, String> final_map=new HashMap<String,String>(){
                {
                    put("TITLE","");
                    put("TIME","");
                    put("LOC","");
                    put("PRICE","");
                }
        };
        Iterator<String[]> okIterator=original_tokens.iterator();
        if(original_tokens.size()==predicated_tags.size()) {
            for (String[] pTags : predicated_tags) {
                String[] oTkns = okIterator.next();
                //Timber.d("%s", Arrays.toString(oTkns));
                //Timber.d("%s", Arrays.toString(pTags));
                for (int i = 0; i < pTags.length; i++) {
                    if (relevantTokens.contains(pTags[i])) {
                        String[] elements = pTags[i].split("-");
                        String tag = elements[1];
                        String val = final_map.get(tag) + oTkns[i] + " ";
                        final_map.put(tag,val);
                    }
                }
            }
        }
        else {
            Timber.d("Tokens and tags not matched %s - %s", original_tokens.size(),predicated_tags.size());
            return null;
        }
        long end=System.currentTimeMillis();
        long total=end-start;
        Timber.d("Pipeline time: %s",total);

        JSONObject statsObj=new JSONObject();
        try {
            statsObj.put("pipeline",Long.toString(total));
            long tot=0;
            for (long t:inferTaskResult.get().getProcessTimes())
                tot+=t;
                statsObj.put("latency_load",Long.toString(inferTaskResult.get().getProcessTimes()[0]));
                statsObj.put("latency_inference",Long.toString(tot-inferTaskResult.get().getProcessTimes()[0]));
                statsObj.put("latency",new JSONArray(inferTaskResult.get().getProcessTimes()));
                statsObj.put("memory_load",Long.toString(inferTaskResult.get().getMemoryUsage()[0]));
        } catch ( ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }


        try {
           // String  s=new JSONObject(final_map).put("STATS",new JSONObject(statsObj)).toString();
            //Timber.d(s);
            return new JSONObject(final_map).put("stats",statsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***** Private Methods *****/

    private static long[] Integer2long(ArrayList<Integer> iList)
    {
        long [] res=new long[iList.size()];
        int j=0;
        for (Integer i: iList
             ) {
                res[j++]=i.longValue();
        }
        return res;
    }

    public static int[] topK(float[] a, final int topk) {
        float[] values = new float[topk];
        Arrays.fill(values, -Float.MAX_VALUE);
        int[] ixs = new int[topk];
        Arrays.fill(ixs, -1);

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < topk; j++) {
                if (a[i] > values[j]) {
                    for (int k = topk - 1; k >= j + 1; k--) {
                        values[k] = values[k - 1];
                        ixs[k] = ixs[k - 1];
                    }
                    values[j] = a[i];
                    ixs[j] = i;
                    break;
                }
            }
        }
        return ixs;
    }

    public static int[] argMax(float[] a) {
        final int size=10;
        final int window=a.length/size;
        int[] ixs = new int[window];
        Arrays.fill(ixs, -1);
        for (int i = 0; i <window; i++) {
            float max=-Float.MAX_VALUE;
            int index=-1;
            for (int j = (i*size); j < (size*(i+1)); j++) {
                if (a[j] > max) {
                    max=a[j];
                    index=j%10;
                }
                ixs[i]=index;
            }
        }
        return ixs;
    }

    private synchronized static String[] processResult(float[] result)
    {
        final String[] tag_values ={"B-TITLE", "I-TITLE", "B-LOC", "I-LOC", "B-TIME", "I-TIME", "B-PRICE", "I-PRICE", "O","PAD"};
        int[] indices= InferencePrepareTask.argMax(result);
        String[] predicted_tags=new String[indices.length];
        for (int i=0;i<indices.length;i++)
            predicted_tags[i]=tag_values[indices[i]];
        return predicted_tags;
    }
}