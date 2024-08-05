package chapter5;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import chapter5.controller.DrawingLoop;
import chapter5.controller.GameLoop;
import chapter5.view.Platform;

public class Launcher extends Application {
    public static void main(String[] args) { launch(args); }
    @Override
    public void start(Stage primaryStage) {
        
        
        Platform platform = new Platform();
        GameLoop gameLoop = new GameLoop(platform);
        DrawingLoop drawingLoop = new DrawingLoop(platform);
        Scene scene = new Scene(platform,platform.WIDTH,platform.HEIGHT);
        scene.setOnKeyPressed(event-> platform.getKeys().add(event.getCode()));
        scene.setOnKeyReleased(event ->  platform.getKeys().remove(event.getCode()));
        primaryStage.setTitle("platformer");
        primaryStage.setScene(scene);
        primaryStage.show();
        Thread gmLoop = new Thread(gameLoop);
        gmLoop.setDaemon(true);
        gmLoop.start();
        Thread dwLoop = new Thread(drawingLoop);
        dwLoop.setDaemon(true);
        dwLoop.start();
    }
}