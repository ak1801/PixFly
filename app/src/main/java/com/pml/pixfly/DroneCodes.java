package com.pml.pixfly;

/**
 * Created by aksmahaj on 12/3/2016.
 */
public interface DroneCodes {
    String LAUNCH = "launch";
    String GOTO = "goto";
    String LAND = "land";
    String FOLLOW_ME = "followme";
    String GUIDED = "GUIDED";
    public enum MODE { GUIDED, LAND, AUTO, RTL, STABILIZE }
}
