package org.verm9.travian.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.verm9.travian.dto.GameData;
import org.verm9.travian.dto.Village;

/**
 * Created by nonu on 10/14/2016.
 */
@Component
public class CentralImpl implements Central {

    @Autowired
    private TravianApi travianApi;

    private GameData gameData = new GameData();

    @Override
    public Village getCurrentVillage() {
        return gameData.getVillage();
    }

    @Override
    public void buildAllToMaxLevel() {

    }
}
