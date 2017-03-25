package com.pml.pixfly;

/**
 * Created by aksmahaj on 12/3/2016.
 */
public interface Constants {
    String LAUNCH = "launch";
    String GOTO = "goto";
    String LAND = "land";
    String FOLLOW_ME = "followme";
    String TRACE_PATH = "tracepath";
    String GUIDED = "GUIDED";
    public enum MODE { GUIDED, LAND, AUTO, RTL, STABILIZE }
    int DEFAULT_DELAY = 1000; //milliseconds
    public static final String TRACE_ME_FILE = "pixfly_trace.txt";
}
