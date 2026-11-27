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

## Exceptions
### Code Coverage
- `ui` package: JavaFX views are not unit tested. They are exercised by
  the opt-in smoke tests (`CS380_RUN_JAVAFX_SMOKE=1 ./gradlew test`) and
  checked by hand; controller logic is tested through view ports.
- `GamePlayController`: the `default` branches of the three message-key
  switches are unreachable, because every enum value has its own case.
  They throw `IllegalStateException` as a guard for new enum values.
- `NopeDecision` and `ExplosionOutcome`: contracts for Nope and the
  Exploding Kitten resolver, which are not on `main` yet. They are covered
  once those features merge.
### Mutation Testing
- PIT targets `domain` and `controller`; enums without behavior are excluded
  (see `build.gradle.kts`).
- `GameSetupController.startGame`: the two "removed call to
  `Deck::shuffle`" mutants survive. The controller creates its own `Deck`,
  so a test cannot control the random order; the tests check card counts,
  hands and Defuse placement instead, which shuffling does not change.
- `GameState.advanceCurrentPlayer`: one mutant times out (the negated loop
  condition never finds the next player). PIT counts a timeout as detected.
### Others
- `GameSetupController` does not have isolated unit tests and is
  covered by integration tests only. See the Week 6 Instructor
  Code Review Feedback for details.
