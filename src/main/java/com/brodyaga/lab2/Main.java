package com.brodyaga.lab2;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        String expression = "2 + x * (3 + sin(y))";

        try {
            double result = evaluator.evaluate(expression);
            System.out.println("Результат выражения '" + expression + "' = " + result);
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
