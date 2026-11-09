package domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

    // GS-02
    @Test
    public void Create_WithFivePlayers_KeepsPlayersDeckAndFirstPlayerIsCurrent() {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        Player player3 = new Player("Player 3");
        Player player4 = new Player("Player 4");
        Player player5 = new Player("Player 5");
        List<Player> players = List.of(player1, player2, player3, player4, player5);
        Deck deck = new Deck();

        GameState gameState = new GameState(players, deck);

        assertEquals(players, gameState.getPlayers());
        assertSame(deck, gameState.getDeck());
        assertSame(player1, gameState.getCurrentPlayer());
    }

    // GS-03
    @Test
    public void Create_WithNullPlayers_ThrowsNullPointerException() {
        Deck deck = new Deck();

        assertThrows(NullPointerException.class, () -> new GameState(null, deck));
    }

    // GS-04
    @Test
    public void Create_WithEmptyPlayers_ThrowsIllegalArgumentException() {
        Deck deck = new Deck();

        assertThrows(IllegalArgumentException.class, () -> new GameState(List.of(), deck));
    }

    // GS-05
    @Test
    public void Create_WithNullDeck_ThrowsNullPointerException() {
        List<Player> players = List.of(new Player("Player 1"), new Player("Player 2"));

        assertThrows(NullPointerException.class, () -> new GameState(players, null));
    }

    // GS-11
    @Test
    public void Create_ThenChangeOriginalList_GameStatePlayersUnchanged() {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        List<Player> originalPlayers = new ArrayList<>(List.of(player1, player2));
        GameState gameState = new GameState(originalPlayers, new Deck());

        originalPlayers.add(new Player("Player 3"));

        assertEquals(List.of(player1, player2), gameState.getPlayers());
    }

    // GS-12
    @Test
    public void GetPlayers_ModifyReturnedList_ThrowsAndPlayersUnchanged() {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        GameState gameState = new GameState(List.of(player1, player2), new Deck());

        assertThrows(
                UnsupportedOperationException.class,
                () -> gameState.getPlayers().add(new Player("Player 3"))
        );
        assertEquals(List.of(player1, player2), gameState.getPlayers());
    }

    // GS-13
    @Test
    public void AdvanceCurrentPlayer_TwoPlayers_MovesToNextPlayer() {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        GameState gameState = new GameState(List.of(player1, player2), new Deck());

        AdvanceResult result = gameState.advanceCurrentPlayer();

        assertEquals(AdvanceResult.ADVANCED, result);
        assertSame(player2, gameState.getCurrentPlayer());
    }
}