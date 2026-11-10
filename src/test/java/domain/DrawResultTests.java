package domain;

import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}