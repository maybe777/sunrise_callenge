package com.sunrise.app.service.impl;

import com.sunrise.app.exceptions.CellException;
import com.sunrise.app.service.CalculationService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CalculationServiceImpl implements CalculationService {

    //main method that returns result double dimensional array
    public String[][] evaluateTable(String[][] arrTable) {
        for (int i = 0; i < arrTable.length; i++) {                                                          //iterate over rows
            for (int j = 0; j < arrTable[i].length; j++) {                                                   //iterate over cell values
                try {
                    String endExpr = transformExpression(arrTable[i][j], arrTable[i].length, arrTable);      //transform links into exact cell input values
                    if (!expressionValidation(endExpr, arrTable.length)) {                                   //validation for correct input
                        throw new CellException("Указаны не верные значения ячейки!");                                   //trow custom exception if there is error
                    }
                    arrTable[i][j] = String.valueOf(calculateResult(endExpr));                               //if all is fine - calculate result
                } catch (Exception ex) {
                    arrTable[i][j] = "err: " + ex.toString();                                                //throw exception if there is something wrong
                }
            }
        }
        return arrTable;                                                                                     //return result table
    }

    //construct expression with cell ID's to standard math expression with numbers
    private String transformExpression(String expression, int columnSize, String[][] arrTable) throws CellException {
        boolean toCount = true;
        while (toCount) {
            toCount = false;
            for (int i = 0; i < columnSize; i++) {                                          //locate current argument
                if (expression.contains(String.valueOf((char) (97 + i)))) {                 //check if expression has link
                    toCount = true;                                                         //recursion entry point
                    for (int tC = 0; tC < arrTable.length; tC++) {                          //recursion entry point
                        for (int tR = arrTable.length; tR > 0; tR--) {                      //iterate over cells to begin table
                            String letter = (char) (97 + tC) + String.valueOf(tR);          //take cell index
                            String value = arrTable[tR - 1][tC];                            //take cell value
                            if (expression.contains(letter)) {
                                expression = expression.replace(letter, "(" + (value.isEmpty() ? 0 : value) + ")"); //replace cell index to value
                            }
                        }
                        if (expression.contains(String.valueOf((char) (97 + tC)))) {
                            throw new CellException("Не верно введено выражение!");
                        }
                    }
                }
            }
        }
        return expression;
    }

    //some bad cell input handle
    private boolean expressionValidation(String expr, int columnSize) {
        Character[] validSym = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+', '/', '*', '.', '(', ')'}; //declare array of valid symbols
        ArrayList<Character> characterList = new ArrayList<>(Arrays.asList(validSym));                                //put array to List
        for (int i = 0; i < columnSize; i++) {
            characterList.add((char) (97 + i));                                                                       //add into array available symbols
        }
        for (int i = 0; i < expr.length(); i++) {                                                                     //iterate over inserted values
            char ch = expr.charAt(i);
            if (!characterList.contains(ch)) {                                                                        //return false if expression does not contain valid symbol
                return false;
            } else {
                if (ch == '.') {                                                                                      //invalid '.' position handle
                    if (i - 1 >= 0 && i + 1 < expr.length()) {
                        if (!((int) expr.charAt(i - 1) >= 48 && (int) expr.charAt(i - 1) <= 57 && (int) expr.charAt(i + 1) >= 48 && (int) expr.charAt(i + 1) <= 57)) {
                            return false;
                        }

                    } else {
                        return false;
                    }
                } else if (ch == '(' && i > 0) {
                    if (i > expr.length() - 3 || !"+-*/(".contains(Character.toString(expr.charAt(i - 1)))) {         //checking position of symbol '('. At the left there have to be operators or symbol '('
                        return false;
                    }
                } else if (ch == ')' && i < expr.length() - 1) {
                    if (i < 2 || !"+-*/)".contains(Character.toString(expr.charAt(i + 1)))) {                         //checking position of symbol ')'. At the right there have to be operators or symbol ')'
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //calculation entry point
    private double calculateResult(String input) throws CellException {
        String output = getExpression(input);                                               //change expression to postfix type
        return counting(output);                                                            //return result
    }

    //change infix form to postfix form according OPN
    private String getExpression(String input) {
        String output = "";                                                                 //expression container
        Deque<Character> opDeque = new ArrayDeque<>();                                      //operator container

        for (int i = 0; i < input.length(); i++) {                                          //iterate each symbol of input
            if (isDelimiter(input.charAt(i))) {                                             //skip separator
                continue;                                                                   //jump to the next symbol
            }
            if (Character.isDigit(input.charAt(i))) {                                       //if symbol is a digit - count all number
                while (!isDelimiter(input.charAt(i)) && !isOperator(input.charAt(i))) {     //fi digit read until to delimiter or operator to get a digit
                    output += input.charAt(i);                                              //add a digit next to another
                    i++;                                                                    //skip to next symbol

                    if (i == input.length())
                        break;                                                              //if there is last symbol - end of story :)
                }

                output += " ";                                                              //add a whitespace to expression
                i--;                                                                        //back to position b4 delimiter
            }
            if (isOperator(input.charAt(i))) {                                              //check if symbol an operator
                if (input.charAt(i) == '(')
                    opDeque.push(input.charAt(i));                                          //if symbol is '(' write to deque
                else if (input.charAt(i) == ')') {
                    char s = opDeque.pop();                                                 //if symbol is '(' add it last after all operators
                    while (s != '(') {
                        output += s + " ";
                        s = opDeque.pop();
                    }
                } else {                                                                    //if symbol any arithmetic operator
                    if (opDeque.size() > 0)                                                 //if deque has elements
                        if (getPriority(input.charAt(i)) <= getPriority(opDeque.peek()))    //and operator priority less or eq of priority of an operator on top of deque
                            output += opDeque.pop() + " ";                                  //then add last operator to expression string
                    opDeque.push(input.charAt(i));                                          //if deque is empty or operator priority higher then add operator on top of deque
                }
            }
        }

        while (opDeque.size() > 0)                                                          //after checking all symbols - kick out of deque rest of operators inline
            output += opDeque.pop() + " ";

        return output;                                                                      //return OPN result record
    }

    //calculation operation
    private double counting(String input) throws ArithmeticException, CellException {
        double result = 0;                                                                  //declare result
        Deque<Double> temp = new ArrayDeque<>();                                            //temporary result vault

        for (int i = 0; i < input.length(); i++) {                                          //iterate over each symbol
            if (Character.isDigit(input.charAt(i))) {                                       //if symbol is a digit, then read a digit and write it on top of deque
                StringBuilder a = new StringBuilder();

                while (!isDelimiter(input.charAt(i)) && !isOperator(input.charAt(i))) {     //if there is not delimiter
                    a.append(input.charAt(i));                                              //add it
                    i++;
                    if (i == input.length()) break;
                }
                temp.push(Double.parseDouble(a.toString()));                                //write into deque
                i--;
            } else if (isOperator(input.charAt(i))) {                                       //if symbol is an operator we take last two deque values
                try {
                    double a = temp.pop();
                    double b = temp.pop();

                    switch (input.charAt(i)) {                                                  //and do exact action
                        case '+':
                            result = b + a;
                            break;
                        case '-':
                            result = b - a;
                            break;
                        case '*':
                            result = b * a;
                            break;
                        case '/':
                            if (a == 0) {
                                throw new ArithmeticException("Деление на ноль!");               //exception handle
                            }
                            result = b / a;
                            break;
                    }
                    temp.push(result);                                                          //push result into deque
                } catch (NoSuchElementException e) {
                    throw new CellException("Отсутствует операнд!");
                }
            }
        }
        if (temp.size() > 0) {
            return temp.peek();                                                             //get and return result
        } else {
            return 0D;
        }
    }

    //check if the symbol are an delimiter
    private boolean isDelimiter(char c) {
        return (" =".contains(Character.toString(c)));
    }


    //check if the symbol are an operator
    private boolean isOperator(char c) {
        return "+-/*()".contains(Character.toString(c));
    }

    //custom operator priority handle
    private byte getPriority(char s) {
        switch (s) {
            case '(':
                return 0;
            case ')':
                return 1;
            case '+':
                return 2;
            case '-':
                return 3;
            case '*':
                return 4;
            case '/':
                return 4;
            default:
                return 5;
        }
    }
}
