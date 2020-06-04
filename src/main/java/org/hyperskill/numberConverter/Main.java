package org.hyperskill.numberConverter;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    protected final static String BINARY_PREFIX = "0b";
    protected final static String OCTAL_PREFIX = "0";
    protected final static String HEXADECIMAL_PREFIX = "0x";

    public static void main(String[] args) {
        int sourceRadix = 0;
        String sourceNumber = "";
        int targetRadix = 0;
        try (Scanner scanner = new Scanner(System.in)) {
            sourceRadix = Integer.parseInt(scanner.next().trim());
            sourceNumber = scanner.next().trim().toLowerCase();
            targetRadix = Integer.parseInt(scanner.next().trim());
        } catch (NumberFormatException e) {
            System.out.println("error: Cannot parse radix numbers");
            return;
        } catch (NoSuchElementException e) {
            System.out.println("error: three parts input needed");
            return;
        }

        String result = "";
        try {
            result = convertNondecimalToAnotherRadix(sourceNumber, sourceRadix, targetRadix);
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println(result);
    }

    public static String decimalToBinary(int decimal) {
        StringBuilder builder = new StringBuilder();
        int number = decimal;
        while (number > 0) {
            if (number % 2 == 0) {
                builder.append("0");
            } else {
                builder.append("1");
            }
            number /= 2;
        }

        return builder.reverse().toString();
    }

    public static String decimalToAny(int decimal, int base) {
        if (decimal == 0 || base > 36) {
            return "0";
        }
        if (base == 1) {
            char[] chars = new char[decimal];
            Arrays.fill(chars, '1');
            return new String(chars);
        }
        StringBuilder builder = new StringBuilder();
        int number = decimal;
        while (number > 0) {
            if (number % base == 0) {
                builder.append("0");
            } else {
                int remainder = number % base;
                String hex = "";
                if (remainder > 9) {
                    hex = String.valueOf((char) (remainder + 87));
                    builder.append(hex);
                } else {
                    builder.append(number % base);
                }
            }
            number /= base;
        }
        return builder.reverse().toString();
    }

    public static String convertFractionToRadix(double fraction, int radix, int precision) {
        StringBuilder builder = new StringBuilder();
        int numberAsInteger = (int) fraction;
        builder.append(decimalToAny((int) fraction, radix))
                .append(".");
        double number = fraction - numberAsInteger;

        int counter = precision;
        while (counter > 0) {
            number *= radix;
            builder.append(decimalToAny((int) number, radix));
            number -= (int) number;
            counter--;
        }
        return builder.toString();
    }

    public static double convertNondecimalFractionToDecimal(String nondecimal, int radix) {
        String[] numberParts = nondecimal.split("\\.");
        double integerPart = 0;
        try {
            integerPart = Integer.parseInt(numberParts[0], radix);
        } catch (NumberFormatException e) {
            System.out.println("Cannot parse number: " + numberParts[0]);
        }
        double result = integerPart;
        int counter = 1;
        for (char c : numberParts[1].toCharArray()) {
            int decimalDigit = 0;
            try {
                decimalDigit = Integer.parseInt(String.valueOf(c), radix);
            } catch (NumberFormatException e) {
                System.out.println("Cannot parse number: " + c + " in " + numberParts[1]);
            }
            result += (double) decimalDigit / Math.pow(radix, (double) counter);
            counter++;
        }
        return result;
    }

    public static String convertNondecimalToAnotherRadix(String nondecimal, int sourceRadix, int targetRadix) throws ArithmeticException {
        double decimal = 0;
        int precision = 5;
        if (sourceRadix == 1) {
            decimal = nondecimal.length();
        }
        if (sourceRadix < 1 || targetRadix < 1) {
            throw new ArithmeticException("error: radix cannot be lesser than 1");
        }
        if (sourceRadix > 36 || targetRadix > 36) {
            throw new ArithmeticException("error: radix cannot be greater than 36");
        }
        if (nondecimal != null) {
            if (nondecimal.contains(".")) {
                decimal = convertNondecimalFractionToDecimal(nondecimal, sourceRadix);
            } else {
                try {
                    decimal = Integer.parseInt(nondecimal, sourceRadix);
                } catch (NumberFormatException e) {
                    System.out.println("Cannot parse number: " + nondecimal);
                }
            }
        }
        return convertFractionToRadix(decimal, targetRadix, precision);
    }
}