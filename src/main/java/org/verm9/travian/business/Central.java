package org.verm9.travian.business;

import org.verm9.travian.dto.GameData;
import org.verm9.travian.dto.Village;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 10/14/2016.
 */
public interface Central {

    Village getCurrentVillage();
    Village setCurrentVillage(Integer villageId);

    GameData getGameData();

    void addNewVillageIfNotAdded(Integer id, Village village);

    void buildAllToMaxLevel();

    void mainCycle();

    Village getVillage(Integer id);
}
