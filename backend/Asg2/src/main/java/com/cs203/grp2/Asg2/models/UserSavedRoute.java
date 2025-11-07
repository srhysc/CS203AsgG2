package com.cs203.grp2.Asg2.models;

import com.cs203.grp2.Asg2.DTO.LandedCostResponse;


public class UserSavedRoute {
    
    //snapshot of calculation
    private LandedCostResponse savedResponse;
    private String name;           

    public UserSavedRoute() {}

    public UserSavedRoute(LandedCostResponse savedResponse,String name) {
        this.savedResponse = savedResponse;
        this.name = name;
    }

    public LandedCostResponse getSavedResponse() { return savedResponse; }
    public void setSavedResponse(LandedCostResponse savedResponse){this.savedResponse = savedResponse;}
    public String getName() { return name; }
    public void setName(String name){this.name = name;}

}
