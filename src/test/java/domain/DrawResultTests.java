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

    @Test
    public void DefuseRequired_ReturnsRequiredStatusWithoutCardType() {
        DrawResult result = DrawResult.defuseRequired();

        assertEquals(
                DrawStatus.DEFUSE_POSITION_REQUIRED,
                result.getStatus()
        );
        assertEquals(
                Optional.empty(),
                result.getDrawnCardType()
        );
    }

    @Test
    public void PlayerEliminated_ReturnsEliminatedStatusWithoutCardType() {
        DrawResult result = DrawResult.playerEliminated();

        assertEquals(
                DrawStatus.PLAYER_ELIMINATED,
                result.getStatus()
        );
        assertEquals(
                Optional.empty(),
                result.getDrawnCardType()
        );
    }

}