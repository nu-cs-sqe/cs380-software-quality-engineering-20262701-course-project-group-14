package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;

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

    @Test
    public void RemoveCards_WithNoneAvailableAndZeroCount_ReturnsEmptyList() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();
        deck.removeCards(CardType.EXPLODING_KITTEN, 4);

        List<Card> removed = deck.removeCards(
                CardType.EXPLODING_KITTEN, 0);

        assertTrue(removed.isEmpty());
        assertEquals(52, deck.size());
        assertEquals(0, deck.countOf(CardType.EXPLODING_KITTEN));

        for (CardType type : CardType.values()) {
            if (type != CardType.EXPLODING_KITTEN) {
                assertEquals(originalDeck.countOf(type), deck.countOf(type));
            }
        }
    }

    @Test
    public void RemoveCards_WithNoneAvailableAndOneRequested_Throws() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();
        deck.removeCards(CardType.EXPLODING_KITTEN, 4);

        assertThrows(
                IllegalArgumentException.class,
                () -> deck.removeCards(CardType.EXPLODING_KITTEN, 1)
        );

        assertEquals(52, deck.size());
        assertEquals(0, deck.countOf(CardType.EXPLODING_KITTEN));

        for (CardType type : CardType.values()) {
            if (type != CardType.EXPLODING_KITTEN) {
                assertEquals(originalDeck.countOf(type), deck.countOf(type));
            }
        }
    }

    @Test
    public void RemoveCards_WithNullType_ThrowsAndKeepsDeckUnchanged() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();

        assertThrows(NullPointerException.class,
                () -> deck.removeCards(null, 1)
        );

        assertEquals(56, deck.size());

        for (CardType type : CardType.values()) {
            assertEquals(originalDeck.countOf(type), deck.countOf(type));
        }
    }

    @Test
    public void AddCards_WithEmptyList_KeepsDeckUnchanged() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();
        deck.addCards(List.of());
        assertEquals(56, deck.size());

        for (CardType type : CardType.values()) {
            assertEquals(originalDeck.countOf(type), deck.countOf(type));
        }
    }

    @Test
    public void AddCards_WithOneCard_AddsItToTheDeck() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();

        List<Card> removed = deck.removeCards(
                CardType.EXPLODING_KITTEN, 4);

        deck.addCards(List.of(removed.get(0)));

        assertEquals(53, deck.size());
        assertEquals(1, deck.countOf(CardType.EXPLODING_KITTEN));

        for (CardType type : CardType.values()) {
            if (type != CardType.EXPLODING_KITTEN) {
                assertEquals(originalDeck.countOf(type), deck.countOf(type));
            }
        }
    }

    @Test
    public void AddCards_WithMultipleCards_RestoresOriginalComposition() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();

        List<Card> removed = deck.removeCards(
                CardType.EXPLODING_KITTEN, 4);

        deck.addCards(removed);

        assertEquals(56, deck.size());

        for (CardType type : CardType.values()) {
            assertEquals(originalDeck.countOf(type), deck.countOf(type));
        }
    }

    @Test
    public void AddCards_ToEmptyDeck_AddsOneCard() {
        Deck deck = new Deck();

        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        assertEquals(0, deck.size());

        deck.addCards(List.of(new Card(CardType.DEFUSE)));

        assertEquals(1, deck.size());

        for (CardType type : CardType.values()) {
            int expected = type == CardType.DEFUSE ? 1 : 0;
            assertEquals(expected, deck.countOf(type));
        }
    }

    @Test
    public void AddCards_WithNullList_ThrowsAndKeepsDeckUnchanged() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();

        assertThrows(
                NullPointerException.class,
                () -> deck.addCards(null)
        );

        assertEquals(56, deck.size());

        for (CardType type : CardType.values()) {
            assertEquals(originalDeck.countOf(type), deck.countOf(type));
        }
    }
    @Test
    public void AddCards_WithNullElement_ThrowsAndKeepsDeckUnchanged() {
        Deck deck = new Deck();
        Deck originalDeck = new Deck();

        List<Card> supplied = new ArrayList<>();
        supplied.add(new Card(CardType.DEFUSE));
        supplied.add(null);

        assertThrows(
                NullPointerException.class,
                () -> deck.addCards(supplied)
        );

        assertEquals(56, deck.size());

        for (CardType type : CardType.values()) {
            assertEquals(originalDeck.countOf(type), deck.countOf(type));
        }
    }

}