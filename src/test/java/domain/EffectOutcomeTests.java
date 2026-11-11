package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}
