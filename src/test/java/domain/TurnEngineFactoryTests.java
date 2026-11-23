package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TurnEngineFactoryTests {

    @Test
    public void Create_WithAttack_RegistersEffectAndAssignsTurnDebt() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        first.addCard(new Card(CardType.ATTACK));
        Deck deck = emptyDeck();
        deck.addCards(List.of(
                new Card(CardType.SKIP),
                new Card(CardType.FAVOR)
        ));
        GameState state = new GameState(List.of(first, second), deck);
        TurnActions actions = TurnEngineFactory.create(state);

        PlayResult playResult = actions.playCard(0);

        assertEquals(PlayStatus.TURN_ENDED, playResult.getStatus());
        assertEquals(0, first.getHand().size());
        assertSame(second, state.getCurrentPlayer());
        assertEquals(1, actions.getSnapshot().getDiscardPileSize());

        DrawResult drawResult = actions.draw();

        assertEquals(DrawStatus.NORMAL_CARD_DRAWN, drawResult.getStatus());
        assertSame(second, state.getCurrentPlayer());
    }

    private static Deck emptyDeck() {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        return deck;
    }
}
