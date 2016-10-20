package org.verm9.travian.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.verm9.travian.business.Central;

/**
 * Created by nonu on 10/20/2016.
 */

@Controller
public class RootWebController {
    @Autowired
    private Central central;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getMainPage(ModelMap model) {
        model.addAttribute("gameData", central.getGameData());
        return "main";
    }
}
