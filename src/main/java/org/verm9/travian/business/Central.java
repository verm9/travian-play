package org.verm9.travian.business;

import org.verm9.travian.dto.Village;

/**
 * Created by nonu on 10/14/2016.
 */
public interface Central {

    Village getCurrentVillage();

    void buildAllToMaxLevel();
}
