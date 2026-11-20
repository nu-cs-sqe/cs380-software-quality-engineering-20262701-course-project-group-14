package ui;

import controller.GamePlayViewPort;
import domain.Card;
import domain.CardType;
import domain.GameSnapshot;
import domain.GameState;
import domain.Player;
import domain.TurnPhase;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GamePlayStartupTests {

    @Test
    public void StartGameplay_InitializesSnapshotAndCallbacks() {
        RecordingView view = new RecordingView();

        MainApp.startGameplay(fixedGame(), view);

        assertEquals("Player 1", view.snapshot.getCurrentPlayerName());
        assertEquals(8, view.snapshot.getCurrentPlayerHand().size());
        assertEquals(CardType.SKIP, view.snapshot.getCurrentPlayerHand().get(0));
        assertEquals(2, view.snapshot.getDeckSize());
        assertEquals(0, view.snapshot.getDiscardPileSize());
        assertEquals(2, view.snapshot.getAlivePlayerCount());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, view.snapshot.getPhase());
        assertNotNull(view.draw);
        assertNotNull(view.play);
        assertNotNull(view.choosePosition);
    }

    private static GameState fixedGame() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        List<Player> players = List.of(first, second);
        List<CardType> hand = List.of(
                CardType.SKIP, CardType.DEFUSE, CardType.FAVOR, CardType.ATTACK,
                CardType.SHUFFLE, CardType.SEE_THE_FUTURE, CardType.NOPE, CardType.TACOCAT);
        for (Player player : players) {
            for (CardType type : hand) {
                player.addCard(new Card(type));
            }
        }
        domain.Deck deck = new domain.Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        deck.addCards(List.of(new Card(CardType.ATTACK), new Card(CardType.SHUFFLE)));
        return new GameState(players, deck);
    }

    private static final class RecordingView implements GamePlayViewPort {
        private Runnable draw;
        private IntConsumer play;
        private Runnable choosePosition;
        private GameSnapshot snapshot;
        private String message;

        @Override
        public void setOnDraw(Runnable action) {
            draw = action;
        }

        @Override
        public void setOnPlayCard(IntConsumer action) {
            play = action;
        }

        @Override
        public void setOnChooseDefusePosition(Runnable action) {
            choosePosition = action;
        }

        @Override
        public void render(GameSnapshot snapshot) {
            this.snapshot = snapshot;
        }

        @Override
        public void showMessage(String messageKey) {
            message = messageKey;
        }

        @Override
        public void showFutureCards(List<CardType> cards) {
            throw new AssertionError("No future-card effect is used in these scenarios.");
        }

        @Override
        public OptionalInt requestDefusePosition(int minimum, int maximum) {
            throw new AssertionError("No Exploding Kitten is drawn in these scenarios.");
        }
    }

    @Test
    public void StartGameplay_DrawEventRunsRealTurnAndRefreshesView() {
        GameState state = fixedGame();
        Player first = state.getPlayers().get(0);
        RecordingView view = new RecordingView();
        MainApp.startGameplay(state, view);

        view.draw.run();

        assertEquals(9, first.getHand().size());
        assertEquals(CardType.ATTACK, first.getHand().get(8).getType());
        assertSame(state.getPlayers().get(1), state.getCurrentPlayer());
        assertEquals("Player 2", view.snapshot.getCurrentPlayerName());
        assertEquals(8, view.snapshot.getCurrentPlayerHand().size());
        assertEquals(1, view.snapshot.getDeckSize());
        assertEquals(0, view.snapshot.getDiscardPileSize());
        assertEquals("draw.normal-card", view.message);
    }

    @Test
    public void StartGameplay_SkipEventRunsRealEffectWithoutDrawing() {
        GameState state = fixedGame();
        Player first = state.getPlayers().get(0);
        List<Card> originalHand = List.copyOf(first.getHand());
        RecordingView view = new RecordingView();
        MainApp.startGameplay(state, view);

        view.play.accept(0);

        assertEquals(7, first.getHand().size());
        assertEquals(originalHand.subList(1, 8), first.getHand());
        assertSame(state.getPlayers().get(1), state.getCurrentPlayer());
        assertEquals("Player 2", view.snapshot.getCurrentPlayerName());
        assertEquals(8, view.snapshot.getCurrentPlayerHand().size());
        assertEquals(2, view.snapshot.getDeckSize());
        assertEquals(1, view.snapshot.getDiscardPileSize());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, view.snapshot.getPhase());
        assertSame(CardType.ATTACK, state.getDeck().drawCard().getType());
        assertSame(CardType.SHUFFLE, state.getDeck().drawCard().getType());
    }

    @Test
    public void StartGameplay_NullStateIsRejected() {
        RecordingView view = new RecordingView();

        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> MainApp.startGameplay(null, view));

        assertEquals("Game state must not be null.", exception.getMessage());
    }

    @Test
    public void StartGameplay_NullViewIsRejected() {
        GameState state = fixedGame();

        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> MainApp.startGameplay(state, null));

        assertEquals("Game play view must not be null.", exception.getMessage());
        assertSame(state.getPlayers().get(0), state.getCurrentPlayer());
        assertEquals(2, state.getDeck().size());
    }

}