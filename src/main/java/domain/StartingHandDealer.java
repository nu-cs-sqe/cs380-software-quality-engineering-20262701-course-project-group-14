package domain;

import java.util.List;

public class StartingHandDealer {

    public void deal(List<Player> players, Deck deck, List<Card> playerDefuses) {
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