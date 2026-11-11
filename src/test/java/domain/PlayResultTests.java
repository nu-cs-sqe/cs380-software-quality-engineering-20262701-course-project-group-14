package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void ContinueTurn_WithRevealedCards_PreservesCardOrder() {
        List<CardType> cards = List.of(
                CardType.SKIP,
                CardType.FAVOR,
                CardType.SHUFFLE
        );

        PlayResult result = PlayResult.continueTurn(cards);

        assertEquals(
                PlayStatus.CONTINUE_TURN,
                result.getStatus()
        );
        assertEquals(
                PlayFailure.NONE,
                result.getFailure()
        );
        assertEquals(
                cards,
                result.getRevealedCards()
        );
    }

    @Test
    public void ContinueTurn_WithNullList_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> PlayResult.continueTurn(null)
        );

        assertEquals(
                "Revealed cards must not be null.",
                exception.getMessage()
        );
    }

}