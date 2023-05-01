package server;

import game.GameConstants;
import game.Level;
import game.Vector2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ServerGameTest {
    /*
    static Pane gameRoot;
    // Used to store the grid of blocks, null if no block is present
    static Level level;
    static final int[] difficultyProbabilities = {50, 35, 15};

    /** The colours which can be given to players */
    static ArrayList<Color> blockColours =
            new ArrayList<>(
                    Arrays.asList(
                            Color.valueOf("#f57dc6"),
                            Color.valueOf("#b3d5f2"),
                            Color.valueOf("#9ae6ae"),
                            Color.valueOf("#fccf78")));

    static HashMap<ClientHandler, Color> clientColours;
    // In-Game variables
    static ServerCube player;
    /** Whether the cube is currently moving */
    static boolean cubeMoving = false;
    static int lives = GameConstants.DEFAULT_LIVES.getValue();
    // Lobby
    static ArrayList<ClientHandler> clients;
    static Lobby lobby;
    static String gameId;
    static int levelsCompleted = 0;
    /** The instance of the game */
    static ServerGame instance;
    /** The boolean used for the game loop */
    static Boolean running = true;
    static Boolean coinCollision = false;

    //@BeforeEach
    //void setUp() {
        /*tester = new ServerCube(new Pane(), new Vector2D(0, 0)); // test Cube

        // game constants
        velocity_constant = GameConstants.CUBE_VELOCITY.getValue();
        acceleration_constant = GameConstants.CUBE_ACCELERATION.getValue();
        blockSize = GameConstants.BLOCK_SIZE.getValue();
        cubeSize = GameConstants.CUBE_SIZE.getValue();
        blocksPerSecond = GameConstants.BLOCKS_PER_SECOND.getValue();

        //initialization of mock clients and threads
        ClientHandler c1 = mock(ClientHandler.class);
        clientThread1 = new Thread(c1);
        clientThread1.start();
        ClientHandler c2 = mock(ClientHandler.class);
        clientThread2 = new Thread(c2);
        clientThread2.start();

        //
        //ServerGame serverGame = new ServerGame(null, null, null);
        //testGameThread = new Thread(serverGame);
        //testGameThread.start();

    }*/

}