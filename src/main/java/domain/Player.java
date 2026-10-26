package domain;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String name;
    private final List<Card> hand = new ArrayList<>();

    public Player(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Player name must not be empty");
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
        throw new UnsupportedOperationException("Not implemented yet");
    }
}