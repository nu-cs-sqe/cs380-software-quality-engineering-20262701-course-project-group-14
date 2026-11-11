package domain;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameSnapshotTests {

    @Test
    public void Create_WithValidValues_ReturnsMatchingSnapshot() {
        GameSnapshot snapshot = GameSnapshot.create(
                "Player 1",
                List.of(CardType.ATTACK, CardType.DEFUSE),
                10,
                0,
                2,
                TurnPhase.ACTIONS_ALLOWED
        );

        assertEquals("Player 1", snapshot.getCurrentPlayerName());
        assertEquals(
                List.of(CardType.ATTACK, CardType.DEFUSE),
                snapshot.getCurrentPlayerHand()
        );
        assertEquals(10, snapshot.getDeckSize());
        assertEquals(0, snapshot.getDiscardPileSize());
        assertEquals(2, snapshot.getAlivePlayerCount());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, snapshot.getPhase());
    }

    @Test
    public void Create_WithEmptyDeck_ReturnsZeroDeckSize() {
        GameSnapshot snapshot = GameSnapshot.create(
                "Player 1",
                List.of(CardType.ATTACK),
                0,
                1,
                2,
                TurnPhase.ACTIONS_ALLOWED
        );

        assertEquals(0, snapshot.getDeckSize());
    }

    @Test
    public void Create_WithNullPlayerName_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> GameSnapshot.create(
                        null,
                        List.of(CardType.ATTACK),
                        10,
                        0,
                        2,
                        TurnPhase.ACTIONS_ALLOWED
                )
        );

        assertEquals(
                "Current player name must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Create_WithNullPlayerHand_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> GameSnapshot.create(
                        "Player 1",
                        null,
                        10,
                        0,
                        2,
                        TurnPhase.ACTIONS_ALLOWED
                )
        );

        assertEquals(
                "Current player hand must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Create_WithNullCardInHand_ThrowsNullPointerException() {
        List<CardType> hand = Arrays.asList(
                CardType.ATTACK,
                null
        );

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> GameSnapshot.create(
                        "Player 1",
                        hand,
                        10,
                        0,
                        2,
                        TurnPhase.ACTIONS_ALLOWED
                )
        );

        assertEquals(
                "Current player hand must not contain null.",
                exception.getMessage()
        );
    }

    @Test
    public void Create_WithNegativeDeckSize_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> GameSnapshot.create(
                        "Player 1",
                        List.of(CardType.ATTACK),
                        -1,
                        0,
                        2,
                        TurnPhase.ACTIONS_ALLOWED
                )
        );

        assertEquals(
                "Deck size must not be negative.",
                exception.getMessage()
        );
    }
}
