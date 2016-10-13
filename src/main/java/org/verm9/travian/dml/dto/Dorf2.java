package org.verm9.travian.dml.dto;

import org.verm9.travian.dml.WrongBuildingIdException;

import java.util.*;

/**
 * Created by nonu on 10/5/2016.
 */
public class Dorf2 {
    private Map<Integer, Building> buildings = new HashMap<>();

    public static class Building {
        private Type type;
        private int level;


        // The enum holds a bunch of metadata about buildings.
        public enum Type {
            NO_DATA(0, -1),
            MAIN_BUILDING(20, 26),
            WAREHOUSE(20, 10),
            GRANARY(20, 11),
            RALLY_POINT(20,16),
            EMBASSY(20, 18),
            CRANNY(20, 23),
            PALISADE(20, 33);

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

            private final int maxLevel;
            private final int id;
            private final List<Building> requirements;

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

    public Map<Integer, Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(Map<Integer, Building> buildings) {
        this.buildings = buildings;
    }
}
