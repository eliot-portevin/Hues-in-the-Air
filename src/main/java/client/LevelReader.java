package client;

import java.io.*;
import java.util.Objects;

/** Reads a level from a csv file for the {@link game.Level} class. */
public class LevelReader {
  /**
   * Reads the level from the csv file and returns it as a string with \n to split lines
   *
   * @param path the path to the level
   * @return the level as a string
   */
  public static String readLevel(String path) {
    String finalOutput;

    File jarFile =
        new File(LevelReader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    if (jarFile.getPath().endsWith(".jar")) {
      finalOutput = readJarLevel(path);
    } else {
      finalOutput = readIdeLevel(path);
    }

    return finalOutput;
  }

  /**
   * Method called to read a level from a file when the server is running from a jar file. This is
   * necessary because the path to the level file is different when running in an IDE than when
   * running from a jar file.
   *
   * @param relativePath the relative path to the level file (relative to the resources folder)
   * @return the level as a string
   */
  private static String readJarLevel(String relativePath) {
    StringBuilder output = new StringBuilder();
    String line = "";
    String finalOutput;

    try {
      InputStream is = LevelReader.class.getResourceAsStream(relativePath);
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }
      finalOutput = output.toString();
      return finalOutput;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Method called to read a level from a file when the server is running in an IDE. This is
   * necessary because the path to the level file is different when running in an IDE than when
   * running from a jar file.
   *
   * @param relativePath the relative path to the level file (relative to the resources folder)
   * @return the level as a string
   */
  private static String readIdeLevel(String relativePath) {
    StringBuilder output = new StringBuilder();
    String line = "";
    String finalOutput;

    String absolutePath = Objects.requireNonNull(LevelReader.class.getResource(relativePath)).getPath();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(absolutePath));
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
