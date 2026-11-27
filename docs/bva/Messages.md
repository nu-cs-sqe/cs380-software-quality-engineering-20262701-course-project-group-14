# Messages BVA

Looks up player-visible text for one language. The base bundle
`i18n/messages.properties` is English; other languages add
`messages_<locale>.properties`. Text is formatted with `MessageFormat` only when
arguments are given, so plain text may contain apostrophes as written.

Test bundles live in `src/test/resources/i18n/test-messages*.properties`.

## Method under test: `text(String key, Object... arguments)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| MS-01 | English messages; the base bundle has `greeting=Hello`. | `"greeting"` | Returns `Hello`. | :white_check_mark: |
| MS-02 | Simplified Chinese messages; the Chinese bundle has `greeting=你好`. | `"greeting"` | Returns `你好`. | :white_check_mark: |
| MS-03 | Simplified Chinese messages; `farewell` exists only in the base bundle. | `"farewell"` | Falls back to the English `Goodbye`. | :white_check_mark: |
| MS-04 | English messages; no bundle has `missing`. | `"missing"` | Returns the key `missing`. | :white_check_mark: |
| MS-05 | English messages; `cards={0,choice,0#no cards\|1#1 card\|1<{0} cards}`. | `"cards"`, `1` and `3` | Returns `1 card` and `3 cards`. | :white_check_mark: |
| MS-06 | English messages; `apostrophe=Don't stop`. | `"apostrophe"` with no arguments | Returns `Don't stop` unchanged. | :white_check_mark: |
| MS-07 | Any messages. | `key = null` | Throws `NullPointerException` with `"Message key must not be null."`. | :white_check_mark: |

## Method under test: `forLanguage(Language)` / `forLanguage(String bundle, Language)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| MS-08 | The JVM default locale is `zh_CN`. | English | Uses the English base bundle, never the system language. | :white_check_mark: |
| MS-09 | No messages exist. | `language = null` | Throws `NullPointerException` with `"Language must not be null."`. | :x: |

## Getter coverage

| Public method | Covered by |
|---|---|
| `getLanguage()` | MS-02 |
