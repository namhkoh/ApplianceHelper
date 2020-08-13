package com.aic.libnilu.nlu;

import android.os.Build;

import androidx.annotation.RequiresApi;

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

    /**
     * This method is used to get the solution of the question using the String question and filename.
     * @param question The question that is asked by the user.
     * @param filename The file that has all the data stored.
     * @return A response object containing information about the solution.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ResponseObject getAnswer(String question, String filename) {

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

            //If the question has the word next previous or repeat return a specific type of object.
            intent = NLU.getIntent(knowledge, question);
            if (intent.equals("next")) {
                return new ResponseObject(true, "next_step");
            } else if (intent.equals("previous")) {
                return new ResponseObject(true, "previous_step");
            } else if (intent.equals("repeat")) {
                return new ResponseObject(true, "repeat_step");
            } else {


                appliance = NLU.getAppliance(knowledge, question);
                slot = NLU.getSlotName(knowledge, question);
                slot_name = NLU.getSlot(knowledge, question);


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


