package org.verm9.travian.dto;

import java.awt.*;
import java.util.*;

/**
 * Created by nonu on 10/5/2016.
 */
public class Village {
    private String name;
    private Point coordinates;
    private Dorf1 dorf1 = new Dorf1();
    private Dorf2 dorf2 = new Dorf2();
    private Map<Resource, Long> availableResources = new HashMap<>(4);
    private boolean isCapital = true;
    private Queue<BuildingOrder> buildingQueue = new LinkedList<>();

    public Village() {
        availableResources.put(Resource.WOOD, -1L);
        availableResources.put(Resource.CLAY, -1L);
        availableResources.put(Resource.IRON, -1L);
        availableResources.put(Resource.CROP, -1L);
    }

    public Village(String name, Point coordinates) {
        this();
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

    public enum Resource {
        WOOD,
        CLAY,
        IRON,
        CROP
    }

    @Override
    public String toString() {
        return "Village{" +
                "coordinates=" + coordinates +
                ", name='" + name + '\'' +
                ", isCapital=" + isCapital +
                '}';
    }
}
