# GameSetupController BVA

## Method under test: `startGame(String playerCountText)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---:|---|---|
| GSC-01 | No ready game exists; setup dependencies contain sufficient cards. | `"1"` | Validation fails and no ready `GameState` is produced. | :white_check_mark: |
| GSC-02 | No ready game exists; setup dependencies contain sufficient cards. | `"2"` | Returns a ready state with 2 players, 8 cards and at least 1 Defuse per player, 1 Exploding Kitten in the remaining shuffled deck, and Player 1 current. | :white_check_mark: |
| GSC-03 | No ready game exists; setup dependencies contain sufficient cards. | `"3"` | Returns a ready state with 3 players, 8 cards and at least 1 Defuse per player, 2 Exploding Kittens in the remaining shuffled deck, and Player 1 current. | :white_check_mark: |
| GSC-04 | No ready game exists; setup dependencies contain sufficient cards. | `"4"` | Returns a ready state with 4 players, 8 cards and at least 1 Defuse per player, 3 Exploding Kittens in the remaining shuffled deck, and Player 1 current. | :white_check_mark: |
| GSC-05 | No ready game exists; setup dependencies contain sufficient cards. | `"5"` | Returns a ready state with 5 players, 8 cards and at least 1 Defuse per player, 4 Exploding Kittens in the remaining shuffled deck, and Player 1 current. | :white_check_mark: |
| GSC-06 | No ready game exists; setup dependencies contain sufficient cards. | `"6"` | Validation fails and no ready `GameState` is produced. | :white_check_mark: |
| GSC-07 | No ready game exists. | `null` | Validation fails and no ready `GameState` is produced. | :white_check_mark: |
| GSC-08 | No ready game exists. | `""` | Validation fails and no ready `GameState` is produced. | :white_check_mark: |
| GSC-09 | No ready game exists. | `"   "` | Validation fails and no ready `GameState` is produced. | :white_check_mark: |
| GSC-10 | No ready game exists. | `"abc"` | Validation fails with the message key `setup.error.player-count`, which the view translates (a whole-number player count from 2 through 5 is required); no ready `GameState` is produced. | :white_check_mark: |
| GSC-11 | No ready game exists. | `"2.5"` | Validation fails with the message key `setup.error.player-count`, which the view translates (a whole-number player count from 2 through 5 is required); no ready `GameState` is produced. | :white_check_mark: |
| GSC-12 | No ready game exists; setup dependencies contain sufficient cards. | `" 3 "` | Surrounding whitespace is ignored and the same ready state as input `"3"` is returned. | :white_check_mark: |
## Method under test: `startNamedGame(List<String> playerNames)`

Players enter their names on the setup screen. The view fills an empty field
with the default name for that seat before calling the controller. Error
results are message keys that the view translates.

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GSC-13 | No ready game exists. | `["Ada", "Bo"]` | Returns a ready state whose players are named `Ada` and `Bo` in seat order. | :white_check_mark: |
| GSC-14 | No ready game exists. | `["  Ada ", "Bo"]` | Surrounding whitespace is removed: the first player is named `Ada`. | :white_check_mark: |
| GSC-15 | No ready game exists. | Five names of exactly 12 characters | Returns a ready state with 5 players keeping all 12 characters of each name. | :white_check_mark: |
| GSC-16 | No ready game exists. | `["Ada", "ABCDEFGHIJKLM"]` (13 characters) | Validation fails with the message key `setup.error.name-length`. | :white_check_mark: |
| GSC-17 | No ready game exists. | `["Ada", "ada"]` | Validation fails with the message key `setup.error.name-duplicate`; names are compared ignoring case. | :white_check_mark: |
| GSC-18 | No ready game exists. | `["Ada", "   "]` | Validation fails with the message key `setup.error.name-blank`. | :white_check_mark: |
| GSC-19 | No ready game exists. | `["Ada"]` | Validation fails with the message key `setup.error.player-count`. | :white_check_mark: |
| GSC-20 | No ready game exists. | Six distinct names | Validation fails with the message key `setup.error.player-count`. | :white_check_mark: |
| GSC-21 | No ready game exists. | `null` | Throws `NullPointerException` with `"Player names must not be null."`. | :white_check_mark: |

### Name characters and normalization

Names are normalized to Unicode NFC and runs of whitespace inside a name become
one space before the length, blank and duplicate checks. Characters the game
cannot show are rejected: characters outside the Basic Multilingual Plane (for
example emoji), control characters, invisible format characters (for example
zero-width spaces), private-use and unassigned code points. The view also
blocks characters missing from the bundled pixel font while typing.

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GSC-22 | No ready game exists. | `["Ada \t  Lovelace", "Bo"]` | The first player is named `Ada Lovelace`. | :white_check_mark: |
| GSC-23 | No ready game exists. | `["é", "é"]` (é composed and decomposed) | Validation fails with the message key `setup.error.name-duplicate`. | :white_check_mark: |
| GSC-24 | No ready game exists. | `["Ada😀", "Bo"]` (Ada + emoji) | Validation fails with the message key `setup.error.name-characters`. | :white_check_mark: |
| GSC-25 | No ready game exists. | `["A\u0007da", "Bo"]` (control character) | Validation fails with the message key `setup.error.name-characters`. | :white_check_mark: |
| GSC-26 | No ready game exists. | `["Ada", "​"]` (zero-width space only) | Validation fails with the message key `setup.error.name-characters`. | :white_check_mark: |
| GSC-27 | No ready game exists. | `["一二三四五六七八九十甲乙", "Bo"]` (12 characters) | Returns a ready state keeping all 12 characters. | :white_check_mark: |
| GSC-28 | No ready game exists. | `["éééééééééééé", "Bo"]` (12 decomposed é, 24 code units) | Returns a ready state; the name has 12 characters after normalization. | :x: |
| GSC-29 | No ready game exists. | `["Ada", null]` | Throws `NullPointerException` with `"Player name must not be null."`. | :x: |
