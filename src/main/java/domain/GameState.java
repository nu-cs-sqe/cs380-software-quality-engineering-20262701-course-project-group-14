package domain;

import java.util.List;

public class GameState {

    private final List<Player> players;
    private final Deck deck;

    public GameState(List<Player> players, Deck deck) {
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