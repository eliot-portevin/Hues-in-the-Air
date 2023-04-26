package server;

import game.Vector2D;
import javafx.scene.layout.Pane;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerCubeTest {
    ServerCube tester;
    @BeforeAll
    static void setUp() {
        ServerCube tester = new ServerCube(new Pane(), new Vector2D(0,0), 30, 50);
    }
    @Test
    void spawnCube() {
        ServerCube tester = new ServerCube(new Pane(), new Vector2D(0,0), 30, 50);
        tester.spawnCube();
        
    }

    @Test
    void setPositionTo() {
    }

    @Test
    void gravityRotationX() {
    }

    @Test
    void gravityRotationY() {
    }

    @Test
    void jump() {
    }

    @Test
    void moveValueX() {
    }

    @Test
    void moveValueY() {
    }

    @Test
    void death() {
    }

    @Test
    void move() {
    }

    @Test
    void initialiseSpeed() {
    }

    @Test
    void resetLevel() {
    }
}