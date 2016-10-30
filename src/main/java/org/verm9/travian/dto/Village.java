package org.verm9.travian.dto;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.*;

/**
 * Created by nonu on 10/5/2016.
 */
public class Village {
    // GAME related data (parsed from the game)
    @JsonView
    private String name;
    @JsonView
    private Point coordinates;
    @JsonView
    private int id;
    @JsonView
    private Dorf1 dorf1 = new Dorf1();
    @JsonView
    private Dorf2 dorf2 = new Dorf2();
    @JsonView
    private Map<Resource, Long> availableResources = new HashMap<>(4);
    @JsonView
    private boolean isCapital = true;

    // APPLICATION LOGIC related data
    @JsonView
    private Queue<BuildingOrder> buildingQueue = new LinkedList<>();
    @JsonView
    private int priority = 1;


    public Village() {
        availableResources.put(Resource.WOOD, -1L);
        availableResources.put(Resource.CLAY, -1L);
        availableResources.put(Resource.IRON, -1L);
        availableResources.put(Resource.CROP, -1L);
    }

    public Village(int id, String name, Point coordinates) {
        this();
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    public Dorf1 getDorf1() {
        return dorf1;
    }

    public void setDorf1(Dorf1 dorf1) {
        this.dorf1 = dorf1;
    }

    public Dorf2 getDorf2() {
        return dorf2;
    }

    public void setDorf2(Dorf2 dorf2) {
        this.dorf2 = dorf2;
    }

    public Map<Resource, Long> getAvailableResources() {
        return availableResources;
    }

    public void setAvailableResources(Map<Resource, Long> availableResources) {
        this.availableResources = availableResources;
    }

    public boolean isDorf1Empty() {
        return dorf1.getFields().isEmpty();
    }

    public boolean isDorf2Empty() {
        return dorf2.getBuildings().isEmpty();
    }

    public boolean areAvailableResourcesEmpty() {
        for (Long i : availableResources.values()) {
            if (i != -1L) {
                return false;
            }
        }
        return true;
    }

    public boolean isCapital() {
        return isCapital;
    }

    public void setCapital(boolean capital) {
        isCapital = capital;
    }

    public Queue<BuildingOrder> getBuildingQueue() {
        return buildingQueue;
    }

    public void setBuildingQueue(Queue<BuildingOrder> buildingQueue) {
        this.buildingQueue = buildingQueue;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if (priority < 0) {
            this.priority = 0;
        } else {
            this.priority = priority;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public enum Resource {
        WOOD,
        CLAY,
        IRON,
        CROP
    }

    @Override
    public String toString() {
        return "Village {" + name + "}";
    }

}
