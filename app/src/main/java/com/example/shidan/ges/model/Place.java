package com.example.shidan.ges.model;

import java.util.ArrayList;

public class Place {
    private String place_id;
    private String place_name;
    private Plant plant;
    private User monitor;
    private PlantState plantState;

    public PlantState getPlantState() {
        return plantState;
    }

    public void setPlantState(PlantState plantState) {
        this.plantState = plantState;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Place(String place_id) {
        this.place_id = place_id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public User getMonitor() {
        return monitor;
    }

    public void setMonitor(User monitor) {
        this.monitor = monitor;
    }



}
