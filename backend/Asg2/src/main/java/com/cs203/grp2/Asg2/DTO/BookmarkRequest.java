package com.cs203.grp2.Asg2.DTO;

import com.cs203.grp2.Asg2.DTO.LandedCostResponse;


public class BookmarkRequest {
    private LandedCostResponse response;
    private String bookmarkName;

    public String getBookmarkName() { return bookmarkName; }
    public void setBookmarkName(String bookmarkName) { this.bookmarkName = bookmarkName; }

    public LandedCostResponse getReponse(){return response;}
}


