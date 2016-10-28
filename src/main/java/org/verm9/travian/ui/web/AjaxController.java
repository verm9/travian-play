package org.verm9.travian.ui.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.verm9.travian.business.Central;
import org.verm9.travian.dto.GameData;

/**
 * Created by nonu on 10/21/2016.
 */
@RestController
public class AjaxController {
    @Autowired
    private Central central;

    @RequestMapping(value = "/ajax/getGameData", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameData getGameData() {
        return central.getGameData();
    }

    @RequestMapping(value = "/ajax/switchRunningState", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean switchRunningState() {
        // Assume central is always run.
        central.setPaused( !central.isPaused() );
        return central.isPaused();
    }

}

