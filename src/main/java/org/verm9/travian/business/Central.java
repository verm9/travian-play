package org.verm9.travian.business;

import org.verm9.travian.dml.dto.Dorf2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 10/2/2016.
 */
public interface Central {
    public void login() throws IOException;

    void getBuldings() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException;

    void dorf1Build(int id) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException;

    void dorf2Build(int idOfPlace, Dorf2.Building.Type whatToBuild) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException;
}
