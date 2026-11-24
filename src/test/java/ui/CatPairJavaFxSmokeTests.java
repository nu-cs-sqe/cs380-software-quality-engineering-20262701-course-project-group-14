package ui;

import domain.Card;
import domain.CardType;
import domain.Deck;
import domain.GameState;
import domain.Player;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "CS380_RUN_JAVAFX_SMOKE", matches = "1")
class CatPairJavaFxSmokeTests {
    @BeforeAll
    static void startJavaFx() throws Exception {
        CompletableFuture<Void> started = new CompletableFuture<>();
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.complete(null);
        });
        started.get(10, TimeUnit.SECONDS);
    }

    @AfterAll
    static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    void FixedGame_CancelsThenPlaysPairThroughRealWindow() throws Exception {
        CompletableFuture<Void> finished = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                exerciseWindow();
                finished.complete(null);
            } catch (Throwable failure) {
                finished.completeExceptionally(failure);
            }
        });
        finished.get(20, TimeUnit.SECONDS);
    }

    private static void exerciseWindow() {
        Card first = new Card(CardType.TACOCAT);
        Card second = new Card(CardType.TACOCAT);
        Card held = new Card(CardType.SKIP);
        Card gift = new Card(CardType.FAVOR);
        Player current = playerWith("Player 1", first, held, second);
        Player eliminated = playerWith("Eliminated");
        eliminated.eliminate();
        Player empty = playerWith("Empty");
        Player target = playerWith("Target", gift);
        Deck deck = new Deck();
        GameState state = new GameState(List.of(current, eliminated, empty, target), deck);
        Stage stage = new Stage();
        Scene scene = new Scene(new StackPane(), 960, 760);
        stage.setScene(scene);
        try {
            stage.show();
            MainApp.showGameplay(scene, state);
            clickPairWith(scene, () -> choose("Tacocat", ButtonData.CANCEL_CLOSE), null);
            assertEquals(List.of(first, held, second), current.getHand());
            assertEquals(List.of(gift), target.getHand());

            clickPairWith(scene, () -> choose("Hand position 3", ButtonData.OK_DONE),
                    () -> choose("Target", ButtonData.CANCEL_CLOSE));
            assertEquals(List.of(first, held, second), current.getHand());
            assertEquals(List.of(gift), target.getHand());

            clickPairWith(scene, () -> choose("Hand position 3", ButtonData.OK_DONE),
                    () -> choose("Target", ButtonData.OK_DONE));
            assertEquals(List.of(held, gift), current.getHand());
            assertEquals(List.of(), target.getHand());
            assertSame(current, state.getCurrentPlayer());
            Button renderedCard = (Button) scene.lookup("#hand-card-1");
            assertNotNull(renderedCard);
            assertTrue(renderedCard.getAccessibleText().contains("Favor"));
        } finally {
            stage.close();
        }
    }

    private static void clickPairWith(Scene scene, Runnable firstChoice, Runnable targetChoice) {
        Button firstCard = (Button) scene.lookup("#hand-card-0");
        assertNotNull(firstCard);
        CompletableFuture<Void> driven = new CompletableFuture<>();
        scheduleChoice(driven, firstChoice, targetChoice);
        firstCard.fire();
        assertTrue(driven.isDone(), "Clicking a Cat Card must open the chooser.");
        driven.join();
    }

    private static void scheduleChoice(CompletableFuture<Void> driven, Runnable choice, Runnable next) {
        Platform.runLater(() -> {
            try {
                choice.run();
                if (next == null) {
                    driven.complete(null);
                } else {
                    scheduleChoice(driven, next, null);
                }
            } catch (Throwable failure) {
                driven.completeExceptionally(failure);
                List.copyOf(Window.getWindows()).stream().filter(window -> dialogPane(window) != null)
                        .forEach(Window::hide);
            }
        });
    }

    private static void choose(String expectedOption, ButtonData action) {
        DialogPane pane = Window.getWindows().stream().map(CatPairJavaFxSmokeTests::dialogPane)
                .filter(candidate -> candidate != null).findFirst().orElseThrow();
        ListView<?> choices = (ListView<?>) pane.lookup("#cat-pair-options");
        assertEquals(1, choices.getItems().size());
        assertTrue(choices.getItems().get(0).toString().contains(expectedOption));
        if (action == ButtonData.OK_DONE) {
            choices.getSelectionModel().select(0);
        }
        ButtonType button = pane.getButtonTypes().stream()
                .filter(type -> type.getButtonData() == action).findFirst().orElseThrow();
        ((Button) pane.lookupButton(button)).fire();
    }

    private static DialogPane dialogPane(Window window) {
        Scene scene = window.getScene();
        if (!window.isShowing() || scene == null) {
            return null;
        }
        return (DialogPane) scene.lookup("#cat-pair-dialog");
    }

    private static Player playerWith(String name, Card... hand) {
        Player player = new Player(name);
        for (Card card : hand) {
            player.addCard(card);
        }
        return player;
    }
}
