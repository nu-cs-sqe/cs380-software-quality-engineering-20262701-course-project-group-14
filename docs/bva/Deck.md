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
| Deck05 | New deck; removeCards(EXPLODING_KITTEN, 4)                                  | Returns 4 Exploding Kittens; size becomes 52; no Exploding Kittens remain | Yes          |
| Deck06 | New deck; removeCards(EXPLODING_KITTEN, -1)                                 | Throws IllegalArgumentException; deck remains unchanged                   | Yes          |
| Deck07 | New deck; removeCards(EXPLODING_KITTEN, 5)                                  | Throws IllegalArgumentException; deck remains unchanged                   | Yes          |
| Deck08 | All 4 Exploding Kittens have been removed; removeCards(EXPLODING_KITTEN, 0) | Returns an empty list; size remains 52; no Exploding Kittens remain       | Yes          |
| Deck09 | All 4 Exploding Kittens have been removed; removeCards(EXPLODING_KITTEN, 1) | Throws IllegalArgumentException; deck remains unchanged                   | Yes          |
| Deck10 | New deck; removeCards(null, 1)                                              | Throws NullPointerException; deck remains unchanged                       | Yes          |

### Method under test: addCards(List<Card> cards)

Adding cards does not shuffle the deck.
Existing cards remain unchanged.
Invalid input leaves the entire deck unchanged.
Later changes to the supplied list do not affect the deck.

| ID     | State of the system / Input                                                              | Expected output                                                                    | Implemented? |
|--------|------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|--------------|
| Deck11 | New deck; add an empty list                                                              | Size remains 56; all type counts remain unchanged                                  | Yes          |
| Deck12 | Remove all 4 Exploding Kittens, then add back 1 of them                                  | Size becomes 53; 1 Exploding Kitten is present; other type counts remain unchanged | Yes          |
| Deck13 | Remove all 4 Exploding Kittens, then add all 4 back                                      | Size returns to 56; all original type counts are restored                          | Yes          |
| Deck14 | Remove every card, then add one Defuse                                                   | Size becomes 1; Defuse count is 1; all other type counts are 0                     | Yes          |
| Deck15 | New deck; addCards(null)                                                                 | Throws NullPointerException; deck remains unchanged                                | Yes          |
| Deck16 | New deck; add a list containing a valid Card followed by null                            | Throws NullPointerException; deck remains unchanged                                | Yes          |
| Deck17 | Remove all 4 Exploding Kittens; add the returned mutable list back; then clear that list | Deck still contains 56 cards, including 4 Exploding Kittens                        | Yes          |

### Method under test: drawCard()

The top card is removed and returned.
addCards() appends cards to the bottom in the supplied order.
Drawing from an empty deck throws NoSuchElementException.

| ID     | State of the system / Input                     | Expected output                                                                      | Implemented? |
|--------|-------------------------------------------------|--------------------------------------------------------------------------------------|--------------|
| Deck18 | Empty deck; call drawCard()                     | Throws NoSuchElementException; size remains 0                                        | Yes          |
| Deck19 | Deck contains one Defuse; call drawCard()       | Returns that same card object; size becomes 0; all type counts become 0              | Yes          |
| Deck20 | Empty deck; add [Attack, Skip], then draw twice | Returns Attack first and Skip second; size changes from 2 to 1 to 0                  | Yes          |
| Deck21 | New 56-card deck; call drawCard()               | Returns a non-null card; size becomes 55; only the drawn type's count decreases by 1 | Yes          |

### Method under test: shuffle()

Shuffling preserves the size, type counts, and actual card objects.
A random shuffle may produce the original order.

| ID     | State of the system / Input                                                        | Expected output                                                                            | Implemented? |
|--------|------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------|--------------|
| Deck22 | Empty deck; call shuffle()                                                         | Completes normally; size remains 0                                                         | Yes          |
| Deck23 | Deck contains one card; call shuffle()                                             | Preserves that same card; size remains 1                                                   | Yes          |
| Deck24 | Deck contains [Attack, Skip]; injected Random always returns 0 from nextInt(bound) | Under the chosen JDK 11 shuffle implementation, drawing returns Skip then Attack           | Yes          |
| Deck25 | Complete 56-card deck; call shuffle()                                              | Size and all type counts remain unchanged; every original card object appears exactly once | Yes          |

### Implementation-derived case: Deck(Random random)

This package-private constructor allows controlled randomness in tests.

| ID     | State of the system / Input         | Expected output             | Implemented? |
|--------|-------------------------------------|-----------------------------|--------------|
| Deck26 | Construct a Deck with a null Random | Throws NullPointerException | Yes          |




