package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EffectOutcomeTests {

    @Test
    public void Constructor_WithEmptyCards_ContinuesTurn() {
        EffectOutcome outcome = new EffectOutcome(false, List.of());

        assertFalse(outcome.endsTurn());
        assertEquals(List.of(), outcome.getRevealedCards());
    }

    @Test
    public void Constructor_WithRevealedCards_PreservesOrder() {
        List<CardType> cards = List.of(
                CardType.SKIP,
                CardType.FAVOR,
                CardType.SHUFFLE
        );

        EffectOutcome outcome = new EffectOutcome(false, cards);

        assertFalse(outcome.endsTurn());
        assertEquals(cards, outcome.getRevealedCards());
    }

    @Test
    public void Constructor_WithEndsTurn_ReturnsEndedOutcome() {
        EffectOutcome outcome = new EffectOutcome(true, List.of());

        assertTrue(outcome.endsTurn());
        assertEquals(List.of(), outcome.getRevealedCards());
    }

    @Test
    public void Constructor_WithNullCards_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new EffectOutcome(false, null)
        );

        assertEquals(
                "Revealed cards must not be null.",
                exception.getMessage()
        );
    }
}
