package util;

import java.util.Arrays;

/** Sorts an array of highscores in descending order. */
public class SortHighscores {
    /**
     * Sorts an array in descending order depending on the integer values of the second word in each
     * element.
     * @param highscores the highscores to sort
     */
    public static void sort(String[] highscores) {
        Arrays.sort(
                highscores,
                (str1, str2) -> {
                    try {
                        int int1 = Integer.parseInt(str1.split(" ")[1]);
                        int int2 = Integer.parseInt(str2.split(" ")[1]);

                        return Integer.compare(int2, int1);
                    } catch (Exception e) {
                        return 0;
                    }
                });
    }
}
