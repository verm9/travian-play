package org.verm9.travian.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.verm9.travian.business.Central;
import org.verm9.travian.dto.Dorf2;
import org.verm9.travian.dto.GameData;

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
    public boolean switchRunningState() {
        LOG.info("Switching game state to " + (central.isPaused() ? "running." : "paused."));
        central.setPaused( !central.isPaused() );
        synchronized (central) {
            central.notifyAll();
        }
        return central.isPaused();
    }

    @Override
    public void buildAtDorf2(int villageId, Dorf2.Building.Type what) {
        LOG.info("Village " + villageId + ": build " + what);
        // Choose where to build. Create and add a BuildOrder to central's building queue.
        // Don't choose chosen building spots (which are free in-game, but occupied by present building queue.
    }

    @Override
    public void changeVillagePriority(int villageId, int priority) {
        LOG.info("Village " + villageId + ": priority " + priority + " is set.");
        central.changeVillagePriority(villageId, priority);
    }

    @Override
    public void buildAllToMaxLevel(int villageId) {
        LOG.info("Village " + villageId + ": building all to max level.");
        central.buildAllToMaxLevel();
    }
}
