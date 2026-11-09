package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

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

    // DP-06
    @Test
    public void DiscardAll_EmptyList_SizeUnchanged() {
        DiscardPile pile = new DiscardPile();

        pile.discardAll(List.of());

        assertEquals(0, pile.size());
    }

    // DP-07
    @Test
    public void DiscardAll_ListWithOneCard_SizeIsOne() {
        DiscardPile pile = new DiscardPile();

        pile.discardAll(List.of(new Card(CardType.SKIP)));

        assertEquals(1, pile.size());
    }

    // DP-08
    @Test
    public void DiscardAll_FullHandWithDuplicates_KeepsAllEightCards() {
        DiscardPile pile = new DiscardPile();
        List<Card> hand = List.of(
                new Card(CardType.TACOCAT),
                new Card(CardType.TACOCAT),
                new Card(CardType.SKIP),
                new Card(CardType.SKIP),
                new Card(CardType.ATTACK),
                new Card(CardType.NOPE),
                new Card(CardType.FAVOR),
                new Card(CardType.DEFUSE)
        );

        pile.discardAll(hand);

        assertEquals(8, pile.size());
    }

    // DP-09
    @Test
    public void DiscardAll_OntoPileWithSameCard_KeepsAllCards() {
        DiscardPile pile = new DiscardPile();
        pile.discard(new Card(CardType.SKIP));

        pile.discardAll(List.of(
                new Card(CardType.SKIP),
                new Card(CardType.ATTACK)
        ));

        assertEquals(3, pile.size());
    }

    // DP-10
    @Test
    public void DiscardAll_NullList_ThrowsNullPointerExceptionAndPileUnchanged() {
        DiscardPile pile = new DiscardPile();

        assertThrows(
                NullPointerException.class,
                () -> pile.discardAll(null)
        );
        assertEquals(0, pile.size());
    }
}