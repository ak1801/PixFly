package com.pml.pixfly;

import android.app.Application;

/**
 * Created by aksmahaj on 3/25/2017.
 */
public class PixFlyApplication extends Application {
    private String mGlobalVarValue;

    public String getGlobalVarValue() {
        return mGlobalVarValue;
    }

    public void setGlobalVarValue(String str) {
        mGlobalVarValue = str;
    }
}
