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
