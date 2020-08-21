package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    private Node parent;
    private String classname;
    private HashMap<String, ArrayList<Node>> nodeChildren;
    private HashMap<String, PropertyNode> propertyChildren;
    private String key;
    private PropertyNode value;
    private String value_info;
    private ResponseObject solution;


    public Node(String classname) {
        this.classname = classname;
        nodeChildren = new HashMap<String, ArrayList<Node>>();
        propertyChildren = new HashMap<>();
    }

    public HashMap<String, ArrayList<Node>> getNodeChildren() {
        return nodeChildren;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getClassname() {
        return classname;
    }

    //Get the specific node that has a particular slot value and slot property.
    public Node getNodeFromArray(String slot, String slot_value, String intent) {
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

    //Return the solution ResponseObject
    public ResponseObject getSolution(KnowledgeBase knowledge, String intent, String question) {
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

    public HashMap<String, PropertyNode> getPropertyChildren() {
        return propertyChildren;
    }



}
