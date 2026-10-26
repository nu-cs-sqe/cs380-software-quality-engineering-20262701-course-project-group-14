package domain;

import java.util.List;
import java.util.Objects;

public class GameState {

    private final List<Player> players;
    private final Deck deck;

    public GameState(List<Player> players, Deck deck) {
        Objects.requireNonNull(players, "Players must not be null.");
        if (players.isEmpty()) {
            throw new IllegalArgumentException("Players must not be empty.");
        }
        this.players = players;
        this.deck = deck;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Deck getDeck() {
        return deck;
    }

    public Player getCurrentPlayer() {
        return players.get(0);
    }
}