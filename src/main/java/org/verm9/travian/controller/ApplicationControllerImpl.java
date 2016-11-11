package org.verm9.travian.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.verm9.travian.business.Central;
import org.verm9.travian.dto.Dorf2;
import org.verm9.travian.dto.GameData;
import org.verm9.travian.model.LoginData;

/**
 * Created by nonu on 10/29/2016.
 */
@Component
public class ApplicationControllerImpl implements ApplicationController {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationControllerImpl.class);

    @Autowired
    private Central central;

    @Override
    public GameData getGameData() {
        return central.getGameData();
    }

    @Override
    public boolean isLoginDataPresent() {
        return central.isLoginDataPresent();
    }

    @Override
    public void setLoginData(LoginData loginData) {
        central.setLoginData(loginData.getServer(), loginData.getLogin(), loginData.getPassword());
    }

    @Override
    public void stop() {
        central.interrupt();
    }

    @Override
    public synchronized void start() {
        if (!central.isAlive()) {
            central.start();
        }
    }

    @Override
    public boolean switchRunningState() {
        LOG.info("Switching game state to " + (central.isPaused() ? "running." : "paused."));
        central.setPaused( !central.isPaused() );
        synchronized (central) {
            central.notifyAll();
        }
        return central.isPaused();
    }

    @Override
    public void buildAtDorf2(int villageId, Dorf2.Building.Type what, int level) {
        LOG.info("Village " + villageId + ": build " + what);
        central.buildAtDorf2(villageId, what, level);

    }

    @Override
    public void changeVillagePriority(int villageId, int priority) {
        LOG.info("Village " + villageId + ": priority " + priority + " is set.");
        central.changeVillagePriority(villageId, priority);
    }

    @Override
    public void buildAllToMaxLevel(int villageId) {
        LOG.info("Village " + villageId + ": building all to max level.");
        central.buildAllToMaxLevel(villageId);
    }

    @Override
    public void buildPattern(int villageId, int pattern) {
        LOG.info("Village " + villageId + ": building Pattern #1.");
        central.buildAllToMaxLevel(villageId);

        central.buildAtDorf2(villageId, Dorf2.Building.Type.MAIN_BUILDING, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.WAREHOUSE, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.GRANARY, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.RALLY_POINT, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.BARRACKS, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.ACADEMY, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.SMITHY, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.STABLE, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.RESIDENCE, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.TREASURY, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.TOWN_HALL, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.MARKETPLACE, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.EMBASSY, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.TRADE_OFFICE, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.HEROMANSION, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.IRON_FOUNDRY, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.BRICKWORKS, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.SAWMILL, 20);
        central.buildAtDorf2(villageId, Dorf2.Building.Type.WORKSHOP, 20);
    }
}
