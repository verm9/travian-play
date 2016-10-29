package org.verm9.travian.business;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.verm9.travian.dml.BuildingQueueIsFullException;
import org.verm9.travian.dto.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by nonu on 10/14/2016.
 */
@Component
public class CentralImpl extends Thread implements Central {

    private static final Logger LOG = getLogger(CentralImpl.class);

    @Autowired
    private TravianApi travianApi;

    private GameData gameData = new GameData();
    private List<Village> villagesMultipliedByPriorities = new ArrayList<>();

    private boolean paused = true;

    @Override
    public void run() {
        LOG.info("Inside run().");
        mainCycle();
    }

    @Override
    public void mainCycle() {
        // Login first (and do action on start). After go to the main loop.
        // TODO: If login session is expired it has to login again.
        LOG.info("Doing init actions.");
        try {
            travianApi.login();
            travianApi.setCapital();
        } catch (IOException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.getMessage());
        }

        // Init advanced data which is used to perform advanced actions.
        initApplicationData();

        // Main loop.
        Random random = new Random();
        BuildingOrder buildingOrder = null;
        Village performNextOrderOnThisVillage = null;
        LOG.info("I'm up to the main loop.");
        while(true) {
            try {
                waitIfPaused();
                LOG.debug("Performing action.");

                // Choose village to perform on it one order.
                performNextOrderOnThisVillage = villagesMultipliedByPriorities.get( random.nextInt(villagesMultipliedByPriorities.size()) );
                buildingOrder = performNextOrderOnThisVillage.getBuildingQueue().poll();

                // Build
                if (buildingOrder != null) {
                    LOG.info("Building " +buildingOrder.toString() +" in "+ performNextOrderOnThisVillage);
                    if (buildingOrder.getWhat() != null) {
                        // Dorf2 build order.
                        travianApi.dorf2Build(buildingOrder.getWhere(), buildingOrder.getWhat());
                    } else {
                        // Dorf1 build order.
                        travianApi.dorf1Build(buildingOrder.getWhere());
                    }
                }

            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof BuildingQueueIsFullException) {
                    LOG.warn("Full building queue when was building " + buildingOrder.getWhat()+ " on " + buildingOrder.getWhere());
                    performNextOrderOnThisVillage.getBuildingQueue().add(buildingOrder); // Send it back to the queue.
                }
            } catch (NoSuchMethodException | IllegalAccessException | IOException e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        }

    }

    private void waitIfPaused() {
        try {
            while (paused) {
                LOG.info("Paused...");
                synchronized (this) {
                    this.wait();
                }
            }
        } catch (InterruptedException e) {
            LOG.info("Continue!");
        }
    }

    private void initApplicationData() {
        // Init villagesMultipliedByPriorities array that is used when choosing which village to process.
        villagesMultipliedByPriorities.clear();
        for (Map.Entry<Integer, Village> entry : gameData.getVillages().entrySet()) {
            Village v = entry.getValue();
            for (int i = 0; i < v.getPriority(); i++) {
                villagesMultipliedByPriorities.add(v);
            }
        }

    }

    @Override
    public Village getCurrentVillage() {
        return gameData.getCurrentVillage();
    }

    @Override
    public Village getVillage(Integer id) {
        return gameData.getVillage(id);
    }

    @Override
    public Village setCurrentVillage(Integer villageId) {
        gameData.setCurrentVillageId(villageId);
        return gameData.getCurrentVillage();
    }

    @Override
    public GameData getGameData() {
        return gameData;
    }

    @Override
    public void addNewVillageIfNotAdded(Integer id, Village village) {
        gameData.addNewVillageIfNotAdded(id, village);
    }

    @Override
    public void buildAllToMaxLevel() {
        for (Map.Entry<Integer, Dorf1.ResourceField> entry : getCurrentVillage().getDorf1().getFields().entrySet()) {
            int maxResourceFieldLevel = getCurrentVillage().isCapital() ? 20 : 10;
            for (int i = entry.getValue().getLevel(); i < maxResourceFieldLevel; i++) {
                getCurrentVillage().getBuildingQueue().add(new BuildingOrder(entry.getKey(), null));
            }
        }

        for (Map.Entry<Integer, Dorf2.Building> entry : getCurrentVillage().getDorf2().getBuildings().entrySet()) {
            for (int i = entry.getValue().getLevel(); i < entry.getValue().getType().getMaxLevel(); i++) {
                getCurrentVillage().getBuildingQueue().add(new BuildingOrder(entry.getKey(), entry.getValue().getType()));
            }
        }
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

}
