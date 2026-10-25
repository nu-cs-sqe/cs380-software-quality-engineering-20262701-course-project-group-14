package domain;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    // Creates the complete original 56-card deck without shuffling
    public Deck() {
        for (CardType type : CardType.values()) {
            int count = 4;

            if (type == CardType.DEFUSE) {
                count = 6;
            } else if (type == CardType.SEE_THE_FUTURE || type == CardType.NOPE) {
                count = 5;
            }

            for (int i = 0; i < count; i++) {
                cards.add(new Card(type));
            }
        }
    }

    // Removes and returns the requested cards.
    public List<Card> removeCards(CardType type, int count) {
        if (count == 0) {
            return new ArrayList<>();
        }

        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Adds the supplied cards without shuffling.
    public void addCards(List<Card> cards) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Removes and returns the top card.
    public Card drawCard() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Shuffles the deck without changing its contents.
    public void shuffle() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    //Returns the current number of cards.
    public int size() {
        return cards.size();
    }

    //Returns the number of cards of the specified type.
    public int countOf(CardType type) {
        int count = 0;

        for (Card card : cards) {
            if (card.getType() == type) {
                count++;
            }
        }
        return count;
    }
}