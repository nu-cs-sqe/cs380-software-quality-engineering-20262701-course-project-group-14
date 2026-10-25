package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class DeckTests {
    @ParameterizedTest(name = "Deck02: {0} count={1}")
    @CsvSource({
            "EXPLODING_KITTEN, 4",
            "DEFUSE, 6",
            "ATTACK, 4",
            "SKIP, 4",
            "FAVOR, 4",
            "SHUFFLE, 4",
            "SEE_THE_FUTURE, 5",
            "NOPE, 5",
            "TACOCAT, 4",
            "CATTERMELON, 4",
            "HAIRY_POTATO_CAT, 4",
            "BEARD_CAT, 4",
            "RAINBOW_RALPHING_CAT, 4"
    })
    public void Create_NewDeck_CountOfReturnsExpectedCount(
            CardType type, int expected) {
        Deck deck = new Deck();

        assertEquals(expected, deck.countOf(type));
    }

    @Test
    public void Create_NewDeck_SizeReturns56() {
        Deck deck = new Deck();
        assertEquals(56, deck.size());
    }

    @Test
    public void RemoveCards_WithZero_ReturnsEmptyListAndKeepsDeckUnchanged() {
        Deck deck = new Deck();

        List<Card> removed = deck.removeCards(
                CardType.EXPLODING_KITTEN, 0);

        assertTrue(removed.isEmpty());
        assertEquals(56, deck.size());
        assertEquals(4, deck.countOf(CardType.EXPLODING_KITTEN));
    }

    @Test
    public void RemoveCards_WithOne_ReturnsEmptyListAndKeepsDeckUnchanged() {
        Deck deck = new Deck();
        Deck deck2 = new Deck();

        List<Card> removed = deck.removeCards(
                CardType.EXPLODING_KITTEN, 1
        );
        assertEquals(1, removed.size());
        assertEquals(CardType.EXPLODING_KITTEN, removed.get(0).getType());
        assertEquals(55, deck.size());
        assertEquals(3, deck.countOf(CardType.EXPLODING_KITTEN));

        for (CardType type : CardType.values()) {
            if (type != CardType.EXPLODING_KITTEN) {
                assertEquals(deck2.countOf(type), deck.countOf(type));
            }
        }
    }

    @Test
    public void RemoveCards_WithAllAvailable_ReturnsAllMatchingCards() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();

        List<Card> removed = deck.removeCards(
                CardType.EXPLODING_KITTEN, 4);

        assertEquals(4, removed.size());

        for (Card card : removed) {
            assertEquals(CardType.EXPLODING_KITTEN, card.getType());
        }

        assertEquals(52, deck.size());
        assertEquals(0, deck.countOf(CardType.EXPLODING_KITTEN));

        for (CardType type : CardType.values()) {
            if (type != CardType.EXPLODING_KITTEN) {
                assertEquals(originalDeck.countOf(type), deck.countOf(type));
            }
        }
    }

    @Test
    public void RemoveCards_WithNegativeCount_ThrowsAndKeepsDeckUnchanged() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();

        assertThrows(
                IllegalArgumentException.class,
                () -> deck.removeCards(CardType.EXPLODING_KITTEN, -1)
        );

        assertEquals(56, deck.size());

        for (CardType type : CardType.values()) {
            assertEquals(originalDeck.countOf(type), deck.countOf(type));
        }
    }

    @Test
    public void RemoveCards_WithInsufficientCards_ThrowsAndKeepsDeckUnchanged() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();

        assertThrows(IllegalArgumentException.class,
                () -> deck.removeCards(CardType.EXPLODING_KITTEN, 5)
        );

        assertEquals(56, deck.size());

        for (CardType type : CardType.values()) {
            assertEquals(originalDeck.countOf(type), deck.countOf(type));
        }
    }
}