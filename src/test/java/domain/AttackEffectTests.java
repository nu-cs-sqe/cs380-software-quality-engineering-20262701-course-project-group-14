package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Objects;

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
}