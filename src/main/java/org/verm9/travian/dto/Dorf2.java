package org.verm9.travian.dto;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.verm9.travian.dml.WrongBuildingIdException;

import java.util.*;

/**
 * Created by nonu on 10/5/2016.
 */
public class Dorf2 {
    private BidiMap<Integer, Building> buildings = new DualHashBidiMap();

    public static class Building {
        private Type type;
        private int level;


        // The enum holds a bunch of metadata about buildings.
        public enum Type {
            NO_DATA(0, -1),
            MAIN_BUILDING(20, 15),
            WAREHOUSE(20, 10),
            GRANARY(20, 11),
            RALLY_POINT(20,16),
            EMBASSY(20, 18),
            CRANNY(10, 23),
            PALISADE(20, 33),
            SAWMILL(5, 5, new Building(MAIN_BUILDING, 5)), // for this and 4 next, additional requirement: level 5/10 of related field
            BRICKWORKS(5, 6, new Building(MAIN_BUILDING, 5)),
            IRON_FOUNDRY(5, 7, new Building(MAIN_BUILDING, 5)),
            GRAIN_MILL(5, 8, new Building(MAIN_BUILDING, 5)),
            BAKERY(5, 9, new Building(MAIN_BUILDING, 5), new Building(GRAIN_MILL, 5)), // level 10 of the field
            TOURNAMENT_SQUARE(20, 14, new Building(RALLY_POINT, 15)),
            MARKETPLACE(20, 17, new Building(MAIN_BUILDING, 3), new Building(WAREHOUSE, 1), new Building(GRANARY, 1)),
            BARRACKS(20, 19, new Building(MAIN_BUILDING, 3), new Building(RALLY_POINT, 1)),
            RESIDENCE(20, 25, new Building(MAIN_BUILDING, 5)), // additional requirement: no Palace
            TREASURY(20, 27, new Building(MAIN_BUILDING, 10)), // additional requirement: no Wonder
            TRAPPER(20, 36, new Building(RALLY_POINT, 1)),
            HEROMANSION(20, 37, new Building(MAIN_BUILDING, 3), new Building(RALLY_POINT, 1)),
            ACADEMY(20, 22, new Building(MAIN_BUILDING, 3), new Building(BARRACKS, 3)),
            SMITHY(20, 12,  new Building(MAIN_BUILDING, 3),  new Building(ACADEMY, 3)),
            WORKSHOP(20, 21,  new Building(MAIN_BUILDING, 5),  new Building(ACADEMY, 10)),
            TOWN_HALL(20, 24,  new Building(MAIN_BUILDING, 10), new Building(ACADEMY, 10)),
            STABLE(20, 20,  new Building(ACADEMY, 5),  new Building(SMITHY, 3)),
            TRADE_OFFICE(20, 28,  new Building(STABLE, 10),  new Building(MARKETPLACE, 20));

            private final int maxLevel;
            private final int id;
            private final List<Building> requirements;

            Type(int maxLevel, int id, Building... requirements) {
                this.maxLevel = maxLevel;
                this.id = id;
                this.requirements = new LinkedList<>(Arrays.asList(requirements));
            }
            Type(int maxLevel, int id) {
                this.maxLevel = maxLevel;
                this.id = id;
                this.requirements = null;
            }

            public int getMaxLevel() {
                return maxLevel;
            }
            public static Type getById(int id) {
                for (Type t : Type.values()) {
                    if (t.id == id)
                        return t;
                }
                throw new WrongBuildingIdException(id + "doesn't exist.");
            }
            public int getId() {
                return id;
            }

            public List<Building> getRequirements() {
                return requirements;
            }
        }

        public Building(Type type, int level) {
            this.type = type;
            this.level = level;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }

    public BidiMap<Integer, Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(Map<Integer, Building> buildings) {
        this.buildings = new DualHashBidiMap<>(buildings);
    }
}
