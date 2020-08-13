package com.aic.libnilu.nlu;

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
    private ArrayList<Node> child_temp;
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
                //System.out.println("Current line: " + lineJustFetched);
                if ((lineJustFetched == null) || (lineJustFetched.isEmpty())) {
                    //System.out.println("End of file read");
                    break;
                } else {

                    //process sentence and put each word into an array.
                    wordsArray = preprocess_sentence(lineJustFetched);

                    //if the line is 0 it contains information about the appliances in use.
                    if (Integer.parseInt(wordsArray.get(0)) == 0) {
                        children = new ArrayList<>();
                        for (int i = 1; i < wordsArray.size(); i++) {
                            //Create the appliance node.
                            name = wordsArray.get(i);
                            temp_node = new Node("Appliance");
                            temp_node.getPropertyChildren().put("applianceName", new StringNode(name));
                            //Array List
                            children.add(temp_node);
                            //Single node in the array list. Every node must be set a parent individually.
                            temp_node.setParent(temp_node);
                        }
                        root.getNodeChildren().put("appliance", children);

                        //if the line is 1 it means that the line contains information about the slot and intent values.
                    } else if (Integer.parseInt(wordsArray.get(0)) == 1) {
                        intent = wordsArray.get(1);
                        appliance = wordsArray.get(2);
                        class_type = wordsArray.get(3);
                        slot_list = new ArrayList<String>();

                        //If the intent already exists get that intent and add another node to it.
                        if(root.getNodeFromArray("applianceName", appliance, "appliance").getNodeChildren().containsKey(intent)) {
                            //System.out.println("Intent exists");
                            children = root.getNodeFromArray("applianceName", appliance, "appliance").getNodeChildren().get(intent);
                        }
                        else {
                            //System.out.println("Intent doesn't exist");
                            children = new ArrayList<>();
                        }

                        //List all of the intent values and slot values so the NLU can do the matching.
                        //Will not be needed when the NLU is replaced with the python slot-intent tagger.
                        intents.add(intent);
                        appliances.add(appliance);


                        //Add all the slot values of this node. May have multiple slots.
                        //Everything after the fourth element is a slotname:slot pair.
                        temp_node = new Node(class_type);
                        for (int i = 4; i < wordsArray.size(); i++) {
                            slot_list.add(wordsArray.get(i));
                            slot_items = slot_list.get(i - 4).split(":");
                            slots.put(slot_items[1], slot_items[0]);
                            slot = slot_items[0];
                            slot_value = slot_items[1];
                            //Create node and add node.
                            temp_node.getPropertyChildren().put(slot_items[0], new StringNode(slot_items[1]));
                            //System.out.println(intent + "*" + appliance + "*" + slot_list.get(i-4));
                        }

                        children.add(temp_node);

                        //If is it not a newly created node (intent had already existed). There is nothing to do.
                        if(root.getNodeFromArray("applianceName", appliance, "appliance").getNodeChildren().containsKey(intent)) {
                            //System.out.println("Don't need to add pointers");
                        }
                        //The node was made this time.
                        else {
                            //Set the parent of the current node.
                            //Add node to hashmap
                            root.getNodeFromArray("applianceName", appliance, "appliance").getNodeChildren().put(intent, children);
                            temp_node.setParent(root.getNodeFromArray("applianceName", appliance, "appliance"));
                        }

                    } else if (Integer.parseInt(wordsArray.get(0)) == 2) {
                        //if the line is 2 it means that the line contains answers to the solution.
                        //System.out.println(slots.get(slot_value) + " "+ slot_value);
                        children = new ArrayList<>();
                        for (int i = 1; i < wordsArray.size(); i += 2) {
                            temp_node = new Node("Step");
                            temp_node.getPropertyChildren().put("stepNumber", new IntegerNode(i / 2 + 1));
                            temp_node.getPropertyChildren().put("stepText", new StringNode(wordsArray.get(i)));
                            temp_node.getPropertyChildren().put("Button", new StringNode(wordsArray.get(i + 1)));
                            children.add(temp_node);
                        }
                        //Get the correct node and put in the array.
                        root.getNodeFromArray("applianceName", appliance, "appliance").getNodeFromArray(slot, slot_value, intent).getNodeChildren().put("instructionStep", children);
                        //Set the parent of this newly created list of property nodes.
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

    /**
     * Match a node with exactly one slot value.
     * Could be removed for the matchNodeList method because they do similar things.
     * Keeping it for easy debugging.
     * @param node current node
     * @param slot slot value to be found
     * @param slot_value the type of the slot value.
     * @param intent the intent to be found.
     * @return
     */
    public Node matchNode(Node node, String slot, String slot_value, String intent) {
        return node.getNodeFromArray(slot, slot_value, intent);
    }

    /**
     * Match a node with one or more slot value
     * @param node current node
     * @param slot slot value to be found
     * @param slot_value the type of the slot value.
     * @param intent the intent to be found.
     * @return
     */
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
        sentence = sentence.toLowerCase();
        sentence = sentence.replaceAll("[\"]", "");
        String[] sentence_token = sentence.split("\t");
        List sentence_list = Arrays.asList(sentence_token);
        return sentence_list;
    }

    /**
     * Returns the root node
     * @return
     */
    public Node getRoot() { return root; }

    /**
     * Get the list of all the intents of this knowledgebase.
     * Will not be needed when NLU is replaced with the slot intent tagger.
     * @return Set string of all the intents.
     */
    public Set<String> getIntents() {
        return intents;
    }

    /**
     * Get the hashmap of all the intents of this knowledgebase.
     * Will not be needed when NLU is replaced with the slot intent tagger.
     * @return Set string of all the intents.
     */
    public HashMap<String, String> getSlots() {
        return slots;
    }

    /**
     * Get the list of all the possible appliances of this knowledgebase.
     * Will not be needed when NLU is replaced with the slot intent tagger.
     * @return Set string of all the appliances.
     */
    public Set<String> getAppliances() {
        return appliances;
    }

}