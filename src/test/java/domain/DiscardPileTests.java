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

    // DP-03
    @Test
    public void Discard_OntoPileWithOneCard_SizeIsTwo() {
        DiscardPile pile = new DiscardPile();
        pile.discard(new Card(CardType.SKIP));

        pile.discard(new Card(CardType.DEFUSE));

        assertEquals(2, pile.size());
    }

    // DP-04
    @Test
    public void Discard_DuplicateCard_KeepsBoth() {
        DiscardPile pile = new DiscardPile();
        pile.discard(new Card(CardType.SKIP));

        pile.discard(new Card(CardType.SKIP));

        assertEquals(2, pile.size());
    }

    // DP-05
    @Test
    public void Discard_NullCard_ThrowsNullPointerExceptionAndPileUnchanged() {
        DiscardPile pile = new DiscardPile();

        assertThrows(
                NullPointerException.class,
                () -> pile.discard(null)
        );
        assertEquals(0, pile.size());
    }
}