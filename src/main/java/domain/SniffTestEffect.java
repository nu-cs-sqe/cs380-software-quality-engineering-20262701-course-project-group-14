package domain;

import java.util.List;

public final class SniffTestEffect implements CardEffect {

    @Override
    public boolean canPlay(Player player, Deck deck) {
        return deck.size() > 0;
    }

    @Override
    public EffectOutcome play(Player player, Deck deck) {
        return new EffectOutcome(false, List.of());
    }
}
