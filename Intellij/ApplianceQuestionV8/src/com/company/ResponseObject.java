package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * This class stores the solution and information of the question asked.
 */
public class ResponseObject {

    private String appliance_name;
    private String model_number;
    private ArrayList<StepObject> steps;
    private boolean is_dialog;
    private String dialog_command;
    private String question;

    /**
     * Default constructor that takes in appliance_name and model_number.
     * @param appliance_name The name of the appliance
     * @param model_number The model number of this appliance.
     */
    public ResponseObject(String appliance_name, String model_number, String question) {
        this.appliance_name = appliance_name;
        this.model_number = model_number;
        this.question = question;
        this.steps = new ArrayList<StepObject>();
        this.is_dialog = false;
    }

    public ResponseObject(boolean is_dialog, String dialog_command){
        this.is_dialog = is_dialog;
        this.dialog_command = dialog_command;
    }

    /**
     * @return The array that contains StepObject instances.
     */
    public ArrayList<StepObject> getSteps() {
        return steps;
    }

    //Print everything inside the Step Object (Debugging purpose)
    public void printResponseSolution() {
        for (int i = 0; i < steps.size(); i++) {
            System.out.println("Step number: " + steps.get(i).getStep_number());
            System.out.println("Text: " + steps.get(i).getText());
            System.out.println("Button Name: " + steps.get(i).getButton_name());
        }
    }

    //Print only the text (Debugging purpose)
    public void printSteps(){
        for(int i = 0; i < steps.size();i++){
            System.out.println(steps.get(i).getText());
        }
    }

    public String getAppliance_name() {
        return appliance_name;
    }

    public String getModel_number() {
        return model_number;
    }

    public boolean isIs_dialog() {
        return is_dialog;
    }

    public String getDialog_command() {
        return dialog_command;
    }

    public String getQuestion() {
        return question;
    }
}
