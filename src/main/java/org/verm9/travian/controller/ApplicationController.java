package org.verm9.travian.controller;

import org.verm9.travian.dto.Dorf2;
import org.verm9.travian.dto.GameData;
import org.verm9.travian.model.LoginData;

/**
 * Created by nonu on 10/29/2016.
 */
public interface ApplicationController {
    GameData getGameData();

    void buildAtDorf2(int villageId, Dorf2.Building.Type what, int level);

    boolean isLoginDataPresent();

    void setLoginData(LoginData loginData);

    void stop();

    void start();

    boolean switchRunningState();

    void changeVillagePriority(int villageId, int priority);

    void buildAllToMaxLevel(int villageId);

    void buildPattern(int villageId, int pattern);
}
