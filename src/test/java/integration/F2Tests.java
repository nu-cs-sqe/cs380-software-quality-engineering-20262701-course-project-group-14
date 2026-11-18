package integration;

import domain.Card;
import domain.CardType;
import domain.Deck;
import domain.DiscardPile;
import domain.DrawResult;
import domain.DrawStatus;
import domain.ExplodingKittenResolver;
import domain.GameState;
import domain.Player;
import domain.TurnEngine;
import domain.TurnPhase;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class F2Tests {

    @Test
    public void F2_01_DrawNormalCard_AddsCardAndAdvancesToNextPlayer() {
        Player firstPlayer = new Player("Player 1");
        Player secondPlayer = new Player("Player 2");
        List<Player> players = List.of(firstPlayer, secondPlayer);

        List<CardType> startingHand = List.of(
                CardType.DEFUSE,
                CardType.ATTACK,
                CardType.SKIP,
                CardType.FAVOR,
                CardType.SHUFFLE,
                CardType.SEE_THE_FUTURE,
                CardType.NOPE,
                CardType.TACOCAT
        );

        for (Player player : players) {
            for (CardType type : startingHand) {
                player.addCard(new Card(type));
            }
        }

        List<Card> firstHandBefore = List.copyOf(firstPlayer.getHand());
        List<Card> secondHandBefore = List.copyOf(secondPlayer.getHand());

        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }

        Card drawnCard = new Card(CardType.SKIP);
        deck.addCards(List.of(drawnCard, new Card(CardType.SHUFFLE)));

        GameState state = new GameState(players, deck);
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile);
        TurnEngine engine = new TurnEngine(
                state, discardPile, resolver, Map.of()
        );

        DrawResult result = engine.draw();

        assertEquals(DrawStatus.NORMAL_CARD_DRAWN, result.getStatus());
        assertEquals(Optional.of(CardType.SKIP), result.getDrawnCardType());
        assertEquals(9, firstPlayer.getHand().size());
        assertEquals(firstHandBefore, firstPlayer.getHand().subList(0, 8));
        assertSame(drawnCard, firstPlayer.getHand().get(8));
        assertEquals(secondHandBefore, secondPlayer.getHand());
        assertEquals(1, deck.size());
        assertEquals(1, deck.countOf(CardType.SHUFFLE));
        assertEquals(0, discardPile.size());
        assertSame(secondPlayer, state.getCurrentPlayer());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
    }
}