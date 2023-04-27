package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LevelReader {
    static String path;

    /**
     * Reads the level from the csv file and returns it as a string with \n to split lines
     * @param path the path to the level
     * @return the level as a string
     */
    public static String readLevel(String path) {
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
