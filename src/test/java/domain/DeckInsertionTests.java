package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeckInsertionTests {
    private static Deck emptyDeck() {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        return deck;
    }
    @Test
    void insertsAtTop() {
        Deck deck = emptyDeck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }

        Card attack = new Card(CardType.ATTACK);
        Card skip = new Card(CardType.SKIP);
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        deck.addCards(List.of(attack, skip));

        deck.insertAt(kitten, 0);

        assertEquals(3, deck.size());
        assertSame(kitten, deck.drawCard());
        assertSame(attack, deck.drawCard());
        assertSame(skip, deck.drawCard());
    }

    @Test
    void insertsBetweenExistingCards() {
        Deck deck = emptyDeck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }

        Card attack = new Card(CardType.ATTACK);
        Card skip = new Card(CardType.SKIP);
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        deck.addCards(List.of(attack, skip));

        deck.insertAt(kitten, 1);

        assertEquals(3, deck.size());
        assertSame(attack, deck.drawCard());
        assertSame(kitten, deck.drawCard());
        assertSame(skip, deck.drawCard());
    }

    @Test
    void insertsAtBottom() {
        Deck deck = emptyDeck();
        Card attack = new Card(CardType.ATTACK);
        Card skip = new Card(CardType.SKIP);
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        deck.addCards(List.of(attack, skip));

        deck.insertAt(kitten, 2);

        assertEquals(3, deck.size());
        assertSame(attack, deck.drawCard());
        assertSame(skip, deck.drawCard());
        assertSame(kitten, deck.drawCard());
    }

    @Test
    void insertsIntoEmptyDeck() {
        Deck deck = emptyDeck();
        Card kitten = new Card(CardType.EXPLODING_KITTEN);

        deck.insertAt(kitten, 0);

        assertEquals(1, deck.size());
        assertSame(kitten, deck.drawCard());
    }

    @Test
    void rejectsNegativePositionWithoutChangingDeck() {
        Deck deck = emptyDeck();
        Card attack = new Card(CardType.ATTACK);
        Card skip = new Card(CardType.SKIP);
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        deck.addCards(List.of(attack, skip));

        assertThrows(IllegalArgumentException.class,
                () -> deck.insertAt(kitten, -1));

        assertEquals(2, deck.size());
        assertSame(attack, deck.drawCard());
        assertSame(skip, deck.drawCard());
    }

    @Test
    void rejectsPositionBeyondBottomWithoutChangingDeck() {
        Deck deck = emptyDeck();
        Card attack = new Card(CardType.ATTACK);
        Card skip = new Card(CardType.SKIP);
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        deck.addCards(List.of(attack, skip));

        assertThrows(IllegalArgumentException.class,
                () -> deck.insertAt(kitten, 3));

        assertEquals(2, deck.size());
        assertSame(attack, deck.drawCard());
        assertSame(skip, deck.drawCard());
    }

    @Test
    void rejectsNullCardWithoutChangingDeck() {
        Deck deck = emptyDeck();
        Card attack = new Card(CardType.ATTACK);
        Card skip = new Card(CardType.SKIP);
        deck.addCards(List.of(attack, skip));

        assertThrows(NullPointerException.class,
                () -> deck.insertAt(null, 1));

        assertEquals(2, deck.size());
        assertSame(attack, deck.drawCard());
        assertSame(skip, deck.drawCard());
    }
}