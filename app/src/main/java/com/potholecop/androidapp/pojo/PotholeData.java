package com.potholecop.androidapp.pojo;

public class PotholeData {

    private Location location;
    private String timestamp;
    private Boolean isFixed;
    private String depth;
    private String diameter;
    private String severity;

    public Location getLocation() {
        return location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Boolean getFixed() {
        return isFixed;
    }

    public String getDepth() {
        return depth;
    }

    public String getDiameter() {
        return diameter;
    }

    public String getSeverity() {
        return severity;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public void setFixed(Boolean fixed) {
        isFixed = fixed;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
