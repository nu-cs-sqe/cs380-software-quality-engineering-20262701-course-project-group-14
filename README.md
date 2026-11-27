# EXPLODING KITTEN ฅ^•ﻌ•^ฅ

[![Gradle Build](https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-14/actions/workflows/main.yml/badge.svg?branch=main)](https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-14/actions/workflows/main.yml)

## Contributors
- Sifan Liu
- Qingyang Xu
- Xiaoyi Wang

## Dependencies
- JDK 11
- JUnit 5.10
- Gradle 8.10

## Running the Game
```bash
./gradlew run
```
JavaFX is downloaded by the Gradle plugin; only JDK 11 needs to be installed.

## How to Play
1. Choose a language and enter the number of players (2–5), then press
   **Start Game**.
2. Enter each player's name (up to 12 characters; an empty field uses the
   default name) and click a cat face to choose another one. Press **Begin**.
3. Players share one computer and take turns. On your turn, play any number of
   cards from your hand (click a card, or use the arrow keys and Enter), then
   draw a card to end the turn. A screen asks the next player to take over
   before their hand is shown.
4. Drawing an Exploding Kitten eliminates you unless you hold a Defuse: then
   you choose where the Kitten goes back into the draw pile.

## Cards
| Card | Effect | Status |
|---|---|---|
| Exploding Kitten | Eliminates the player who draws it, unless they defuse it. | Engine flow done; the reviewed resolver is pending, a temporary stub is used until then |
| Defuse | Saves you from an Exploding Kitten; you put the Kitten back anywhere in the draw pile. | Engine flow done; waits for the resolver |
| Attack | Ends your turn without drawing; the next player takes two turns. | Done |
| Skip | Ends your turn without drawing. | Done |
| Cat Cards (5 kinds) | Play two matching Cat Cards to take a random card from another player. | Done |
| Sniff Test (original) | See below. | Done; number of copies in the deck to be decided by the team |
| See the Future, Shuffle, Favor, Nope | Original Edition rules. | Not implemented yet; shown as SOON in the hand |

## Win Conditions
- **Last cat standing** (Original Edition): the last player who has not
  exploded wins.
- **Defuse Master** (our additional rule): the first player to complete two
  successful Defuses wins immediately.

Whichever condition is met first ends the game. The rules are in
`docs/requirements/game-rules.md`.

## Original Card: Sniff Test
The current player sniffs the top card of the draw pile. If it is an Exploding
Kitten, the Kitten moves to the bottom of the draw pile; otherwise nothing
changes. Only the current player sees the sniffed card, and the turn continues.
Design notes: `docs/design/original-card-sniff-test.md`.

## Testing and Quality Reports
| Command | What it does | Report |
|---|---|---|
| `./gradlew test` | Unit and integration tests | `build/reports/tests/test/index.html` |
| `CS380_RUN_JAVAFX_SMOKE=1 ./gradlew test` | Also runs the JavaFX smoke tests (opens real windows) | same |
| `./gradlew jacocoTestReport` | Code coverage | `build/reports/jacoco/test/html/index.html` |
| `./gradlew pitest` | Mutation testing of `domain`, `controller` and `localization` | `build/reports/pitest/index.html` |
| `./gradlew build` | Everything CI runs on pull requests | — |

Every tested class has a boundary value analysis table in `docs/bva/`; test
cases are named after its IDs (for example `TE-DM-01`). End-of-game scenarios
are listed in `docs/bva/EndGameScenarios.md`.

## Project Structure
| Package | Responsibility |
|---|---|
| `domain` | Game rules: cards, deck, players, turn engine, card effects |
| `controller` | Setup and gameplay controllers; talk to views through view ports |
| `ui` | JavaFX views, pixel art and the `MainApp` entry point |
| `localization` | Languages and translated texts (`src/main/resources/i18n/`) |

## Languages
English (default) and Simplified Chinese, chosen on the setup screen. See
`docs/design/i18n-plan.md` for how to add a language.

## Third-party assets
- [Ark Pixel Font](https://github.com/TakWolf/ark-pixel-font) 12px proportional
  (Simplified Chinese) by TakWolf, licensed under the SIL Open Font License 1.1
  (`src/main/resources/fonts/ark-pixel-OFL.txt`). Used to draw Chinese text in
  the game's pixel style.

## Exceptions
### Code Coverage
N/A
### Mutation Testing
N/A
### Others
- `GameSetupController` does not have isolated unit tests and is
  covered by integration tests only. See the Week 6 Instructor
  Code Review Feedback for details.
