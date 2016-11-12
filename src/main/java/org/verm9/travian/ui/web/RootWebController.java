package org.verm9.travian.ui.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.verm9.travian.controller.ApplicationController;
import org.verm9.travian.model.LoginData;

import javax.validation.Valid;

/**
 * Created by nonu on 10/20/2016.
 */

@Controller
public class RootWebController {

    @Autowired
    private ApplicationController applicationController;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getMainPage(ModelMap model) {
        if (applicationController.isLoginDataPresent()) {
            return new ModelAndView("main");
        } else {
            return new ModelAndView("enter", "loginData", new LoginData());
        }

    }

    @RequestMapping(value = "/quit", method = RequestMethod.GET)
    public String quit(ModelMap model) {
        applicationController.stop();
        return "redirect:/enter";
    }

    @RequestMapping(value = "/enter", method = RequestMethod.GET)
    public ModelAndView showEnterForm(ModelMap model) {
        return new ModelAndView("enter", "loginData", new LoginData());
    }

    @RequestMapping(value = "/enter", method = RequestMethod.POST)
    public String sendEnterForm(@Valid @ModelAttribute("loginData")LoginData loginData,
                                BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "enter";
        }
        applicationController.setLoginData(loginData);
        applicationController.start();
        return "redirect:/";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(
            @RequestParam(value = "error", required = false) String error) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid  passphrase!");
        }

        model.setViewName("login");

        return model;

    }

}
