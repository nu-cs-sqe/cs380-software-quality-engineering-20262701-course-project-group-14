package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane(
                new Label("Exploding Kittens")
        );

        Scene scene = new Scene(root, 640, 400);

        stage.setTitle("Exploding Kittens");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
