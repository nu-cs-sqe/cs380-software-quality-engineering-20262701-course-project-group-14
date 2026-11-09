package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardPileTests {

    // DP-01
    @Test
    public void Create_NewPile_IsEmpty() {
        DiscardPile pile = new DiscardPile();

        assertEquals(0, pile.size());
    }
}