package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


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
}