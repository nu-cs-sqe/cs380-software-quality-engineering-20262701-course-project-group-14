package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
