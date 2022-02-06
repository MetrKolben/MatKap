package com.example.myapplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

public class Utils {
    public static int[] getNRandomNumbers(int n, int bound) {
        int[] testResult = new int[n];
        for (int i = 0; i < n; i++) {
            testResult[i] = i;
        }
        return testResult;
//        Random r = new Random();
//        int[] numbers = new int[n];
//        Arrays.fill(numbers, Integer.MAX_VALUE);
//        for (int i = 0; i < n; i++) {
//            boolean lock = true;
//            while (lock) {
//                int number = r.nextInt(bound);
//                if (!contains(numbers, number)) {
//                    lock = false;
//                    numbers[i] = number;
//                }
//            }
//        }
//        return numbers;
    }

    public static boolean contains(int[] numbers, int number) {
        for (int n : numbers) {
            if (n == number) return true;
        }
        return false;
    }

    public static boolean inputStreamEquals(InputStream toBeCompared, InputStream template) throws IOException {
        try {
            while (true) {
                int fr = toBeCompared.read();
                int tr = template.read();

                if (fr != tr)
                    return false;

                if (fr == -1)
                    return true;
            }

        } finally {
            if (toBeCompared != null)
                toBeCompared.close();
            if (template != null)
                template.close();
        }
    }
}
