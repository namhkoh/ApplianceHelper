package com.company;

public class StringNode implements PropertyNode{

    private String information;

    public StringNode(String information) {
        this.information = information;
    }

    @Override
    public Object getInformation() {
        return information;
    }
}
