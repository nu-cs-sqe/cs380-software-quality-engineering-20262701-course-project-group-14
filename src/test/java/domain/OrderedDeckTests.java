package domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderedDeckTests {
    @Test
    void preservesDrawOrderAfterSourceListIsCleared() {
        Card skip = new Card(CardType.SKIP);
        Card attack = new Card(CardType.ATTACK);
        Card defuse = new Card(CardType.DEFUSE);
        List<Card> cards = new ArrayList<>(List.of(skip, attack, defuse));

        Deck deck = Deck.fromTopToBottom(cards);
        cards.clear();

        assertEquals(3, deck.size());
        assertSame(skip, deck.drawCard());
        assertSame(attack, deck.drawCard());
        assertSame(defuse, deck.drawCard());
    }

    @Test
    void acceptsAnEmptyOrder() {
        Deck deck = Deck.fromTopToBottom(List.of());

        assertEquals(0, deck.size());
    }

    @Test
    void rejectsANullOrder() {
        assertThrows(NullPointerException.class,
                () -> Deck.fromTopToBottom(null));
    }

    @Test
    void rejectsANullCardInTheOrder() {
        Card skip = new Card(CardType.SKIP);
        List<Card> cards = Arrays.asList(skip, null);

        assertThrows(NullPointerException.class,
                () -> Deck.fromTopToBottom(cards));
    }
}
