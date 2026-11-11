package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayResultTests {

    @Test
    public void ContinueTurn_WithEmptyRevealedCards_ReturnsContinueResult() {
        PlayResult result = PlayResult.continueTurn(List.of());

        assertEquals(
                PlayStatus.CONTINUE_TURN,
                result.getStatus()
        );
        assertEquals(
                PlayFailure.NONE,
                result.getFailure()
        );
        assertEquals(
                List.of(),
                result.getRevealedCards()
        );
    }
}