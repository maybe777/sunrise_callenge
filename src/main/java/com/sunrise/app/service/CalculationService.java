package com.sunrise.app.service;


import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CalculationService {

    public String TranformExpression(String expression, int columnSize, String[][] arrTable) {
        boolean toBeCont = true;
        while (toBeCont) {
            toBeCont = false;
            for (int i = 0; i < columnSize; i++) {
                if (expression.contains(String.valueOf((char) (97 + i)))) {
                    toBeCont = true;
                    for (int tR = arrTable.length; tR > 0; tR--) {
                        for (int tC = 0; tC < arrTable.length; tC++) {
                            try {
                                String letter = (char) (97 + tC) + String.valueOf(tR);
                                String value = arrTable[tR - 1][tC];

                                if (expression.contains(letter)) {
                                    expression = expression.replace(letter, "(" + (value.isEmpty() ? 0 : value) + ")");
                                }
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        }
                    }
                }
            }
        }
        return expression;
    }

    public String[][] evaluateTable(String[][] arrTable) {
        for (int r = 0; r < arrTable.length; r++) {
            for (int c = 0; c < arrTable[r].length; c++) {
                try {
                    String endExpr = TranformExpression(arrTable[r][c], arrTable[r].length, arrTable);
                    arrTable[r][c] = String.valueOf(calculateResult(endExpr));
                } catch (Exception ex) {
                    arrTable[r][c] = "err: " + ex.toString();
                }
            }
        }
        return arrTable;
    }

    //Метод возвращает true, если проверяемый символ - разделитель ("пробел" или "равно")
    private boolean isDelimeter(char c) {
        if ((" =".contains(Character.toString(c)))) {
            return true;
        }
        return false;
    }

    //Метод возвращает true, если проверяемый символ - оператор
    private boolean isOperator(char c) {
        if ("+-/*^()".contains(Character.toString(c))) {
            return true;
        }
        return false;
    }

    //Метод возвращает приоритет оператора
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
            case '^':
                return 5;
            default:
                return 6;
        }
    }

    //"Входной" метод класса
    public double calculateResult(String input) {
        String output = getExpression(input); //Преобразовываем выражение в постфиксную запись
        return counting(output); //Возвращаем результат
    }

    private String getExpression(String input) {
        String output = ""; //Строка для хранения выражения
        Stack<Character> operStack = new Stack<>(); //Стек для хранения операторов

        for (int i = 0; i < input.length(); i++) //Для каждого символа в входной строке
        {
            //Разделители пропускаем
            if (isDelimeter(input.charAt(i))) {
                continue; //Переходим к следующему символу
            }
            //Если символ - цифра, то считываем все число
            if (Character.isDigit(input.charAt(i))) {
                //Если цифра
                //Читаем до разделителя или оператора, что бы получить число
                while (!isDelimeter(input.charAt(i)) && !isOperator(input.charAt(i))) {
                    output += input.charAt(i); //Добавляем каждую цифру числа к нашей строке
                    i++; //Переходим к следующему символу

                    if (i == input.length()) break; //Если символ - последний, то выходим из цикла
                }

                output += " "; //Дописываем после числа пробел в строку с выражением
                i--; //Возвращаемся на один символ назад, к символу перед разделителем
            }

            //Если символ - оператор
            if (isOperator(input.charAt(i))) //Если оператор
            {
                if (input.charAt(i) == '(') //Если символ - открывающая скобка
                    operStack.push(input.charAt(i)); //Записываем её в стек
                else if (input.charAt(i) == ')') //Если символ - закрывающая скобка
                {
                    //Выписываем все операторы до открывающей скобки в строку
                    char s = operStack.pop();

                    while (s != '(') {
                        output += Character.toString(s) + " ";
                        s = operStack.pop();
                    }
                } else //Если любой другой оператор
                {
                    if (operStack.size() > 0) //Если в стеке есть элементы
                        if (getPriority(input.charAt(i)) <= getPriority(operStack.peek())) //И если приоритет нашего оператора меньше или равен приоритету оператора на вершине стека
                            output += Character.toString(operStack.pop()) + " "; //То добавляем последний оператор из стека в строку с выражением
                    operStack.push(input.charAt(i)); //Если стек пуст, или же приоритет оператора выше - добавляем операторов на вершину стека
                }
            }
        }

        //Когда прошли по всем символам, выкидываем из стека все оставшиеся там операторы в строку
        while (operStack.size() > 0)
            output += operStack.pop() + " ";

        return output; //Возвращаем выражение в постфиксной записи
    }

    private double counting(String input) {
        double result = 0; //Результат
        Stack<Double> temp = new Stack<>(); //Временный стек для решения временный

        for (int i = 0; i < input.length(); i++) //Для каждого символа в строке
        {
            //Если символ - цифра, то читаем все число и записываем на вершину стека
            if (Character.isDigit(input.charAt(i))) {
                String a = "";

                while (!isDelimeter(input.charAt(i)) && !isOperator(input.charAt(i))) //Пока не разделитель
                {
                    a += Character.toString(input.charAt(i)); //Добавляем
                    i++;
                    if (i == input.length()) break;
                }
                temp.push(Double.parseDouble(a)); //Записываем в стек
                i--;
            } else if (isOperator(input.charAt(i))) //Если символ - оператор
            {
                //Берем два последних значения из стека
                double a = temp.pop();
                double b = temp.pop();

                switch (input.charAt(i)) //И производим над ними действие, согласно оператору
                {
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
                        result = b / a;
                        break;
//                    case '^': result = double.Parse(Math.Pow(double.Parse(b.ToString()), double.Parse(a.ToString())).ToString()); break;
                }
                temp.push(result); //Результат вычисления записываем обратно в стек
            }
        }
        if (temp.size() > 0) {
            return temp.peek(); //Забираем результат всех вычислений из стека и возвращаем его
        } else {
            return 0D;
        }
    }
}