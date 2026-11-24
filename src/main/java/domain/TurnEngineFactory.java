package domain;

import java.util.Map;
import java.util.Objects;

public final class TurnEngineFactory {
    private TurnEngineFactory() {
    }

    public static TurnActions create(GameState state) {
        GameState checkedState = Objects.requireNonNull(
                state,
                "Game state must not be null."
        );
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver = new ExplodingKittenResolver(
                discardPile
        );
        return new TurnEngine(
                checkedState,
                discardPile,
                resolver,
                Map.of(
                        CardType.ATTACK, new AttackEffect(),
                        CardType.SKIP, new SkipEffect()
                )
        );
    }
}
