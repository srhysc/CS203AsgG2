package com.cs203.grp2.Asg2.models;

import com.cs203.grp2.Asg2.DTO.LandedCostResponse;


public class UserSavedRoute {
    
    //snapshot of calculation
    private LandedCostResponse response;
    private String name;           


    public UserSavedRoute(LandedCostResponse response,String name) {
        this.response = response;
        this.name = name;
    }

    public LandedCostResponse getRequest() { return response; }
    public String getName() { return name; }
}
