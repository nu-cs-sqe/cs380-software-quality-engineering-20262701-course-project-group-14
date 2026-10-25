# BVA: Deck

### Methods under test: Deck(), size(), countOf(CardType type)

| ID     | State of the system / Input                                                 | Expected output                                                     | Implemented? |
|--------|-----------------------------------------------------------------------------|---------------------------------------------------------------------|--------------|
| Deck01 | Create a new Deck and call size()                                           | Construction succeeds; size() returns 56                            | Yes          |
| Deck02 | Create a new Deck and call countOf(type) for each of the 13 CardType values | DEFUSE: 6; SEE_THE_FUTURE and NOPE: 5 each; all other types: 4 each | No           |