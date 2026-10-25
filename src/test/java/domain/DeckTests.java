package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeckTests {
    @Test
    public void Create_NewDeck_SizeReturns56() {
        Deck deck = new Deck();
        assertEquals(56, deck.size());
    }
}