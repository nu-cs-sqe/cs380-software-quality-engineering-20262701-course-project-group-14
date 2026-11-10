package domain;

import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DrawResultTests {

    @Test
    public void Normal_WithValidCardType_ReturnsNormalDrawResult() {
        DrawResult result = DrawResult.normal(CardType.ATTACK);

        assertEquals(
                DrawStatus.NORMAL_CARD_DRAWN,
                result.getStatus()
        );
        assertEquals(
                Optional.of(CardType.ATTACK),
                result.getDrawnCardType()
        );
    }

    @Test
    public void Normal_WithNullCardType_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> DrawResult.normal(null)
        );

        assertEquals(
                "Drawn card type must not be null.",
                exception.getMessage()
        );
    }

}