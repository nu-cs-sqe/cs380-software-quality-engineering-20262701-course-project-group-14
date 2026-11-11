package domain;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
