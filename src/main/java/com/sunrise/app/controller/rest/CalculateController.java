package com.sunrise.app.controller.rest;

import com.sunrise.app.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class CalculateController {

    private final CalculationService calculationService;

    @Autowired
    public CalculateController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    //calculation mapping
    @PostMapping(value = "/calculate")
    public ResponseEntity<String[][]> getResult(@RequestParam("arr") String str) {

        //parse incoming string into double dimensional array for call evaluateTable function
        String[][] arrInput = Arrays.stream(str.substring(2, str.length() - 2).split("\\],\\["))
                .map(e -> Arrays.stream(e.split("\\s*,\\s*")).map(strpar -> strpar.replace("\"",""))
                        .toArray(String[]::new)).toArray(String[][]::new);

        String[][] arrResult = calculationService.evaluateTable(arrInput);

        return ResponseEntity.ok(arrResult);
    }

}
