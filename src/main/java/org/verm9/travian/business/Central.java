package org.verm9.travian.business;

import org.verm9.travian.dml.dto.Dorf2;

/**
 * Created by nonu on 10/2/2016.
 */
public interface Central {
    public void login();

    void getBuldings();

    void dorf1Build(int id);

    void dorf2Build(int idOfPlace, Dorf2.Building.Type whatToBuild);
}
