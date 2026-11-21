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
import domain.PlayFailure;
import domain.PlayResult;
import domain.PlayStatus;
import domain.SkipEffect;
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
    @Test
    public void F2_02_PlaySkip_DiscardsCardAndAdvancesWithoutDrawing() {
        Player firstPlayer = new Player("Player 1");
        Player secondPlayer = new Player("Player 2");
        List<Player> players = List.of(firstPlayer, secondPlayer);
        Card skip = new Card(CardType.SKIP);
        firstPlayer.addCard(skip);
        secondPlayer.addCard(new Card(CardType.SKIP));

        List<CardType> remainingHand = List.of(
                CardType.DEFUSE,
                CardType.ATTACK,
                CardType.FAVOR,
                CardType.SHUFFLE,
                CardType.SEE_THE_FUTURE,
                CardType.NOPE,
                CardType.TACOCAT
        );
        for (Player player : players) {
            for (CardType type : remainingHand) {
                player.addCard(new Card(type));
            }
        }

        List<Card> firstHandBefore = List.copyOf(firstPlayer.getHand());
        List<Card> secondHandBefore = List.copyOf(secondPlayer.getHand());
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        Card attack = new Card(CardType.ATTACK);
        Card shuffle = new Card(CardType.SHUFFLE);
        deck.addCards(List.of(attack, shuffle));

        GameState state = new GameState(players, deck);
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of(CardType.SKIP, new SkipEffect())
        );

        PlayResult result = engine.playCard(0);

        assertEquals(PlayStatus.TURN_ENDED, result.getStatus());
        assertEquals(PlayFailure.NONE, result.getFailure());
        assertEquals(List.of(), result.getRevealedCards());
        assertEquals(7, firstPlayer.getHand().size());
        assertEquals(firstHandBefore.subList(1, 8), firstPlayer.getHand());
        assertEquals(secondHandBefore, secondPlayer.getHand());
        assertEquals(1, discardPile.size());
        assertEquals(2, deck.size());
        assertSame(secondPlayer, state.getCurrentPlayer());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());

        // Inspect the remaining order after checking the action's postconditions.
        assertSame(attack, deck.drawCard());
        assertSame(shuffle, deck.drawCard());
    }

    @Test
    public void F2_03_DrawTwice_ReturnsToFirstPlayer() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        first.addCard(new Card(CardType.DEFUSE));
        second.addCard(new Card(CardType.DEFUSE));
        Card attack = new Card(CardType.ATTACK);
        Card shuffle = new Card(CardType.SHUFFLE);
        Deck deck = fixedDeck(List.of(attack, shuffle));
        GameState state = new GameState(List.of(first, second), deck);
        DiscardPile discard = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state, discard, new ExplodingKittenResolver(discard), Map.of());

        DrawResult firstResult = engine.draw();

        assertEquals(DrawStatus.NORMAL_CARD_DRAWN, firstResult.getStatus());
        assertEquals(Optional.of(CardType.ATTACK), firstResult.getDrawnCardType());
        assertSame(second, state.getCurrentPlayer());
        assertEquals("Player 2", engine.getSnapshot().getCurrentPlayerName());
        assertEquals(2, first.getHand().size());
        assertEquals(1, second.getHand().size());
        assertEquals(1, deck.size());

        DrawResult secondResult = engine.draw();

        assertEquals(DrawStatus.NORMAL_CARD_DRAWN, secondResult.getStatus());
        assertEquals(Optional.of(CardType.SHUFFLE), secondResult.getDrawnCardType());
        assertSame(first, state.getCurrentPlayer());
        assertEquals("Player 1", engine.getSnapshot().getCurrentPlayerName());
        assertEquals(2, first.getHand().size());
        assertEquals(2, second.getHand().size());
        assertSame(attack, first.getHand().get(1));
        assertSame(shuffle, second.getHand().get(1));
        assertEquals(0, deck.size());
        assertEquals(0, discard.size());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
    }

    private Deck fixedDeck(List<Card> cards) {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        deck.addCards(cards);
        return deck;
    }

    @Test
    public void F2_04_DrawFromEmptyDeck_PreservesIntegratedState() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        first.addCard(new Card(CardType.DEFUSE));
        second.addCard(new Card(CardType.DEFUSE));
        List<Card> firstHand = List.copyOf(first.getHand());
        List<Card> secondHand = List.copyOf(second.getHand());
        Deck deck = fixedDeck(List.of());
        GameState state = new GameState(List.of(first, second), deck);
        DiscardPile discard = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state, discard, new ExplodingKittenResolver(discard), Map.of());

        DrawResult result = engine.draw();

        assertEquals(DrawStatus.EMPTY_DECK, result.getStatus());
        assertEquals(Optional.empty(), result.getDrawnCardType());
        assertEquals(firstHand, first.getHand());
        assertEquals(secondHand, second.getHand());
        assertSame(first, state.getCurrentPlayer());
        assertEquals(0, deck.size());
        assertEquals(0, discard.size());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
        assertEquals("Player 1", engine.getSnapshot().getCurrentPlayerName());
        assertEquals(List.of(CardType.DEFUSE),
                engine.getSnapshot().getCurrentPlayerHand());
        assertEquals(2, engine.getSnapshot().getAlivePlayerCount());
        assertEquals(0, engine.getSnapshot().getDeckSize());
        assertEquals(0, engine.getSnapshot().getDiscardPileSize());
    }
}