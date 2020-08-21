package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node in this graph data structure.
 */
public class Node {

    private Node parent;
    private String classname;
    private HashMap<String, ArrayList<Node>> nodeChildren;
    private HashMap<String, PropertyNode> propertyChildren;
    private ResponseObject solution;

    /**
     * Deafult constructor that takes in the name of the class.
     * There is no significance of the classname yet.
     * @param classname The type of this node (Food)
     */
    public Node(String classname) {
        this.classname = classname;
        nodeChildren = new HashMap<String, ArrayList<Node>>();
        propertyChildren = new HashMap<>();
    }

    /**
     * Set the parent of the current node.
     * @param parent The node that is the parent of this node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Get the hashmap of the children nodes of the current node.
     * @return hashmap of children.
     */
    public HashMap<String, ArrayList<Node>> getNodeChildren() {
        return nodeChildren;
    }

    /**
     * Get the hashmap of the property nodes of the current node.
     * @return hasmap of property nodes.
     */
    public HashMap<String, PropertyNode> getPropertyChildren() {
        return propertyChildren;
    }

    //Get the specific node that has a particular slot value and slot property.
    public Node getNodeFromArray(String slot, String slot_value, String intent) {
//        System.out.println(slot+";"+slot_value+";"+intent);
        ArrayList<Node> array = getNodeChildren().get(intent);
        Node temp = null;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getPropertyChildren().get(slot).getInformation().equals(slot_value)) {
                temp = array.get(i);
            }
        }
        return temp;
    }

    public Node getNodeFromArrayMulti(List<String> slot, List<String> slot_value, String intent) {
        ArrayList<Node> array = getNodeChildren().get(intent);
        Node temp = null;
        Boolean match = false;
        for(int j = 0; j < slot.size(); j++) {
            match = false;
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).getPropertyChildren().get(slot.get(j)).getInformation().equals(slot_value.get(j))) {
                    temp = array.get(i);
                    match = true;
                    break;
                }
            }
            if(match == false){
                System.out.println("No Match");
                temp = null;
                break;
            }
        }
        return temp;
    }

    /**
     * Convenient method to get a quick printout of the information of the solution.
     * @param intent The intent is the edge. For now it will always be instructionStep.
     * @param question The question that was asked from the user. Store in the response object for convenience.
     * @return
     */
    public ResponseObject getSolution(String intent, String question) {
        solution = new ResponseObject("Oven", "000-000-000", question);

        for (Node a : nodeChildren.get(intent)) {
            a.getPropertyChildren().get("Button");
            a.getPropertyChildren().get("stepNumber");
            a.getPropertyChildren().get("stepText");
            solution.getSteps().add(new StepObject((int) a.getPropertyChildren().
                    get("stepNumber").getInformation(), (String) a.getPropertyChildren().
                    get("stepText").getInformation(), (String) a.getPropertyChildren().
                    get("Button").getInformation()));
        }
        return solution;
    }

    //May not have a use.
    public Node getParent() {
        return parent;
    }

    public String getClassname() {
        return classname;
    }




}
