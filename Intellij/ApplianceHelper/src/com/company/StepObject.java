package com.company;

/**
 * The class that represents a single step of the solution.
 * An array of StepObjects will be stored in the ResponseObject.
 */
public class StepObject {
    private int step_number;
    private String text;
    private String button_name;

    /**
     * Default constructor that takes in step_number, text and button_name
     * @param step_number The step number of the solution.
     * @param text The actual solution of the question.
     * @param button_name The button that needs to be pressed for this step.
     */
    public StepObject(int step_number, String text, String button_name) {
        this.step_number = step_number;
        this.text = text;
        this.button_name = button_name;
    }

    /**
     * @return The step number of the solution.
     */
    public int getStep_number() {
        return step_number;
    }

    /**
     * @return The actual solution of the question.
     */
    public String getText() {
        return text;
    }

    /**
     * @return The button that needs to be pressed for this step.
     */
    public String getButton_name() {
        return button_name;
    }
}

