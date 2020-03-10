package com.example.shidan.ges.model;

public class DeviceComp {
    private static final int PART_ON = 1;
    private static final int PART_OFF = 0;
    private static final int PART_BROKE = -1;

    private int comp_id;
    private String comp_name;
    private int comp_status;
    private int comp_online;

    public int getComp_id() {
        return comp_id;
    }

    public void setComp_id(int comp_id) {
        this.comp_id = comp_id;
    }

    public String getComp_name() {
        return comp_name;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public int getComp_status() {
        return comp_status;
    }

    public void setComp_status(int comp_status) {
        this.comp_status = comp_status;
    }

    public int getComp_online() {
        return comp_online;
    }

    public void setComp_online(int comp_online) {
        this.comp_online = comp_online;
    }
}
