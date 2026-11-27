package integration;

import domain.AttackEffect;
import domain.Card;
import domain.CardType;
import domain.DefuseResult;
import domain.Deck;
import domain.DiscardPile;
import domain.ExplodingKittenResolver;
import domain.ExplosionOutcome;
import domain.GameState;
import domain.GameSnapshot;
import domain.Player;
import domain.SkipEffect;
import domain.SniffTestEffect;
import domain.TurnEngine;
import domain.TurnPhase;
import domain.WinReason;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Plays fixed games from the first turn to a winner. See docs/bva/EndGameScenarios.md. */
class EndGameScenarioTests {
    // TESTS
    @Test
    void InvalidDefusePosition_KeepsDefusePending_ThenValidPositionContinuesGame() {
        Player first = player("Ada", CardType.DEFUSE, CardType.SKIP);
        Player second = player("Bo");
        GameState state = new GameState(List.of(first, second),
                deckOf(CardType.EXPLODING_KITTEN, CardType.TACOCAT));
        TurnEngine engine = engine(state, new DiscardPile());
        engine.draw();

        assertEquals(DefuseResult.INVALID_POSITION, engine.resolveDefuse(3));
        assertEquals(TurnPhase.AWAITING_DEFUSE_POSITION, engine.getPhase());
        assertTrue(first.hasCard(CardType.DEFUSE));
        assertEquals(DefuseResult.DEFUSED, engine.resolveDefuse(1));

        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
        assertEquals(second, state.getCurrentPlayer());
        assertEquals(2, state.getDeck().size());
        assertEquals(Optional.empty(), engine.getSnapshot().getWinReason());
    }

    @Test
    void TwoPlayers_DefusedKittenPutOnTop_OpponentExplodes() {
        Player first = player("Ada", CardType.DEFUSE);
        Player second = player("Bo", CardType.SKIP);
        TurnEngine engine = engine(new GameState(List.of(first, second),
                deckOf(CardType.EXPLODING_KITTEN, CardType.TACOCAT)), new DiscardPile());

        engine.draw();
        engine.resolveDefuse(0);
        engine.draw();

        assertTrue(second.isEliminated());
        assertWinner(engine, 0, WinReason.LAST_SURVIVOR);
    }

    @Test
    void TwoPlayers_SecondDefuseByFirstPlayer_DefuseMasterWins() {
        Player first = player("Ada", CardType.DEFUSE, CardType.DEFUSE);
        Player second = player("Bo");
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = engine(new GameState(List.of(first, second), deckOf(
                CardType.EXPLODING_KITTEN, CardType.TACOCAT, CardType.BEARD_CAT)), discardPile);

        engine.draw();
        assertEquals(DefuseResult.DEFUSED, engine.resolveDefuse(1));
        engine.draw();
        engine.draw();
        assertEquals(DefuseResult.DEFUSED, engine.resolveDefuse(0));

        assertEquals(List.of(), first.getHand());
        assertEquals(2, discardPile.size());
        assertFalse(second.isEliminated());
        assertWinner(engine, 0, WinReason.DEFUSE_MASTER);
    }

    @Test
    void ThreePlayers_AttackThenTwoExplosions_ThirdPlayerIsLastSurvivor() {
        Player first = player("Ada", CardType.ATTACK);
        Player second = player("Bo");
        Player third = player("Cy");
        TurnEngine engine = engine(new GameState(List.of(first, second, third), deckOf(
                CardType.TACOCAT, CardType.EXPLODING_KITTEN,
                CardType.CATTERMELON, CardType.EXPLODING_KITTEN)), new DiscardPile());

        engine.playCard(0);
        engine.draw();
        assertFalse(second.isEliminated());
        engine.draw();
        assertTrue(second.isEliminated());
        engine.draw();
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
        engine.draw();

        assertTrue(first.isEliminated());
        assertFalse(third.isEliminated());
        assertWinner(engine, 2, WinReason.LAST_SURVIVOR);
    }

    @Test
    void TwoPlayers_FirstPlayerExplodes_SecondPlayerIsLastSurvivor() {
        Player first = player("Ada", CardType.SKIP, CardType.TACOCAT);
        Player second = player("Bo", CardType.DEFUSE);
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = engine(new GameState(List.of(first, second),
                deckOf(CardType.EXPLODING_KITTEN, CardType.BEARD_CAT)), discardPile);

        engine.draw();

        assertTrue(first.isEliminated());
        assertEquals(List.of(), first.getHand());
        assertEquals(3, discardPile.size());
        assertWinner(engine, 1, WinReason.LAST_SURVIVOR);
    }


    private static Player player(String name, CardType... hand) {
        Player player = new Player(name);
        for (CardType type : hand) {
            player.addCard(new Card(type));
        }
        return player;
    }

    /** A deck holding exactly these cards; the first one is drawn first. */
    private static Deck deckOf(CardType... types) {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        for (CardType type : types) {
            deck.addCards(List.of(new Card(type)));
        }
        return deck;
    }

    private static TurnEngine engine(GameState state, DiscardPile discardPile) {
        return new TurnEngine(state, discardPile, new RuleBookResolver(discardPile), Map.of(
                CardType.ATTACK, new AttackEffect(),
                CardType.SKIP, new SkipEffect(),
                CardType.SNIFF_TEST, new SniffTestEffect()));
    }

    private static void assertWinner(TurnEngine engine, int seat, WinReason reason) {
        GameSnapshot snapshot = engine.getSnapshot();
        assertEquals(TurnPhase.GAME_OVER, engine.getPhase());
        assertEquals(OptionalInt.of(seat), snapshot.getWinnerIndex());
        assertEquals(Optional.of(reason), snapshot.getWinReason());
        assertEquals(0, snapshot.getRemainingTurns());
    }

    /**
     * Follows the Original Edition rules until the reviewed resolver replaces the temporary stub:
     * a Defuse holder puts the Kitten back and discards the Defuse; anyone else is eliminated.
     */
    private static final class RuleBookResolver extends ExplodingKittenResolver {
        private final DiscardPile discardPile;

        private RuleBookResolver(DiscardPile discardPile) {
            super(discardPile);
            this.discardPile = discardPile;
        }

        @Override
        public ExplosionOutcome begin(Player player, Card kitten) {
            if (player.hasCard(CardType.DEFUSE)) {
                return ExplosionOutcome.DEFUSE_POSITION_REQUIRED;
            }
            player.eliminate();
            discardPile.discardAll(player.removeAllCards());
            discardPile.discard(kitten);
            return ExplosionOutcome.PLAYER_ELIMINATED;
        }

        @Override
        public DefuseResult defuse(Player player, Card kitten, Deck deck, int position) {
            if (position < 0 || position > deck.size()) {
                return DefuseResult.INVALID_POSITION;
            }
            Optional<Card> defuse = player.removeOne(CardType.DEFUSE);
            if (defuse.isEmpty()) {
                return DefuseResult.DEFUSE_NOT_FOUND;
            }
            discardPile.discard(defuse.get());
            deck.insertAt(kitten, position);
            return DefuseResult.DEFUSED;
        }
    }
}
