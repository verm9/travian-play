package org.verm9.travian.dto;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nonu on 10/15/2016.
 */
public class GameData {
    @JsonView
    private Map<Integer, Village> villages = new HashMap<>();

    @JsonView
    Integer currentVillageId = -1;

    public void updateCurrentVillage(Village from) {
        Village village = getCurrentVillage();
        if (!from.isDorf1Empty()) {
            village.setDorf1( from.getDorf1() );
        }

        if (from.isDorf2Empty()) {
            village.setDorf2( from.getDorf2() );
        }

        if (from.areAvailableResourcesEmpty()) {
            village.setAvailableResources( from.getAvailableResources() );
        }
    }

    public Village getCurrentVillage() {
        return villages.get(currentVillageId);
    }

    public void setCurrentVillageId(Integer currentVillageId) {
        this.currentVillageId = currentVillageId;
    }

    public void addNewVillageIfNotAdded(Integer id, Village village) {
        if (!villages.containsKey(id)) {
            villages.put(id, village);
        }

    }

    public Village getVillage(Integer id) {
        return villages.get(id);
    }
}
