package com.pml.pixfly;

import java.util.Date;

/**
 * Created by aksmahaj on 3/25/2017.
 */
public class Mission {
    private String mission_name;
    private Date launch_date;
    private Double latitude;
    private Double longitude;
    private Double altitude;

    public String getMission_name() {
        return mission_name;
    }

    public void setMission_name(String mission_name) {
        this.mission_name = mission_name;
    }

    public Date getLaunch_date() {
        return launch_date;
    }

    public void setLaunch_date(Date launch_date) {
        this.launch_date = launch_date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public String toString(){
        return mission_name+":"+launch_date.toString()+";"+latitude.toString()+","+longitude.toString();
    }
}
