package ui;

import controller.GamePlayController;
import domain.Card;
import domain.CardType;
import domain.Deck;
import domain.DiscardPile;
import domain.ExplodingKittenResolver;
import domain.ExplosionOutcome;
import domain.GameState;
import domain.Player;
import domain.TurnEngine;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        runInJavaFx(CatPairJavaFxSmokeTests::exerciseWindow);
    }

    @Test
    void FixedGame_LastOpponentEliminated_ShowsWinnerAndLocksPlay() throws Exception {
        runInJavaFx(CatPairJavaFxSmokeTests::exerciseWinner);
    }

    private static void runInJavaFx(Runnable exercise) throws Exception {
        CompletableFuture<Void> finished = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                exercise.run();
                finished.complete(null);
            } catch (Throwable failure) {
                finished.completeExceptionally(failure);
            }
        });
        finished.get(20, TimeUnit.SECONDS);
    }

    private static void exerciseWinner() {
        Player first = playerWith("First");
        Card held = new Card(CardType.TACOCAT);
        Player winner = playerWith("Winner", held);
        GameState state = new GameState(List.of(first, winner), new Deck());
        DiscardPile discard = new DiscardPile();
        ExplodingKittenResolver elimination = new ExplodingKittenResolver(discard) {
            @Override
            public ExplosionOutcome begin(Player player, Card kitten) {
                player.eliminate();
                return ExplosionOutcome.PLAYER_ELIMINATED;
            }
        };
        GamePlayView view = new GamePlayView();
        TurnEngine engine = new TurnEngine(state, discard, elimination, Map.of());
        new GamePlayController(engine, view).initialize();
        Stage stage = new Stage();
        Scene scene = new Scene(view, 960, 760);
        stage.setScene(scene);
        try {
            stage.show();
            Button draw = (Button) scene.lookup("#draw-card");
            assertNotNull(draw);
            draw.fire();

            assertEquals("Winner wins!", scene.lookup("#gameplay-message").getAccessibleText());
            assertTrue(draw.isDisabled());
            Button card = handCard(scene, 0);
            assertTrue(card.getAccessibleText().contains("locked"));
            card.fire();
            draw.fire();
            assertEquals(List.of(held), winner.getHand());
            assertSame(winner, state.getCurrentPlayer());
        } finally {
            stage.close();
        }
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
            selectFirstInHand(scene);
            handCard(scene, 1).fire();
            assertTrue(hasState(handCard(scene, 0), "pair-first"));
            handCard(scene, 0).fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "",
                    KeyCode.ESCAPE, false, false, false, false));
            assertFalse(hasState(handCard(scene, 0), "pair-first"));
            assertFalse(hasState(handCard(scene, 2), "pair-match"));
            assertEquals(List.of(first, held, second), current.getHand());
            assertEquals(List.of(gift), target.getHand());

            selectFirstInHand(scene);
            handCard(scene, 0).fire();
            assertFalse(hasState(handCard(scene, 0), "pair-first"));

            selectFirstInHand(scene);
            clickMatchingCard(scene, ButtonData.CANCEL_CLOSE);
            assertFalse(hasState(handCard(scene, 0), "pair-first"));
            assertEquals(List.of(first, held, second), current.getHand());
            assertEquals(List.of(gift), target.getHand());

            selectFirstInHand(scene);
            clickMatchingCard(scene, ButtonData.OK_DONE);
            assertEquals(List.of(held, gift), current.getHand());
            assertEquals(List.of(), target.getHand());
            assertSame(current, state.getCurrentPlayer());
            Button renderedCard = (Button) scene.lookup("#hand-card-1");
            assertNotNull(renderedCard);
            assertTrue(renderedCard.getAccessibleText().contains("Favor"));
            Node discardPile = scene.lookup("#discard-pile");
            assertNotNull(discardPile);
            assertEquals(List.of("Tacocat", "Tacocat"), discardPile.lookupAll(".discard-face")
                    .stream().map(Node::getAccessibleText).collect(Collectors.toList()));
        } finally {
            stage.close();
        }
    }

    private static Button handCard(Scene scene, int index) {
        Button card = (Button) scene.lookup("#hand-card-" + index);
        assertNotNull(card);
        return card;
    }

    private static boolean hasState(Button card, String state) {
        return card.getPseudoClassStates().contains(PseudoClass.getPseudoClass(state));
    }

    private static void selectFirstInHand(Scene scene) {
        AtomicBoolean selecting = new AtomicBoolean(true);
        AtomicBoolean openedDialog = new AtomicBoolean();
        Platform.runLater(() -> {
            if (selecting.get()) {
                Window.getWindows().stream().filter(window -> dialogPane(window) != null)
                        .findFirst().ifPresent(window -> {
                            openedDialog.set(true);
                            window.hide();
                        });
            }
        });
        handCard(scene, 0).fire();
        selecting.set(false);
        assertFalse(openedDialog.get(), "The first click stays in the hand.");
        assertTrue(hasState(handCard(scene, 0), "pair-first"));
        assertFalse(hasState(handCard(scene, 1), "pair-match"));
        assertTrue(hasState(handCard(scene, 2), "pair-match"));
    }

    private static void clickMatchingCard(Scene scene, ButtonData action) {
        CompletableFuture<Void> driven = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                choose("Target", action);
                driven.complete(null);
            } catch (Throwable failure) {
                driven.completeExceptionally(failure);
                List.copyOf(Window.getWindows()).stream().filter(window -> dialogPane(window) != null)
                        .forEach(Window::hide);
            }
        });
        handCard(scene, 2).fire();
        assertTrue(driven.isDone(), "The matching card opens the target chooser.");
        driven.join();
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
