package org.verm9.travian.dml;

import java.util.Map;

/**
 * Created by nonu on 10/10/2016.
 */
public class DataToSend {
    public enum Type{
        GET,
        POST
    }

    private Type type;
    private Map<String, String> data; // Contains one logical entry

    public DataToSend(Map<String, String> data) {
        this.data = data;
    }

    public DataToSend(Map<String, String> data, Type type) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
