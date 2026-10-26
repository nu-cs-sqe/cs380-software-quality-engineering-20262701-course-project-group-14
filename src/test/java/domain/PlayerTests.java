package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PlayerTests {

    // PL-01
    @Test
    public void Create_WithNonEmptyName_HasNameAndEmptyHand() {
        Player player = new Player("Player 1");

        assertEquals("Player 1", player.getName());
        assertTrue(player.getHand().isEmpty());
    }
}