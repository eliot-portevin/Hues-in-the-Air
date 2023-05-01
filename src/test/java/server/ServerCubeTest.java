package server;

import game.GameConstants;
import game.Vector2D;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerCubeTest {
    static ServerCube tester;
    static int velocity_constant;
    static int acceleration_constant;
    static int blockSize;
    static int cubeSize;
    static int blocksPerSecond;
    static Thread testGameThread;
    static Thread clientThread1;
    static Thread clientThread2;

    @BeforeEach
    void setUp() {
        tester = new ServerCube(new Pane(), new Vector2D(0, 0)); // test Cube

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
}