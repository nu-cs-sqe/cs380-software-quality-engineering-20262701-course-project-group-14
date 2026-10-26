package integration;

import controller.GameSetupController;
import domain.CardType;
import domain.GameState;
import domain.Player;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class GameSetupIntegrationTest {

    @ParameterizedTest(name = "{0} players")
    @CsvSource({"2, 35", "3, 29", "4, 23", "5, 16"})
    public void StartGame_WithValidPlayerCount_GameIsReadyForFirstTurn(
            int playerCount, int expectedDeckSize) {
        GameSetupController controller = new GameSetupController();

        GameState gameState = controller.startGame(String.valueOf(playerCount));

        assertEquals(playerCount, gameState.getPlayers().size());
        for (Player player : gameState.getPlayers()) {
            assertEquals(8, player.getHand().size());
            assertTrue(countOfType(player, CardType.DEFUSE) >= 1);
            assertEquals(0, countOfType(player, CardType.EXPLODING_KITTEN));
        }
        assertEquals(playerCount - 1,
                gameState.getDeck().countOf(CardType.EXPLODING_KITTEN));
        assertEquals(expectedDeckSize, gameState.getDeck().size());
        assertEquals("Player 1", gameState.getCurrentPlayer().getName());
    }

    @ParameterizedTest(name = "input \"{0}\"")
    @ValueSource(strings = {"1", "6"})
    public void StartGame_WithPlayerCountOutOfRange_ThrowsIllegalArgumentException(
            String input) {
        GameSetupController controller = new GameSetupController();

        assertThrows(IllegalArgumentException.class,
                () -> controller.startGame(input));
    }

    private long countOfType(Player player, CardType type) {
        return player.getHand().stream()
                .filter(card -> card.getType() == type)
                .count();
    }
}