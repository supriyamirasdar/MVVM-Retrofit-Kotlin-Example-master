package com.landmarkgroup.smartkiosk.ui.pdppage.model;

public class ColorDto {
    private  String id;
    private String colour;

    public ColorDto(String id, String colour) {
        this.id = id;
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
