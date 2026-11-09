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

    // DP-02
    @Test
    public void Discard_OntoEmptyPile_SizeIsOne() {
        DiscardPile pile = new DiscardPile();

        pile.discard(new Card(CardType.SKIP));

        assertEquals(1, pile.size());
    }
}