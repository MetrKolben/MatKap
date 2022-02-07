package com.example.myapplication;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

public class Utils {

    /**
     * Generates an array of random int values
     * @param n the length os the array
     * @param bound max value
     * @return an array
     */
    public static int[] getNRandomNumbers(int n, int bound) {
        Random r = new Random();
        int[] numbers = new int[n];
        Arrays.fill(numbers, Integer.MAX_VALUE);
        for (int i = 0; i < n; i++) {
            boolean lock = true;
            while (lock) {
                int number = r.nextInt(bound);
                if (!contains(numbers, number)) {
                    lock = false;
                    numbers[i] = number;
                }
            }
        }
        return numbers;
    }

    /**
     * Creates an array that contains random numbers generated from a seed that is unique for every day
     * @param bound max value
     * @param count the length of the array
     * @return an array
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int[] getNNumbersFromDate(int bound, int count){
        int[] numbers = new int[count];
        int seed = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()).hashCode();
        Arrays.fill(numbers, Integer.MIN_VALUE);
        fillNumbers(seed, numbers, 0, bound);
        return numbers;
    }

    /**
     * Recursively fills given array with random numbers from a given seed
     * @param seed passed to {@link java.util.Random}
     * @param numbers the array, that is being filled
     * @param index position of the current number
     * @param bound max value
     */
    private static void fillNumbers(int seed, int[] numbers, int index, int bound) {
        if (index >= numbers.length) return;
        Random r = new Random(seed);
        int n = r.nextInt(bound);
        if (!contains(numbers, n)) {
            numbers[index] = n;
            fillNumbers(r.nextInt(), numbers, index+1, bound);
            return;
        }
        fillNumbers(r.nextInt(), numbers, index, bound);
    }

    /**
     * Checks if the array contains the given number
     * @param numbers the array that is being checked
     * @param number the number that is being sought
     * @return
     */
    public static boolean contains(int[] numbers, int number) {
        for (int n : numbers) {
            if (n == number) return true;
        }
        return false;
    }


    /**
     * Compares two {@link java.io.InputStream} objects
     * @param toBeCompared first of the two objects being compared
     * @param template second of the two objects being compared
     * @return true if the two {@link java.io.InputStream} objects contain the same content
     * @throws IOException
     */
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
