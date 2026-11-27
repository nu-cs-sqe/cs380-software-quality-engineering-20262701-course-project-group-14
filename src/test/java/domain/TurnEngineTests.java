package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TurnEngineTests {

    @Test
    public void GetSnapshot_WithEliminatedSeat_PreservesSeatOrderAndCounts() {
        Player first = new Player("First");
        Player eliminated = new Player("Eliminated");
        eliminated.eliminate();
        Player current = new Player("Current");
        current.addCard(new Card(CardType.TACOCAT));
        GameState state = new GameState(List.of(first, eliminated, current), new Deck());
        state.advanceCurrentPlayer();
        TurnActions engine = TurnEngineFactory.create(state);

        GameSnapshot snapshot = engine.getSnapshot();

        List<PlayerSummary> players = snapshot.getPlayers();
        assertEquals(3, players.size());
        assertEquals(0, players.get(0).getPlayerIndex());
        assertEquals(1, players.get(1).getPlayerIndex());
        assertEquals(2, players.get(2).getPlayerIndex());
        assertTrue(players.get(1).isEliminated());
        assertEquals("Current", players.get(2).getName());
        assertEquals(2, snapshot.getCurrentPlayerIndex());
        assertThrows(UnsupportedOperationException.class, () -> players.remove(0));
        current.addCard(new Card(CardType.SKIP));
        assertEquals(1, players.get(2).getHandSize());
        assertEquals(2, engine.getSnapshot().getPlayers().get(2).getHandSize());
    }

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

    @Test
    public void PlayCard_WithNegativeIndex_ReturnsInvalidIndex() {
        Player player = new Player("Player 1");
        player.addCard(new Card(CardType.SKIP));
        Deck deck = new Deck();
        GameState state = new GameState(List.of(player), deck);
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of(CardType.SKIP, new StubEffect())
        );
        int originalDeckSize = deck.size();

        PlayResult result = engine.playCard(-1);

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(PlayFailure.INVALID_HAND_INDEX, result.getFailure());
        assertEquals(1, player.getHand().size());
        assertEquals(originalDeckSize, deck.size());
        assertEquals(player, state.getCurrentPlayer());
    }

    @Test
    public void PlayCard_WithIndexAtHandSize_ReturnsInvalidIndex() {
        Player player = new Player("Player 1");
        player.addCard(new Card(CardType.SKIP));
        Deck deck = new Deck();
        GameState state = new GameState(List.of(player), deck);
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of(CardType.SKIP, new StubEffect())
        );

        PlayResult result = engine.playCard(1);

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(PlayFailure.INVALID_HAND_INDEX, result.getFailure());
        assertEquals(1, player.getHand().size());
        assertEquals(player, state.getCurrentPlayer());
    }

    @Test
    public void PlayCard_WithoutRegisteredEffect_ReturnsNotPlayable() {
        Player player = new Player("Player 1");
        player.addCard(new Card(CardType.ATTACK));
        Deck deck = new Deck();
        GameState state = new GameState(List.of(player), deck);
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of()
        );

        PlayResult result = engine.playCard(0);

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(PlayFailure.CARD_NOT_PLAYABLE, result.getFailure());
        assertEquals(1, player.getHand().size());
        assertEquals(player, state.getCurrentPlayer());
    }

    @Test
    public void PlayCard_WhenEffectCannotPlay_ReturnsPreconditionFailure() {
        Player player = new Player("Player 1");
        player.addCard(new Card(CardType.SKIP));
        Deck deck = new Deck();
        GameState state = new GameState(List.of(player), deck);
        DiscardPile discardPile = new DiscardPile();
        StubEffect effect = new StubEffect();
        effect.canPlay = false;
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of(CardType.SKIP, effect)
        );

        PlayResult result = engine.playCard(0);

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(PlayFailure.PRECONDITION_NOT_MET, result.getFailure());
        assertEquals(1, effect.canPlayCalls);
        assertEquals(0, effect.playCalls);
        assertEquals(1, player.getHand().size());
        assertEquals(player, state.getCurrentPlayer());
    }

    @Test
    public void Constructor_CopiesMutableEffectsMap() {
        Player player = new Player("Player 1");
        player.addCard(new Card(CardType.SKIP));
        GameState state = new GameState(List.of(player), new Deck());
        DiscardPile discardPile = new DiscardPile();
        StubEffect originalEffect = new StubEffect();
        originalEffect.canPlay = false;
        Map<CardType, CardEffect> sourceEffects = new HashMap<>();
        sourceEffects.put(CardType.SKIP, originalEffect);
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                sourceEffects
        );

        sourceEffects.clear();
        PlayResult result = engine.playCard(0);

        assertEquals(PlayFailure.PRECONDITION_NOT_MET, result.getFailure());
        assertEquals(1, originalEffect.canPlayCalls);
    }

    @Test
    public void Draw_WhenDefuseIsPending_RejectsWithoutDrawing() {
        Player firstPlayer = new Player("Player 1");
        firstPlayer.addCard(new Card(CardType.DEFUSE));
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(new Card(CardType.EXPLODING_KITTEN), new Card(CardType.SKIP)));
        GameState state = new GameState(List.of(firstPlayer), deck);
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(state, discardPile, waitingResolver(discardPile), Map.of());
        assertEquals(DrawStatus.DEFUSE_POSITION_REQUIRED, engine.draw().getStatus());
        List<Card> handBefore = List.copyOf(firstPlayer.getHand());

        DrawResult result = engine.draw();

        assertEquals(DrawStatus.REJECTED_PENDING_DEFUSE, result.getStatus());
        assertEquals(1, deck.size());
        assertEquals(1, deck.countOf(CardType.SKIP));
        assertEquals(handBefore, firstPlayer.getHand());
        assertSame(firstPlayer, state.getCurrentPlayer());
        assertEquals(TurnPhase.AWAITING_DEFUSE_POSITION, engine.getPhase());
    }

    private static ExplodingKittenResolver waitingResolver(DiscardPile discardPile) {
        return new ExplodingKittenResolver(discardPile) {
            @Override
            public ExplosionOutcome begin(Player player, Card kitten) {
                return ExplosionOutcome.DEFUSE_POSITION_REQUIRED;
            }
        };
    }
    @Test
    public void PlayCard_WhenDefuseIsPending_RejectsWithoutPlaying() {
        Player firstPlayer = new Player("Player 1");
        Card skip = new Card(CardType.SKIP);
        Card defuse = new Card(CardType.DEFUSE);
        firstPlayer.addCard(skip);
        firstPlayer.addCard(defuse);
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(new Card(CardType.EXPLODING_KITTEN)));
        GameState state = new GameState(List.of(firstPlayer), deck);
        DiscardPile discardPile = new DiscardPile();
        StubEffect effect = new StubEffect();
        TurnEngine engine = new TurnEngine(
                state, discardPile, waitingResolver(discardPile), Map.of(CardType.SKIP, effect));
        assertEquals(DrawStatus.DEFUSE_POSITION_REQUIRED, engine.draw().getStatus());

        PlayResult result = engine.playCard(0);

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(PlayFailure.DEFUSE_RESOLUTION_REQUIRED, result.getFailure());
        assertEquals(List.of(skip, defuse), firstPlayer.getHand());
        assertEquals(0, deck.size());
        assertEquals(0, effect.canPlayCalls);
        assertEquals(0, effect.playCalls);
        assertSame(firstPlayer, state.getCurrentPlayer());
        assertEquals(TurnPhase.AWAITING_DEFUSE_POSITION, engine.getPhase());
    }
    @Test
    public void PlayCard_WithNegativeIndexWhenDefuseIsPending_ReportsDefuseRequired() {
        Player firstPlayer = new Player("Player 1");
        Card skip = new Card(CardType.SKIP);
        Card defuse = new Card(CardType.DEFUSE);
        firstPlayer.addCard(skip);
        firstPlayer.addCard(defuse);
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(new Card(CardType.EXPLODING_KITTEN)));
        GameState state = new GameState(List.of(firstPlayer), deck);
        DiscardPile discardPile = new DiscardPile();
        StubEffect effect = new StubEffect();
        TurnEngine engine = new TurnEngine(
                state, discardPile, waitingResolver(discardPile), Map.of(CardType.SKIP, effect));
        assertEquals(DrawStatus.DEFUSE_POSITION_REQUIRED, engine.draw().getStatus());

        PlayResult result = engine.playCard(-1);

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(PlayFailure.DEFUSE_RESOLUTION_REQUIRED, result.getFailure());
        assertEquals(List.of(skip, defuse), firstPlayer.getHand());
        assertEquals(0, deck.size());
        assertEquals(0, effect.canPlayCalls);
        assertEquals(0, effect.playCalls);
        assertSame(firstPlayer, state.getCurrentPlayer());
        assertEquals(TurnPhase.AWAITING_DEFUSE_POSITION, engine.getPhase());
    }
    @Test
    public void Draw_WhenResolverEliminatesPlayer_AdvancesOnce() {
        Player firstPlayer = new Player("Player 1");
        Player secondPlayer = new Player("Player 2");
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(kitten));
        AdvancingState state = new AdvancingState(firstPlayer, secondPlayer, deck);
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver = new ExplodingKittenResolver(discardPile) {
            @Override
            public ExplosionOutcome begin(Player player, Card drawnKitten) {
                assertSame(firstPlayer, player);
                assertSame(kitten, drawnKitten);
                return ExplosionOutcome.PLAYER_ELIMINATED;
            }
        };
        TurnEngine engine = new TurnEngine(state, discardPile, resolver, Map.of());

        DrawResult result = engine.draw();

        assertEquals(DrawStatus.PLAYER_ELIMINATED, result.getStatus());
        assertEquals(0, deck.size());
        assertEquals(List.of(), firstPlayer.getHand());
        assertSame(secondPlayer, state.getCurrentPlayer());
        assertEquals(1, state.advanceCount);
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
    }

    private static final class AdvancingState extends GameState {
        private Player currentPlayer;
        private final Player secondPlayer;
        private int advanceCount;

        private AdvancingState(Player firstPlayer, Player secondPlayer, Deck deck) {
            super(List.of(firstPlayer, secondPlayer), deck);
            currentPlayer = firstPlayer;
            this.secondPlayer = secondPlayer;
        }

        @Override
        public Player getCurrentPlayer() {
            return currentPlayer;
        }

        @Override
        public AdvanceResult advanceCurrentPlayer() {
            advanceCount++;
            currentPlayer = secondPlayer;
            return AdvanceResult.ADVANCED;
        }
    }
    @Test
    public void PlayCard_WhenEffectContinuesTurn_DiscardsSelectedCardAndKeepsOrder() {
        Player player = new Player("Player 1");
        Card firstCard = new Card(CardType.SEE_THE_FUTURE);
        Card selectedCard = new Card(CardType.SEE_THE_FUTURE);
        player.addCard(firstCard);
        player.addCard(selectedCard);
        Deck deck = createEmptyDeck();
        GameState state = new GameState(List.of(player), deck);
        RecordingDiscardPile discardPile = new RecordingDiscardPile();
        PlayingEffect effect = new PlayingEffect(
                new EffectOutcome(false, List.of(CardType.FAVOR, CardType.SKIP)));
        TurnEngine engine = new TurnEngine(state, discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of(CardType.SEE_THE_FUTURE, effect));

        PlayResult result = engine.playCard(1);

        assertEquals(PlayStatus.CONTINUE_TURN, result.getStatus());
        assertEquals(List.of(CardType.FAVOR, CardType.SKIP), result.getRevealedCards());
        assertEquals(1, player.getHand().size());
        assertSame(firstCard, player.getHand().get(0));
        assertEquals(1, discardPile.discarded.size());
        assertSame(selectedCard, discardPile.discarded.get(0));
        assertEquals(1, effect.playCalls);
        assertSame(player, state.getCurrentPlayer());
    }

    private static final class RecordingDiscardPile extends DiscardPile {
        private final List<Card> discarded = new ArrayList<>();

        @Override
        public void discard(Card card) {
            discarded.add(card);
        }

        @Override
        public int size() {
            return discarded.size();
        }
    }

    private static final class PlayingEffect implements CardEffect {
        private final EffectOutcome outcome;
        private int playCalls;

        private PlayingEffect(EffectOutcome outcome) {
            this.outcome = outcome;
        }

        @Override
        public boolean canPlay(Player player, Deck deck) {
            return true;
        }

        @Override
        public EffectOutcome play(Player player, Deck deck) {
            playCalls++;
            return outcome;
        }
    }

    @Test
    public void PlayCard_WhenEffectEndsTurn_DiscardsAndAdvancesOnce() {
        Player firstPlayer = new Player("Player 1");
        Player secondPlayer = new Player("Player 2");
        Card skip = new Card(CardType.SKIP);
        firstPlayer.addCard(skip);
        Deck deck = createEmptyDeck();
        AdvancingState state = new AdvancingState(firstPlayer, secondPlayer, deck);
        RecordingDiscardPile discardPile = new RecordingDiscardPile();
        PlayingEffect effect = new PlayingEffect(new EffectOutcome(true, List.of()));
        TurnEngine engine = new TurnEngine(state, discardPile,
                new ExplodingKittenResolver(discardPile), Map.of(CardType.SKIP, effect));

        PlayResult result = engine.playCard(0);

        assertEquals(PlayStatus.TURN_ENDED, result.getStatus());
        assertEquals(List.of(), firstPlayer.getHand());
        assertEquals(1, discardPile.discarded.size());
        assertSame(skip, discardPile.discarded.get(0));
        assertEquals(1, effect.playCalls);
        assertSame(secondPlayer, state.getCurrentPlayer());
        assertEquals(1, state.advanceCount);
        assertEquals(0, deck.size());
    }

    @Test
    public void ResolveDefuse_WhenPositionIsInvalid_KeepsDefusePending() {
        DefuseScenario scenario = new DefuseScenario(-1, DefuseResult.INVALID_POSITION);

        DefuseResult result = scenario.engine.resolveDefuse(-1);

        assertEquals(DefuseResult.INVALID_POSITION, result);
        scenario.assertStillPending();
        assertEquals(DefuseResult.INVALID_POSITION, scenario.engine.resolveDefuse(-1));
    }

    private static final class DefuseScenario {
        private final Player firstPlayer = new Player("Player 1");
        private final Player secondPlayer = new Player("Player 2");
        private final Card defuse = new Card(CardType.DEFUSE);
        private final Deck deck = createEmptyDeck();
        private final AdvancingState state = new AdvancingState(firstPlayer, secondPlayer, deck);
        private final RecordingDiscardPile discardPile = new RecordingDiscardPile();
        private final TurnEngine engine;

        private DefuseScenario(int requestedPosition, DefuseResult resolved) {
            Card kitten = new Card(CardType.EXPLODING_KITTEN);
            firstPlayer.addCard(defuse);
            deck.addCards(List.of(kitten));
            ExplodingKittenResolver resolver = new ExplodingKittenResolver(discardPile) {
                @Override
                public ExplosionOutcome begin(Player player, Card drawnKitten) {
                    return ExplosionOutcome.DEFUSE_POSITION_REQUIRED;
                }

                @Override
                public DefuseResult defuse(
                        Player player, Card pending, Deck drawPile, int position) {
                    assertSame(firstPlayer, player);
                    assertSame(kitten, pending);
                    assertSame(deck, drawPile);
                    assertEquals(requestedPosition, position);
                    return resolved;
                }
            };
            engine = new TurnEngine(state, discardPile, resolver, Map.of());
            assertEquals(DrawStatus.DEFUSE_POSITION_REQUIRED, engine.draw().getStatus());
        }

        private void assertStillPending() {
            assertEquals(TurnPhase.AWAITING_DEFUSE_POSITION, engine.getPhase());
            assertSame(firstPlayer, state.getCurrentPlayer());
            assertEquals(0, state.advanceCount);
            assertEquals(List.of(defuse), firstPlayer.getHand());
            assertEquals(0, deck.size());
            assertEquals(List.of(), discardPile.discarded);
        }
    }

    @Test
    public void ResolveDefuse_WhenDefuseIsMissing_KeepsDefusePending() {
        DefuseScenario scenario = new DefuseScenario(0, DefuseResult.DEFUSE_NOT_FOUND);
        scenario.firstPlayer.removeCardAt(0);

        DefuseResult result = scenario.engine.resolveDefuse(0);

        assertEquals(DefuseResult.DEFUSE_NOT_FOUND, result);
        assertEquals(TurnPhase.AWAITING_DEFUSE_POSITION, scenario.engine.getPhase());
        assertSame(scenario.firstPlayer, scenario.state.getCurrentPlayer());
        assertEquals(0, scenario.state.advanceCount);
        assertEquals(List.of(), scenario.firstPlayer.getHand());
        assertEquals(0, scenario.deck.size());
        assertEquals(List.of(), scenario.discardPile.discarded);
        assertEquals(DefuseResult.DEFUSE_NOT_FOUND, scenario.engine.resolveDefuse(0));
    }

    @Test
    public void ResolveDefuse_WhenSuccessful_ClearsPendingKittenAndAdvancesOnce() {
        DefuseScenario scenario = new DefuseScenario(0, DefuseResult.DEFUSED);

        DefuseResult result = scenario.engine.resolveDefuse(0);

        assertEquals(DefuseResult.DEFUSED, result);
        assertEquals(TurnPhase.ACTIONS_ALLOWED, scenario.engine.getPhase());
        assertSame(scenario.secondPlayer, scenario.state.getCurrentPlayer());
        assertEquals(1, scenario.state.advanceCount);
        assertEquals(DefuseResult.NOT_AWAITING_DEFUSE, scenario.engine.resolveDefuse(0));
        assertEquals(1, scenario.state.advanceCount);
    }

    @Test
    public void GetSnapshot_WithPendingKitten_ReturnsIndependentCurrentState() {
        Player current = new Player("Player 1");
        current.addCard(new Card(CardType.SKIP));
        current.addCard(new Card(CardType.DEFUSE));
        Player next = new Player("Player 2");
        Player eliminated = new Player("Player 3") {
            @Override
            public boolean isEliminated() {
                return true;
            }
        };
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(new Card(CardType.EXPLODING_KITTEN),
                new Card(CardType.FAVOR), new Card(CardType.ATTACK)));
        GameState state = new GameState(List.of(current, next, eliminated), deck);
        RecordingDiscardPile discardPile = new RecordingDiscardPile();
        discardPile.discard(new Card(CardType.NOPE));
        TurnActions actions = new TurnEngine(
                state, discardPile, waitingResolver(discardPile), Map.of());
        assertEquals(DrawStatus.DEFUSE_POSITION_REQUIRED, actions.draw().getStatus());

        GameSnapshot snapshot = actions.getSnapshot();

        current.addCard(new Card(CardType.FAVOR));
        assertEquals("Player 1", snapshot.getCurrentPlayerName());
        assertEquals(List.of(CardType.SKIP, CardType.DEFUSE), snapshot.getCurrentPlayerHand());
        assertEquals(2, snapshot.getDeckSize());
        assertEquals(1, snapshot.getDiscardPileSize());
        assertEquals(2, snapshot.getAlivePlayerCount());
        assertEquals(TurnPhase.AWAITING_DEFUSE_POSITION, snapshot.getPhase());
        assertThrows(UnsupportedOperationException.class, snapshot.getCurrentPlayerHand()::clear);
    }

    private static Deck createEmptyDeck() {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        return deck;
    }

    @Test
    public void GetSnapshot_AfterPlayingSkip_ReportsSkipAsTopDiscard() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        first.addCard(new Card(CardType.SKIP));
        GameState state = new GameState(List.of(first, second), createEmptyDeck());
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(state, discardPile,
                new ExplodingKittenResolver(discardPile), Map.of(CardType.SKIP, new SkipEffect()));

        engine.playCard(0);

        assertEquals(Optional.of(CardType.SKIP), engine.getSnapshot().getTopDiscard());
    }

    @Test
    public void GetSnapshot_AfterDefuseFollowsSkip_ReportsDefuseAsTopDiscard() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        first.addCard(new Card(CardType.SKIP));
        Card defuse = new Card(CardType.DEFUSE);
        second.addCard(defuse);
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(new Card(CardType.EXPLODING_KITTEN), new Card(CardType.ATTACK)));
        GameState state = new GameState(List.of(first, second), deck);
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver = new ExplodingKittenResolver(discardPile) {
            @Override
            public ExplosionOutcome begin(Player player, Card kitten) {
                return ExplosionOutcome.DEFUSE_POSITION_REQUIRED;
            }

            @Override
            public DefuseResult defuse(Player player, Card kitten, Deck pile, int position) {
                discardPile.discard(player.removeOne(CardType.DEFUSE).orElseThrow());
                pile.insertAt(kitten, position);
                return DefuseResult.DEFUSED;
            }
        };
        TurnEngine engine = new TurnEngine(state, discardPile, resolver,
                Map.of(CardType.SKIP, new SkipEffect()));
        engine.playCard(0);
        engine.draw();

        engine.resolveDefuse(0);

        assertEquals(2, engine.getSnapshot().getDiscardPileSize());
        assertEquals(Optional.of(CardType.DEFUSE), engine.getSnapshot().getTopDiscard());
    }

    private static final class StubEffect implements CardEffect {
        private boolean canPlay = true;
        private int canPlayCalls;
        private int playCalls;

        @Override
        public boolean canPlay(Player player, Deck deck) {
            canPlayCalls++;
            return canPlay;
        }

        @Override
        public EffectOutcome play(Player player, Deck deck) {
            playCalls++;
            return new EffectOutcome(false, List.of());
        }
    }

    @Test
    public void Draw_WithNormalCard_AddsCardAndAdvancesTurn() {
        Player firstPlayer = new Player("Player 1");
        Player secondPlayer = new Player("Player 2");
        Card topCard = new Card(CardType.SKIP);
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(topCard));

        GameState state = new GameState(List.of(firstPlayer, secondPlayer), deck) {
            private Player currentPlayer = firstPlayer;

            @Override
            public Player getCurrentPlayer() {
                return currentPlayer;
            }

            @Override
            public AdvanceResult advanceCurrentPlayer() {
                assertEquals(firstPlayer, currentPlayer);
                currentPlayer = secondPlayer;
                return AdvanceResult.ADVANCED;
            }
        };
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of()
        );

        DrawResult result = engine.draw();

        assertEquals(DrawStatus.NORMAL_CARD_DRAWN, result.getStatus());
        assertEquals(CardType.SKIP, result.getDrawnCardType().orElseThrow());
        assertEquals(List.of(topCard), firstPlayer.getHand());
        assertEquals(0, deck.size());
        assertEquals(secondPlayer, state.getCurrentPlayer());
    }

    @Test
    public void Draw_WithKittenAndDefuse_WaitsForPosition() {
        Player firstPlayer = new Player("Player 1");
        Player secondPlayer = new Player("Player 2");
        Card defuse = new Card(CardType.DEFUSE);
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        firstPlayer.addCard(defuse);

        Deck deck = createEmptyDeck();
        deck.addCards(List.of(kitten));
        GameState state = new GameState(List.of(firstPlayer, secondPlayer), deck);
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver = new ExplodingKittenResolver(discardPile) {
            @Override
            public ExplosionOutcome begin(Player player, Card drawnKitten) {
                assertSame(firstPlayer, player);
                assertSame(kitten, drawnKitten);
                return ExplosionOutcome.DEFUSE_POSITION_REQUIRED;
            }
        };
        TurnEngine engine = new TurnEngine(state, discardPile, resolver, Map.of());

        DrawResult result = engine.draw();

        assertEquals(DrawStatus.DEFUSE_POSITION_REQUIRED, result.getStatus());
        assertEquals(TurnPhase.AWAITING_DEFUSE_POSITION, engine.getPhase());
        assertSame(firstPlayer, state.getCurrentPlayer());
        assertEquals(List.of(defuse), firstPlayer.getHand());
        assertEquals(0, deck.size());
    }

    @Test
    public void PlayAttack_AssignsTwoTurnsToNextPlayer() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        first.addCard(new Card(CardType.ATTACK));
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(
                new Card(CardType.SKIP),
                new Card(CardType.SHUFFLE)
        ));
        GameState state = new GameState(List.of(first, second), deck);
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of(CardType.ATTACK, new AttackEffect())
        );

        PlayResult result = engine.playCard(0);

        assertEquals(PlayStatus.TURN_ENDED, result.getStatus());
        assertSame(second, state.getCurrentPlayer());
        assertEquals(1, discardPile.size());

        engine.draw();

        assertSame(second, state.getCurrentPlayer());

        engine.draw();

        assertSame(first, state.getCurrentPlayer());
    }

    @Test
    public void PlayAttack_WhenAlreadyAttacked_TransfersFourTurns() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        Player third = new Player("Player 3");
        first.addCard(new Card(CardType.ATTACK));
        second.addCard(new Card(CardType.ATTACK));
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(
                new Card(CardType.SKIP),
                new Card(CardType.SHUFFLE),
                new Card(CardType.FAVOR),
                new Card(CardType.NOPE)
        ));
        GameState state = new GameState(
                List.of(first, second, third),
                deck
        );
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of(CardType.ATTACK, new AttackEffect())
        );

        engine.playCard(0);
        engine.playCard(0);

        assertSame(third, state.getCurrentPlayer());
        for (int turn = 0; turn < 3; turn++) {
            engine.draw();
            assertSame(third, state.getCurrentPlayer());
        }

        engine.draw();

        assertSame(first, state.getCurrentPlayer());
        assertEquals(2, discardPile.size());
    }

    @Test
    public void PlayAttack_AfterOneAttackTurn_TransfersThreeTurns() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        Player third = new Player("Player 3");
        first.addCard(new Card(CardType.ATTACK));
        second.addCard(new Card(CardType.ATTACK));
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(
                new Card(CardType.SKIP),
                new Card(CardType.SHUFFLE),
                new Card(CardType.FAVOR),
                new Card(CardType.NOPE)
        ));
        GameState state = new GameState(
                List.of(first, second, third),
                deck
        );
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of(CardType.ATTACK, new AttackEffect())
        );

        engine.playCard(0);
        engine.draw();

        assertSame(second, state.getCurrentPlayer());

        engine.playCard(0);

        assertSame(third, state.getCurrentPlayer());
        for (int turn = 0; turn < 2; turn++) {
            engine.draw();
            assertSame(third, state.getCurrentPlayer());
        }

        engine.draw();

        assertSame(first, state.getCurrentPlayer());
        assertEquals(2, discardPile.size());
    }

    @Test
    public void PlaySkip_DuringAttack_ConsumesOneAttackTurn() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        Player third = new Player("Player 3");
        first.addCard(new Card(CardType.ATTACK));
        second.addCard(new Card(CardType.SKIP));
        Deck deck = createEmptyDeck();
        deck.addCards(List.of(new Card(CardType.SHUFFLE)));
        GameState state = new GameState(
                List.of(first, second, third),
                deck
        );
        DiscardPile discardPile = new DiscardPile();
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                new ExplodingKittenResolver(discardPile),
                Map.of(
                        CardType.ATTACK, new AttackEffect(),
                        CardType.SKIP, new SkipEffect()
                )
        );

        engine.playCard(0);
        engine.playCard(0);

        assertSame(second, state.getCurrentPlayer());
        assertEquals(2, discardPile.size());

        engine.draw();

        assertSame(third, state.getCurrentPlayer());
    }

    @Test
    public void ResolveDefuse_DuringAttack_ConsumesOneAttackTurn() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        Player third = new Player("Player 3");
        first.addCard(new Card(CardType.ATTACK));
        second.addCard(new Card(CardType.DEFUSE));
        Deck deck = createEmptyDeck();
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        deck.addCards(List.of(
                kitten,
                new Card(CardType.SHUFFLE)
        ));
        GameState state = new GameState(
                List.of(first, second, third),
                deck
        );
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile) {
                    @Override
                    public ExplosionOutcome begin(
                            Player player,
                            Card drawnKitten
                    ) {
                        assertSame(second, player);
                        assertSame(kitten, drawnKitten);
                        return ExplosionOutcome.DEFUSE_POSITION_REQUIRED;
                    }

                    @Override
                    public DefuseResult defuse(
                            Player player,
                            Card drawnKitten,
                            Deck currentDeck,
                            int position
                    ) {
                        assertSame(second, player);
                        assertSame(kitten, drawnKitten);
                        assertSame(deck, currentDeck);
                        assertEquals(0, position);
                        return DefuseResult.DEFUSED;
                    }
                };
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                resolver,
                Map.of(CardType.ATTACK, new AttackEffect())
        );

        engine.playCard(0);
        DrawResult drawResult = engine.draw();

        assertEquals(
                DrawStatus.DEFUSE_POSITION_REQUIRED,
                drawResult.getStatus()
        );
        assertSame(second, state.getCurrentPlayer());

        DefuseResult defuseResult = engine.resolveDefuse(0);

        assertEquals(DefuseResult.DEFUSED, defuseResult);
        assertSame(second, state.getCurrentPlayer());

        engine.draw();

        assertSame(third, state.getCurrentPlayer());
    }

    @Test
    public void DrawKitten_DuringAttack_ClearsEliminatedPlayersTurnDebt() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        Player third = new Player("Player 3");
        first.addCard(new Card(CardType.ATTACK));
        Deck deck = createEmptyDeck();
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        deck.addCards(List.of(
                kitten,
                new Card(CardType.SHUFFLE)
        ));
        GameState state = new GameState(
                List.of(first, second, third),
                deck
        );
        DiscardPile discardPile = new DiscardPile();
        ExplodingKittenResolver resolver =
                new ExplodingKittenResolver(discardPile) {
                    @Override
                    public ExplosionOutcome begin(
                            Player player,
                            Card drawnKitten
                    ) {
                        assertSame(second, player);
                        assertSame(kitten, drawnKitten);
                        player.eliminate();
                        return ExplosionOutcome.PLAYER_ELIMINATED;
                    }
                };
        TurnEngine engine = new TurnEngine(
                state,
                discardPile,
                resolver,
                Map.of(CardType.ATTACK, new AttackEffect())
        );

        engine.playCard(0);
        DrawResult elimination = engine.draw();

        assertEquals(DrawStatus.PLAYER_ELIMINATED, elimination.getStatus());
        assertSame(third, state.getCurrentPlayer());

        engine.draw();

        assertSame(first, state.getCurrentPlayer());
    }
}
