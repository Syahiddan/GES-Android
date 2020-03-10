package com.example.shidan.ges.model;

public class Schedule {
    private int schedule_id;
    private String day_id;
    private String time;
    private String status;
    private int ard_id ;

    public int getArd_id() {
        return ard_id;
    }

    public void setArd_id(int ard_id) {
        this.ard_id = ard_id;
    }



    public int getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(int schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getDay_id() {
        return day_id;
    }

    public void setDay_id(int day_id) {
        String temp = ((day_id == 1)? "Monday":(day_id == 2)? "Tuesday":(day_id == 3)?"Wednesday":(day_id == 4)?"Thursday":(day_id == 5)?"Friday":(day_id == 6)?"Saturday":(day_id == 7)?"Sunday":"Error");
        this.day_id = temp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
