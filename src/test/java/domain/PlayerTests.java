package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTests {

    // PL-01
    @Test
    public void Create_WithNonEmptyName_HasNameAndEmptyHand() {
        Player player = new Player("Player 1");

        assertEquals("Player 1", player.getName());
        assertTrue(player.getHand().isEmpty());
    }

    // PL-02
    @Test
    public void Create_WithEmptyName_ThrowsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Player("")
        );
    }
}