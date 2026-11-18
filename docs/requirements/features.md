# Features for Integration Testing

| ID | Feature | Test class |
|---|---|---|
| F1 | Game Setup | GameSetupIntegrationTest |
| F2 | One Complete Turn | F2Tests — F2-01 implemented |

## F2 — One Complete Turn

Integrates TurnEngine, GameState, Player and Deck.

### F2-01 — Draw an ordinary card

- Given two living players each hold 8 cards, Player 1 is current,
  and the deck contains [SKIP, SHUFFLE] with SKIP on top.
- When Player 1 draws once.
- Then Player 1 receives SKIP and holds 9 cards.
- And Player 2's hand stays unchanged and Player 2 becomes current.
- And only SHUFFLE remains in the deck, the discard pile stays empty,
  and the phase remains ACTIONS_ALLOWED.

Further turn scenarios will be added under F2.
F2-01 uses real turn state, players, deck and discard pile.
The resolver remains a compilation stub and is not exercised by this scenario.

### F2-02 — Play Skip

- Given two living players each hold 8 cards, Player 1 is current,
  and Player 1's first card is SKIP.
- And the deck contains [ATTACK, SHUFFLE], the discard pile is empty,
  and SkipEffect is registered.
- When Player 1 plays the first card.
- Then SKIP leaves Player 1's hand and enters the discard pile.
- And Player 2 becomes current with an unchanged hand.
- And the deck remains unchanged.
```