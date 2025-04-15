package com.brodyaga.lab2;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для разбора и вычисления арифметических выражений с поддержкой переменных и функций.
 * Поддерживает стандартные операторы (+, -, *, /, ^), скобки, переменные и функции (sin, cos, tan, sqrt, log).
 */
public class ExpressionEvaluator {
    private Map<String, Double> variables;
    private final Set<String> supportedFunctions = Set.of("sin", "cos", "tan", "sqrt", "log");
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Конструктор, инициализирующий переменные заданной Map.
     * @param variables отображение переменных и их значений
     */
    public ExpressionEvaluator(Map<String, Double> variables) {
        this.variables = variables;
    }

    /**
     * Конструктор по умолчанию, создающий пустое множество переменных.
     */
    public ExpressionEvaluator() {
        this.variables = new HashMap<>();
    }

    /**
     * Основной метод вычисления значения выражения.
     * @param expression выражение в виде строки.
     * @return результат вычисления.
     */
    public double evaluate(String expression) {
        expression = expression.replaceAll("\\s+", "");
        List<String> tokens = tokenize(expression);
        Set<String> vars = extractVariables(tokens);

        requestVariables(vars);

        List<String> rpn = toRPN(tokens);
        return evaluateRPN(rpn);
    }

    /**
     * Разбивает выражение на токены: числа, операторы, переменные и функции.
     * @param expr строка выражения
     * @return список токенов
     */
    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "([a-zA-Z]+)|" +         // переменные и функции
                        "(\\d+(\\.\\d+)?)|" +    // числа
                        "([+\\-*/^(),])"         // операторы и скобки
        );
        Matcher matcher = pattern.matcher(expr);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    /**
     * Извлекает имена переменных из списка токенов.
     * @param tokens список токенов
     * @return множество переменных
     */
    private Set<String> extractVariables(List<String> tokens) {
        Set<String> vars = new HashSet<>();
        for (String token : tokens) {
            if (token.matches("[a-zA-Z]+") && !supportedFunctions.contains(token)) {
                vars.add(token);
            }
        }
        return vars;
    }

    /**
     * Запрашивает у пользователя значения для переменных, которых ещё нет в Map.
     * @param vars множество переменных
     */
    private void requestVariables(Set<String> vars) {
        for (String var : vars) {
            if (!variables.containsKey(var)) {
                System.out.print("Введите значение переменной " + var + ": ");
                double value = Double.parseDouble(scanner.nextLine());
                variables.put(var, value);
            }
        }
    }

    /**
     * Возвращает приоритет оператора.
     * @param op оператор
     * @return целое число, соответствующее приоритету
     */
    private int precedence(String op) {
        switch (op) {
            case "^": return 4;
            case "*":
            case "/": return 3;
            case "+":
            case "-": return 2;
            default: return 0;
        }
    }

    /**
     * Проверяет, является ли оператор правоассоциативным.
     * @param op оператор
     * @return true, если оператор правоассоциативный
     */
    private boolean isRightAssociative(String op) {
        return op.equals("^");
    }

    /**
     * Преобразует инфиксную запись (обычное выражение) в постфиксную (обратная польская нотация).
     * Использует алгоритм сортировочной станции.
     * @param tokens список токенов инфиксного выражения
     * @return список токенов в постфиксной записи
     */
    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
            if (token.matches("\\d+(\\.\\d+)?")) {
                output.add(token);
            } else if (supportedFunctions.contains(token)) {
                stack.push(token);
            } else if (token.matches("[a-zA-Z]+")) {
                output.add(token);
            } else if ("+-*/^".contains(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    String top = stack.peek();
                    if ((precedence(top) > precedence(token)) ||
                            (precedence(top) == precedence(token) && !isRightAssociative(token))) {
                        output.add(stack.pop());
                    } else break;
                }
                stack.push(token);
            } else if ("(".equals(token)) {
                stack.push(token);
            } else if (")".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new RuntimeException("Несоответствие скобок");
                stack.pop();
                if (!stack.isEmpty() && supportedFunctions.contains(stack.peek())) {
                    output.add(stack.pop());
                }
            } else {
                throw new RuntimeException("Недопустимый токен: " + token);
            }
        }

        while (!stack.isEmpty()) {
            String top = stack.pop();
            if (top.equals("(") || top.equals(")")) {
                throw new RuntimeException("Несоответствие скобок");
            }
            output.add(top);
        }

        return output;
    }

    /**
     * Вычисляет значение выражения, представленного в обратной польской нотации.
     * @param rpn список токенов в RPN
     * @return результат вычисления
     */
    private double evaluateRPN(List<String> rpn) {
        Stack<Double> stack = new Stack<>();

        for (String token : rpn) {
            if (token.matches("\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else if (variables.containsKey(token)) {
                stack.push(variables.get(token));
            } else if (supportedFunctions.contains(token)) {
                double value = stack.pop();
                stack.push(applyFunction(token, value));
            } else if ("+-*/^".contains(token)) {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                    case "^": stack.push(Math.pow(a, b)); break;
                    default: throw new RuntimeException("Неизвестный оператор: " + token);
                }
            } else {
                throw new RuntimeException("Недопустимый токен: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new RuntimeException("Ошибка в выражении: стек не пуст после вычисления");
        }

        return stack.pop();
    }

    /**
     * Применяет математическую функцию к значению.
     * @param func имя функции
     * @param value значение аргумента
     * @return результат применения функции
     */
    private double applyFunction(String func, double value) {
        switch (func) {
            case "sin": return Math.sin(value);
            case "cos": return Math.cos(value);
            case "tan": return Math.tan(value);
            case "sqrt": return Math.sqrt(value);
            case "log": return Math.log(value);
            default: throw new RuntimeException("Неизвестная функция: " + func);
        }
    }
}
