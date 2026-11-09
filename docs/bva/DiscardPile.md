# DiscardPile BVA

## Public Methods

| Method                          | Purpose                                                                 |
|---------------------------------|-------------------------------------------------------------------------|
| `DiscardPile()`                 | Creates an empty discard pile.                                          |
| `discard(Card card)`            | Adds one card to the pile. Rejects `null`.                              |
| `discardAll(List<Card> cards)`  | Adds all cards to the pile. Checks every card first; if any is invalid, adds nothing. |
| `size()`                        | Returns the number of cards in the pile.                                |


## Test Cases

### Method under test: `DiscardPile()`

| Test Case                          | State of the System | Expected output | Implemented? |
|------------------------------------|---------------------|-----------------|--------------|
| DP-01: A new discard pile is empty | none                | `size() = 0`    | :x:          |


### Method under test: `discard(Card card)`

| Test Case                                        | State of the System: pile | Input: `card` | Expected output                               | Implemented? |
|--------------------------------------------------|---------------------------|---------------|-----------------------------------------------|--------------|
| DP-02: Discard a card onto an empty pile         | `[]`                      | `SKIP`        | `size() = 1`                                  | :x:          |
| DP-03: Discard a card onto a pile with one card  | `[SKIP]`                  | `DEFUSE`      | `size() = 2`                                  | :x:          |
| DP-04: Discard a duplicate card                  | `[SKIP]`                  | `SKIP`        | `size() = 2` (duplicates kept)                | :x:          |
| DP-05: Reject a null card                        | `[]`                      | `null`        | Throws `NullPointerException`; `size() = 0`   | :x:          |

### Method under test: `discardAll(List<Card> cards)`

| Test Case                                              | State of the System: pile | Input: `cards`                  | Expected output                                              | Implemented? |
|--------------------------------------------------------|---------------------------|---------------------------------|--------------------------------------------------------------|--------------|
| DP-06: Discard an empty list                           | `[]`                      | `[]`                            | `size() = 0`                                                 | :x:          |
| DP-07: Discard a list with one card                    | `[]`                      | `[SKIP]`                        | `size() = 1`                                                 | :x:          |
| DP-08: Discard a full hand with duplicates             | `[]`                      | 8 cards, e.g. `[TACOCAT, TACOCAT, SKIP, SKIP, ATTACK, NOPE, FAVOR, DEFUSE]` | `size() = 8` (duplicates kept)          | :x:          |
| DP-09: Discard onto a pile that already has the same card | `[SKIP]`               | `[SKIP, ATTACK]`                | `size() = 3`                                                 | :x:          |
| DP-10: Reject a null list                              | `[]`                      | `null`                          | Throws `NullPointerException`; `size() = 0`                  | :x:          |
| DP-11: Reject a list containing null; add nothing      | `[]`                      | `[SKIP, null]`                  | Throws `NullPointerException`; `size() = 0` (no partial add) | :x:          |

### Method under test: `size()`

| Test Case                              | State of the System: pile | Expected output | Implemented?         |
|----------------------------------------|---------------------------|-----------------|----------------------|
| DP-12: Size of an empty pile           | `[]`                      | `0`             | implemented in DP-01 |
| DP-13: Size of a pile with one card    | `[SKIP]`                  | `1`             | implemented in DP-02 |
| DP-14: Size of a pile with many cards  | 8 cards                   | `8`             | implemented in DP-08 |