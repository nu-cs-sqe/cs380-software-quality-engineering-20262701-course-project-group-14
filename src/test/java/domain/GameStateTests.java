package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTests {

    // GS-01
    @Test
    public void Create_WithTwoPlayers_KeepsPlayersDeckAndFirstPlayerIsCurrent() {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        List<Player> players = List.of(player1, player2);
        Deck deck = new Deck();

        GameState gameState = new GameState(players, deck);

        assertEquals(players, gameState.getPlayers());
        assertSame(deck, gameState.getDeck());
        assertSame(player1, gameState.getCurrentPlayer());
    }
}