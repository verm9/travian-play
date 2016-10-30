package org.verm9.travian.controller;

import org.verm9.travian.dto.Dorf2;
import org.verm9.travian.dto.GameData;

/**
 * Created by nonu on 10/29/2016.
 */
public interface ApplicationController {
    GameData getGameData();

    void buildAtDorf2(int villageId, Dorf2.Building.Type what);

    boolean switchRunningState();


    void changeVillagePriority(int villageId, int priority);

    void buildAllToMaxLevel(int villageId);
}
