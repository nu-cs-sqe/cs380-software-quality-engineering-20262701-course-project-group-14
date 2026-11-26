package domain;

import java.util.List;
import java.util.Objects;

public final class SniffTestEffect implements CardEffect {

    @Override
    public boolean canPlay(Player player, Deck deck) {
        Objects.requireNonNull(player, "Player must not be null.");
        Objects.requireNonNull(deck, "Deck must not be null.");
        return deck.size() > 0;
    }

    @Override
    public EffectOutcome play(Player player, Deck deck) {
        return new EffectOutcome(false, List.of());
    }
}
