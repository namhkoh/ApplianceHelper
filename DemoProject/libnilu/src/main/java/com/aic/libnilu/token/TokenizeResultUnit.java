package com.aic.libnilu.token;

public class TokenizeResultUnit {
    private String token;
    private String orgToken;
    private Integer spaceInfo;
    private boolean neverSplit;

    public TokenizeResultUnit(String token, String orgToken, Integer spaceInfo, boolean neverSplit){
        this.token = token;
        this.orgToken = orgToken;
        this.spaceInfo = spaceInfo;
        this.neverSplit = neverSplit;
    }

    public String getToken(){
        return this.token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getOrgToken(){
        return this.orgToken;
    }

    public void setOrgToken(String orgToken){
        this.orgToken = orgToken;
    }

    public Integer getSpaceInfo(){
        return this.spaceInfo;
    }

    public void setSpaceInfo(Integer spaceInfo){
        this.spaceInfo = spaceInfo;
    }

    public boolean getNeverSplit(){
        return this.neverSplit;
    }
}
