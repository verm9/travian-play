package org.verm9.travian.dto;

/**
 * Created by nonu on 10/15/2016.
 */
public class BuildingOrder {
    private Integer whereToBuild;
    private Dorf2.Building.Type whatToBuild;

    public BuildingOrder(Integer whereToBuild, Dorf2.Building.Type whatToBuild) {
        this.whereToBuild = whereToBuild;
        this.whatToBuild = whatToBuild;
    }

    public Integer getWhere() {
        return whereToBuild;
    }

    public Dorf2.Building.Type getWhat() {
        return whatToBuild;
    }

    @Override
    public String toString() {
        return "BuildingOrder{" +
                whatToBuild +
                " on " + whereToBuild +
                '}';
    }
}
