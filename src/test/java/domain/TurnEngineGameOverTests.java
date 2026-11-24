package domain;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurnEngineGameOverTests {
    @Test
    void Draw_LastOpponentEliminated_ReportsWinnerAndZeroTurns() {
        FinalTurn game = new FinalTurn();

        DrawResult result = game.engine.draw();

        GameSnapshot snapshot = game.engine.getSnapshot();
        assertEquals(DrawStatus.PLAYER_ELIMINATED, result.getStatus());
        assertEquals(TurnPhase.GAME_OVER, game.engine.getPhase());
        assertEquals(TurnPhase.GAME_OVER, snapshot.getPhase());
        assertEquals(OptionalInt.of(1), snapshot.getWinnerIndex());
        assertEquals("Winner", snapshot.getCurrentPlayerName());
        assertEquals(1, snapshot.getAlivePlayerCount());
        assertEquals(0, snapshot.getRemainingTurns());
    }

    @Test
    void PlayCard_CatPairAfterGameOver_RejectsAndPreservesWinner() {
        FinalTurn game = new FinalTurn();
        game.engine.draw();

        PlayResult result = game.engine.playCard(PlayCommand.pair(0, 1, 0));

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(PlayFailure.GAME_OVER, result.getFailure());
        game.assertGameUnchanged();
    }

    private static final class FinalTurn {
        private final Player loser = new Player("Loser");
        private final Player winner = new Player("Winner");
        private final Deck deck = Deck.fromTopToBottom(List.of(
                new Card(CardType.EXPLODING_KITTEN), new Card(CardType.SKIP)));
        private final DiscardPile discard = new DiscardPile();
        private final TurnEngine engine;

        private FinalTurn() {
            winner.addCard(new Card(CardType.TACOCAT));
            winner.addCard(new Card(CardType.TACOCAT));
            GameState state = new GameState(List.of(loser, winner), deck);
            ExplodingKittenResolver elimination = new ExplodingKittenResolver(discard) {
                @Override
                public ExplosionOutcome begin(Player player, Card kitten) {
                    player.eliminate();
                    return ExplosionOutcome.PLAYER_ELIMINATED;
                }
            };
            engine = new TurnEngine(state, discard, elimination, Map.of(), bound -> {
                throw new AssertionError("Game-over actions must not use randomness.");
            });
        }

        private void assertGameUnchanged() {
            GameSnapshot snapshot = engine.getSnapshot();
            assertEquals(TurnPhase.GAME_OVER, snapshot.getPhase());
            assertEquals(OptionalInt.of(1), snapshot.getWinnerIndex());
            assertEquals(0, snapshot.getRemainingTurns());
            assertEquals(List.of(CardType.TACOCAT, CardType.TACOCAT), snapshot.getCurrentPlayerHand());
            assertEquals(List.of(), loser.getHand());
            assertEquals(1, deck.size());
            assertEquals(CardType.SKIP, deck.peekTopCard().getType());
            assertEquals(0, discard.size());
        }
    }
}
