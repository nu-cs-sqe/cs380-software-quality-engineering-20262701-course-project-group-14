# Week 8 Quality Baseline

## Tested Revision

- Branch: `build/week8-quality-reports`
- Source commit: `8711c28`
- Commands:
    - `./gradlew test jacocoTestReport pitest`
- Tool versions:
    - JDK 11.0.32.1 (Amazon Corretto)
    - Gradle 8.10
    - JUnit 5.10.0
    - JaCoCo 0.8.12
    - PIT 1.15.8
    - PIT JUnit 5 plugin 1.2.1
- Test result: 155 tests passed; 0 failed, 0 errors, and 0 skipped.

## JaCoCo

The default report includes every production class, including the JavaFX `ui` package.

### Complete Production Report

- Instructions: 1,333 covered / 17,794 missed (6% shown by JaCoCo)
- Branches: 93 covered / 810 missed (10% shown by JaCoCo)
- Lines: 303 covered / 2,313 missed
- Complexity: 125 covered / 632 missed
- Methods: 75 covered / 220 missed
- Classes: 19 covered / 17 missed

The full-project percentage is dominated by the JavaFX view and pixel-rendering code:

- `ui`: 0% instruction coverage and 0% branch coverage
- `domain`: 95% instruction coverage and 98% branch coverage
- `controller`: 95% instruction coverage and 91% branch coverage

### Course-Eligible Logic Subset

Treating the `ui` package as View/Main code and excluding the listed enums, the remaining `domain` and `controller` classes have:

- Instructions: 972 covered / 15 missed
- Branches: 93 covered / 4 missed
- Lines: 258 covered / 3 missed
- Complexity: 118 covered / 4 missed
- Methods: 68 covered / 0 missed
- Classes: 12 covered / 0 missed

The complete report remains available so that the exclusions do not hide untested production code. The course-eligible calculation must be checked against the instructor’s final interpretation of View, Main, and Enum exceptions.

## PIT

- Initial mutation targets: `domain.*` and `controller.*`
- Excluded enums:
    - `CardType`
    - `TurnPhase`
    - `DrawStatus`
    - `PlayStatus`
    - `PlayFailure`
    - `DefuseResult`
    - `AdvanceResult`
    - `ExplosionOutcome`
- Generated: 129
- Killed: 125
- Survived: 4
- No coverage: 0
- Timed out: 0
- Errors: 0
- Mutation coverage: 97%
- Test strength: 97%
- Mutated-class line coverage: 258/261 (99%)

This initial scope is not a claim of final course-wide compliance.

### Remaining Findings

1. `GameSnapshot.getDiscardPileSize()` at line 99
    - Mutation: replaced the returned value with `0`
    - Owner: Xiaoyi Wang
    - Next action: add a BVA/TDD case using a non-zero discard-pile size.

2. `StartingHandDealer.deal()` at line 10
    - Mutation: changed the insufficient-deck conditional boundary
    - Owner: Qingyang Xu
    - Next action: test a deck containing exactly the required number of cards.

3. `GameSetupController.startGame()` at line 37
    - Mutation: removed the first call to `Deck.shuffle()`
    - Owner: Xiaoyi Wang / team design decision
    - Next action: discuss a deterministic or call-observable shuffle seam. Do not use a probabilistic test.

4. `GameSetupController.startGame()` at line 47
    - Mutation: removed the final call to `Deck.shuffle()`
    - Owner: Xiaoyi Wang / team design decision
    - Next action: use the same approved shuffle seam or document the limitation following instructor guidance.

## Limitations

- No temporary production implementations were found in this tested revision.
- PIT currently excludes all `ui` production classes and the listed enums.
- JaCoCo’s complete-project percentage includes the JavaFX view and pixel-rendering classes, which currently have no automated execution coverage.
- No surviving mutation is currently classified as equivalent.
- The two shuffle mutations are behaviorally meaningful but are not reliably observable through the current `GameSetupController` interface.
- Coverage and mutation results must be regenerated after subsequent feature branches are merged.