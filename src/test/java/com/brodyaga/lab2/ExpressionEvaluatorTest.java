package com.brodyaga.lab2;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    @Test
    void testSimpleExpression() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(Map.of("x", 4.0, "y", 1.57));
        double result = evaluator.evaluate("2 + x * (3 + sin(y))");
        assertEquals(18.0, result, 0.01);
    }

    @Test
    void testWithSqrt() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(Map.of("a", 9.0));
        double result = evaluator.evaluate("sqrt(a)");
        assertEquals(3.0, result, 0.01);
    }

    @Test
    void testWithLog() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(Map.of("a", 10.0));
        double result = evaluator.evaluate("log(a)");
        assertEquals(Math.log(10), result, 0.01);  // Поскольку log по умолчанию - это натуральный логарифм
    }

    @Test
    void testWithCos() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(Map.of("a", Math.PI / 3));  // cos(π/3) = 0.5
        double result = evaluator.evaluate("cos(a)");
        assertEquals(0.5, result, 0.01);
    }

    @Test
    void testWithTan() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(Map.of("a", Math.PI / 4));  // tan(π/4) = 1
        double result = evaluator.evaluate("tan(a)");
        assertEquals(1.0, result, 0.01);
    }



    @Test
    void testInvalidExpressionWithUnknownOperator() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(Map.of("x", 4.0));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            evaluator.evaluate("2 + x $ 5");
        });
        assertEquals("Недопустимый токен: $", exception.getMessage());
    }
}
