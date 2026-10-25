package domain;

import java.util.List;

public class Deck {

    // Creates the complete original 56-card deck without shuffling
    public Deck() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Removes and returns the requested cards.
    public List<Card> removeCards(CardType type, int count) {
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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    //Returns the number of cards of the specified type.
    public int countOf(CardType type) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}