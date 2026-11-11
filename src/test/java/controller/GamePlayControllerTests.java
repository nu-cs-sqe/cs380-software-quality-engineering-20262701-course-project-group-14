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
import static org.junit.jupiter.api.Assertions.assertSame;
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

    @Test
    public void Initialize_RendersCurrentSnapshotOnce() {
        GameSnapshot snapshot = createSnapshot(10);
        StubActions actions = new StubActions(snapshot);
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.initialize();

        assertEquals(1, actions.snapshotCalls);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    private static GameSnapshot createSnapshot(int deckSize) {
        return GameSnapshot.create(
                "Player 1",
                List.of(CardType.SKIP),
                deckSize,
                0,
                2,
                TurnPhase.ACTIONS_ALLOWED
        );
    }

    private static final class StubActions implements TurnActions {
        private final GameSnapshot snapshot;
        private int snapshotCalls;

        private StubActions() {
            this(null);
        }

        private StubActions(GameSnapshot snapshot) {
            this.snapshot = snapshot;
        }

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
            snapshotCalls++;
            return snapshot;
        }
    }

    private static final class RecordingView implements GamePlayViewPort {
        private GameSnapshot renderedSnapshot;
        private int renderCalls;

        @Override
        public void render(GameSnapshot snapshot) {
            renderCalls++;
            renderedSnapshot = snapshot;
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
