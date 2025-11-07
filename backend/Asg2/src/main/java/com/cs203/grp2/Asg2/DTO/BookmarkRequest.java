package com.cs203.grp2.Asg2.DTO;

import com.cs203.grp2.Asg2.DTO.LandedCostResponse;


public class BookmarkRequest {
    private LandedCostResponse savedResponse;
    private String bookmarkName;

    public String getBookmarkName() { return bookmarkName; }
    public void setBookmarkName(String bookmarkName) { this.bookmarkName = bookmarkName; }

    public LandedCostResponse getSavedResponse(){return savedResponse;}
    public void setSavedResponse(LandedCostResponse savedResponse) { this.savedResponse = savedResponse; }

}


