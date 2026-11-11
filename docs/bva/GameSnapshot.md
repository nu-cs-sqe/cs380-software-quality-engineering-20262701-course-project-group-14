# GameSnapshot BVA

## Method under test: `create(String currentPlayerName, List<CardType> currentPlayerHand, int deckSize, int discardPileSize, int alivePlayerCount, TurnPhase phase)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GSP-01 | A valid game is in progress. | `"Player 1"`, `[ATTACK, DEFUSE]`, deck size `10`, discard size `0`, alive count `2`, `ACTIONS_ALLOWED` | Returns a snapshot containing exactly those values. | :white_check_mark: |
| GSP-02 | A valid game has no cards in the draw pile. | Deck size `0`; all other inputs valid | Returns a snapshot whose deck size is `0`. | :white_check_mark: |
| GSP-03 | No snapshot exists. | `currentPlayerName = null`; all other inputs valid | Throws `NullPointerException` with `"Current player name must not be null."`; no snapshot is created. | :white_check_mark: |
| GSP-04 | No snapshot exists. | `currentPlayerHand = null`; all other inputs valid | Throws `NullPointerException` with `"Current player hand must not be null."`; no snapshot is created. | :white_check_mark: |
| GSP-05 | No snapshot exists. | `currentPlayerHand` contains `null`; all other inputs valid | Throws `NullPointerException` with `"Current player hand must not contain null."`; no snapshot is created. | :white_check_mark: |
| GSP-06 | No snapshot exists. | `deckSize = -1`; all other inputs valid | Throws `IllegalArgumentException` with `"Deck size must not be negative."`; no snapshot is created. | :white_check_mark: |
| GSP-07 | No snapshot exists. | `discardPileSize = -1`; all other inputs valid | Throws `IllegalArgumentException` with `"Discard pile size must not be negative."`; no snapshot is created. | :white_check_mark: |
| GSP-08 | No snapshot exists. | `alivePlayerCount = -1`; all other inputs valid | Throws `IllegalArgumentException` with `"Alive player count must be at least 1."`; no snapshot is created. | :white_check_mark: |
| GSP-09 | No snapshot exists. | `alivePlayerCount = 0`; all other inputs valid | Throws `IllegalArgumentException` with `"Alive player count must be at least 1."`; no snapshot is created. | :white_check_mark: |
| GSP-10 | One player remains alive. | `alivePlayerCount = 1`; all other inputs valid | Returns a snapshot whose living-player count is `1`. | :white_check_mark: |
| GSP-11 | No snapshot exists. | `phase = null`; all other inputs valid | Throws `NullPointerException` with `"Turn phase must not be null."`; no snapshot is created. | :white_check_mark: |
| GSP-12 | A caller owns a mutable hand list. | A mutable hand containing `[ATTACK, DEFUSE]` | Later changes to the source list do not change the snapshot's hand. | :x: |

## Method under test: `getCurrentPlayerName()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GSP-13 | A valid snapshot exists for Player 1. | None | Returns `"Player 1"` and never returns `null`. | implemented in GSP-01 |

## Method under test: `getCurrentPlayerHand()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GSP-14 | A snapshot contains `[ATTACK, DEFUSE]`. | None | Returns `[ATTACK, DEFUSE]` in the same order. | implemented in GSP-01 |
| GSP-15 | A snapshot contains a hand. | Attempt to modify the returned list. | The modification is rejected with `UnsupportedOperationException`; the snapshot remains unchanged. | :x: |

## Method under test: `getDeckSize()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GSP-16 | A snapshot was created with deck size `10`. | None | Returns `10`. | implemented in GSP-01 |
| GSP-17 | A snapshot was created with deck size `0`. | None | Returns `0`. | implemented in GSP-02 |

## Method under test: `getDiscardPileSize()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GSP-18 | A snapshot was created with discard-pile size `0`. | None | Returns `0`. | implemented in GSP-01 |

## Method under test: `getAlivePlayerCount()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GSP-19 | A snapshot was created with `2` living players. | None | Returns `2`. | implemented in GSP-01 |
| GSP-20 | A snapshot was created with `1` living player. | None | Returns `1`. | implemented in GSP-10 |

## Method under test: `getPhase()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GSP-21 | A snapshot was created in `ACTIONS_ALLOWED`. | None | Returns `ACTIONS_ALLOWED` and never returns `null`. | implemented in GSP-01 |
| GSP-22 | A snapshot was created in `AWAITING_DEFUSE_POSITION`. | None | Returns `AWAITING_DEFUSE_POSITION`. | :x: |
