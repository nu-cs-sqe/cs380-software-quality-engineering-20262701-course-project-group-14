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
}


