package com.sunrise.app.controller;


import com.sunrise.app.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Map;

@Controller
public class AppController {

    private final CalculationService calculation;

    @Autowired
    public AppController(CalculationService calculation) {
        this.calculation = calculation;
    }

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @PostMapping(value = "/calculate")
    public ResponseEntity<String> getResult(@RequestParam ("arr") String str) {

        String[][] arrResult = calculation.evaluateTable
                (Arrays.stream(str.substring(2, str.length() - 2)
                        .split("\\],\\["))
                        .map(e -> Arrays.stream(e.split("\\s*,\\s*"))
                                .toArray(String[]::new)).toArray(String[][]::new));

        System.out.print(Arrays.deepToString(arrResult));
        return ResponseEntity.ok("dsfsdf");
    }

}
