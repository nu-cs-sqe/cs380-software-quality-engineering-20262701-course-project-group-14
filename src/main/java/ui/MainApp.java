package ui;

import controller.GameSetupController;
import domain.Card;
import domain.CardType;
import domain.GameSnapshot;
import domain.GameState;
import domain.Player;
import domain.TurnPhase;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        GameSetupView view = new GameSetupView();
        Scene scene = new Scene(view, 900, 760);
        view.setOnStart(() -> {
            try {
                startGame(view.getPlayerCountInput(), state -> showGameplay(scene, state),
                        view::showMessage);
            } catch (IllegalArgumentException exception) {
                view.showMessage(exception.getMessage());
            }
        });

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
        Player currentPlayer = state.getCurrentPlayer();
        List<CardType> hand = currentPlayer.getHand().stream()
                .map(Card::getType)
                .collect(Collectors.toList());
        GameSnapshot initial = GameSnapshot.create(currentPlayer.getName(), hand,
                state.getDeck().size(), 0, state.getPlayers().size(), TurnPhase.ACTIONS_ALLOWED);
        gameplay.render(initial);
        gameplay.disableTurnActions();
        scene.setRoot(gameplay);
    }

    static void startGame(
            String playerCountText,
            Consumer<GameState> openGameplay,
            Consumer<String> showError
    ) {
        GameState state = new GameSetupController().startGame(playerCountText);
        openGameplay.accept(state);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
