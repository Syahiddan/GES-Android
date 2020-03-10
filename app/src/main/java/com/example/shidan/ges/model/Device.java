package com.example.shidan.ges.model;

import java.util.ArrayList;

public class Device {
    int device_id;
    String device_name;
    String place_id;
    int isOnline;
    ArrayList<DeviceComp> deviceComps;
    ArrayList<ScheduleGroup> scheduleGroups;
    ArrayList<DeviceReading> deviceReadings;

    public ArrayList<ScheduleGroup> getScheduleGroups() {
        return scheduleGroups;
    }

    public void setScheduleGroups(ArrayList<ScheduleGroup> scheduleGroups) {
        this.scheduleGroups = scheduleGroups;
    }



    public ArrayList<DeviceReading> getDeviceReadings() {
        return deviceReadings;
    }

    public void setDeviceReadings(ArrayList<DeviceReading> deviceReadings) {
        this.deviceReadings = deviceReadings;
    }






    public ArrayList<DeviceComp> getDeviceComps() {
        return deviceComps;
    }

    public void setDeviceComps(ArrayList<DeviceComp> deviceComps) {
        this.deviceComps = deviceComps;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }
}
