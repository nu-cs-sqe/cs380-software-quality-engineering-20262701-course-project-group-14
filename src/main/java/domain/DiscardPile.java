package domain;

import java.util.ArrayList;
import java.util.List;

public class DiscardPile {

    private final List<Card> cards = new ArrayList<>();

    public DiscardPile() {
    }

    public void discard(Card card) {
        cards.add(card);
    }

    public void discardAll(List<Card> cards) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int size() {
        return cards.size();
    }
}