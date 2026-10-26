package domain;

import java.util.List;

public class StartingHandDealer {

    private static final int CARDS_PER_PLAYER = 7;

    public void deal(List<Player> players, Deck deck, List<Card> playerDefuses) {
        if (deck.size() < 7 * players.size()) {
            throw new IllegalArgumentException(
                    "Not enough cards in the deck to deal starting hands.");
        }
        for (int round = 0; round < 7; round++) {
            for (Player player : players) {
                player.addCard(deck.drawCard());
            }
        }
        for (Player player : players) {
            player.addCard(playerDefuses.remove(0));
        }
    }
}