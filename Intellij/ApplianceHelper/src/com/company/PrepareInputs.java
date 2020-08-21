package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrepareInputs {

    public static void prepare_inputs_for_bert_xlnet(String[] words, int[] lens, String tokenizer,boolean cls_token_at_end, String cls_token, String sep_token, int cls_token_segment, boolean pad_on_left, int pad_token_segment_id, String device){
        System.out.println(Arrays.toString(words));
        System.out.print(Arrays.toString(lens) + " ");
        System.out.print(cls_token_at_end + " ");
        System.out.print(cls_token + " ");
        System.out.print(sep_token + " ");
        System.out.print(cls_token_segment + " ");
        System.out.print(pad_on_left + " ");
        System.out.print(pad_token_segment_id + " ");
        System.out.println(device);

        int max_length_of_sequences = Arrays.stream(lens).max().getAsInt();
        System.out.println(max_length_of_sequences);
        List tokens = new ArrayList();
        List segment_ids = new ArrayList();
        List selected_indices = new ArrayList();
        int start_pos = 0;



        List selected_index = new ArrayList();
        List ts = new ArrayList();

        for (String var : words)
        {
            selected_index.add(ts.size() + 1);
            ts += 

        }


    }

}
