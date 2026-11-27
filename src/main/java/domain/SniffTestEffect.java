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
        Objects.requireNonNull(player, "Player must not be null.");
        Objects.requireNonNull(deck, "Deck must not be null.");
        if (deck.size() == 0) {
            throw new IllegalStateException("Sniff Test requires a non-empty deck.");
        }
        Card topCard = deck.drawCard();
        int position = topCard.getType() == CardType.EXPLODING_KITTEN
                ? deck.size()
                : 0;
        deck.insertAt(topCard, position);
        return new EffectOutcome(false, List.of(topCard.getType()));
    }
}
