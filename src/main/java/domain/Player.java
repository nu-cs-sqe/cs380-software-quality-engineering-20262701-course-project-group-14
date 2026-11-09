package domain;

import java.util.ArrayList;
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
        return hand;
    }

    public void addCard(Card card) {
        Objects.requireNonNull(card, "Card must not be null");
        hand.add(card);
    }

    public boolean hasCard(CardType type) {
        return false;
    }

    public Optional<Card> removeOne(CardType type) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Card> removeAllCards() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void eliminate() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isEliminated() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}