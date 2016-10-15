package org.verm9.travian.business;

import org.verm9.travian.dto.Village;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 10/14/2016.
 */
public interface Central {

    Village getCurrentVillage();

    void buildAllToMaxLevel();

    void mainCycle();
}
