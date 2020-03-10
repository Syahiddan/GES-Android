package com.example.shidan.ges.model;

import java.util.ArrayList;

public class ScheduleGroup {
    private int day_id;
    private String day_name;
    private ArrayList<Schedule> schedules;

    public int getDay_id() {
        return day_id;
    }

    public void setDay_id(int day_id) {
        this.day_id = day_id;
    }

    public String getDay_name() {
        return day_name;
    }

    public void setDay_name(String day_name) {
        this.day_name = day_name;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }



}
