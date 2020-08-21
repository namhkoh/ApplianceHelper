package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.*;

public class KnowledgeBase {

    private Node root;
    private Node temp_node;
    private String lineJustFetched;
    private FileReader reader;
    private BufferedReader buf;
    private List<String> wordsArray;
    private String name;
    private List<String> slot_list;
    private String intent;
    private String appliance;
    private ArrayList<Node> children;
    private String class_type;
    private String slot;
    private String slot_value;
    private String[] slot_items;
    private Node current_node;
    //Temporary
    private Set<String> intents;
    private HashMap<String, String> slots;
    private Set<String> appliances;


    public void load(String filename) {

        root = new Node("KnowledgeBase");

        try {
            reader = new FileReader(filename);
            buf = new BufferedReader(reader);
            intents = new HashSet<String>();
            slots = new HashMap<>();
            appliances = new HashSet<>();
            intents.add("next");
            intents.add("previous");
            intents.add("repeat");
            intents.add("exit");

            while (true) {
                //read line
                lineJustFetched = buf.readLine();

                if (lineJustFetched == null) {
                    //System.out.println("End of file read");
                    break;
                } else {
                    //process sentence and put each word into an array.
                    wordsArray = preprocess_sentence(lineJustFetched);
                    if (Integer.parseInt(wordsArray.get(0)) == 0) {
                        //if the line is 0 it contains information about the appliances in use.
                        children = new ArrayList<>();
                        for (int i = 1; i < wordsArray.size(); i++) {
                            name = wordsArray.get(i);
                            temp_node = new Node("Appliance");
                            temp_node.getPropertyChildren().put("applianceName", new StringNode(name));
                            children.add(temp_node);
                            temp_node.setParent(temp_node);
                        }
                        root.getNodeChildren().put("appliance", children);
                    } else if (Integer.parseInt(wordsArray.get(0)) == 1) {
                        //if the line is 1 it means that the line contains information about the slot and intent values.
                        intent = wordsArray.get(1);
                        appliance = wordsArray.get(2);
                        class_type = wordsArray.get(3);
                        slot_list = new ArrayList<String>();
                        children = new ArrayList<>();
                        intents.add(intent);
                        appliances.add(appliance);
                        temp_node = new Node(class_type);

                        //change later on
                        for (int i = 4; i < wordsArray.size(); i++) {
                            slot_list.add(wordsArray.get(i));
                            slot_items = slot_list.get(i - 4).split(":");
                            slots.put(slot_items[1], slot_items[0]);
                            slot = slot_items[0];
                            slot_value = slot_items[1];
                            //Create node and add node.
                            temp_node.getPropertyChildren().put(slot_items[0], new StringNode(slot_items[1]));
                        }

                        children.add(temp_node);
                        //Add node to hashmap
                        root.getNodeFromArray("applianceName", appliance, "appliance").getNodeChildren().put(intent, children);
                        //Set the parent of the current node.
                        temp_node.setParent(root.getNodeFromArray("applianceName", appliance, "appliance"));

                    } else if (Integer.parseInt(wordsArray.get(0)) == 2) {
                        //if the line is 2 it means that the line contains answers to the solution.
                        children = new ArrayList<>();
                        for (int i = 1; i < wordsArray.size(); i += 2) {
                            temp_node = new Node("Step");
                            temp_node.getPropertyChildren().put("stepNumber", new IntegerNode(i / 2 + 1));
                            temp_node.getPropertyChildren().put("stepText", new StringNode(wordsArray.get(i)));
                            temp_node.getPropertyChildren().put("Button", new StringNode(wordsArray.get(i + 1)));
                            children.add(temp_node);
                        }
                        root.getNodeFromArray("applianceName", appliance, "appliance").getNodeFromArray(slot, slot_value, intent).getNodeChildren().put("instructionStep", children);
                        temp_node.setParent(root.getNodeFromArray("applianceName", appliance, "appliance").getNodeFromArray(slot, slot_value, intent));

                    } else {
                        System.out.println("Something is wrong. The first column of the datafile should be 1,2 or 3");
                    }
                }

            }

            reader.close();
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Node getRoot() { return root; }

    public Set<String> getIntents() {
        return intents;
    }

    public HashMap<String, String> getSlots() {
        return slots;
    }

    public Set<String> getAppliances() {
        return appliances;
    }

    public Node matchNode(Node node, String slot, String slot_value, String intent) {
        return node.getNodeFromArray(slot, slot_value, intent);
    }

    public Node matchNodeList(Node node, List<String> slot, List<String> slot_value, String intent) {
        return node.getNodeFromArrayMulti(slot, slot_value, intent);
    }

    /**
     * Breaks the words of a tab delimited sentence.
     * " is removed because for some unknown reason the tsv file puts " on some sentences.
     *
     * @param sentence The question that was given as input
     * @return A list of words in the sentence.
     */
    private static List preprocess_sentence(String sentence) {
        sentence = sentence.replaceAll("[\"]", "");
        String[] sentence_token = sentence.split("\t");
        List sentence_list = Arrays.asList(sentence_token);
        return sentence_list;
    }
}