package ui;
import controller.GameSetupController;
import domain.GameState;
import java.util.Objects;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class MainApp extends Application {
    private GameState gameState;

    @Override
    public void start(Stage stage) {
        GameSetupView view = new GameSetupView();
        GameSetupController controller = new GameSetupController();
        view.setOnStart(() -> handleStartGame(controller, view));
        Scene scene = new Scene(view, 900, 760);

        scene.getStylesheets().add(
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
    private void handleStartGame(
            GameSetupController controller,
            GameSetupView view
    ) {
        try {
            gameState = controller.startGame(
                    view.getPlayerCountInput()
            );

            view.showMessage(
                    "Game ready! "
                            + gameState.getPlayers().size()
                            + " players, "
                            + gameState.getDeck().size()
                            + " cards remaining. "
                            + gameState.getCurrentPlayer().getName()
                            + " goes first."
            );
        } catch (IllegalArgumentException exception) {
            view.showMessage(exception.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
