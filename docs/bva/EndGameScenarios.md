# End-of-game scenario tests

Integration tests in `src/test/java/integration/EndGameScenarioTests.java` play
fixed games through `TurnEngine` from the first turn until a winner is declared.

They use `RuleBookResolver`, a test resolver that follows the Original Edition
rules: a player holding a Defuse chooses where the Kitten goes back and discards
the Defuse; a player without one is eliminated and discards their hand. When the
reviewed `ExplodingKittenResolver` replaces the temporary stub, these scenarios
should run against it unchanged by building the engine with the real resolver.

| ID | Starting state | Play | Expected end | Implemented? |
|---|---|---|---|---|
| ES-01 | Two players; Player 1 holds no Defuse; a Kitten is on top of the deck. | Player 1 draws. | Player 1 is eliminated and their hand is discarded; `GAME_OVER`; winner is seat 1 (`LAST_SURVIVOR`). | :white_check_mark: |
| ES-02 | Three players without Defuses; Player 1 holds Attack. Deck: Taco, Kitten, Melon, Kitten. | Player 1 plays Attack; Player 2 draws twice; Player 3 draws; Player 1 draws. | Player 2 is out on the second Attack turn; Player 1 is out next; `GAME_OVER`; winner is seat 2 (`LAST_SURVIVOR`). | :white_check_mark: |
| ES-03 | Two players; Player 1 holds two Defuses. Deck: Kitten, Taco, Beard. | Player 1 draws and puts the Kitten back at position 1; Player 2 draws; Player 1 draws and defuses again. | Both Defuses are discarded; `GAME_OVER` with Player 2 still in the game; winner is seat 0 (`DEFUSE_MASTER`). | :x: |
| ES-04 | Two players; Player 1 holds one Defuse, Player 2 none. Deck: Kitten, Taco. | Player 1 draws and puts the Kitten back on top; Player 2 draws it. | Player 2 is eliminated; winner is seat 0 by `LAST_SURVIVOR`, not `DEFUSE_MASTER` (one Defuse). | :x: |
| ES-05 | Two players; Player 1 holds one Defuse and Skip. Deck: Kitten, Taco. | Player 1 draws and chooses position 3 on a one-card deck, then position 1. | The invalid position keeps the Defuse pending and changes nothing; the valid one defuses and the game continues with Player 2. | :x: |
