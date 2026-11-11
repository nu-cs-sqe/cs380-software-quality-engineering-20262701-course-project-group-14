package controller;

import domain.CardType;
import domain.DefuseResult;
import domain.DrawResult;
import domain.GameSnapshot;
import domain.PlayResult;
import domain.TurnActions;
import domain.TurnPhase;
import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GamePlayControllerTests {

    @Test
    public void Constructor_WithNullActions_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new GamePlayController(null, new RecordingView())
        );

        assertEquals(
                "Turn actions must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Constructor_WithNullView_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new GamePlayController(new StubActions(), null)
        );

        assertEquals(
                "Game play view must not be null.",
                exception.getMessage()
        );
    }

    private static final class StubActions implements TurnActions {
        @Override
        public DrawResult draw() {
            throw new AssertionError("Unexpected draw call.");
        }

        @Override
        public PlayResult playCard(int handIndex) {
            throw new AssertionError("Unexpected playCard call.");
        }

        @Override
        public DefuseResult resolveDefuse(int position) {
            throw new AssertionError("Unexpected resolveDefuse call.");
        }

        @Override
        public TurnPhase getPhase() {
            throw new AssertionError("Unexpected getPhase call.");
        }

        @Override
        public GameSnapshot getSnapshot() {
            throw new AssertionError("Unexpected getSnapshot call.");
        }
    }

    private static final class RecordingView implements GamePlayViewPort {
        @Override
        public void render(GameSnapshot snapshot) {
        }

        @Override
        public void showMessage(String messageKey) {
        }

        @Override
        public void showFutureCards(List<CardType> cards) {
        }

        @Override
        public OptionalInt requestDefusePosition(
                int minimum,
                int maximum
        ) {
            return OptionalInt.empty();
        }
    }
}
