# Game-Over Verification

## Winner display and locked actions

- Given two players remain and an Exploding Kitten is on top of the draw pile.
- When the current player draws and the resolver reports their elimination.
- Then the real game view names the remaining player as the winner and locks
  drawing and card play. The final hands stay visible and unchanged.

The local window test uses a hand-written resolver adapter for the elimination
outcome. The winner message and action locks passed in that window. Final
acceptance still requires the reviewed resolver, a full game through normal
startup, and the return-to-setup interaction.
