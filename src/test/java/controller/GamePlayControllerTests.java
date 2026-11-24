package controller;

import domain.Card;
import domain.CardType;
import domain.CommandPlayActions;
import domain.Deck;
import domain.DefuseResult;
import domain.DrawResult;
import domain.GameSnapshot;
import domain.GameState;
import domain.PlayCommand;
import domain.PlayFailure;
import domain.PlayResult;
import domain.Player;
import domain.PlayerSummary;
import domain.TurnActions;
import domain.TurnEngineFactory;
import domain.TurnPhase;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.function.IntConsumer;

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

    @Test
    public void HandleDraw_NormalCard_ShowsMessageAndRenders() {
        GameSnapshot snapshot = createSnapshot(9);
        StubActions actions = new StubActions(
                snapshot,
                DrawResult.normal(CardType.ATTACK)
        );
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleDraw();

        assertEquals(1, actions.drawCalls);
        assertEquals("draw.normal-card", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void HandleDraw_EmptyDeck_ShowsEmptyDeckMessageAndRenders() {
        GameSnapshot snapshot = createSnapshot(0);
        StubActions actions = new StubActions(
                snapshot,
                DrawResult.emptyDeck()
        );
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleDraw();

        assertEquals(1, actions.drawCalls);
        assertEquals("draw.empty-deck", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void HandleDraw_PendingDefuse_ShowsRejectionAndRenders() {
        GameSnapshot snapshot = createSnapshot(5);
        StubActions actions = new StubActions(
                snapshot,
                DrawResult.rejectedPendingDefuse()
        );
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleDraw();

        assertEquals(1, actions.drawCalls);
        assertEquals("draw.pending-defuse", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void HandleDraw_PlayerEliminated_ShowsMessageAndRenders() {
        GameSnapshot snapshot = createSnapshot(4);
        StubActions actions = new StubActions(
                snapshot,
                DrawResult.playerEliminated()
        );
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleDraw();

        assertEquals(1, actions.drawCalls);
        assertEquals("draw.player-eliminated", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void HandleDraw_DefuseRequestCancelled_DoesNotResolve() {
        GameSnapshot snapshot = createSnapshot(6);
        StubActions actions = new StubActions(
                snapshot,
                DrawResult.defuseRequired()
        );
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleDraw();

        assertEquals("draw.defuse-position-required", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertEquals(1, view.positionRequestCalls);
        assertEquals(0, view.requestedMinimum);
        assertEquals(6, view.requestedMaximum);
        assertEquals(0, actions.resolveDefuseCalls);
    }

    @Test
    public void HandleDraw_DefusePositionSupplied_DelegatesPosition() {
        GameSnapshot snapshot = createSnapshot(6);
        StubActions actions = new StubActions(
                snapshot,
                DrawResult.defuseRequired()
        );
        RecordingView view = new RecordingView();
        view.positionResponse = OptionalInt.of(3);
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleDraw();

        assertEquals(1, actions.resolveDefuseCalls);
        assertEquals(3, actions.resolvedPosition);
    }

    @Test
    public void HandlePlay_ContinueWithoutReveal_DelegatesAndRenders() {
        GameSnapshot snapshot = createSnapshot(8);
        StubActions actions = new StubActions(snapshot);
        actions.playResult = PlayResult.continueTurn(List.of());
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handlePlayCard(2);

        assertEquals(1, actions.playCardCalls);
        assertEquals(2, actions.playedHandIndex);
        assertEquals(0, view.futureCardsCalls);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void HandlePlay_ContinueWithReveal_ShowsCardsInOrder() {
        GameSnapshot snapshot = createSnapshot(8);
        List<CardType> revealed = List.of(
                CardType.SKIP,
                CardType.ATTACK
        );
        StubActions actions = new StubActions(snapshot);
        actions.playResult = PlayResult.continueTurn(revealed);
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handlePlayCard(0);

        assertEquals(1, view.futureCardsCalls);
        assertEquals(revealed, view.futureCards);
        assertEquals(1, view.renderCalls);
    }

    @Test
    public void HandlePlay_TurnEnded_RendersWithoutFailureMessage() {
        GameSnapshot snapshot = createSnapshot(7);
        StubActions actions = new StubActions(snapshot);
        actions.playResult = PlayResult.turnEnded();
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handlePlayCard(0);

        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
        assertNull(view.messageKey);
        assertEquals(0, view.futureCardsCalls);
    }

    @Test
    public void HandlePlay_InvalidIndex_ShowsFailureAndRenders() {
        GameSnapshot snapshot = createSnapshot(7);
        StubActions actions = new StubActions(snapshot);
        actions.playResult = PlayResult.rejected(
                PlayFailure.INVALID_HAND_INDEX
        );
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handlePlayCard(-1);

        assertEquals("play.invalid-hand-index", view.messageKey);
        assertEquals(1, view.renderCalls);
    }

    @Test
    public void HandlePlay_CardNotPlayable_ShowsFailureAndRenders() {
        GameSnapshot snapshot = createSnapshot(7);
        StubActions actions = new StubActions(snapshot);
        actions.playResult = PlayResult.rejected(
                PlayFailure.CARD_NOT_PLAYABLE
        );
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handlePlayCard(0);

        assertEquals("play.card-not-playable", view.messageKey);
        assertEquals(1, view.renderCalls);
    }

    @Test
    public void HandlePlay_InvalidSelection_ShowsFailureAndRenders() {
        GameSnapshot snapshot = createSnapshot(7);
        StubActions actions = new StubActions(snapshot);
        actions.playResult = PlayResult.rejected(PlayFailure.INVALID_SELECTION);
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handlePlayCard(0);

        assertEquals("play.card-not-playable", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void HandlePlay_PreconditionNotMet_ShowsFailureAndRenders() {
        GameSnapshot snapshot = createSnapshot(7);
        StubActions actions = new StubActions(snapshot);
        actions.playResult = PlayResult.rejected(
                PlayFailure.PRECONDITION_NOT_MET
        );
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handlePlayCard(0);

        assertEquals("play.precondition-not-met", view.messageKey);
        assertEquals(1, view.renderCalls);
    }

    @Test
    public void HandlePlay_DefuseRequired_ShowsFailureAndRenders() {
        GameSnapshot snapshot = createSnapshot(7);
        StubActions actions = new StubActions(snapshot);
        actions.playResult = PlayResult.rejected(
                PlayFailure.DEFUSE_RESOLUTION_REQUIRED
        );
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handlePlayCard(0);

        assertEquals("play.defuse-required", view.messageKey);
        assertEquals(1, view.renderCalls);
    }

    @Test
    public void HandleChooseDefuse_Cancelled_OnlyRequestsPosition() {
        StubActions actions = new StubActions(createSnapshot(4));
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleChooseDefusePosition();

        assertEquals(1, view.positionRequestCalls);
        assertEquals(0, view.requestedMinimum);
        assertEquals(4, view.requestedMaximum);
        assertEquals(0, actions.resolveDefuseCalls);
        assertNull(view.messageKey);
        assertEquals(0, view.renderCalls);
    }

    @Test
    public void HandleChooseDefuse_Success_ShowsMessageAndRenders() {
        GameSnapshot snapshot = createSnapshot(4);
        StubActions actions = new StubActions(snapshot);
        actions.defuseResult = DefuseResult.DEFUSED;
        RecordingView view = new RecordingView();
        view.positionResponse = OptionalInt.of(2);
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleChooseDefusePosition();

        assertEquals(2, actions.resolvedPosition);
        assertEquals("defuse.success", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void HandleChooseDefuse_InvalidPosition_ShowsMessageAndRenders() {
        GameSnapshot snapshot = createSnapshot(4);
        StubActions actions = new StubActions(snapshot);
        actions.defuseResult = DefuseResult.INVALID_POSITION;
        RecordingView view = new RecordingView();
        view.positionResponse = OptionalInt.of(5);
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleChooseDefusePosition();

        assertEquals("defuse.invalid-position", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void HandleChooseDefuse_NotAwaiting_ShowsMessageAndRenders() {
        GameSnapshot snapshot = createSnapshot(4);
        StubActions actions = new StubActions(snapshot);
        actions.defuseResult = DefuseResult.NOT_AWAITING_DEFUSE;
        RecordingView view = new RecordingView();
        view.positionResponse = OptionalInt.of(1);
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleChooseDefusePosition();

        assertEquals("defuse.not-awaiting", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void HandleChooseDefuse_NotFound_ShowsMessageAndRenders() {
        GameSnapshot snapshot = createSnapshot(4);
        StubActions actions = new StubActions(snapshot);
        actions.defuseResult = DefuseResult.DEFUSE_NOT_FOUND;
        RecordingView view = new RecordingView();
        view.positionResponse = OptionalInt.of(1);
        GamePlayController controller = new GamePlayController(actions, view);

        controller.handleChooseDefusePosition();

        assertEquals("defuse.not-found", view.messageKey);
        assertEquals(1, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void Initialize_DrawEvent_DelegatesAndRefreshes() {
        GameSnapshot snapshot = createSnapshot(9);
        StubActions actions = new StubActions(
                snapshot, DrawResult.normal(CardType.ATTACK));
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);
        controller.initialize();

        view.drawAction.run();

        assertEquals(1, actions.drawCalls);
        assertEquals("draw.normal-card", view.messageKey);
        assertEquals(2, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void Initialize_PlayCardEvent_PreservesIndexAndRefreshes() {
        GameSnapshot snapshot = createSnapshot(9);
        StubActions actions = new StubActions(snapshot);
        actions.playResult = PlayResult.continueTurn(List.of());
        RecordingView view = new RecordingView();
        GamePlayController controller = new GamePlayController(actions, view);
        controller.initialize();

        view.playCardAction.accept(0);

        assertEquals(1, actions.playCardCalls);
        assertEquals(0, actions.playedHandIndex);
        assertEquals(2, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }

    @Test
    public void Initialize_DefusePositionEvent_RequestsPositionAndRefreshes() {
        GameSnapshot snapshot = createSnapshot(4);
        StubActions actions = new StubActions(snapshot);
        RecordingView view = new RecordingView();
        view.positionResponse = OptionalInt.of(2);
        GamePlayController controller = new GamePlayController(actions, view);
        controller.initialize();

        view.chooseDefusePositionAction.run();

        assertEquals(1, view.positionRequestCalls);
        assertEquals(0, view.requestedMinimum);
        assertEquals(4, view.requestedMaximum);
        assertEquals(1, actions.resolveDefuseCalls);
        assertEquals(2, actions.resolvedPosition);
        assertEquals("defuse.success", view.messageKey);
        assertEquals(2, view.renderCalls);
        assertSame(snapshot, view.renderedSnapshot);
    }


    @Test
    public void HandlePlay_CatCard_OffersOnlyItsMatchingOriginalHandIndex() {
        PairActions actions = new PairActions(catPairSnapshot());
        PairRecordingView view = new PairRecordingView();

        new GamePlayController(actions, view).handlePlayCard(0);

        assertEquals(List.of(2), view.matchingHandIndices);
    }

    @Test
    public void HandlePlay_CloseSecondCardChooser_SubmitsNoPlay() {
        PairActions actions = new PairActions(catPairSnapshot());
        PairRecordingView view = new PairRecordingView();

        new GamePlayController(actions, view).handlePlayCard(0);

        assertEquals(List.of(2), view.matchingHandIndices);
        assertEquals(0, ((StubActions) actions).playCardCalls);
        assertNull(view.targetCandidates);
    }

    @Test
    public void HandlePlay_CatPair_OffersOnlyEligibleTargetAtOriginalSeat() {
        Player eliminated = playerWith("Eliminated", CardType.FAVOR);
        eliminated.eliminate();
        Player empty = playerWith("Empty");
        Player target = playerWith("Target", CardType.ATTACK);
        PairActions actions = new PairActions(catPairSnapshot(eliminated, empty, target));
        PairRecordingView view = new PairRecordingView();
        view.secondCard = OptionalInt.of(2);

        new GamePlayController(actions, view).handlePlayCard(0);

        assertEquals(1, view.targetCandidates.size());
        PlayerSummary candidate = view.targetCandidates.get(0);
        assertEquals(3, candidate.getPlayerIndex());
        assertEquals("Target", candidate.getName());
        assertEquals(1, candidate.getHandSize());
    }

    @Test
    public void HandlePlay_CloseTargetChooser_SubmitsNoPlay() {
        Player target = playerWith("Target", CardType.ATTACK);
        PairActions actions = new PairActions(catPairSnapshot(target));
        PairRecordingView view = new PairRecordingView();
        view.secondCard = OptionalInt.of(2);

        new GamePlayController(actions, view).handlePlayCard(0);

        assertEquals(1, view.targetCandidates.size());
        assertEquals(0, ((StubActions) actions).playCardCalls);
        assertEquals(0, ((RecordingView) view).renderCalls);
    }

    @Test
    public void HandlePlay_ConfirmCatPair_SubmitsOnceWithOriginalIndicesAndRenders() {
        GameSnapshot initial = catPairSnapshot(playerWith("Empty"),
                playerWith("Second target", CardType.ATTACK),
                playerWith("Chosen target", CardType.FAVOR));
        GameSnapshot latest = GameSnapshot.create("Player 1",
                List.of(CardType.SKIP, CardType.BEARD_CAT, CardType.FAVOR),
                initial.getDeckSize(), 2, 4, TurnPhase.ACTIONS_ALLOWED);
        PairActions actions = new PairActions(initial);
        actions.commandResult = PlayResult.continueTurn(List.of());
        actions.afterPlay = latest;
        PairRecordingView view = new PairRecordingView();
        view.secondCard = OptionalInt.of(2);
        view.targetSeat = OptionalInt.of(3);

        new GamePlayController(actions, view).handlePlayCard(0);

        assertEquals(1, actions.commandCalls);
        assertEquals(0, actions.submitted.getFirstHandIndex());
        assertEquals(2, actions.submitted.getSecondHandIndex().orElseThrow());
        assertEquals(3, actions.submitted.getTargetPlayerIndex().orElseThrow());
        assertEquals(0, ((StubActions) actions).playCardCalls);
        assertEquals(1, ((RecordingView) view).renderCalls);
        assertSame(latest, ((RecordingView) view).renderedSnapshot);
    }

    @Test
    public void HandlePlay_RejectedCatPair_ShowsFeedbackAndDoesNotRetry() {
        assertPairRejectionShowsFeedback(PlayFailure.INVALID_TARGET);
    }

    @Test
    public void HandlePlay_CatPairTargetNowEmpty_ShowsFeedbackAndDoesNotRetry() {
        assertPairRejectionShowsFeedback(PlayFailure.TARGET_HAND_EMPTY);
    }

    private static void assertPairRejectionShowsFeedback(PlayFailure failure) {
        GameSnapshot snapshot = catPairSnapshot(playerWith("Target", CardType.ATTACK));
        PairActions actions = new PairActions(snapshot);
        actions.commandResult = PlayResult.rejected(failure);
        PairRecordingView view = new PairRecordingView();
        view.secondCard = OptionalInt.of(2);
        view.targetSeat = OptionalInt.of(1);

        new GamePlayController(actions, view).handlePlayCard(0);

        assertEquals(1, actions.commandCalls);
        assertEquals(0, ((StubActions) actions).playCardCalls);
        assertEquals("play.card-not-playable", ((RecordingView) view).messageKey);
        assertSame(snapshot, ((RecordingView) view).renderedSnapshot);
        assertEquals(1, ((RecordingView) view).renderCalls);
    }

    @Test
    public void HandlePlay_CatPairWithoutEligibleTarget_ShowsFeedbackAndDoesNotPlay() {
        PairActions actions = new PairActions(catPairSnapshot(playerWith("Empty")));
        PairRecordingView view = new PairRecordingView();
        view.secondCard = OptionalInt.of(2);

        new GamePlayController(actions, view).handlePlayCard(0);

        assertEquals("play.card-not-playable", ((RecordingView) view).messageKey);
        assertNull(view.targetCandidates);
        assertEquals(0, ((StubActions) actions).playCardCalls);
        assertEquals(0, actions.commandCalls);
    }

    private static GameSnapshot catPairSnapshot(Player... others) {
        List<Player> players = new ArrayList<>();
        players.add(playerWith("Player 1", CardType.TACOCAT, CardType.SKIP,
                CardType.TACOCAT, CardType.BEARD_CAT));
        players.addAll(List.of(others));
        return TurnEngineFactory.create(new GameState(players, new Deck())).getSnapshot();
    }

    private static Player playerWith(String name, CardType... types) {
        Player player = new Player(name);
        for (CardType type : types) {
            player.addCard(new Card(type));
        }
        return player;
    }

    private static final class PairActions extends StubActions implements CommandPlayActions {
        private PlayResult commandResult;
        private PlayCommand submitted;
        private GameSnapshot afterPlay;
        private int commandCalls;

        private PairActions(GameSnapshot snapshot) {
            super(snapshot);
            super.playResult = PlayResult.rejected(PlayFailure.INVALID_SELECTION);
        }

        @Override
        public PlayResult playCard(PlayCommand command) {
            if (commandResult == null) {
                throw new AssertionError("No play is expected in this scenario.");
            }
            commandCalls++;
            submitted = command;
            return commandResult;
        }

        @Override
        public GameSnapshot getSnapshot() {
            if (commandCalls > 0 && afterPlay != null) {
                return afterPlay;
            }
            return super.getSnapshot();
        }
    }

    private static final class PairRecordingView extends RecordingView implements CatPairViewPort {
        private List<Integer> matchingHandIndices;
        private OptionalInt secondCard = OptionalInt.empty();
        private OptionalInt targetSeat = OptionalInt.empty();
        private List<PlayerSummary> targetCandidates;

        @Override
        public OptionalInt requestSecondCard(List<Integer> candidates) {
            matchingHandIndices = candidates;
            return secondCard;
        }

        @Override
        public OptionalInt requestTargetPlayer(List<PlayerSummary> candidates) {
            targetCandidates = candidates;
            return targetSeat;
        }
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

    private static class StubActions implements TurnActions {
        private final GameSnapshot snapshot;
        private final DrawResult drawResult;
        private PlayResult playResult;
        private DefuseResult defuseResult = DefuseResult.DEFUSED;
        private int drawCalls;
        private int playCardCalls;
        private int playedHandIndex;
        private int resolveDefuseCalls;
        private int resolvedPosition;
        private int snapshotCalls;

        private StubActions() {
            this(null, null);
        }

        private StubActions(GameSnapshot snapshot) {
            this(snapshot, null);
        }

        private StubActions(
                GameSnapshot snapshot,
                DrawResult drawResult
        ) {
            this.snapshot = snapshot;
            this.drawResult = drawResult;
        }

        @Override
        public DrawResult draw() {
            drawCalls++;
            return drawResult;
        }

        @Override
        public PlayResult playCard(int handIndex) {
            playCardCalls++;
            playedHandIndex = handIndex;
            return playResult;
        }

        @Override
        public DefuseResult resolveDefuse(int position) {
            resolveDefuseCalls++;
            resolvedPosition = position;
            return defuseResult;
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

    private static class RecordingView implements GamePlayViewPort {
        private Runnable drawAction = () -> { };
        private IntConsumer playCardAction = index -> { };
        private Runnable chooseDefusePositionAction = () -> { };

        public void setOnDraw(Runnable action) {
            drawAction = action;
        }

        public void setOnPlayCard(IntConsumer action) {
            playCardAction = action;
        }

        public void setOnChooseDefusePosition(Runnable action) {
            chooseDefusePositionAction = action;
        }

        private GameSnapshot renderedSnapshot;
        private String messageKey;
        private int renderCalls;
        private int futureCardsCalls;
        private List<CardType> futureCards;
        private int positionRequestCalls;
        private int requestedMinimum;
        private int requestedMaximum;
        private OptionalInt positionResponse = OptionalInt.empty();

        @Override
        public void render(GameSnapshot snapshot) {
            renderCalls++;
            renderedSnapshot = snapshot;
        }

        @Override
        public void showMessage(String messageKey) {
            this.messageKey = messageKey;
        }

        @Override
        public void showFutureCards(List<CardType> cards) {
            futureCardsCalls++;
            futureCards = cards;
        }

        @Override
        public OptionalInt requestDefusePosition(
                int minimum,
                int maximum
        ) {
            positionRequestCalls++;
            requestedMinimum = minimum;
            requestedMaximum = maximum;
            return positionResponse;
        }
    }

}
