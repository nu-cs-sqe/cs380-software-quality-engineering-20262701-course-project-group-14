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
        Card topCard = deck.drawCard();
        deck.insertAt(topCard, 0);
        return new EffectOutcome(false, List.of(topCard.getType()));
    }
}
