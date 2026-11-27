# FallbackFont BVA

## Method under test: `canDisplay(int codePoint)`

Tells the name fields which characters the game can draw: every player name is
drawn with the bitmap font or the bundled Ark Pixel font.

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| FF-01 | The bundled font is available. | `'A'` | `true` | :white_check_mark: |
| FF-02 | The bundled font is available. | `'橘'` (common Chinese character) | `true` | :white_check_mark: |
| FF-03 | The bundled font is available. | `U+1F600` (emoji) | `false` | :white_check_mark: |
| FF-04 | The bundled font is available. | `'暹'` (Chinese character missing from the font) | `false` | :white_check_mark: |
| FF-05 | The bundled font is available. | `' '` (space) | `true` | :white_check_mark: |
