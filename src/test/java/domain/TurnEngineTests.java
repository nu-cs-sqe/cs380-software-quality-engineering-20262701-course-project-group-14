package domain;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TurnEngineTests {

    @Test
    public void Constructor_WithValidDependencies_StartsWithActionsAllowed() {
        GameState state = new GameState(
                List.of(new Player("Player 1")),
                new Deck()
        );
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile);

        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                resolver,
                Map.of()
        );

        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
    }

    @Test
    public void Constructor_WithNullState_ThrowsNullPointerException() {
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new TurnEngine(
                        null,
                        discardPile,
                        resolver,
                        Map.of()
                )
        );

        assertEquals(
                "Game state must not be null.",
                exception.getMessage()
        );
    }
}
