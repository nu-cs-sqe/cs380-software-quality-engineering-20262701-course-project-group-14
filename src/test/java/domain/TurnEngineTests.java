package domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TurnEngineTests {

    @Test
    public void Constructor_WithValidDependencies_StartsWithActionsAllowed() {
        GameState state = new GameState(
                List.of(new Player("Player 1")),
                new Deck()
        );
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile);

        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                resolver,
                Map.of()
        );

        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
    }

    @Test
    public void Constructor_WithNullState_ThrowsNullPointerException() {
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new TurnEngine(
                        null,
                        discardPile,
                        resolver,
                        Map.of()
                )
        );

        assertEquals(
                "Game state must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Constructor_WithNullDiscardPile_ThrowsNullPointerException() {
        GameState state = new GameState(
                List.of(new Player("Player 1")),
                new Deck()
        );
        DiscardPile resolverPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(resolverPile);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new TurnEngine(
                        state,
                        null,
                        resolver,
                        Map.of()
                )
        );

        assertEquals(
                "Discard pile must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Constructor_WithNullResolver_ThrowsNullPointerException() {
        GameState state = new GameState(
                List.of(new Player("Player 1")),
                new Deck()
        );
        DiscardPile discardPile = new DiscardPile();

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new TurnEngine(
                        state,
                        discardPile,
                        null,
                        Map.of()
                )
        );

        assertEquals(
                "Exploding Kitten resolver must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Constructor_WithNullEffects_ThrowsNullPointerException() {
        GameState state = new GameState(
                List.of(new Player("Player 1")),
                new Deck()
        );
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new TurnEngine(
                        state,
                        discardPile,
                        resolver,
                        null
                )
        );

        assertEquals(
                "Card effects must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Constructor_WithNullEffectType_ThrowsNullPointerException() {
        GameState state = new GameState(
                List.of(new Player("Player 1")),
                new Deck()
        );
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile);
        Map<CardType, CardEffect> effects = new HashMap<>();
        effects.put(null, new StubEffect());

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new TurnEngine(
                        state,
                        discardPile,
                        resolver,
                        effects
                )
        );

        assertEquals(
                "Card effect type must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Constructor_WithNullEffect_ThrowsNullPointerException() {
        GameState state = new GameState(
                List.of(new Player("Player 1")),
                new Deck()
        );
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile);
        Map<CardType, CardEffect> effects = new HashMap<>();
        effects.put(CardType.SKIP, null);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new TurnEngine(
                        state,
                        discardPile,
                        resolver,
                        effects
                )
        );

        assertEquals(
                "Card effect must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void ResolveDefuse_WithNoPendingKitten_ReturnsNotAwaiting() {
        Player player = new Player("Player 1");
        Deck deck = new Deck();
        GameState state = new GameState(List.of(player), deck);
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile);
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                resolver,
                Map.of()
        );
        int originalDeckSize = deck.size();

        DefuseResult result = engine.resolveDefuse(0);

        assertEquals(DefuseResult.NOT_AWAITING_DEFUSE, result);
        assertEquals(originalDeckSize, deck.size());
        assertEquals(player, state.getCurrentPlayer());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
    }

    @Test
    public void Draw_WithEmptyDeck_ReturnsEmptyDeckWithoutMutation() {
        Player player = new Player("Player 1");
        Deck deck = createEmptyDeck();
        GameState state = new GameState(List.of(player), deck);
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of()
        );

        DrawResult result = engine.draw();

        assertEquals(DrawStatus.EMPTY_DECK, result.getStatus());
        assertEquals(0, deck.size());
        assertEquals(0, player.getHand().size());
        assertEquals(player, state.getCurrentPlayer());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
    }

    private static Deck createEmptyDeck() {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        return deck;
    }

    private static final class StubEffect implements CardEffect {
        @Override
        public boolean canPlay(Player player, Deck deck) {
            return true;
        }

        @Override
        public EffectOutcome play(Player player, Deck deck) {
            return new EffectOutcome(false, List.of());
        }
    }
}
