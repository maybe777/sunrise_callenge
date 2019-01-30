package com.sunrise.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class AppController {

    //default mapping
    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

}
