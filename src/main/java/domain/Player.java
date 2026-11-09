package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Player {

    private final String name;
    private final List<Card> hand = new ArrayList<>();

    public Player(String name) {
        Objects.requireNonNull(name, "Player name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Player name must not be blank");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public void addCard(Card card) {
        Objects.requireNonNull(card, "Card must not be null");
        hand.add(card);
    }

    public boolean hasCard(CardType type) {
        Objects.requireNonNull(type, "Card type must not be null");
        for (Card card : hand) {
            if (card.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public Optional<Card> removeOne(CardType type) {
        Objects.requireNonNull(type, "Card type must not be null");
        for (int index = 0; index < hand.size(); index++) {
            if (hand.get(index).getType() == type) {
                return Optional.of(hand.remove(index));
            }
        }
        return Optional.empty();
    }

    public List<Card> removeAllCards() {
        List<Card> removedCards = new ArrayList<>(hand);
        hand.clear();
        return removedCards;
    }

    public void eliminate() {
    }

    public boolean isEliminated() {
        return true;
    }
}