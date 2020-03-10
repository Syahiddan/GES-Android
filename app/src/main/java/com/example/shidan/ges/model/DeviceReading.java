package com.example.shidan.ges.model;

public class DeviceReading {
    private int part_id;
    private String part_name;
    private double reading;
    private String reading_unit;

    public int getPart_id() {
        return part_id;
    }

    public void setPart_id(int part_id) {
        this.part_id = part_id;
    }

    public String getPart_name() {
        return part_name;
    }

    public void setPart_name(String part_name) {
        this.part_name = part_name;
    }

    public double getReading() {
        return reading;
    }

    public void setReading(double reading) {
        this.reading = reading;
    }

    public String getReading_unit() {
        return reading_unit;
    }

    public void setReading_unit(String reading_unit) {
        this.reading_unit = reading_unit;
    }



}
