package domain;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public final class TurnEngineFactory {
    private TurnEngineFactory() {
    }

    public static TurnActions create(GameState state) {
        return create(state, new Random()::nextInt);
    }

    public static TurnEngine create(GameState state, RandomSource random) {
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
                ),
                random
        );
    }
}
