package com.cs203.grp2.Asg2.DTO;

import java.util.List;

public class ConvertableResponseDTO {
    private String hscode;
    private String name;
    private List<ConvertToResponseDTO> to;

    public ConvertableResponseDTO(String hscode, String name, List<ConvertToResponseDTO> to) {
        this.hscode = hscode;
        this.name = name;
        this.to = to;
    }

    public String getHscode() { return hscode; }
    public String getName() { return name; }
    public List<ConvertToResponseDTO> getTo() { return to; }
}