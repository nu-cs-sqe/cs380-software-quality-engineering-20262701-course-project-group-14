package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}
