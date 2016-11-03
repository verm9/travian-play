package org.verm9.travian.business;

import org.apache.commons.collections4.BidiMap;
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
import static org.verm9.travian.dto.Dorf2.Building.Type.NO_DATA;

/**
 * Created by nonu on 10/14/2016.
 */
@Component
public class CentralImpl extends Thread implements Central {

    private static final Logger LOG = getLogger(CentralImpl.class);

    @Autowired
    private TravianApi travianApi;

    private GameData gameData = new GameData();
    private final List<Village> villagesMultipliedByPriorities = new ArrayList<>();

    private boolean paused = true;

    @Override
    public void run() {
        LOG.trace("Inside run().");
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
        updateApplicationData();

        // Main loop.
        Random random = new Random();
        BuildingOrder buildingOrder = null;
        Village performNextOrderOnThisVillage = null;
        LOG.trace("I'm up to the main loop.");
        while(true) {
            try {
                waitIfPaused();

                // Choose village to perform one order on it.
                synchronized (villagesMultipliedByPriorities) { // it may be accessed from controller
                    if (villagesMultipliedByPriorities.size() > 0) {
                        performNextOrderOnThisVillage = villagesMultipliedByPriorities.get(random.nextInt(villagesMultipliedByPriorities.size()));
                    } else {
                        continue; // every village has priority 0
                    }
                }
                buildingOrder = performNextOrderOnThisVillage.getBuildingQueue().poll();

                // Choose village
                changeVillage(performNextOrderOnThisVillage.getId());

                // Build
                if (buildingOrder != null) {
                    LOG.info("Attempt of building " + buildingOrder.toString() +" in "+ performNextOrderOnThisVillage);
                    if (buildingOrder.getWhat() != null) {
                        // Dorf2 build order.
                        travianApi.dorf2Build(buildingOrder.getWhere(), buildingOrder.getWhat());
                    } else {
                        // Dorf1 build order.
                        travianApi.dorf1Build(buildingOrder.getWhere());
                    }
                } else {
                    // Just update dorf2.
                    travianApi.getBuldings();
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
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        }

    }

    private void changeVillage(int id) throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException {
        if (getCurrentVillage().getId() != id) {
            travianApi.changeVillage(id);
            setCurrentVillage(id);
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

    private void updateApplicationData() {
        // Update villagesMultipliedByPriorities array that is used when choosing which village to process.
        synchronized (villagesMultipliedByPriorities) {
            villagesMultipliedByPriorities.clear();
            for (Map.Entry<Integer, Village> entry : gameData.getVillages().entrySet()) {
                Village v = entry.getValue();
                for (int i = 0; i < v.getPriority(); i++) {
                    villagesMultipliedByPriorities.add(v);
                }
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
    public void buildAllToMaxLevel(int villageId) {
        Village village = getVillage(villageId);
        for (Map.Entry<Integer, Dorf1.ResourceField> entry : village.getDorf1().getFields().entrySet()) {
            int maxResourceFieldLevel = village.isCapital() ? 20 : 10;
            for (int i = entry.getValue().getLevel(); i < maxResourceFieldLevel; i++) {
                addToBuildingQueue(villageId, new BuildingOrder(entry.getKey(), null));
            }
        }

        for (Map.Entry<Integer, Dorf2.Building> entry : village.getDorf2().getBuildings().entrySet()) {
            for (int i = entry.getValue().getLevel(); i < entry.getValue().getType().getMaxLevel(); i++) {
                addToBuildingQueue(villageId, new BuildingOrder(entry.getKey(), entry.getValue().getType()));
            }
        }
    }

    @Override
    public void buildAtDorf2(int villageId, Dorf2.Building.Type what, int level){
        Village village = getVillage(villageId);
        BidiMap<Integer, Dorf2.Building> buildings = village.getDorf2().getBuildings();

        // Throw exception - building list is empty (dorf2 hasn't been visited and parsed before)
        if ( buildings.isEmpty() ) {
            throw new Dorf2IsNotParsedException();
        }

        // Choose where to build. Create and add a BuildOrder to central's building queue.
        // Don't choose chosen building spots (which are free in-game, but occupied by present building queue.
        Integer whereIs = null;
        for (BidiMap.Entry<Integer, Dorf2.Building> e : buildings.entrySet()) {
            if (e.getValue().getType() == what) {
                whereIs = e.getKey();
            }
        }
        Integer whereToBuild;
        int currentLevel = 0;
        if (whereIs == null) {
            // building is not built - choose a empty spot for it;
            whereToBuild = buildings.getKey(NO_DATA);
            if (whereToBuild == null) {
                LOG.error("No empty slot for building " + what + " in " + village);
                throw new NoFreeSpaceForBuildingException();
            }
        } else {
            whereToBuild = whereIs;
            currentLevel = buildings.get(whereIs).getLevel();
        }

        for (int i = currentLevel; i < what.getMaxLevel(); i++) {
            addToBuildingQueue( villageId, new BuildingOrder(whereToBuild, what) );
        }
    }

    /**
     * Adds building order. Follows the rule that every order in building orders queue and current village state can't
     * result in a overleveled state (it's level with fully executed building queue will be higher than it's max level).
     * In-game queue isn't counted yet.
     * @param villageId
     * @param buildingOrder
     */
    private void addToBuildingQueue(int villageId, BuildingOrder buildingOrder) {
        int maxLevel = 0;
        int currentLevel = 0;

        Village village = getVillage(villageId);
        if (buildingOrder.getWhere() <= 18) {
            // Dorf1 resource field build.
            maxLevel = village.isCapital() ? 20 : 10;
            currentLevel = village.getDorf1().getFields().get(buildingOrder.getWhere()).getLevel();
        } else {
            // Dorf2 build.
            maxLevel = buildingOrder.getWhat().getMaxLevel();
            currentLevel = village.getDorf2().getBuildings().get(buildingOrder.getWhere()).getLevel();
        }

        long count = village.getBuildingQueue().stream()
                .filter(order -> order.getWhere().equals(buildingOrder.getWhere()))
                .count();

        if (count + currentLevel < maxLevel) {
            village.getBuildingQueue().add(buildingOrder);
        }
    }



    @Override
    public void changeVillagePriority(int villageId, int priority) {
        getVillage(villageId).setPriority(priority);
        updateApplicationData();
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
