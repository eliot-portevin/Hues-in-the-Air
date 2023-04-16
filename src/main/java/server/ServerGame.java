package server;

import javafx.application.Application;
import javafx.stage.Stage;
import server.ServerCube;
import client.LevelData;
import client.Vector2D;
import gui.Colours;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ServerGame implements Runnable {
  public HashMap<KeyCode, Boolean> keys = new HashMap<>();
  private ArrayList<Node> platforms = new ArrayList<>(); // Used to store platforms
  private ArrayList<Node> death_platforms = new ArrayList<>();
  private ArrayList<Node> stars = new ArrayList<>(); // Used to store collectable stars
  private Pane gameRoot;
  private ServerCube player;
  private int gridSize = 50;
  private boolean jumped;
  private AnimationTimer timer;
  public boolean pause = true;
  private boolean allClientsReady = false;
  private String[] levelData = LevelData.Level1;

  public static ArrayList<Color> blockColours =
      new ArrayList<>(
          Arrays.asList(
              Color.valueOf("#f57dc6"),
              Color.valueOf("#b3d5f2"),
              Color.valueOf("#9ae6ae"),
              Color.valueOf("#fccf78")));
  private final HashMap<ClientHandler, Color> clientColours;
  private Boolean running = true;
  private ArrayList<ClientHandler> clients;
  public ServerGame(HashMap<ClientHandler, Color> clientColours, ArrayList<ClientHandler> clients) {

    this.clientColours = clientColours;
    initializeContent();
    this.clients = clients;
    System.out.println("Game initialized");
  }

  // TODO add a method to handle client jumping
  // TODO add a method to update the client's position
  // TODO add a method to updated the client's velocity and gravity
  // TODO add a method to pause game when client disconnects
  // TODO add a method to unpause game when client reconnects

  protected boolean handleJumpRequest(ClientHandler client) {
    if (client.canJump) {
      player.jump();
      return true;
    }
    return false;
  }

  protected void updateAllClientPositions() {
    for (ClientHandler client : clients) {
      client.smallUpdate(ServerProtocol.POSITION_UPDATE.toString() + ServerProtocol.SEPARATOR.toString() + player.position.getX() + ServerProtocol.SEPARATOR.toString() + player.position.getY());
    }
  }

  protected void updateAllClientsGravitationAndVelocity() {
    for (ClientHandler client : clients) {
      client.smallUpdate(ServerProtocol.BIG_UPDATE.toString() + ServerProtocol.SEPARATOR.toString() + player.velocity.getX() + ServerProtocol.SEPARATOR.toString() + player.velocity.getY() + ServerProtocol.SEPARATOR.toString() + player.g.getX() + ServerProtocol.SEPARATOR.toString() + player.g.getY());
    }
    //TODO: BROADCAST GRAVITATION AND VELOCITY TO ALL CLIENTS
    //Statement string = BIGUPDATE + SEPERATOR + player.position.x + SEPERATOR + player.position.y + SEPERATOR + player.velocity.x + SEPERATOR + player.velocity.y + SEPERATOR + player.gravity.x + SEPERATOR + player.gravity.y;
  }

  protected void pauseGame() {
    pause = true;
  }

  /**
   * Checks for collisions with the white blocks and calls resetsPosition if it collides with one
   */
  public void checkForWhiteBlockHit() {
    for (Node platform : death_platforms) {
      if (player.rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())){
        //TODO send message to client to reset position
        //Statement String = DEATH
        //System.out.println("DEATH");
      }
    }
  }
  /**
   * Called every frame and handles the game logic
   */
  public void update(double deltaF){
    if (!pause) {
      this.gameUpdate(deltaF);
    }
    else {
      this.pauseUpdate(deltaF);
    }
  }

  /**
   * The update method that is called if the game is not paused. Handles the game logic.
   */
  private void gameUpdate(double deltaF) {
    player.move(player.velocity);
    player.setPositionTo(player.start_position.getX(), player.start_position.getY());
    checkForWhiteBlockHit();
  }


  /**
   * The update method that is called if the game is paused.
   */
  private void pauseUpdate(double deltaF) {
    System.out.println("Game is paused");
    //Todo: Add pause menu and pause logic
  }

  /**
   * Sets whether the game is paused or not.
   */
  public void setPause(boolean pause) {
    this.pause = pause;
  }


  /**
   * Creates a new rectangle entity
   * @param x - x position
   * @param y - y position
   * @param w - width
   * @param h - height
   * @param color - colour
   * @return - returns the rectangle entity
   */
  private Node createEntity(int x, int y, int w, int h, Color color) {
    Rectangle entity = new Rectangle(w, h);
    entity.setTranslateX(x);
    entity.setTranslateY(y);
    entity.setFill(color);
    gameRoot.getChildren().add(entity);
    return entity;
  }

  /**
   * Initializes the content of the game
   * Loads the level data and creates the platforms
   * Creates the player
   * Creates the stars
   * Will create the coin to finish the game
   */
  public void initializeContent() {
    gameRoot = new Pane();
    load_platforms(); // Loads the platforms
  }

  /**
   * Loads the platforms from the level data
   */
  private void load_platforms() {
    for (int i=0; i<this.levelData.length; i++) { // Creates the platforms
      String line = this.levelData[i];
      for (int j = 0; j < line.length(); j++) {
        switch (line.charAt(j)) {
          case '0':
            break;
          case '1':
            Node platform1 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.WHITE.getHex());
            platforms.add(platform1);
            death_platforms.add(platform1);
            break;
          case '2':
            Node platform2 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.PINK.getHex());
            platforms.add(platform2);
            break;
          case '3':
            Node platform3 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.BLUE1.getHex());
            platforms.add(platform3);
            break;
          case '4':
            Node platform4 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.GREEN.getHex());
            platforms.add(platform4);
            break;
          case '5':
            Node platform5 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.YELLOW.getHex());
            platforms.add(platform5);
            break;
          case '7':
            load_player(new Vector2D(j*gridSize, i*gridSize));
        }
      }
    }
  }

  /**
   * Loads the player
   */
  private void load_player(Vector2D position){
    player = new ServerCube(gameRoot, position, new Vector2D(20,20));  // creates the player
    player.start_position = position;
    player.platforms = platforms; // Sets the platforms for the player
    player.death_platforms = death_platforms;
    player.gridSize = gridSize; // Sets the grid size for the player
  }

  /**
   * Make the cube jump
   */
  public void jump() {
    this.player.jump();
  }

  /**
   * Gets the frame rate in hertz
   * @param deltaTimeNano - the time between frames in nanoseconds
   * @return - the frame rate in hertz
   */
  public double getFrameRateHertz(long deltaTimeNano) {
    double frameRate = 1d / deltaTimeNano;
    return frameRate * 1e9;
  }

  public void startGameLoop() {
    this.running = true;
    this.pause = false;
  }

  public void pause() {
    this.pause = true;
  }

  public void unpause() {
    this.pause = false;
  }

  /**
   * Runnable run method. This method is called when the thread is started.
   * */
  @Override
  public void run() {
    this.initializeContent();

    double timePerFrame = 1e9 / 60; // 60 FPS
    double deltaF = 0;
    long previousTime = System.nanoTime();
    int frames = 0;
    long lastCheck = System.currentTimeMillis();
    long now = 0;



    while (!allClientsReady){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        allClientsReady = true;
        for (ClientHandler client : clients){
            if (!client.ready){
                allClientsReady = false;
                System.out.println("Client not ready");
                break;
            }
        }
    }

    while(this.running) {
      now = System.nanoTime();
      try {
        Thread.sleep((long) Math.floor(1e3 / 60));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      if (!pause) {
        deltaF += (System.nanoTime() - previousTime) / timePerFrame;
        previousTime = System.nanoTime();
        if (deltaF >= 1) {
          while (deltaF >= 1) {
            update(deltaF);
            frames++;
            deltaF--;
          }
        }
        updateAllClientPositions();
        if (System.currentTimeMillis() - lastCheck >= 1000) {
          System.out.println("FPS: " + frames);
          frames = 0;
          lastCheck = System.currentTimeMillis();
        }
      } else {
        pauseUpdate(deltaF);
        deltaF += (System.nanoTime() - previousTime) / timePerFrame;
        previousTime = System.nanoTime();
        if (deltaF >= 1) {
          while (deltaF >= 1) {
            update(deltaF);
            frames++;
            deltaF--;
          }
        }
      }
    }
  };
}
