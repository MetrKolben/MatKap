package cz.matkap;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Utils {
    private static char[] alphabet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'á', 'b', 'c', 'č', 'd', 'ď', 'e', 'é', 'ě', 'f', 'g', 'h', 'i', 'í', 'j', 'k',
            'l', 'm', 'n', 'ň', 'o', 'ó', 'p', 'q', 'r', 'ř', 's', 'š', 't', 'ť',
            'u', 'ú', 'ů', 'v', 'w', 'x', 'y', 'ý', 'z', 'ž'
    };

    /**
     * Used just for debugging
     */
    public static class TIMER {
        private static long ms = 0;
        public static void start() {
            ms = System.currentTimeMillis();
        }
        public static long stop(){
            long temp = System.currentTimeMillis()-ms;
            ms = 0;
            return temp;
        }
    }

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
        int seed = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()).hashCode()*1234;
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

    static class UTFComparator implements Comparator {
        private final NameAcquire nameAcquire;

        public UTFComparator(NameAcquire nameAcquire) {
            this.nameAcquire = nameAcquire;
        }

        @Override
        public int compare(Object o1, Object o2) {
            String word1 = nameAcquire.getName(o1).toLowerCase().replaceAll(" ", "");
            String word2 = nameAcquire.getName(o2).toLowerCase().replaceAll(" ", "");
            int res = compareStrings(word1, word2);
            return res;
        }

        private int compareStrings(String word1, String word2) {
            char[] chars1 = word1.toCharArray(), chars2 = word2.toCharArray();
            boolean word1last = chars1.length == 1;
            boolean word2last = chars2.length == 1;
            if (chars1[0] == chars2[0]) {
                if (word1last && word2last) return 0; /**both*/
                if (word1last) return -1; /**word1*/
                if (word2last) return 1; /**word2*/
                return compareStrings(word1.substring(1), word2.substring(1));
            }
            for (int i = 0; i < alphabet.length; i++) {
                if (chars1[0] == alphabet[i]) {
                    return -1; /**word1*/
                } else if (chars2[0] == alphabet[i]) return 1; /**word2*/
            }
            if (word1last && word2last) return 0; /**both*/
            if (word1last) return -1; /**word1*/
            if (word2last) return 1; /**word2*/
            return compareStrings(word1.substring(1), word2.substring(1));
        }
    }

    public interface NameAcquire {
        String getName(Object o);
    }

}
