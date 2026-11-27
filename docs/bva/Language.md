# Language BVA

A supported game language: a BCP 47 language tag, the name shown in the
language menu, and the matching `Locale`.

## Method under test: `Language(String code, String displayName)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| LG-01 | No language exists. | `code = "en"`, `displayName = "English"` | Keeps the code and name; the locale has language `en` and no country. | :white_check_mark: |
| LG-02 | No language exists. | `code = "zh-CN"`, `displayName = "简体中文"` | Keeps the code and name; the locale has language `zh` and country `CN`. | :white_check_mark: |
| LG-03 | No language exists. | `code = null` | Throws `NullPointerException` with `"Language code must not be null."`. | :white_check_mark: |
| LG-04 | No language exists. | `code = "  "` | Throws `IllegalArgumentException` with `"Language code must not be blank."`. | :white_check_mark: |
| LG-05 | No language exists. | `code = "not a tag"` | Throws `IllegalArgumentException` with `"Language code must be a language tag: not a tag"`. | :x: |
| LG-06 | No language exists. | `displayName = null` | Throws `NullPointerException` with `"Language display name must not be null."`. | :x: |
| LG-07 | No language exists. | `displayName = ""` | Throws `IllegalArgumentException` with `"Language display name must not be blank."`. | :x: |

## Method under test: `toString()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| LG-08 | A language `zh-CN` named `简体中文` exists. | None | Returns `简体中文`, the text shown in the language menu. | :x: |

## Getter coverage

| Public method | Covered by |
|---|---|
| `getCode()` | LG-01, LG-02 |
| `getDisplayName()` | LG-01, LG-02 |
| `getLocale()` | LG-01, LG-02 |
