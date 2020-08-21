package com.company;

import java.util.List;

public class MainManager {

    private static Node current_node;
    private static KnowledgeBase knowledge;
    private static String intent;
    private static String appliance;
    private static List<String> slot;
    private static List<String> slot_name;
    private static String this_question;

    public static ResponseObject getAnswer(String question, String filename) {
        knowledge = new KnowledgeBase();
        knowledge.load(filename);
        this_question = question;

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

            //Returns the Response Object.
            return current_node.getSolution(knowledge, "instructionStep", question);
        }
    }

}
