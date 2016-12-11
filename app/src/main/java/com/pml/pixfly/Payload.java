package com.pml.pixfly;

/**
 * Created by aksmahaj on 11/20/2016.
 */
public class Payload {
    private String command = null;
    private DroneCodes.MODE mode = null;
    private double lat = 0.0;
    private double lon = 0.0;
    private double alt = 0.0;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public DroneCodes.MODE getMode() {
        return mode;
    }

    public void setMode(DroneCodes.MODE mode) {
        this.mode = mode;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setAlt(double alt) { this.alt = alt; }

    public double getAlt() { return alt; }
}
