package ui;

import controller.GamePlayController;
import controller.GamePlayViewPort;
import controller.GameSetupController;
import domain.GameState;
import domain.TurnActions;
import domain.TurnEngineFactory;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        GameSetupView view = new GameSetupView();
        Scene scene = new Scene(view, 900, 760);
        view.setOnStart(() -> startGame(
                view.getPlayerCountInput(),
                state -> showGameplay(scene, state),
                view::showMessage
        ));

        view.getStylesheets().add(
                Objects.requireNonNull(
                        MainApp.class.getResource("/ui/game-setup.css"),
                        "Missing game-setup.css"
                ).toExternalForm()
        );

        stage.setTitle("Exploding Kittens");
        stage.setMinWidth(780);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    private static void showGameplay(Scene scene, GameState state) {
        GamePlayView gameplay = new GamePlayView();
        startGameplay(state, gameplay);
        scene.setRoot(gameplay);
    }

    static void startGameplay(GameState state, GamePlayViewPort view) {
        TurnActions actions = TurnEngineFactory.create(state);
        new GamePlayController(actions, view).initialize();
    }

    static void startGame(
            String playerCountText,
            Consumer<GameState> openGameplay,
            Consumer<String> showError
    ) {
        GameState state;
        try {
            state = new GameSetupController().startGame(playerCountText);
        } catch (IllegalArgumentException exception) {
            showError.accept(exception.getMessage());
            return;
        }
        openGameplay.accept(state);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
