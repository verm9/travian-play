package org.verm9.travian.business;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.verm9.travian.dml.BuildingQueueIsFullException;
import org.verm9.travian.dto.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by nonu on 10/14/2016.
 */
@Component
public class CentralImpl implements Central {

    private static final Logger LOG = getLogger(CentralImpl.class);

    @Autowired
    private TravianApi travianApi;

    private GameData gameData = new GameData();

    @Override
    public void mainCycle() {
        Village currentVillage = getCurrentVillage();
        LOG.info("I'm up to the main loop.");
        while(true) {
            BuildingOrder order = currentVillage.getBuildingQueue().poll();
            try {
                if (order != null) {
                    if (order.getWhat() != null) {
                        // Dorf2 build order.
                        travianApi.dorf2Build(order.getWhere(), order.getWhat());
                    } else {
                        // Dorf1 build order.
                        travianApi.dorf1Build(order.getWhere());
                    }
                }

                Thread.sleep(15);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof BuildingQueueIsFullException) {
                    LOG.warn("Full building queue when was building " + order.getWhat()+ " on " + order.getWhere());
                    currentVillage.getBuildingQueue().add(order); // Send it back to the queue.
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
}
