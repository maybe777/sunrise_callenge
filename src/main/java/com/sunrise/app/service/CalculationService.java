package com.sunrise.app.service;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class CalculationService {

    private static int pairOfBrackets;

    private static String enteredResult = "";
    private static String copyOfLineOfResult;
    private static String calcOfIntermediateResults;
    private static String stringFromOperationsArray;

    private static List<Integer> positions_OfBrackets;
    private static List<String> dataInsideBrackets;
    private static List<String> operationsFromBrackets;
    private static List<String> numbersFromBrackets;
    private static List<String> array_ResultsOfOperationsInBrackets;
    private static List<String> operationsFromEnteredResult;


    //main method
    public String[][] evaluateTable(String[][] arrTable) {
        for (int r = 0; r < arrTable.length; r++) {
            for (int c = 0; c < arrTable[r].length; c++) {
                String endExpr = transformExpression(arrTable[r][c], arrTable[r].length, arrTable);
                arrTable[r][c] = calculateResult(endExpr);
            }
        }
        return arrTable;
    }

    public String transformExpression(String expression, int columnSize, String[][] arrTable) {
        for (int i = 0; i < columnSize; i++) {
            if (expression.contains(String.valueOf((char) (97 + i)))) {
                for (int tR = arrTable.length - 1; tR >= 0; tR--) {
                    for (int tC = 0; tC < arrTable.length; tC++) {
                        try {
                            String letter = (char) (97 + tC) + String.valueOf(tR);
                            String value = arrTable[tR][tC];

                            if (expression.contains(letter)) {
                                expression = expression.replace(letter, value);
                            }
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                }
                transformExpression(expression, columnSize, arrTable);
            }
        }
        return expression;
    }


    private String calculateResult(String resultFromView) {
        String result = "";
        // получаю строку из представления
        enteredResult = enteringOfData(resultFromView);

        // получаю позиции скобок в введенной строке
        positions_OfBrackets = getBracketPositions(enteredResult);

        // если в строке есть скобки
        if (!positions_OfBrackets.isEmpty()) {
            // пока в есть скобки
            while (!positions_OfBrackets.isEmpty()) {
                array_ResultsOfOperationsInBrackets = new ArrayList<>();
                dataInsideBrackets = getDataInsideBrackets(enteredResult, positions_OfBrackets); // получаю массивы данных в скобках
                for (String dataInsBrackets : dataInsideBrackets) { // для каждого элемента этого массива получаю
                    operationsFromBrackets = getOperations(dataInsBrackets); // массив операторов и
                    numbersFromBrackets = getNumbers(dataInsBrackets); // массив числел
                    calcOfIntermediateResults = calculation(numbersFromBrackets, operationsFromBrackets); // провожу вычисления промежуточного результата и
                    array_ResultsOfOperationsInBrackets.add(calcOfIntermediateResults); // помещаю в массив промежуточных результатов
                }
                //подставляю полученные значения из этого массива обратно в введенную строку
                enteredResult = insertIntermediateResultsBackIntoEnteredResult(enteredResult,
                        dataInsideBrackets,
                        array_ResultsOfOperationsInBrackets);

                operationsFromBrackets = getOperations(enteredResult); // получаю массив операторов
                stringFromOperationsArray = getStringFromOperationsArray(operationsFromBrackets); // преобразую полученный массив в строку
                positions_OfBrackets = getBracketPositions(stringFromOperationsArray); // получаю позиции скобок в введенной строке
                // удаляю скобки из этой строки
                operationsFromEnteredResult = removingOfBracketsAndGettingArrayOfOperatorsFromLine(stringFromOperationsArray, positions_OfBrackets);
                numbersFromBrackets = getArrayOfNumbersFromLineOfResults(enteredResult); // получаю массив чисел для вычисления
                result = calculation(numbersFromBrackets, operationsFromEnteredResult);
            }
        } else {
            operationsFromBrackets = getOperations(enteredResult); // массив операторов и
            numbersFromBrackets = getNumbers(enteredResult); // массив числел и
            result = calculation(numbersFromBrackets, operationsFromBrackets); // провожу вычисления промежуточного результата
        }
        return result;
    }
    //main method


    private String enteringOfData(String resultFromView) {
        // метод ввода данных
        enteredResult = resultFromView;
        return enteredResult;
    }

    private List<Integer> getBracketPositions(String result) {
        // метод получения позиций скобок
        List<String> positions = new ArrayList<>();
        positions_OfBrackets = new ArrayList<>();
        String[] innerStrings = result.split("");
        Collections.addAll(positions, innerStrings);
        positions = iterateAndRemoveNull(positions);
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).equals("(") || positions.get(i).equals(")")) {
                positions_OfBrackets.add(i);
            }
        }

        return positions_OfBrackets;
    }

    private List<String> getDataInsideBrackets(String enteredResult, List<Integer> positionsOfBrackets) {
        // метод, получающий данные (числа и знаки) из скобок
        String dataInsideBrackets = "";
        List<String> arrayOfDataInsideBrackets = new ArrayList<>();
        pairOfBrackets = positionsOfBrackets.size() / 2;
        for (int i = 0; i < pairOfBrackets; i++) {
            dataInsideBrackets = enteredResult.substring(positionsOfBrackets.get(0) + 1, positionsOfBrackets.get(1));
            arrayOfDataInsideBrackets.add(dataInsideBrackets);
            positionsOfBrackets.remove(0);
            positionsOfBrackets.remove(0);
        }
        return iterateAndRemoveNull(arrayOfDataInsideBrackets);
    }

    private List<String> getListFromString(String template) {
        List<String> symbols = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < template.length(); i++) {
            if ("*".equals(template.substring(i, i + 1))) {
                if (i > start) {
                    symbols.add(template.substring(start, i));
                    start = i + 1;
                }
            }
            if ("+".equals(template.substring(i, i + 1))) {
                if (i > start) {
                    symbols.add(template.substring(start, i));
                    start = i + 1;
                }
            }
            if ("/".equals(template.substring(i, i + 1))) {
                if (i > start) {
                    symbols.add(template.substring(start, i));
                    start = i + 1;
                }
            }
            if ("-".equals(template.substring(i, i + 1))) {
                if (i > start) {
                    symbols.add(template.substring(start, i));
                    start = i + 1;
                }
            }
        }
        if (start <= template.length()) {
            symbols.add(template.substring(start, template.length()));
        }
        return symbols;
    }

    private List<String> getNumbers(String enteredResult) {

        List<String> intermediateArray = getListFromString(enteredResult);
        return iterateAndRemoveNull(intermediateArray);

    }

    private List<String> getOperations(String enteredResult) {
        // метод получения знаков операций из строки
        List<String> arrayOfOperations = new ArrayList<>();
        String[] intermediateArray = enteredResult.split("");

        for (String anIntermediateArray : intermediateArray) {
            try {
                double number = Double.parseDouble(anIntermediateArray);
            } catch (NumberFormatException e) {
                if (!anIntermediateArray.equals("."))
                    arrayOfOperations.add(anIntermediateArray);
            }
        }
        return iterateAndRemoveNull(arrayOfOperations);
    }

    private String calculation(List<String> numbers, List<String> operations) {
        String operator;

        if (operations.size() == numbers.size())
            numbers.add(0, String.valueOf(0)); // если в начале строки есть отрицптельное
        // число, добавляем 0
        while (!operations.isEmpty()) {
            try {
                operator = operations.get(0);
                calcAction(numbers, operations, operator);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (ArithmeticException e) {
                System.out.println("на ноль делить нельзя");
            }
        }
        return numbers.get(0);
    }

    private void calcAction(List<String> numbers, List<String> operations, String operator)
            throws NumberFormatException, ArithmeticException {
        int i;

        if ((operations.contains("*")) && (operations.contains("/"))) {
            if ((operations.indexOf("/") < operations.indexOf("*"))) {
                operator = "/";
                while (operations.contains(operator)) {
                    i = operations.indexOf(operator);
                    innerCalc(numbers, operator, i);
                    operations.remove(i);
                }
            }
        }
        if (operations.contains("*")) {
            operator = "*";
            while (operations.contains(operator)) {
                i = operations.indexOf(operator);
                innerCalc(numbers, operator, i);
                operations.remove(i);
            }
        }
        if (operations.contains("/")) {
            operator = "/";
            while (operations.contains(operator)) {
                i = operations.indexOf(operator);
                innerCalc(numbers, operator, i);
                operations.remove(i);
            }
        }
        if (operations.contains("+")) {
            if (operations.get(0).equals("+")) {
                operator = "+";
                innerCalc(numbers, operator, 0);
                operations.remove(0);
                return;
            }
        }
        if (operations.contains("-")) {
            if (operations.get(0).equals("-")) {
                operator = "-";
                try {
                    innerCalc(numbers, operator, 0);
                } catch (ArithmeticException e) {
                }
                operations.remove(0);
                return;
            }
        }
    }

    private void innerCalc(List<String> numbers, String operator, int i) throws ArithmeticException {
        double number1 = 0;
        double number2 = 0;

        try {
            number2 = Double.parseDouble(numbers.get(i + 1));
            number1 = Double.parseDouble(numbers.get(i));
        } catch (NumberFormatException e) {
        }
        numbers.remove(i);

        switch (operator) {
            case ("/"): {
                if (number2 == 0) throw new ArithmeticException();
                numbers.set(i, String.valueOf(number1 / number2));
                break;
            }
            case ("*"): {
                numbers.set(i, String.valueOf(number1 * number2));
                break;
            }
            case ("+"): {
                numbers.set(i, String.valueOf(number1 + number2));
                break;
            }
            case ("-"): {
                numbers.set(i, String.valueOf(number1 - number2));
                break;
            }
        }
    }

    private String insertIntermediateResultsBackIntoEnteredResult(String enteredResult,
                                                                  List<String> dataInsideBrackets,
                                                                  List<String> calcResults) {
        for (int i = 0; i < pairOfBrackets; i++) {
            enteredResult = enteredResult.replace(dataInsideBrackets.get(i), calcResults.get(i));
        }
        return enteredResult;
    }

    private static String getStringFromOperationsArray(List<String> arrayList) {
        String stringFromOperationsArray = "";
        for (String str : arrayList) {
            stringFromOperationsArray += str;
        }
        return stringFromOperationsArray;
    }

    private List<String> removingOfBracketsAndGettingArrayOfOperatorsFromLine(String lineOfOperators,
                                                                              List<Integer> bracketsPosition) {
        List<String> arrayListOfStrings = new ArrayList<>();

        String copyOfLineOfOperators = lineOfOperators;
        int pairOfBrackets = bracketsPosition.size() / 2;
        for (int i = 0; i < pairOfBrackets; i++) {
            String innerString = copyOfLineOfOperators.substring(bracketsPosition.get(0), bracketsPosition.get(1) + 1);
            lineOfOperators = lineOfOperators.replace(innerString, "").trim();
            bracketsPosition.remove(0);
            bracketsPosition.remove(0);
        }
        String[] strings = lineOfOperators.split("");
        Collections.addAll(arrayListOfStrings, strings);
        return iterateAndRemoveNull(arrayListOfStrings);
    }

    private List<String> getArrayOfNumbersFromLineOfResults(String lineOfResults) {
        List<String> arrayListOfNumbers = new ArrayList<>();
        List<String> arrayListForX = new ArrayList<>();

        List<Integer> bracketPosition = getBracketPositions(lineOfResults);

        double d = 0;

        copyOfLineOfResult = new String(lineOfResults);

        int pairOfBrackets = bracketPosition.size() / 2;
        for (int i = 0; i < pairOfBrackets; i++) {
            String innerString = lineOfResults.substring(bracketPosition.get(0) + 1, bracketPosition.get(1));
            copyOfLineOfResult = copyOfLineOfResult.replace(innerString, "X");
            arrayListForX.add(innerString);
            bracketPosition.remove(0);
            bracketPosition.remove(0);
        }
        // принудительно заменяю цифры не в скобках на Х
        List<String> intermediateArray = getListFromString(copyOfLineOfResult);
        Iterator<String> iter = intermediateArray.iterator();

        for (String interItem : intermediateArray) {
            try {
                d = Double.parseDouble(interItem);
                arrayListOfNumbers.add(String.valueOf(d));
            } catch (NumberFormatException e) {
                if ("(X)".equals(interItem)) {
                    arrayListOfNumbers.add(arrayListForX.get(0));
                }
                arrayListForX.remove(0);
            } catch (IndexOutOfBoundsException e) {
            }
        }
        return iterateAndRemoveNull(arrayListOfNumbers);
    }

    private List<String> iterateAndRemoveNull(List<String> arrayList) {
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals("")) iterator.remove();
        }
        return arrayList;
    }
}