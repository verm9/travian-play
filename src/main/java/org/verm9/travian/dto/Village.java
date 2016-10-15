package org.verm9.travian.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nonu on 10/5/2016.
 */
public class Village {
    private Dorf1 dorf1 = new Dorf1();
    private Dorf2 dorf2 = new Dorf2();
    private Map<Resource, Integer> availableResources = new HashMap<>(4);

    public Village() {
        availableResources.put(Resource.WOOD, -1);
        availableResources.put(Resource.CLAY, -1);
        availableResources.put(Resource.IRON, -1);
        availableResources.put(Resource.CROP, -1);
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

    public Map<Resource, Integer> getAvailableResources() {
        return availableResources;
    }

    public void setAvailableResources(Map<Resource, Integer> availableResources) {
        this.availableResources = availableResources;
    }

    public boolean isDorf1Empty() {
        return dorf1.getFields().isEmpty();
    }

    public boolean isDorf2Empty() {
        return dorf2.getBuildings().isEmpty();
    }

    public boolean areAvailableResourcesEmpty() {
        for (Integer i : availableResources.values()) {
            if (i != -1) {
                return false;
            }
        }
        return true;
    }

    public enum Resource {
        WOOD,
        CLAY,
        IRON,
        CROP
    }
}
