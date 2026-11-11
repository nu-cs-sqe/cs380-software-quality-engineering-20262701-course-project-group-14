package domain;

import java.util.List;
import java.util.Objects;

public final class SkipEffect implements CardEffect {

    @Override
    public boolean canPlay(Player player, Deck deck) {
        Objects.requireNonNull(player, "Player must not be null.");
        Objects.requireNonNull(deck, "Deck must not be null.");
        return true;
    }

    @Override
    public EffectOutcome play(Player player, Deck deck) {
        Objects.requireNonNull(player, "Player must not be null.");
        Objects.requireNonNull(deck, "Deck must not be null.");
        return new EffectOutcome(true, List.of());
    }
}
