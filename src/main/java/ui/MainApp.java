package ui;

import controller.GamePlayController;
import controller.GamePlayViewPort;
import controller.GameSetupController;
import domain.GameState;
import domain.TurnEngineFactory;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        GameSetupView view = new GameSetupView();
        Scene scene = new Scene(view, 900, 760);
        configureSetup(scene, view);

        stage.setTitle(Texts.get("window.title"));
        stage.setMinWidth(780);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    private static void configureSetup(Scene scene, GameSetupView view) {
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
    }

    static void showGameplay(Scene scene, GameState state) {
        GamePlayView gameplay = new GamePlayView();
        bindGameplayNavigation(scene, createController(state, gameplay));
        scene.setRoot(gameplay);
    }

    static void startGameplay(GameState state, GamePlayViewPort view) {
        createController(state, view).initialize();
    }

    private static GamePlayController createController(GameState state, GamePlayViewPort view) {
        return new GamePlayController(TurnEngineFactory.create(state), view);
    }

    static void bindGameplayNavigation(Scene scene, GamePlayController controller) {
        controller.setOnReturnToSetup(() -> {
            GameSetupView setup = new GameSetupView();
            configureSetup(scene, setup);
            scene.setRoot(setup);
        });
        controller.initialize();
    }

    static void checkPlayerCount(String playerCountText, IntConsumer showNameStep,
                                 Consumer<String> showError) {
        startGame(playerCountText, state -> showNameStep.accept(state.getPlayers().size()), showError);
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
            showError.accept(Texts.get(exception.getMessage()));
            return;
        }
        openGameplay.accept(state);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
