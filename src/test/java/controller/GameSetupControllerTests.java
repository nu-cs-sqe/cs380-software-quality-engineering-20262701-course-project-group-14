package controller;

import domain.CardType;
import domain.GameState;
import domain.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameSetupControllerTests {

    @Test
    public void StartGame_WithPlayerCountBelowMinimum_ThrowsIllegalArgumentException() {
        GameSetupController controller = new GameSetupController();

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.startGame("1")
        );
    }

    @Test
    public void StartGame_WithMinimumPlayerCount_ReturnsReadyGameState() {
        GameSetupController controller = new GameSetupController();

        GameState gameState = controller.startGame("2");

        assertEquals(2, gameState.getPlayers().size());

        for (Player player : gameState.getPlayers()) {
            assertEquals(8, player.getHand().size());

            long defuseCount = player.getHand().stream()
                    .filter(card -> card.getType() == CardType.DEFUSE)
                    .count();

            assertTrue(defuseCount >= 1);
        }

        assertEquals(
                1,
                gameState.getDeck().countOf(CardType.EXPLODING_KITTEN)
        );
        assertSame(
                gameState.getPlayers().get(0),
                gameState.getCurrentPlayer()
        );

    }
    @Test
    public void StartGame_WithThreePlayers_ReturnsReadyGameState() {
        GameSetupController controller = new GameSetupController();

        GameState gameState = controller.startGame("3");

        assertEquals(3, gameState.getPlayers().size());

        for (Player player : gameState.getPlayers()) {
            assertEquals(8, player.getHand().size());

            long defuseCount = player.getHand().stream()
                    .filter(card -> card.getType() == CardType.DEFUSE)
                    .count();

            assertTrue(defuseCount >= 1);
        }

        assertEquals(
                2,
                gameState.getDeck().countOf(CardType.EXPLODING_KITTEN)
        );
        assertSame(
                gameState.getPlayers().get(0),
                gameState.getCurrentPlayer()
        );
    }
    @Test
    public void StartGame_WithFourPlayers_ReturnsReadyGameState() {
        GameSetupController controller = new GameSetupController();

        GameState gameState = controller.startGame("4");

        assertEquals(4, gameState.getPlayers().size());

        for (Player player : gameState.getPlayers()) {
            assertEquals(8, player.getHand().size());

            long defuseCount = player.getHand().stream()
                    .filter(card -> card.getType() == CardType.DEFUSE)
                    .count();

            assertTrue(defuseCount >= 1);
        }

        assertEquals(
                3,
                gameState.getDeck().countOf(CardType.EXPLODING_KITTEN)
        );
        assertSame(
                gameState.getPlayers().get(0),
                gameState.getCurrentPlayer()
        );
    }
    @Test
    public void StartGame_WithMaximumPlayerCount_ReturnsReadyGameState() {
        GameSetupController controller = new GameSetupController();

        GameState gameState = controller.startGame("5");

        assertEquals(5, gameState.getPlayers().size());

        for (Player player : gameState.getPlayers()) {
            assertEquals(8, player.getHand().size());

            long defuseCount = player.getHand().stream()
                    .filter(card -> card.getType() == CardType.DEFUSE)
                    .count();

            assertTrue(defuseCount >= 1);
        }

        assertEquals(
                4,
                gameState.getDeck().countOf(CardType.EXPLODING_KITTEN)
        );
        assertSame(
                gameState.getPlayers().get(0),
                gameState.getCurrentPlayer()
        );
    }
    @Test
    public void StartGame_WithPlayerCountAboveMaximum_ThrowsIllegalArgumentException() {
        GameSetupController controller = new GameSetupController();

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.startGame("6")
        );
    }
    @Test
    public void StartGame_WithNullPlayerCount_ThrowsIllegalArgumentException() {
        GameSetupController controller = new GameSetupController();

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.startGame(null)
        );
    }
    @Test
    public void StartGame_WithEmptyPlayerCount_ThrowsIllegalArgumentException() {
        GameSetupController controller = new GameSetupController();

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.startGame("")
        );
    }
    @Test
    public void StartGame_WithBlankPlayerCount_ThrowsIllegalArgumentException() {
        GameSetupController controller = new GameSetupController();

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.startGame("   ")
        );
    }
}

