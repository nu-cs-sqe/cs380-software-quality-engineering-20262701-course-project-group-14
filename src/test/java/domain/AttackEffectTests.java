package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AttackEffectTests {

    @Test
    public void CanPlay_WithValidPlayerAndDeck_ReturnsTrue() {
        AttackEffect effect = new AttackEffect();

        assertTrue(effect.canPlay(new Player("Player 1"), new Deck()));
    }

    @Test
    public void CanPlay_WithNullPlayer_ThrowsNullPointerException() {

        AttackEffect effect = new AttackEffect();

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> effect.canPlay(null, new Deck())
        );

        assertEquals("Player must not be null.", exception.getMessage());
    }

    @Test
    public void CanPlay_WithNullDeck_ThrowsNullPointerException() {
        AttackEffect effect = new AttackEffect();

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> effect.canPlay(new Player("Player 1"), null)
        );

        assertEquals("Deck must not be null.", exception.getMessage());
    }

    @Test
    public void Play_WithValidPlayerAndDeck_ReturnsAttackOutcome() {
        AttackEffect effect = new AttackEffect();

        EffectOutcome outcome = effect.play(
                new Player("Player 1"),
                new Deck()
        );

        assertEquals(
                TurnDirective.ATTACK_NEXT_PLAYER,
                outcome.getTurnDirective()
        );
        assertTrue(outcome.endsTurn());
        assertEquals(List.of(), outcome.getRevealedCards());
    }
}
