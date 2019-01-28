package com.sunrise.app.controller;


import com.sunrise.app.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
    public ResponseEntity<String> getResult(@RequestParam Map<String, String> arr) {
//        String[] fruit1DArray;
//        String[][] fruit2DArray;
//
//        Map<String, String> fruitMap = new HashMap<>();
//
//        fruit2DArray   = map.entrySet()
//                .stream()
//                .map(e -> new String[]{e.getKey(),e.getValue()})
//                .toArray(String[][]::new);

//        String[][] array = new String[map.size()][2];
//        int count = 0;
//        for (Map.Entry<String,String[]> entry : map.entrySet()){
//            array[count][0] = entry.getKey();
//            array[count][1] = entry.getValue();
//            count++;
//        }
//
//        String[][] arrResult = calculation.evaluateTable(array);


        //        array = array.replace("],[","@");
//        String[] arr = array.split("@");
//        int count2 = 0;
//        if (arr.length>0) {
//            String templ = arr[0].replace("[","");
//            String[] row = templ.split(",");
//            count2 = row.length;
//        }
//        for (String row : arr) {
//
//        }
        //        String[][] result = calculation.evaluateTable(arr);

        return ResponseEntity.ok("dsfsdf");
    }

}
