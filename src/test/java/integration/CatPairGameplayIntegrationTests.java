package integration;

import domain.Card;
import domain.CardType;
import domain.Deck;
import domain.GameState;
import domain.PlayCommand;
import domain.PlayFailure;
import domain.PlayResult;
import domain.PlayStatus;
import domain.Player;
import domain.TurnEngine;
import domain.TurnEngineFactory;
import domain.TurnPhase;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CatPairGameplayIntegrationTests {

    @ParameterizedTest
    @EnumSource(value = CardType.class, names = {
            "TACOCAT", "CATTERMELON", "HAIRY_POTATO_CAT", "BEARD_CAT", "RAINBOW_RALPHING_CAT"
    })
    void FactoryGame_WithMatchingPair_TransfersSelectedCardAndContinuesTurn(CardType cat) {
        Player current = new Player("Player 1");
        Player target = new Player("Player 2");
        Card held = new Card(CardType.SKIP);
        Card leftWithTarget = new Card(CardType.ATTACK);
        Card transferred = new Card(CardType.FAVOR);
        current.addCard(new Card(cat));
        current.addCard(new Card(cat));
        current.addCard(held);
        target.addCard(leftWithTarget);
        target.addCard(transferred);
        Deck deck = fixedDeck();
        GameState state = new GameState(List.of(current, target), deck);
        TurnEngine engine = TurnEngineFactory.create(state, bound -> {
            assertEquals(2, bound);
            return 1;
        });

        PlayResult result = engine.playCard(PlayCommand.pair(0, 1, 1));

        assertEquals(PlayStatus.CONTINUE_TURN, result.getStatus());
        assertEquals(PlayFailure.NONE, result.getFailure());
        assertEquals(List.of(held, transferred), current.getHand());
        assertEquals(List.of(leftWithTarget), target.getHand());
        assertEquals(2, engine.getSnapshot().getDiscardPileSize());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
        assertSame(current, state.getCurrentPlayer());
        assertEquals(2, deck.size());
        assertEquals(CardType.SHUFFLE, engine.draw().getDrawnCardType().orElseThrow());
        assertSame(target, state.getCurrentPlayer());
        assertEquals(CardType.NOPE, deck.drawCard().getType());
    }

    private static Deck fixedDeck() {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        deck.addCards(List.of(new Card(CardType.SHUFFLE), new Card(CardType.NOPE)));
        return deck;
    }
}
