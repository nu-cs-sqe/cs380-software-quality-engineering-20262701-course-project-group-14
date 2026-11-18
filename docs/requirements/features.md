# Features for Integration Testing

| ID | Feature | Test class |
|---|---|---|
| F1 | Game Setup | GameSetupIntegrationTest |
| F2 | One Complete Turn | F2Tests (planned) |

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