package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SkipEffectTests {

    @Test
    public void CanPlay_WithValidPlayerAndDeck_ReturnsTrueWithoutMutation() {
        Player player = new Player("Player 1");
        Deck deck = new Deck();
        int originalDeckSize = deck.size();
        int originalHandSize = player.getHand().size();
        SkipEffect effect = new SkipEffect();

        boolean canPlay = effect.canPlay(player, deck);

        assertTrue(canPlay);
        assertEquals(originalDeckSize, deck.size());
        assertEquals(originalHandSize, player.getHand().size());
    }

    @Test
    public void CanPlay_WithNullPlayer_ThrowsWithoutChangingDeck() {
        Deck deck = new Deck();
        int originalDeckSize = deck.size();
        SkipEffect effect = new SkipEffect();

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> effect.canPlay(null, deck)
        );

        assertEquals("Player must not be null.", exception.getMessage());
        assertEquals(originalDeckSize, deck.size());
    }
}
