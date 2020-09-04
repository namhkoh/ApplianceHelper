/*
 * Copyright (c) 2020.
 * Author: Sandeep Nama
 * Company: Samsung Research America, AIC, DI/NLU
 * Email: s.nama@samsung.com
 * { Project - MARS (MDC2020) }
 */

package com.aic.libnilu;

import android.content.Context;

import com.aic.libnilu.token.*;

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