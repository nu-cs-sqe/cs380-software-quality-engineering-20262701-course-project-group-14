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

    @Test
    public void CanPlay_WithNullDeck_ThrowsWithoutChangingPlayer() {
        Player player = new Player("Player 1");
        int originalHandSize = player.getHand().size();
        SkipEffect effect = new SkipEffect();

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> effect.canPlay(player, null)
        );

        assertEquals("Deck must not be null.", exception.getMessage());
        assertEquals(originalHandSize, player.getHand().size());
    }

    @Test
    public void Play_WithValidPlayerAndDeck_EndsTurnWithoutMutation() {
        Player player = new Player("Player 1");
        Deck deck = new Deck();
        int originalDeckSize = deck.size();
        int originalHandSize = player.getHand().size();
        SkipEffect effect = new SkipEffect();

        EffectOutcome outcome = effect.play(player, deck);

        assertTrue(outcome.endsTurn());
        assertTrue(outcome.getRevealedCards().isEmpty());
        assertEquals(originalDeckSize, deck.size());
        assertEquals(originalHandSize, player.getHand().size());
    }

    @Test
    public void Play_WithNullPlayer_ThrowsWithoutChangingDeck() {
        Deck deck = new Deck();
        int originalDeckSize = deck.size();
        SkipEffect effect = new SkipEffect();

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> effect.play(null, deck)
        );

        assertEquals("Player must not be null.", exception.getMessage());
        assertEquals(originalDeckSize, deck.size());
    }
}
