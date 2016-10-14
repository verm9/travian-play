package org.verm9.travian.dml.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nonu on 10/4/2016.
 */
public class Dorf1 {
    private Map<Integer, ResourceField> fields = new HashMap<>();

    public Dorf1() {
    }

    public Map<Integer, ResourceField> getFields() {
        return fields;
    }

    public void setFields(Map<Integer, ResourceField> fields) {
        this.fields = fields;
    }

    public static class ResourceField {
        private ResourceType type;
        private int level;

        public ResourceType getType() {
            return type;
        }

        public void setType(ResourceType type) {
            this.type = type;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
        public enum ResourceType {
            WOODCUTTER,
            CLAY_PIT,
            IRON_MINE,
            CROPLAND,
            FULLY_UPGRADED  // there is no easy way to understand which one exactly fully upgraded field is
        }

        @Override
        public String toString() {
            return "ResourceField{" +
                    "type=" + type +
                    ", level=" + level +
                    '}';
        }
    }


}
