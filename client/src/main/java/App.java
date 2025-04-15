/*package com.example.javafxapp;

// IMPORTS
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;

public class App extends Application {
    @Override
    public void start(Stage stage) {

        Image image = new Image(getClass().getResourceAsStream("/connect4logo.png"));

        // SCENE 1 - MAIN MENU
        
        ImageView imageView1 = new ImageView(image);
        imageView1.setFitWidth(300);
        imageView1.setPreserveRatio(true);
        Label imgCaption1 = new Label("JavaFX Edition");
        Label prompt = new Label("Enter your username:");
        TextField username = new TextField("Type username here");
        TextField valid = new TextField("Username");

        // conditional logic to determine whether username is in use or not

        Button connect = new Button("Connect");
        VBox box1 = new VBox(imageView1, imgCaption1, prompt, username, valid, connect);
        box1.setAlignment(Pos.CENTER);

        // SCENE 2 - CONNECTING...
        
        ImageView imageView2 = new ImageView(image);
        imageView2.setFitWidth(300);
        imageView2.setPreserveRatio(true);
        Label imgCaption2 = new Label("JavaFX Edition");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        
        Button goBack = new Button("Go Back");
        Label status = new Label("Waiting to connect to server...");
        VBox box2 = new VBox(imageView2, imgCaption2, progressBar, status, goBack);
        box2.setAlignment(Pos.CENTER);

        // SCENE 3 - GAMEPLAY
        
        TextField numRound = new TextField("Round ");
        TextField turn = new TextField("turn...");
        Button quitGame = new Button("Quit Game");
        Button reset = new Button("Reset");
        VBox col1 = new VBox();
        VBox col2 = new VBox();
        VBox col3 = new VBox();
        HBox box3 = new HBox(col1, col2, col3);

        // SCENE 4 - GAME END
        
        TextField gameEndMessage = new TextField("Congratulations...");
        // if a draw, display: Draw! (no username)
        Button playAgain = new Button("Play Again");
        Button quit = new Button("Quit Game");
        VBox box4 = new VBox(gameEndMessage, playAgain, quit);

        // BUTTON BEHAVIOR
        

        // SCENE CONTROL
        Scene scene1 = new Scene(box1, 700, 500);
        Scene scene2 = new Scene(box2, 700, 500);
        Scene scene3 = new Scene(box3, 700, 500);
        Scene scene4 = new Scene(box4, 700, 500);

        // b1.setOnAction(e -> primaryStage.setScene(scene2)); // scene 2
        connect.setOnAction(e -> stage.setScene(scene2));
        goBack.setOnAction(e -> stage.setScene(scene1));

        stage.setScene(scene1);
        stage.setTitle("CONNECT 4 - JAVAFX EDITION");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/

package com.example.javafxapp;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


