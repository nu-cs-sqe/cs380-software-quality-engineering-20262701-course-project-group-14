package domain;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Test
    public void ContinueTurn_WithNullCard_ThrowsNullPointerException() {
        List<CardType> cards = Arrays.asList(
                CardType.SKIP,
                null
        );

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> PlayResult.continueTurn(cards)
        );

        assertEquals(
                "Revealed cards must not contain null.",
                exception.getMessage()
        );
    }

    @Test
    public void ContinueTurn_WithMutableList_DefensivelyCopiesCards() {
        List<CardType> expectedCards = List.of(
                CardType.SKIP,
                CardType.FAVOR,
                CardType.SHUFFLE
        );
        List<CardType> sourceCards = new ArrayList<>(expectedCards);

        PlayResult result = PlayResult.continueTurn(sourceCards);
        sourceCards.clear();

        assertEquals(
                expectedCards,
                result.getRevealedCards()
        );
    }

    @Test
    public void TurnEnded_ReturnsEndedResultWithoutFailureOrCards() {
        PlayResult result = PlayResult.turnEnded();

        assertEquals(
                PlayStatus.TURN_ENDED,
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
    public void Rejected_WithInvalidHandIndex_ReturnsRejectedResult() {
        PlayResult result = PlayResult.rejected(
                PlayFailure.INVALID_HAND_INDEX
        );

        assertEquals(
                PlayStatus.REJECTED,
                result.getStatus()
        );
        assertEquals(
                PlayFailure.INVALID_HAND_INDEX,
                result.getFailure()
        );
        assertEquals(
                List.of(),
                result.getRevealedCards()
        );
    }

    @Test
    public void Rejected_WithNullFailure_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> PlayResult.rejected(null)
        );

        assertEquals(
                "Play failure must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Rejected_WithNoFailure_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PlayResult.rejected(PlayFailure.NONE)
        );

        assertEquals(
                "Rejected result requires a failure reason.",
                exception.getMessage()
        );
    }

}