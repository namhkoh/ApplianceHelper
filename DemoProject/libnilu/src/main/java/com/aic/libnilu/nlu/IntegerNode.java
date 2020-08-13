package com.aic.libnilu.nlu;

public class IntegerNode implements PropertyNode{

    private Integer information;

    public IntegerNode(Integer information) {
        this.information = information;
    }

    @Override
    public Integer getInformation() {
        return information;
    }
}
