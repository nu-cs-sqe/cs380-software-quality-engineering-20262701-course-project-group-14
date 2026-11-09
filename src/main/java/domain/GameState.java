package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameState {

    private final List<Player> players;
    private final Deck deck;
    private int currentPlayerIndex;

    public GameState(List<Player> players, Deck deck) {
        Objects.requireNonNull(players, "Players must not be null.");
        if (players.isEmpty()) {
            throw new IllegalArgumentException("Players must not be empty.");
        }
        Objects.requireNonNull(deck, "Deck must not be null.");
        this.players = new ArrayList<>(players);
        this.deck = deck;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Deck getDeck() {
        return deck;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public AdvanceResult advanceCurrentPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (getCurrentPlayer().isEliminated());
        return AdvanceResult.ADVANCED;
    }
}