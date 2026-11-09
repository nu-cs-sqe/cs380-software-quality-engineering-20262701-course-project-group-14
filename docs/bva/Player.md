# Player BVA

### Method under test: `Player(String name)`

| Test Case                                    | Input: `name` | Expected output                                                  | Implemented?       |
|----------------------------------------------|---------------|------------------------------------------------------------------|--------------------|
| PL-01: Create a player with a non-empty name | `"Player 1"`  | A player is created with the name `"Player 1"` and an empty hand | :white_check_mark: |
| PL-02: Reject an empty name                  | `""`          | Throws `IllegalArgumentException`; no player is created          | :white_check_mark: |
| PL-03: Reject a blank name                   | `"   "`       | Throws `IllegalArgumentException`; no player is created          | :white_check_mark: |
| PL-04: Reject a null name                    | `null`        | Throws `NullPointerException`; no player is created              | :white_check_mark: |

### Method under test: `addCard(Card card)`

| Test Case                                                     | State of the System: `hand`                                              | Input: `card` | Expected output                                                                                                   | Implemented?       |
|---------------------------------------------------------------|--------------------------------------------------------------------------|---------------|-------------------------------------------------------------------------------------------------------------------|--------------------|
| PL-05: Add a card to an empty hand                            | `[]`                                                                     | `SKIP`        | `hand = [SKIP]`                                                                                                   | :white_check_mark: |
| PL-06: Add a card to a hand with exactly one card             | `[TACOCAT]`                                                              | `ATTACK`      | `hand = [TACOCAT, ATTACK]`                                                                                        | :white_check_mark: |
| PL-07: Add a duplicate card to a hand at maximum size (7 → 8) | `[TACOCAT, TACOCAT, BEARD_CAT, BEARD_CAT, CATTERMELON, TACOCAT, DEFUSE]` | `DEFUSE`      | `hand = [TACOCAT, TACOCAT, BEARD_CAT, BEARD_CAT, CATTERMELON, TACOCAT, DEFUSE, DEFUSE]` (size 8, duplicates kept) | :white_check_mark: |
| PL-08: Reject a null card                                     | `[]`                                                                     | `null`        | Throws `NullPointerException`; `hand = []`                                                                        | :white_check_mark: |

### Method under test: `getHand()`

| Test Case                                       | State of the System: `hand`                                                      | Expected output                    | Implemented?         |
|-------------------------------------------------|----------------------------------------------------------------------------------|------------------------------------|----------------------|
| PL-09: Get an empty hand                        | `[]`                                                                             | `[]`                               | implemented in PL-01 |
| PL-10: Get a hand with exactly one card         | `[SKIP]`                                                                         | `[SKIP]`                           | implemented in PL-05 |
| PL-11: Get a full starting hand with duplicates | `[TACOCAT, TACOCAT, BEARD_CAT, BEARD_CAT, CATTERMELON, TACOCAT, DEFUSE, DEFUSE]` | the same 8 cards in the same order | implemented in PL-07 |

### Method under test: `getName()`

| Test Case                 | State of the System: `name` | Expected output | Implemented?         |
|---------------------------|-----------------------------|-----------------|----------------------|
| PL-12: Get a typical name | `"Player 1"`                | `"Player 1"`    | implemented in PL-01 |

### Method under test: `getHand()` (read-only)

| Test Case                                   | State of the System: `hand` | Action                          | Expected output                                                  | Implemented? |
|---------------------------------------------|-----------------------------|---------------------------------|------------------------------------------------------------------|--------------|
| PL-13: Reject changes to the returned hand  | `[SKIP]`                    | `getHand().add(DEFUSE)`         | Throws `UnsupportedOperationException`; `hand = [SKIP]`          | :white_check_mark: |

### Method under test: `hasCard(CardType type)`

| Test Case                                  | State of the System: `hand` | Input: `type` | Expected output               | Implemented? |
|--------------------------------------------|-----------------------------|---------------|-------------------------------|--------------|
| PL-14: Empty hand                          | `[]`                        | `DEFUSE`      | `false`                       | :white_check_mark: |
| PL-15: Hand contains one card of the type  | `[SKIP, DEFUSE]`            | `DEFUSE`      | `true`                        | :white_check_mark: |
| PL-16: Hand contains two cards of the type | `[DEFUSE, SKIP, DEFUSE]`    | `DEFUSE`      | `true`                        | :white_check_mark: |
| PL-17: Hand has cards but not the type     | `[SKIP]`                    | `DEFUSE`      | `false`                       | :white_check_mark: |
| PL-18: Reject a null type                  | `[SKIP]`                    | `null`        | Throws `NullPointerException` | :white_check_mark: |

### Method under test: `removeOne(CardType type)`

| Test Case                                           | State of the System: `hand` | Input: `type` | Expected output                                                   | Implemented? |
|-----------------------------------------------------|-----------------------------|---------------|-------------------------------------------------------------------|--------------|
| PL-19: Remove from an empty hand                    | `[]`                        | `DEFUSE`      | Returns `Optional.empty()`; `hand = []`                           | :white_check_mark: |
| PL-20: Remove the only card of the type             | `[SKIP, DEFUSE]`            | `DEFUSE`      | Returns the `DEFUSE` card; `hand = [SKIP]`                        | :white_check_mark: |
| PL-21: Remove one of two cards of the type          | `[DEFUSE, SKIP, DEFUSE]`    | `DEFUSE`      | Returns one `DEFUSE` card; `hand = [SKIP, DEFUSE]`                | :white_check_mark: |
| PL-22: Remove a type that is not in the hand        | `[SKIP]`                    | `DEFUSE`      | Returns `Optional.empty()`; `hand = [SKIP]`                       | :white_check_mark: |
| PL-23: Reject a null type                           | `[SKIP]`                    | `null`        | Throws `NullPointerException`; `hand = [SKIP]`                    | :white_check_mark: |

### Method under test: `removeAllCards()`

| Test Case                                    | State of the System: `hand`                                                      | Expected output                                                     | Implemented? |
|----------------------------------------------|----------------------------------------------------------------------------------|---------------------------------------------------------------------|--------------|
| PL-24: Remove all cards from an empty hand   | `[]`                                                                             | Returns `[]`; `hand = []`                                           | :white_check_mark: |
| PL-25: Remove all cards from a one-card hand | `[SKIP]`                                                                         | Returns `[SKIP]`; `hand = []`                                       | :white_check_mark: |
| PL-26: Remove all cards from a full hand     | `[TACOCAT, TACOCAT, BEARD_CAT, BEARD_CAT, CATTERMELON, TACOCAT, DEFUSE, DEFUSE]` | Returns the same 8 cards in the same order; `hand = []`             | :white_check_mark: |

### Method under test: `eliminate()`

| Test Case                                     | State of the System | Expected output                       | Implemented? |
|-----------------------------------------------|---------------------|---------------------------------------|--------------|
| PL-27: Eliminate a player                     | not eliminated      | `isEliminated() = true`               | :white_check_mark: |
| PL-28: Eliminating twice keeps the player out | already eliminated  | `isEliminated() = true`; no exception | :white_check_mark: |

### Method under test: `isEliminated()`

| Test Case                                  | State of the System | Expected output | Implemented?         |
|--------------------------------------------|---------------------|-----------------|----------------------|
| PL-29: A new player is not eliminated      | new player          | `false`         | :white_check_mark:   |
| PL-30: An eliminated player is eliminated  | eliminated          | `true`          | implemented in PL-27 |