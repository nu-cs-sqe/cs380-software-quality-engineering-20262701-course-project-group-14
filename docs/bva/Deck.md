# BVA: Deck

### Methods under test: Deck(), size(), countOf(CardType type)

| ID     | State of the system / Input                                                 | Expected output                                                     | Implemented? |
|--------|-----------------------------------------------------------------------------|---------------------------------------------------------------------|--------------|
| Deck01 | Create a new Deck and call size()                                           | Construction succeeds; size() returns 56                            | Yes          |
| Deck02 | Create a new Deck and call countOf(type) for each of the 13 CardType values | DEFUSE: 6; SEE_THE_FUTURE and NOPE: 5 each; all other types: 4 each | Yes          |

### Method under test: removeCards(CardType type, int count)

Successful calls return only cards of the requested type.
Cards of other types remain unchanged.
Failed calls leave the entire deck unchanged.

| ID     | State of the system / Input                                                 | Expected output                                                           | Implemented? |
|--------|-----------------------------------------------------------------------------|---------------------------------------------------------------------------|--------------|
| Deck03 | New deck; removeCards(EXPLODING_KITTEN, 0)                                  | Returns an empty list; size remains 56; 4 Exploding Kittens remain        | Yes          |
| Deck04 | New deck; removeCards(EXPLODING_KITTEN, 1)                                  | Returns 1 Exploding Kitten; size becomes 55; 3 Exploding Kittens remain   | Yes          |
| Deck05 | New deck; removeCards(EXPLODING_KITTEN, 4)                                  | Returns 4 Exploding Kittens; size becomes 52; no Exploding Kittens remain | No           |
| Deck06 | New deck; removeCards(EXPLODING_KITTEN, -1)                                 | Throws IllegalArgumentException; deck remains unchanged                   | No           |
| Deck07 | New deck; removeCards(EXPLODING_KITTEN, 5)                                  | Throws IllegalArgumentException; deck remains unchanged                   | No           |
| Deck08 | All 4 Exploding Kittens have been removed; removeCards(EXPLODING_KITTEN, 0) | Returns an empty list; size remains 52; no Exploding Kittens remain       | No           |
| Deck09 | All 4 Exploding Kittens have been removed; removeCards(EXPLODING_KITTEN, 1) | Throws IllegalArgumentException; deck remains unchanged                   | No           |
| Deck10 | New deck; removeCards(null, 1)                                              | Throws NullPointerException; deck remains unchanged                       | No           |