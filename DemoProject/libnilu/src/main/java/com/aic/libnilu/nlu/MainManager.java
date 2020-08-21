package com.aic.libnilu.nlu;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.aic.libnilu.InferenceTask;
import com.aic.libnilu.token.RunTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible for connecting the different classes.
 * It is like the brain of the program.
 * The getAnswer method is public to be called from an outer class.
 */
public class MainManager {

    private static Node current_node;
    private static KnowledgeBase knowledge;
    private static String intent;
    private static String appliance;
    private static List<String> slot;
    private static List<String> slot_name;
    private static String this_question;
    private static ResponseObject response;
    private static NLU nlu;

    /**
     * This method is used to get the solution of the question using the String question and filename.
     * @param question The question that is asked by the user.
     * @param filename The file that has all the data stored.
     * @return A response object containing information about the solution.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ResponseObject getAnswer(String question, String filename, String modelpath, String vocabpath, String configpath, String intentpath, String slotpath) {

        try {
            knowledge = new KnowledgeBase();
            knowledge.load(filename);
            this_question = question;

            question = question.toLowerCase();

            //Remove
//        System.out.println(knowledge.getIntents());
//        System.out.println(knowledge.getSlots().keySet());
//        System.out.println("slot: " + slot);
//        System.out.println("Slot_name: " + slot_name);

            String appliance_tagger = null;

            //If the question has the word next previous or repeat return a specific type of object.
            intent = NLU.getIntent(knowledge, question);
            if (intent.equals("next")) {
                return new ResponseObject(true, "next_step");
            } else if (intent.equals("previous")) {
                return new ResponseObject(true, "previous_step");
            } else if (intent.equals("repeat")) {
                return new ResponseObject(true, "repeat_step");
            } else {

                nlu = new NLU(modelpath,vocabpath,configpath,intentpath, slotpath);

                question = "how do i set the clock on the microwave";

                RunTokenizer tokenizer = new RunTokenizer(configpath, vocabpath);
                tokenizer.tokenize(question);
                System.out.println(tokenizer.getTokens());
                System.out.println(tokenizer.getOriginal_tokens());
                System.out.println(tokenizer.getSplit_tokens());

                Object[] object_tokens = tokenizer.getTokens().toArray();

                long[] tokens = new long[object_tokens.length+2];
                tokens[0] = Long.valueOf(101);
                tokens[object_tokens.length+1] = Long.valueOf(102);

                for(int i = 1; i < object_tokens.length+1; i++){
                    tokens[i] = Long.valueOf((Integer)object_tokens[i-1]);
                }

                long[] select = tokenizer.getSelect(question);
                long[] input = tokenizer.getInput();
                long[] segment = tokenizer.getSegment();
                long[] copies = tokenizer.getCopies(question);

                System.out.println(Arrays.toString(tokens));

                InferenceTask inference = new InferenceTask(modelpath);
                inference.setVocab(intentpath,slotpath);
                inference.predict_2(tokens, select, input, segment, copies);

                int[] slot_idx = inference.getMax_slot();

                String[] split_question = question.split(" ");
                System.out.println("Split question" + split_question);
                String[] slot_type = inference.getSlot_solution();

                for(int i = 0; i < slot_idx.length; i++){
//                    System.out.println(slot_type[i]);
//                    System.out.println(slot_type[i].equals("O"));
                    if(!slot_type[i].equals("O")){
                        System.out.println(slot_type[i]);
                        if(slot_type[i].equals("B-appliance")){
                            System.out.println(split_question[i]);
                            appliance_tagger = split_question[i];
                        }
                        else if(slot_type[i].charAt(0) == 'B'){
                            slot = new ArrayList<>();
                            slot.add(split_question[i]);
                        }
                    }
                }



                intent = inference.getIntent();

                //appliance = NLU.getAppliance(knowledge, question);
                //slot = NLU.getSlotName(knowledge, question);
                slot_name = NLU.getSlot(knowledge, question);
                System.out.println(slot);

                appliance = appliance_tagger;
                
                System.out.println(slot_name);


                //Go to the appliance node by matching the appliance to the slot_name
                current_node = knowledge.matchNode(knowledge.getRoot(), "applianceName", appliance, "appliance");

                //Temp need to test more than one.
                current_node = knowledge.matchNodeList(current_node, slot_name, slot, intent);


                //The response object or solution.
                response = current_node.getSolution("instructionStep", question);


            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        //Returns the Response Object.
            return response;
        }
    }


