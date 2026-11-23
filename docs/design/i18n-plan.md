# i18n Plan

Status: Proposed; to be confirmed by the team.
Owner: Xiaoyi Wang. Tracked in #36.

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

- Compare the key sets of all supported language files.
- Test configured-language loading and default fallback explicitly.
- Verify translated Setup, normal draw, Defuse and elimination flows.
- Check long text at the minimum supported window size.

## Week 8 deliverable

Discuss and record the plan on the Project Board. Full second-language
implementation belongs to later work.