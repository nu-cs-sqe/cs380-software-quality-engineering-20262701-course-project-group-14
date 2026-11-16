package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Objects;
import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private final Random random;
    // Creates the complete original 56-card deck without shuffling
    public Deck() {
        this(new Random());
    }

    Deck(Random random) {
        this(random, List.of());

        for (CardType type : CardType.values()) {
            int count = 4;

            if (type == CardType.DEFUSE) {
                count = 6;
            } else if (type == CardType.SEE_THE_FUTURE
                    || type == CardType.NOPE) {
                count = 5;
            }

            for (int i = 0; i < count; i++) {
                cards.add(new Card(type));
            }
        }
    }

    static Deck fromTopToBottom(List<Card> cards) {
        return new Deck(new Random(), cards);
    }

    private Deck(Random random, List<Card> initialCards) {
        this.random = Objects.requireNonNull(random, "Random must not be null");
        validateCards(initialCards);
        cards.addAll(initialCards);
    }

    // Removes and returns the requested cards.
    public List<Card> removeCards(CardType type, int count) {
        Objects.requireNonNull(type, "Card type must not be null");
        validateRemovalCount(type, count);

        List<Card> removed = new ArrayList<>();
        Iterator<Card> iterator = cards.iterator();

        while (iterator.hasNext() && removed.size() < count) {
            Card card = iterator.next();

            if (card.getType() == type) {
                removed.add(card);
                iterator.remove();
            }
        }

        return removed;
    }
    private void validateRemovalCount(CardType type, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must not be negative");
        }
        if (count > countOf(type)) {
            throw new IllegalArgumentException("Not enough cards of the requested type");
        }
    }

    // Adds the supplied cards without shuffling.
    public void addCards(List<Card> cards) {
        validateCards(cards);
        this.cards.addAll(cards);
    }

    private static void validateCards(List<Card> cards) {
        Objects.requireNonNull(cards, "Cards must not be null");

        for (Card card : cards) {
            Objects.requireNonNull(card, "Card must not be null");
        }
    }

    public void insertAt(Card card, int position) {
        Objects.requireNonNull(card, "Card must not be null");
        if (position < 0 || position > cards.size()) {
            throw new IllegalArgumentException(
                    "Position must be between 0 and the deck size");
        }
        cards.add(position, card);
    }

    // Removes and returns the top card.
    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new NoSuchElementException("Cannot draw from an empty deck");
        }

        return cards.remove(0);
    }

    // Shuffles the deck without changing its contents.
    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    //Returns the current number of cards.
    public int size() {
        return cards.size();
    }

    //Returns the number of cards of the specified type.
    public int countOf(CardType type) {
        Objects.requireNonNull(type, "Card type must not be null");
        int count = 0;
        for (Card card : cards) {
            if (card.getType() == type) {
                count++;
            }
        }
        return count;
    }
}