package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class PendingDefuseTests {

    @Test
    public void Constructor_WithExplodingKitten_KeepsSameKitten() {
        Card kitten = new Card(CardType.EXPLODING_KITTEN);

        PendingDefuse pending = new PendingDefuse(kitten);

        assertSame(kitten, pending.getKitten());
    }
}
