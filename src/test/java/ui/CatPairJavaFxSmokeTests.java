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
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
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
    void FixedGame_OneTarget_PlaysPairWithOneClick() throws Exception {
        runInJavaFx(CatPairJavaFxSmokeTests::exerciseOneTarget);
    }

    @Test
    void FixedGame_TwoTargets_CancelsThenStealsFromChosenSeat() throws Exception {
        runInJavaFx(CatPairJavaFxSmokeTests::exerciseTwoTargets);
    }

    @Test
    void FixedGame_LastOpponentEliminated_ShowsWinnerAndLocksPlay() throws Exception {
        runInJavaFx(() -> exerciseWinner((scene, controller) -> { }));
    }

    @Test
    void FixedGame_LateDrawAfterGameOver_KeepsWinnerVisible() throws Exception {
        runInJavaFx(() -> exerciseWinner((scene, controller) -> controller.handleDraw()));
    }

    @Test
    void FixedGame_LatePlayAfterGameOver_KeepsWinnerVisible() throws Exception {
        runInJavaFx(() -> exerciseWinner((scene, controller) -> controller.handlePlayCard(0)));
    }

    @Test
    void FixedGame_ReturnToSetup_OpensFreshSetupInTheSameWindow() throws Exception {
        runInJavaFx(() -> exerciseWinner((scene, controller) -> {
            Button back = (Button) scene.lookup("#return-to-setup");
            assertNotNull(back);
            assertTrue(back.isVisible());
            back.fire();
            assertTrue(scene.getRoot() instanceof GameSetupView);
        }));
    }

    @Test
    void FixedGame_StartAfterReturningToSetup_OpensANewGame() throws Exception {
        runInJavaFx(() -> exerciseWinner((scene, controller) -> {
            Node finishedGame = scene.getRoot();
            ((Button) scene.lookup("#return-to-setup")).fire();
            assertTrue(scene.getRoot() instanceof GameSetupView);

            TextField playerCount = (TextField) scene.lookup(".text-field");
            assertNotNull(playerCount);
            playerCount.setText("2");
            Button start = scene.getRoot().lookupAll(".button").stream()
                    .filter(node -> "Start Game".equals(node.getAccessibleText()))
                    .map(Button.class::cast)
                    .findFirst().orElseThrow();
            start.fire();

            assertTrue(scene.getRoot() instanceof GameSetupView);
            List<TextField> names = scene.getRoot().lookupAll(".name-field").stream()
                    .map(TextField.class::cast).collect(Collectors.toList());
            assertEquals(2, names.size());
            names.get(0).setText("Ada");
            scene.getRoot().lookupAll(".button").stream()
                    .filter(node -> "Begin".equals(node.getAccessibleText()))
                    .map(Button.class::cast)
                    .findFirst().orElseThrow().fire();

            assertTrue(scene.getRoot() instanceof GamePlayView);
            assertTrue(scene.lookup("#seat-0").getAccessibleText().contains("Ada"));
            assertTrue(scene.lookup("#seat-1").getAccessibleText().contains("Player 2"));
            assertNotSame(finishedGame, scene.getRoot());
            assertEquals(8, scene.getRoot().lookupAll(".card-button").size());
            assertFalse(scene.lookup("#draw-card").isDisabled());
            assertFalse(scene.lookup("#return-to-setup").isVisible());
        }));
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

    private static void exerciseWinner(BiConsumer<Scene, GamePlayController> afterGameOver) {
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
        GamePlayController controller = new GamePlayController(engine, view);
        Stage stage = new Stage();
        Scene scene = new Scene(view, 960, 760);
        MainApp.bindGameplayNavigation(scene, controller);
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
            afterGameOver.accept(scene, controller);
            if (scene.getRoot() == view) {
                assertEquals("Winner wins!", scene.lookup("#gameplay-message").getAccessibleText());
            }
            assertEquals(List.of(held), winner.getHand());
            assertSame(winner, state.getCurrentPlayer());
        } finally {
            stage.close();
        }
    }

    private static void exerciseOneTarget() {
        Card first = new Card(CardType.TACOCAT);
        Card second = new Card(CardType.TACOCAT);
        Card held = new Card(CardType.SKIP);
        Card gift = new Card(CardType.FAVOR);
        Player current = playerWith("Player 1", first, held, second);
        Player eliminated = playerWith("Eliminated");
        eliminated.eliminate();
        Player empty = playerWith("Empty");
        Player target = playerWith("Target", gift);
        GameState state = new GameState(List.of(current, eliminated, empty, target), new Deck());
        Stage stage = new Stage();
        Scene scene = new Scene(new StackPane(), 960, 760);
        stage.setScene(scene);
        try {
            stage.show();
            MainApp.showGameplay(scene, state);

            handCard(scene, 0).fire();

            assertEquals(List.of(held, gift), current.getHand());
            assertEquals(List.of(), target.getHand());
            assertSame(current, state.getCurrentPlayer());
            assertTrue(handCard(scene, 1).getAccessibleText().contains("Favor"));
            Node discardPile = scene.lookup("#discard-pile");
            assertNotNull(discardPile);
            assertEquals(List.of("Tacocat", "Tacocat"), discardPile.lookupAll(".discard-face")
                    .stream().map(Node::getAccessibleText).collect(Collectors.toList()));
        } finally {
            stage.close();
        }
    }

    private static void exerciseTwoTargets() {
        Card first = new Card(CardType.TACOCAT);
        Card held = new Card(CardType.SKIP);
        Card second = new Card(CardType.TACOCAT);
        Card favor = new Card(CardType.FAVOR);
        Card nope = new Card(CardType.NOPE);
        Player current = playerWith("Player 1", first, held, second);
        Player bram = playerWith("Bram", favor);
        Player cleo = playerWith("Cleo", nope);
        Deck deck = new Deck();
        GameState state = new GameState(List.of(current, bram, cleo), deck);
        Stage stage = new Stage();
        Scene scene = new Scene(new StackPane(), 960, 760);
        stage.setScene(scene);
        try {
            stage.show();
            MainApp.showGameplay(scene, state);
            int deckSize = deck.size();

            whileChoosingTarget(scene, () -> {
                assertTrue(hasState(scene.lookup("#seat-1"), "targetable"));
                assertTrue(hasState(scene.lookup("#seat-2"), "targetable"));
                assertTrue(hasState(handCard(scene, 0), "pair-first"));
                assertTrue(hasState(handCard(scene, 2), "pair-first"));
                handCard(scene, 1).fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "",
                        KeyCode.ESCAPE, false, false, false, false));
            }, () -> handCard(scene, 0).fire());
            assertEquals(List.of(first, held, second), current.getHand());
            assertFalse(hasState(scene.lookup("#seat-1"), "targetable"));
            assertFalse(hasState(handCard(scene, 0), "pair-first"));

            whileChoosingTarget(scene, () -> press(scene.lookup("#draw-card")),
                    () -> handCard(scene, 2).fire());
            assertEquals(List.of(first, held, second), current.getHand());
            assertEquals(deckSize, deck.size(), "A click away cancels without drawing.");

            whileChoosingTarget(scene, () -> press(scene.lookup("#seat-2")),
                    () -> handCard(scene, 2).fire());
            assertEquals(List.of(held, nope), current.getHand());
            assertEquals(List.of(favor), bram.getHand());
            assertEquals(List.of(), cleo.getHand());
            assertSame(current, state.getCurrentPlayer());
        } finally {
            stage.close();
        }
    }

    /** Runs {@code duringChoice} inside the seat choice that {@code start} opens. */
    private static void whileChoosingTarget(Scene scene, Runnable duringChoice, Runnable start) {
        CompletableFuture<Void> driven = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                duringChoice.run();
                driven.complete(null);
            } catch (Throwable failure) {
                driven.completeExceptionally(failure);
                press(scene.lookup("#draw-card"));
            }
        });
        start.run();
        assertTrue(driven.isDone(), "The pair waits for a seat choice.");
        driven.join();
    }

    private static void press(Node node) {
        assertNotNull(node);
        node.fireEvent(new MouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, true, null));
    }

    private static Button handCard(Scene scene, int index) {
        Button card = (Button) scene.lookup("#hand-card-" + index);
        assertNotNull(card);
        return card;
    }

    private static boolean hasState(Node node, String state) {
        assertNotNull(node);
        return node.getPseudoClassStates().contains(PseudoClass.getPseudoClass(state));
    }

    private static Player playerWith(String name, Card... hand) {
        Player player = new Player(name);
        for (Card card : hand) {
            player.addCard(card);
        }
        return player;
    }
}
