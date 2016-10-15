package org.verm9.travian.dto;

/**
 * Created by nonu on 10/15/2016.
 */
public class GameData {
    private Village village = new Village();

    public void updateVillage(Village from) {
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

    public Village getVillage() {
        return village;
    }
}
