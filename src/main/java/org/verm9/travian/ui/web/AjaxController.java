package org.verm9.travian.ui.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.verm9.travian.controller.ApplicationController;
import org.verm9.travian.dto.Dorf2.Building.Type;
import org.verm9.travian.dto.GameData;

/**
 * Created by nonu on 10/21/2016.
 */
@RestController
public class AjaxController {
    @Autowired
    private ApplicationController applicationController;

    @RequestMapping(value = "/ajax/getGameData", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameData getGameData() {
        return applicationController.getGameData();
    }

    @RequestMapping(value = "/ajax/switchRunningState", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean switchRunningState() {
        // Assume applicationController is always run (stuck in the mainCycle).
        return applicationController.switchRunningState();
    }

    @RequestMapping(value = "/ajax/changePriority", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeVillagePriority(@RequestParam int villageId, @RequestParam int priority) {
        applicationController.changeVillagePriority(villageId, priority);
    }

    @RequestMapping(value = "/ajax/maxAllBuildings", produces = MediaType.APPLICATION_JSON_VALUE)
    public void buildAllToMaxLevel(@RequestParam int villageId) {
        applicationController.buildAllToMaxLevel(villageId);
    }

    @RequestMapping(value = "/ajax/buildAtDorf2", produces = MediaType.APPLICATION_JSON_VALUE)
    public void buildAllToMaxLevel(@RequestParam int villageId, @RequestParam Type what, @RequestParam int level) {
        applicationController.buildAtDorf2(villageId, what, level);
    }

}

