package domain;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SniffTestEffectTests {

    @Test
    public void CanPlay_WithNonEmptyDeck_ReturnsTrueWithoutMutation() {
        Card skip = new Card(CardType.SKIP);
        Deck deck = Deck.fromTopToBottom(List.of(skip));
        Player player = playerHolding(new Card(CardType.SNIFF_TEST));
        List<Card> hand = List.copyOf(player.getHand());

        boolean canPlay = new SniffTestEffect().canPlay(player, deck);

        assertTrue(canPlay);
        assertEquals(hand, player.getHand());
        assertEquals(List.of(skip), drain(deck));
    }

    @Test
    public void CanPlay_WithEmptyDeck_ReturnsFalseWithoutMutation() {
        Deck deck = Deck.fromTopToBottom(List.of());
        Player player = playerHolding(new Card(CardType.SNIFF_TEST));
        List<Card> hand = List.copyOf(player.getHand());

        boolean canPlay = new SniffTestEffect().canPlay(player, deck);

        assertFalse(canPlay);
        assertEquals(hand, player.getHand());
        assertEquals(0, deck.size());
    }

    @Test
    public void CanPlay_WithNullPlayer_ThrowsWithoutChangingDeck() {
        Card skip = new Card(CardType.SKIP);
        Deck deck = Deck.fromTopToBottom(List.of(skip));

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SniffTestEffect().canPlay(null, deck)
        );

        assertEquals("Player must not be null.", exception.getMessage());
        assertEquals(List.of(skip), drain(deck));
    }

    @Test
    public void CanPlay_WithNullDeck_ThrowsWithoutChangingPlayer() {
        Player player = playerHolding(new Card(CardType.SNIFF_TEST));
        List<Card> hand = List.copyOf(player.getHand());

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SniffTestEffect().canPlay(player, null)
        );

        assertEquals("Deck must not be null.", exception.getMessage());
        assertEquals(hand, player.getHand());
    }

    @Test
    public void Play_WithSafeTopCard_RevealsItAndKeepsDeckOrder() {
        Card skip = new Card(CardType.SKIP);
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        Deck deck = Deck.fromTopToBottom(List.of(skip, kitten));
        Player player = playerHolding(new Card(CardType.ATTACK));
        List<Card> hand = List.copyOf(player.getHand());

        EffectOutcome outcome = new SniffTestEffect().play(player, deck);

        assertFalse(outcome.endsTurn());
        assertEquals(List.of(CardType.SKIP), outcome.getRevealedCards());
        assertEquals(hand, player.getHand());
        assertEquals(List.of(skip, kitten), drain(deck));
    }

    private static Player playerHolding(Card... cards) {
        Player player = new Player("Player 1");
        for (Card card : cards) {
            player.addCard(card);
        }
        return player;
    }

    private static List<Card> drain(Deck deck) {
        List<Card> cards = new ArrayList<>();
        while (deck.size() > 0) {
            cards.add(deck.drawCard());
        }
        return cards;
    }
}
