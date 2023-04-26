package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LevelReader {
    static String pathEasyPrefix = "src/main/resources/Levels/EasyLevels/";
    static String pathMediumPrefix = "src/main/resources/Levels/MediumLevels/";
    static String pathHardPrefix = "src/main/resources/Levels/HardLevels/";
    static String path;

    /**
     * Reads the level from the csv file and returns it as a string with \n to split lines
     * @param levelNum
     * @param difficulty
     * @return
     */
    public static String readLevel(int levelNum, String difficulty) {
        switch (difficulty) {
            case "easy" -> path = pathEasyPrefix + "level" + levelNum + ".csv";
            case "medium" -> path = pathMediumPrefix + "level" + levelNum + ".csv";
            case "hard" -> path = pathHardPrefix + "level" + levelNum + ".csv";
        }
        StringBuilder output = new StringBuilder();
        String line = "";
        String finalOutput;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            finalOutput = output.toString();
            return finalOutput;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
