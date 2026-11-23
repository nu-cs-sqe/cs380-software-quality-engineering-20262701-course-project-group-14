package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerSummaryTests {

    @Test
    public void Create_WithActivePlayer_PreservesSummaryValues() {
        PlayerSummary summary = PlayerSummary.create(
                0,
                "Player 1",
                0,
                false
        );

        assertEquals(0, summary.getPlayerIndex());
        assertEquals("Player 1", summary.getName());
        assertEquals(0, summary.getHandSize());
        assertFalse(summary.isEliminated());
    }

    @Test
    public void Create_WithEliminatedPlayer_PreservesSummaryValues() {
        PlayerSummary summary = PlayerSummary.create(
                4,
                "Player 5",
                8,
                true
        );

        assertEquals(4, summary.getPlayerIndex());
        assertEquals("Player 5", summary.getName());
        assertEquals(8, summary.getHandSize());
        assertTrue(summary.isEliminated());
    }

    @Test
    public void Create_WithNegativePlayerIndex_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PlayerSummary.create(-1, "Player 1", 0, false)
        );

        assertEquals(
                "Player index must not be negative.",
                exception.getMessage()
        );
    }

    @Test
    public void Create_WithNullName_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> PlayerSummary.create(0, null, 0, false)
        );

        assertEquals(
                "Player name must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Create_WithEmptyName_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PlayerSummary.create(0, "", 0, false)
        );

        assertEquals(
                "Player name must not be blank.",
                exception.getMessage()
        );
    }
}
