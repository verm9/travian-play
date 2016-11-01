package org.verm9.travian.business;

import org.verm9.travian.dto.Dorf2;
import org.verm9.travian.dto.GameData;
import org.verm9.travian.dto.Village;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 10/14/2016.
 */
public interface Central extends Runnable{

    Village getCurrentVillage();
    Village setCurrentVillage(Integer villageId);

    GameData getGameData();

    void addNewVillageIfNotAdded(Integer id, Village village);

    void buildAllToMaxLevel(int villageId);

    void mainCycle();

    Village getVillage(Integer id);

    void changeVillagePriority(int villageId, int priority);

    boolean isPaused();

    void setPaused(boolean paused);

    boolean isAlive();

    void start();

    void buildAtDorf2(int villageId, Dorf2.Building.Type what, int level);
}
