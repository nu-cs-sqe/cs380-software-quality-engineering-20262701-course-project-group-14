package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        GameSetupView view = new GameSetupView();
        Scene scene = new Scene(view, 640, 400);

        stage.setTitle("Exploding Kittens");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
