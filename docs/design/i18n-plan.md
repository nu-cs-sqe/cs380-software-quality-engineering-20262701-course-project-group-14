# i18n Plan

Status: Implemented for English (default) and Simplified Chinese.
Owner: Xiaoyi Wang. Tracked in #36.

## How it works

- Player-visible text lives in Java resource bundles:
  `src/main/resources/i18n/labels.properties` (English base) and
  `labels_zh_CN.properties`. `i18n.Messages` loads them with
  `ResourceBundle.getBundle` and never falls back to the operating system's
  language, so English stays English on a Chinese computer.
- `src/main/resources/i18n/languages.properties` lists the languages offered on
  the setup screen, their display names and the default. `i18n.LanguageCatalog`
  reads it.
- The setup screen has a language menu; the chosen language is used for the
  whole game. Views read text by key through `ui.Texts`.
- The pixel bitmap font only covers A–Z, digits and a few symbols. Languages
  whose bundle sets `ui.font=fallback` are drawn with the bundled Ark Pixel font
  (SIL Open Font License 1.1), rasterized to the same crisp pixel style.

## Adding a language

1. Add the language code and display name to `languages.properties`.
2. Add `labels_<locale>.properties` with every key of `labels.properties`.
3. Set `ui.font=fallback` if the bitmap font cannot draw the script.

No Java code changes. `MessagesBundleTests` fails when a key is missing or
extra, and `FallbackFontCoverageTests` fails when a character is missing from
the bundled font.

## Language selection

- Select a language during Game Setup. Proposed languages: English as the
  default and Simplified Chinese as the second language.
- Read language codes and display names from locale configuration.
  Adding a supported language requires resources and configuration, without
  changing the Java language-selection logic.
- Switching language during an active game is deferred.

## Text ownership

- Domain returns business results and does not contain translated UI text.
- Controller maps results to message keys.
- View resolves keys through ResourceBundle.
- Use message parameters for player names, numbers and insertion ranges.
- First migrate Setup labels and player-count errors, gameplay controls and
  status, card names, Defuse prompts and elimination messages.
- Missing translations fall back to the default English resource. Define and
  test the behavior when a key is absent from the default resource as well.

## Validation plan

- Compare the key sets of all supported language files. Done: `MB-01`.
- Test configured-language loading and default fallback explicitly. Done:
  `LC-01`–`LC-13`, `MS-03` (missing translation), `MS-04` (missing everywhere
  returns the key), `MS-08` (no fallback to the system language).
- Check every translated character against the bundled font. Done: `MB-02`.
- Verify translated Setup, normal draw, Defuse and elimination flows. Checked on
  screenshots of both languages, including the Kitten position dialog and the
  victory screen.
- Check long text at the minimum supported window size. Checked at 960×760.

## Week 8 deliverable

Discuss and record the plan on the Project Board. Full second-language
implementation belongs to later work.