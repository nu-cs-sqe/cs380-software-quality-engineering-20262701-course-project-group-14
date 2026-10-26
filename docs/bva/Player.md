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
| PL-06: Add a card to a hand with exactly one card             | `[TACOCAT]`                                                              | `ATTACK`      | `hand = [TACOCAT, ATTACK]`                                                                                        | :x:                |
| PL-07: Add a duplicate card to a hand at maximum size (7 → 8) | `[TACOCAT, TACOCAT, BEARD_CAT, BEARD_CAT, CATTERMELON, TACOCAT, DEFUSE]` | `DEFUSE`      | `hand = [TACOCAT, TACOCAT, BEARD_CAT, BEARD_CAT, CATTERMELON, TACOCAT, DEFUSE, DEFUSE]` (size 8, duplicates kept) | :x:                |
| PL-08: Reject a null card                                     | `[]`                                                                     | `null`        | Throws `NullPointerException`; `hand = []`                                                                        | :x:                |

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