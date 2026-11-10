package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiscardPile {

    private final List<Card> cards = new ArrayList<>();

    public DiscardPile() {
    }

    public void discard(Card card) {
        Objects.requireNonNull(card, "Card must not be null");
        cards.add(card);
    }

    public void discardAll(List<Card> newCards) {
        Objects.requireNonNull(newCards, "Cards must not be null");
        for (Card card : newCards) {
            Objects.requireNonNull(card, "Card must not be null");
        }
        cards.addAll(newCards);
    }

    public int size() {
        return cards.size();
    }
}