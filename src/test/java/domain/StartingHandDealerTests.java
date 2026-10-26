package domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StartingHandDealerTests {

    // SHD-01
    @Test
    public void Deal_ToTwoPlayers_EachHandHasEightCardsEndingWithDefuse() {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        List<Player> players = List.of(player1, player2);
        Deck deck = new Deck();
        deck.removeCards(CardType.EXPLODING_KITTEN, 4);
        List<Card> reservedDefuses = deck.removeCards(CardType.DEFUSE, 4);
        List<Card> playerDefuses = new ArrayList<>(reservedDefuses.subList(0, 2));
        StartingHandDealer dealer = new StartingHandDealer();

        dealer.deal(players, deck, playerDefuses);

        for (Player player : players) {
            assertEquals(8, player.getHand().size());
            assertEquals(CardType.DEFUSE, player.getHand().get(7).getType());
        }
        assertEquals(34, deck.size());
        assertTrue(playerDefuses.isEmpty());
    }

    // SHD-02
    @Test
    public void Deal_ToFivePlayers_EachHandHasEightCardsEndingWithDefuse() {
        List<Player> players = List.of(
                new Player("Player 1"),
                new Player("Player 2"),
                new Player("Player 3"),
                new Player("Player 4"),
                new Player("Player 5"));
        Deck deck = new Deck();
        deck.removeCards(CardType.EXPLODING_KITTEN, 4);
        List<Card> playerDefuses = new ArrayList<>(deck.removeCards(CardType.DEFUSE, 5));
        StartingHandDealer dealer = new StartingHandDealer();

        dealer.deal(players, deck, playerDefuses);

        for (Player player : players) {
            assertEquals(8, player.getHand().size());
            assertEquals(CardType.DEFUSE, player.getHand().get(7).getType());
        }
        assertEquals(12, deck.size());
        assertTrue(playerDefuses.isEmpty());
    }

    // SHD-03
    @Test
    public void Deal_WithDeckOneCardShort_ThrowsAndChangesNothing() {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        List<Player> players = List.of(player1, player2);
        Deck deck = new Deck();
        while (deck.size() > 13) {
            deck.drawCard();
        }
        List<Card> playerDefuses = new ArrayList<>(
                List.of(new Card(CardType.DEFUSE), new Card(CardType.DEFUSE)));
        StartingHandDealer dealer = new StartingHandDealer();

        assertThrows(IllegalArgumentException.class,
                () -> dealer.deal(players, deck, playerDefuses));

        assertTrue(player1.getHand().isEmpty());
        assertTrue(player2.getHand().isEmpty());
        assertEquals(13, deck.size());
        assertEquals(2, playerDefuses.size());
    }
}