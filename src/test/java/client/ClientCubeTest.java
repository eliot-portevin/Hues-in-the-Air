package server;

import game.Block;
import game.GameConstants;
import game.Vector2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.ClientHandler;
import server.Lobby;
import server.ServerGame;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientCubeTest {
    static ClientCube tester;
    static int velocity_constant;
    static int acceleration_constant;
    static int blockSize;
    static int cubeSize;
    static int blocksPerSecond;
    static Thread testGameThread;
    static Thread clientThread1;
    static Thread clientThread2;
    static HashMap<ClientHandler, Color> clientsAndColours;

    @BeforeEach
    void setUp() {
        tester = new ClientCube(new Pane(), new Vector2D(0, 0)); // test Cube

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

        clientsAndColours = new HashMap<>();
        clientsAndColours.put(c1, Color.PINK);
        clientsAndColours.put(c2, Color.BLUE);


        ServerGame serverGame = new ServerGame(clientsAndColours, "1", Mockito.mock(Lobby.class));
        testGameThread = new Thread(serverGame);
        testGameThread.start();
    }

    @Test
    void testSetPosition() {
        Vector2D testPosition = new Vector2D(100, 100);
        tester.setPositionTo(100, 100);
        assertAll(
            () -> assertEquals(testPosition.getX(), tester.getPosition().getX()),
            () -> assertEquals(testPosition.getY(), tester.getPosition().getY())
        );
    }

    @Test
    void testInitialiseSpeed() {
        tester.initialiseSpeed();
        assertAll(
            () -> assertEquals(blockSize * blocksPerSecond, tester.getVelocity().getX()),
            () -> assertEquals(0, tester.getVelocity().getY()),
            () -> assertEquals(0, tester.getAcceleration().getX()),
            () -> assertEquals(acceleration_constant, tester.getAcceleration().getY()),
            () -> assertEquals(0, tester.accelerationAngle)
        );
    }

    @Test
    void testOnlySetAccelerationAngle() {
        tester.onlySetAccelerationAngle(90);
        assertAll(
            () -> assertEquals(90, tester.accelerationAngle),
            () -> assertEquals(acceleration_constant, tester.getAcceleration().getX(), 0.0001),
            () -> assertEquals(0, tester.getAcceleration().getY(), 0.0001)
        );
    }

    @Test
    void testSetAccelerationAngle180() {
        Vector2D initVelocity = new Vector2D(tester.getVelocity().getX(), tester.getVelocity().getY());
        tester.setAccelerationAngle(180);
        assertAll(
            () -> assertEquals(180, tester.accelerationAngle),
            () -> assertEquals(initVelocity.getX(), tester.getVelocity().getX(), 0.0001),
            () -> assertEquals(initVelocity.getY(), tester.getVelocity().getY(), 0.0001)
        );
    }

    @Test
    void testSetAccelerationAngleNot180() {
        Vector2D initVelocity = new Vector2D(tester.getVelocity().getX(), tester.getVelocity().getY());
        Vector2D initAcceleration = new Vector2D(tester.getAcceleration().getX(), tester.getAcceleration().getY());
        tester.setAccelerationAngle(90);
        assertAll(
            () -> assertEquals(90, tester.accelerationAngle),
            () -> assertEquals(acceleration_constant * Math.cos(Math.toRadians(90)), tester.getAcceleration().getY(), 0.0001),
            () -> assertEquals(acceleration_constant * Math.sin(Math.toRadians(90)), tester.getAcceleration().getX(), 0.0001),
            () -> assertEquals(Math.signum(-initAcceleration.getX()) * velocity_constant, tester.getVelocity().getX(), 0.0001),
            () -> assertEquals(Math.signum(-initAcceleration.getY()) * velocity_constant, tester.getVelocity().getY(), 0.0001)
        );
    }

    @Test
    void testResetMovement() {
        tester.setPositionTo(100, 100);
        tester.resetMovement();
        assertAll(
            () -> assertEquals(tester.start_position.getX(), tester.getPosition().getX()),
            () -> assertEquals(tester.start_position.getY(), tester.getPosition().getY()),
            () -> assertEquals(0, tester.accelerationAngle),
            () -> assertEquals(0, tester.getVelocity().getX(), 0.0001),
            () -> assertEquals(0, tester.getVelocity().getY(), 0.0001)
        );
    }

    @Test
    void checkForRotationY() {
        tester.canRotate = true;
        Vector2D initPosition = new Vector2D(100, 100);

        tester.rotationPoint = null;
        if(tester.rotationPoint == null) return;

        tester.setPositionTo(100, 100);
        tester.setVelocityTo(200, 100);
        Vector2D initVelocity = tester.getVelocity();
        Vector2D finalInitVelocity = initVelocity;

        tester.rotationPoint = new Vector2D(100, 50);
        tester.onlySetAccelerationAngle(0);
        tester.checkForRotation();
        assertAll(
            () -> assertEquals(180, tester.accelerationAngle),
            () -> assertEquals(-finalInitVelocity.getX(), tester.getVelocity().getX(), 0.0001),
            () -> assertFalse(tester.canRotate)
        );

        tester.canRotate = true;
        tester.setPositionTo(100, 100);
        tester.setVelocityTo(200, 100);
        initVelocity = tester.getVelocity();
        Vector2D finalInitVelocity1 = initVelocity;

        tester.rotationPoint = new Vector2D(100, 150);
        tester.onlySetAccelerationAngle(180);
        tester.checkForRotation();
        assertAll(
            () -> assertEquals(0, tester.accelerationAngle),
            () -> assertEquals(-finalInitVelocity1.getX(), tester.getVelocity().getX(), 0.0001),
            () -> assertFalse(tester.canRotate)
        );
    }

    @Test
    void checkForRotationX() {
        tester.canRotate = true;
        tester.rotationPoint = null;
        if(tester.rotationPoint == null) return;

        tester.setPositionTo(100, 100);
        tester.setVelocityTo(100, 200);
        Vector2D initVelocity = tester.getVelocity();
        Vector2D finalInitVelocity = initVelocity;

        tester.rotationPoint = new Vector2D(50, 100);
        tester.onlySetAccelerationAngle(90);
        tester.checkForRotation();
        assertAll(
            () -> assertEquals(270, tester.accelerationAngle),
            () -> assertEquals(-finalInitVelocity.getY(), tester.getVelocity().getY(), 0.0001),
            () -> assertFalse(tester.canRotate)
        );

        tester.canRotate = true;
        tester.setPositionTo(100, 100);
        tester.setVelocityTo(100, 200);
        initVelocity = tester.getVelocity();
        Vector2D finalInitVelocity1 = initVelocity;

        tester.rotationPoint = new Vector2D(150, 100);
        tester.onlySetAccelerationAngle(270);
        assertAll(
            () -> assertEquals(90, tester.accelerationAngle),
            () -> assertEquals(-finalInitVelocity1.getY(), tester.getVelocity().getY(), 0.0001),
            () -> assertFalse(tester.canRotate)
        );
    }

    @Test
    void testJump() {
        tester.rotationPoint = new Vector2D(100, 100);
        tester.jumping = false;
        tester.canRotate = false;
        tester.colourCanJump = Color.BLACK;
        tester.setVelocityTo(100, 0);

        Vector2D resultRotationPoint = new Vector2D(tester.getPosition().getX(), tester.getPosition().getY());
        resultRotationPoint.addInPlace(tester.getVelocity().multiply(0.5));
        resultRotationPoint.addInPlace(
                new Vector2D(
                        Math.signum(tester.getAcceleration().getX()) * blockSize,
                        Math.signum(tester.getAcceleration().getY()) * blockSize));

        Vector2D resultJumpVector =
                new Vector2D(
                        Math.sin(Math.toRadians(tester.accelerationAngle)),
                        Math.cos(Math.toRadians(tester.accelerationAngle)));
        resultJumpVector.multiplyInPlace(-tester.maxVelocity);
        Vector2D resultVelocity = new Vector2D(tester.getVelocity().getX() + resultJumpVector.getX(), tester.getVelocity().getY() + resultJumpVector.getY());

        tester.jump(Color.BLACK);


        assertAll(
                () -> assertEquals(resultRotationPoint.getX(), tester.rotationPoint.getX(), 0.0001),
                () -> assertEquals(resultRotationPoint.getY(), tester.rotationPoint.getY(), 0.0001),
                () -> assertEquals(resultVelocity.getX(), tester.getVelocity().getX(), 0.0001),
                () -> assertEquals(resultVelocity.getY(), tester.getVelocity().getY(), 0.0001),
                () -> assertTrue(tester.jumping),
                () -> assertTrue(tester.canRotate)
        );
    }

    @Test
    void moveWithoutCollision() {
        Block[] neighbours = new Block[9];
        for(int i = 0; i < neighbours.length; i++) {
            neighbours[i] = null;
        }
        Vector2D startPosition = new Vector2D(500, 500);
        tester.setPositionTo(500, 500);
        tester.initialiseSpeed();
        tester.getVelocity().addInPlace(tester.acceleration.multiply(0.5));
        tester.move(neighbours, 1);
        Vector2D endPosition = new Vector2D(startPosition.getX() + tester.getVelocity().getX(), startPosition.getY() + tester.getVelocity().getY());

        assertAll(
                () -> assertEquals(endPosition.getX(), tester.getPosition().getX(), 0.0001),
                () -> assertEquals(endPosition.getY(), tester.getPosition().getY(), 0.0001)
        );
    }

    @Test
    void moveWithCollisionRight() {
        double dt = 1 / 60.0;
        Block[] neighbours = new Block[1];
        neighbours[0] = new Block(Color.BLACK, 0, 0, 50);

        Vector2D startPosition = new Vector2D(0, 0);
        tester.setPositionTo(startPosition.getX(), startPosition.getY());
        tester.setVelocityTo(400, 0);
        tester.initialiseSpeed();
        tester.getVelocity().addInPlace(tester.acceleration.multiply(dt));
        tester.move(neighbours, dt);
        Vector2D endPosition = new Vector2D(0 - cubeSize, 0);

        assertAll(
                // we create a Wall in front of the Block at 0,0, the Block is moving to the right and collides with the Wall.
                // Now the X coordinate must be set to the Wall X position - Cubesize
                () -> assertEquals(endPosition.getX(), tester.getPosition().getX(), 0.0001)
        );
    }

    @Test
    void moveWithCollisionLeft() {
        double dt = 1/60.0;
        Block[] neighbours = new Block[1];
        neighbours[0] = new Block(Color.BLACK, 0, 0, 50);

        Vector2D startPosition = new Vector2D(50, 0);
        tester.setPositionTo(startPosition.getX(), startPosition.getY());
        tester.setVelocityTo(-400,0);
        tester.getVelocity().addInPlace(tester.acceleration.multiply(dt));
        tester.move(neighbours, dt);
        Vector2D endPosition = new Vector2D(0+blockSize, 0);

        assertAll(
                // we create a wall in front of the block at 0,0, the block is moving to the left and collides with the wall.
                // Now the X coordinate must be set to the wall X position + wall size because the X coordinate of the wall is 0 and the block has a with = size
                // so the correct position will be at the right edge of the wall = 0 + size
                () -> assertEquals(endPosition.getX(), tester.getPosition().getX(), 0.0001)
        );
    }


    @Test
    void moveWithCollisionDown() {
        double dt = 1/60.0;
        Block[] neighbours = new Block[1];

        neighbours[0] = new Block(Color.BLACK, 0, 0, 50);

        Vector2D startPosition = new Vector2D(10, 0);
        tester.setPositionTo(startPosition.getX(), startPosition.getY());
        tester.setVelocityTo(0,400);
        tester.getVelocity().addInPlace(tester.acceleration.multiply(dt));
        tester.move(neighbours, dt);
        Vector2D endPosition = new Vector2D(0, 0-cubeSize);

        assertAll(
                // we create a Wall under the Block at 0,0, the Block is moving down and collides with the Wall.
                // Now the Y coordinate must be set to the Wall X position - Cubesize
                () -> assertEquals(endPosition.getY(), tester.getPosition().getY(), 0.0001)
        );
    }

    @Test
    void moveWithCollisionUp() {
        double dt = 1/60.0;
        Block[] neighbours = new Block[1];
        neighbours[0] = new Block(Color.BLACK, 0, 0, 50);

        Vector2D startPosition = new Vector2D(10, 30);
        tester.setPositionTo(startPosition.getX(), startPosition.getY());
        tester.setVelocityTo(0,-400);
        tester.getVelocity().addInPlace(tester.acceleration.multiply(dt));
        tester.move(neighbours, dt);
        Vector2D endPosition = new Vector2D(0, 0+blockSize);

        assertAll(
                // we create a wall above the block at 0,0, the block is moving to up and collides with the wall.
                // Now the Y coordinate must be set to the wall Y position + it's size so the block is at the bottom of the wall
                () -> assertEquals(endPosition.getY(), tester.getPosition().getY(), 0.0001)
        );
    }

    @Test
    void isEdgeCollisionX() {
        Block block = new Block(null, 50, 50, 50);
        tester.setPositionTo(50, 20);
        assertTrue(tester.isEdgeCollision(block, true));
        tester.setPositionTo(50, 100);
        assertTrue(tester.isEdgeCollision(block, true));
    }

    @Test
    void isEdgeCollisionY() {
        Block block = new Block(null, 50, 50, 50);
        tester.setPositionTo(20, 50);
        assertTrue(tester.isEdgeCollision(block, false));
        tester.setPositionTo(100, 50);
        assertTrue(tester.isEdgeCollision(block, false));
    }


}