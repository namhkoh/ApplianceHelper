package com.aic.libnilu.nlu;

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
        this.dialog_command = "Placeholder";
    }

    /**
     * Default constructor for dialog keywords like next, previous, repeat.
     * @param is_dialog Set to true to identify it as a dialog ResponseObject
     * @param dialog_command The name of the dialog (next, previous, repeat)
     */
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

    /**
     * Print everything in the response object.
     */
    public void printResponseSolution() {
        if(is_dialog){
            System.out.println("Dialog Command: " + dialog_command);
        }
        else {
            for (int i = 0; i < steps.size(); i++) {
                System.out.println("Step number: " + steps.get(i).getStep_number());
                System.out.println("Text: " + steps.get(i).getText());
                System.out.println("Button Name: " + steps.get(i).getButton_name());
            }
        }
    }

    /**
     * Print just the steps in the response object.
     */
    public void printSteps(){
        for(int i = 0; i < steps.size();i++){
            System.out.println(steps.get(i).getText());
        }
    }

    //Will be used from the main
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
