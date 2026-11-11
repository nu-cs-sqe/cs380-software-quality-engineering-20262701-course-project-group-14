package domain;

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
        throw new UnsupportedOperationException(
                "Skip effect is not implemented."
        );
    }
}
