package integration;

import controller.CatPairViewPort;
import controller.GamePlayController;
import domain.Card;
import domain.CardType;
import domain.Deck;
import domain.GameState;
import domain.GameSnapshot;
import domain.PlayCommand;
import domain.PlayFailure;
import domain.PlayResult;
import domain.PlayStatus;
import domain.Player;
import domain.PlayerSummary;
import domain.TurnEngine;
import domain.TurnEngineFactory;
import domain.TurnPhase;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CatPairGameplayIntegrationTests {

    @ParameterizedTest
    @EnumSource(value = CardType.class, names = {
            "TACOCAT", "CATTERMELON", "HAIRY_POTATO_CAT", "BEARD_CAT", "RAINBOW_RALPHING_CAT"
    })
    void FactoryGame_WithMatchingPair_TransfersSelectedCardAndContinuesTurn(CardType cat) {
        Player current = new Player("Player 1");
        Player target = new Player("Player 2");
        Card held = new Card(CardType.SKIP);
        Card leftWithTarget = new Card(CardType.ATTACK);
        Card transferred = new Card(CardType.FAVOR);
        current.addCard(new Card(cat));
        current.addCard(new Card(cat));
        current.addCard(held);
        target.addCard(leftWithTarget);
        target.addCard(transferred);
        Deck deck = fixedDeck();
        GameState state = new GameState(List.of(current, target), deck);
        TurnEngine engine = TurnEngineFactory.create(state, bound -> {
            assertEquals(2, bound);
            return 1;
        });

        PlayResult result = engine.playCard(PlayCommand.pair(0, 1, 1));

        assertEquals(PlayStatus.CONTINUE_TURN, result.getStatus());
        assertEquals(PlayFailure.NONE, result.getFailure());
        assertEquals(List.of(held, transferred), current.getHand());
        assertEquals(List.of(leftWithTarget), target.getHand());
        assertEquals(2, engine.getSnapshot().getDiscardPileSize());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
        assertSame(current, state.getCurrentPlayer());
        assertEquals(2, deck.size());
        assertEquals(CardType.SHUFFLE, engine.draw().getDrawnCardType().orElseThrow());
        assertSame(target, state.getCurrentPlayer());
        assertEquals(CardType.NOPE, deck.drawCard().getType());
    }

    @Test
    void FactoryGame_WithSingleCatCard_RejectsWithoutChangingTheGame() {
        Player current = new Player("Player 1");
        Player target = new Player("Player 2");
        Card cat = new Card(CardType.TACOCAT);
        Card gift = new Card(CardType.FAVOR);
        current.addCard(cat);
        target.addCard(gift);
        Deck deck = fixedDeck();
        GameState state = new GameState(List.of(current, target), deck);
        TurnEngine engine = TurnEngineFactory.create(state, bound -> {
            throw new AssertionError("A single Cat Card cannot select a target card.");
        });

        PlayResult result = engine.playCard(0);

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(PlayFailure.INVALID_SELECTION, result.getFailure());
        assertEquals(List.of(cat), current.getHand());
        assertEquals(List.of(gift), target.getHand());
        assertEquals(0, engine.getSnapshot().getDiscardPileSize());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
        assertSame(current, state.getCurrentPlayer());
        assertEquals(2, deck.size());
        assertEquals(CardType.SHUFFLE, engine.draw().getDrawnCardType().orElseThrow());
        assertSame(target, state.getCurrentPlayer());
        assertEquals(CardType.NOPE, deck.drawCard().getType());
    }

    @ParameterizedTest
    @EnumSource(value = CardType.class, names = {
            "TACOCAT", "CATTERMELON", "HAIRY_POTATO_CAT", "BEARD_CAT", "RAINBOW_RALPHING_CAT"
    })
    void GameplayClick_UsesOriginalHandIndexAndTargetSeat(CardType cat) {
        Player current = new Player("Player 1");
        Player eliminated = new Player("Eliminated");
        eliminated.eliminate();
        Player empty = new Player("Empty");
        Player target = new Player("Target");
        Card held = new Card(CardType.SKIP);
        Card gift = new Card(CardType.FAVOR);
        current.addCard(new Card(cat));
        current.addCard(held);
        current.addCard(new Card(cat));
        target.addCard(gift);
        GameState state = new GameState(List.of(current, eliminated, empty, target), fixedDeck());
        TurnEngine engine = TurnEngineFactory.create(state, bound -> {
            assertEquals(1, bound);
            return 0;
        });
        PairView view = new PairView();
        new GamePlayController(engine, view).initialize();

        view.playCard.accept(0);

        assertEquals(List.of(2), view.handIndices);
        assertEquals(List.of(3), view.targetSeats);
        assertEquals(List.of(held, gift), current.getHand());
        assertEquals(List.of(), target.getHand());
        assertEquals(List.of(CardType.SKIP, CardType.FAVOR), view.snapshot.getCurrentPlayerHand());
        assertEquals(2, view.snapshot.getDiscardPileSize());
        assertEquals(0, view.snapshot.getCurrentPlayerIndex());
        assertSame(current, state.getCurrentPlayer());
    }

    private static final class PairView implements CatPairViewPort {
        private GameSnapshot snapshot;
        private IntConsumer playCard;
        private List<Integer> handIndices;
        private List<Integer> targetSeats;

        @Override
        public void requestSecondCard(int firstHandIndex, List<Integer> indices,
                                      IntConsumer onMatchSelected) {
            handIndices = indices;
            onMatchSelected.accept(2);
        }

        @Override
        public OptionalInt requestTargetPlayer(List<PlayerSummary> candidates) {
            targetSeats = candidates.stream().map(PlayerSummary::getPlayerIndex)
                    .collect(Collectors.toList());
            return OptionalInt.of(3);
        }

        @Override
        public void setOnDraw(Runnable action) {
        }

        @Override
        public void setOnPlayCard(IntConsumer action) {
            playCard = action;
        }

        @Override
        public void setOnChooseDefusePosition(Runnable action) {
        }

        @Override
        public void render(GameSnapshot gameSnapshot) {
            snapshot = gameSnapshot;
        }

        @Override
        public void showMessage(String messageKey) {
            throw new AssertionError("Unexpected message: " + messageKey);
        }

        @Override
        public void showFutureCards(List<CardType> cards) {
            throw new AssertionError("A pair cannot reveal future cards.");
        }

        @Override
        public OptionalInt requestDefusePosition(int minimum, int maximum) {
            throw new AssertionError("A pair cannot request Defuse.");
        }
    }

    private static Deck fixedDeck() {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        deck.addCards(List.of(new Card(CardType.SHUFFLE), new Card(CardType.NOPE)));
        return deck;
    }
}
