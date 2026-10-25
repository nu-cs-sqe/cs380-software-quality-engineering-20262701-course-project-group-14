# BVA: Card

### Methods under test: Card(CardType type), getType()

| ID | State of the system / Input | Expected output | Implemented? |
|---|---|---|---|
| Card01 | Create a Card with each of the 13 non-null CardType values | Construction succeeds; getType() returns the supplied type | No |
| Card02 | Create a Card with null | Throws NullPointerException | No |