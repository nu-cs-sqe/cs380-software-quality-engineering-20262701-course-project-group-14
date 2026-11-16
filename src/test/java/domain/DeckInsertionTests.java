package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DeckInsertionTests {
    @Test
    void insertsAtTop() {
        Deck deck = new Deck();
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
        Deck deck = new Deck();
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
}