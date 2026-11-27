package controller;

import domain.CardType;
import domain.GameState;
import domain.Player;
import java.util.List;
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
    @Test
    public void StartGame_WithNonNumericPlayerCount_ThrowsClearIllegalArgumentException() {
        GameSetupController controller = new GameSetupController();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.startGame("abc")
        );

        assertEquals("setup.error.player-count", exception.getMessage());
    }
    @Test
    public void StartGame_WithDecimalPlayerCount_ThrowsClearIllegalArgumentException() {
        GameSetupController controller = new GameSetupController();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.startGame("2.5")
        );

        assertEquals("setup.error.player-count", exception.getMessage());
    }
    @Test
    public void StartNamedGame_WithTwoNames_NamesPlayersInSeatOrder() {
        GameSetupController controller = new GameSetupController();

        GameState gameState = controller.startNamedGame(List.of("Ada", "Bo"));

        assertEquals("Ada", gameState.getPlayers().get(0).getName());
        assertEquals("Bo", gameState.getPlayers().get(1).getName());
        assertEquals(8, gameState.getPlayers().get(1).getHand().size());
    }

    @Test
    public void StartNamedGame_WithSurroundingWhitespace_TrimsName() {
        GameSetupController controller = new GameSetupController();

        GameState gameState = controller.startNamedGame(List.of("  Ada ", "Bo"));

        assertEquals("Ada", gameState.getPlayers().get(0).getName());
    }

    @Test
    public void StartNamedGame_WithFiveNamesOfMaximumLength_KeepsEveryName() {
        GameSetupController controller = new GameSetupController();

        List<String> names = List.of("AAAAAAAAAAAA", "BBBBBBBBBBBB", "CCCCCCCCCCCC",
                "DDDDDDDDDDDD", "EEEEEEEEEEEE");

        GameState gameState = controller.startNamedGame(names);

        assertEquals(5, gameState.getPlayers().size());
        for (int seat = 0; seat < 5; seat++) {
            assertEquals(names.get(seat), gameState.getPlayers().get(seat).getName());
        }
    }

    @Test
    public void StartNamedGame_WithThirteenCharacterName_ThrowsNameLengthKey() {
        GameSetupController controller = new GameSetupController();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.startNamedGame(List.of("Ada", "ABCDEFGHIJKLM")));

        assertEquals("setup.error.name-length", exception.getMessage());
    }

    @Test
    public void StartNamedGame_WithNamesDifferingOnlyInCase_ThrowsDuplicateKey() {
        GameSetupController controller = new GameSetupController();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.startNamedGame(List.of("Ada", "ada")));

        assertEquals("setup.error.name-duplicate", exception.getMessage());
    }

    @Test
    public void StartNamedGame_WithBlankName_ThrowsNameBlankKey() {
        GameSetupController controller = new GameSetupController();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.startNamedGame(List.of("Ada", "   ")));

        assertEquals("setup.error.name-blank", exception.getMessage());
    }

    @Test
    public void StartNamedGame_WithOneName_ThrowsPlayerCountKey() {
        GameSetupController controller = new GameSetupController();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.startNamedGame(List.of("Ada")));

        assertEquals("setup.error.player-count", exception.getMessage());
    }

    @Test
    public void StartNamedGame_WithSixNames_ThrowsPlayerCountKey() {
        GameSetupController controller = new GameSetupController();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.startNamedGame(List.of("A", "B", "C", "D", "E", "F")));

        assertEquals("setup.error.player-count", exception.getMessage());
    }

    @Test
    public void StartNamedGame_WithNullList_ThrowsNullPointerException() {
        GameSetupController controller = new GameSetupController();

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> controller.startNamedGame(null));

        assertEquals("Player names must not be null.", exception.getMessage());
    }

    @Test
    public void StartNamedGame_WithWhitespaceRunInside_CollapsesToOneSpace() {
        GameSetupController controller = new GameSetupController();

        GameState gameState = controller.startNamedGame(List.of("Ada \t  Lovelace", "Bo"));

        assertEquals("Ada Lovelace", gameState.getPlayers().get(0).getName());
    }

    @Test
    public void StartNamedGame_WithComposedAndDecomposedAccent_ThrowsDuplicateKey() {
        GameSetupController controller = new GameSetupController();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.startNamedGame(List.of("\u00e9", "e\u0301")));

        assertEquals("setup.error.name-duplicate", exception.getMessage());
    }

    @Test
    public void StartNamedGame_WithEmoji_ThrowsNameCharactersKey() {
        GameSetupController controller = new GameSetupController();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.startNamedGame(List.of("Ada\uD83D\uDE00", "Bo")));

        assertEquals("setup.error.name-characters", exception.getMessage());
    }

    @Test
    public void StartGame_WithSurroundingWhitespace_ReturnsReadyGameState() {
        GameSetupController controller = new GameSetupController();

        GameState gameState = controller.startGame(" 3 ");

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
}

