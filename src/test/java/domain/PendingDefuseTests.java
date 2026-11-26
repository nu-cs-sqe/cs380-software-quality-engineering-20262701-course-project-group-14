package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PendingDefuseTests {

    @Test
    public void Constructor_WithExplodingKitten_KeepsSameKitten() {
        Card kitten = new Card(CardType.EXPLODING_KITTEN);

        PendingDefuse pending = new PendingDefuse(kitten);

        assertSame(kitten, pending.getKitten());
    }

    @Test
    public void Constructor_WithNullKitten_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new PendingDefuse(null)
        );

        assertEquals("Pending Kitten must not be null.", exception.getMessage());
    }

    @Test
    public void Constructor_WithNonKittenCard_ThrowsIllegalArgumentException() {
        Card defuse = new Card(CardType.DEFUSE);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new PendingDefuse(defuse)
        );

        assertEquals(
                "Pending Defuse requires an Exploding Kitten.",
                exception.getMessage()
        );
    }
}
