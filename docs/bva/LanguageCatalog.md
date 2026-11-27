# LanguageCatalog BVA

Reads the supported languages from `src/main/resources/i18n/languages.properties`:

```properties
languages=en,zh-CN
default=en
name.en=English
name.zh-CN=简体中文
```

Adding a language means adding its code and name here and a
`messages_<locale>.properties` file; no Java code changes.

## Method under test: `fromProperties(Properties)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| LC-01 | No catalog exists. | `languages=en,zh-CN`, `default=en`, both names | Lists `en` then `zh-CN` with their names, in configuration order; the default is `en`. | :white_check_mark: |
| LC-02 | No catalog exists. | Same list, `default=zh-CN` | The default is `zh-CN`. | :white_check_mark: |
| LC-03 | No catalog exists. | `languages=" en , zh-CN "` | Codes are trimmed: `en`, `zh-CN`. | :white_check_mark: |
| LC-04 | No catalog exists. | No `languages` entry | Throws `IllegalArgumentException` with `"Language list must not be empty."`. | :white_check_mark: |
| LC-05 | No catalog exists. | `languages=` (blank) | Throws `IllegalArgumentException` with `"Language list must not be empty."`. | :white_check_mark: |
| LC-06 | No catalog exists. | `languages=en,fr` without `name.fr` | Throws `IllegalArgumentException` with `"Missing display name for language: fr"`. | :white_check_mark: |
| LC-07 | No catalog exists. | `languages=en,en` | Throws `IllegalArgumentException` with `"Duplicate language: en"`. | :white_check_mark: |
| LC-08 | No catalog exists. | `default=fr`, `fr` not listed | Throws `IllegalArgumentException` with `"Default language must be one of the supported languages: fr"`. | :white_check_mark: |
| LC-09 | No catalog exists. | No `default` entry | Throws `IllegalArgumentException` with `"Default language must be one of the supported languages: null"`. | :white_check_mark: |
| LC-10 | No catalog exists. | `properties = null` | Throws `NullPointerException` with `"Language configuration must not be null."`. | :white_check_mark: |

## Method under test: `getLanguages()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| LC-11 | A catalog with two languages exists. | Add a language to the returned list | Throws `UnsupportedOperationException`; the catalog is unchanged. | :white_check_mark: |

## Method under test: `load()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| LC-12 | The bundled configuration is on the classpath. | None | Lists English (`en`, default) and Simplified Chinese (`zh-CN`, named `简体中文`, read as UTF-8). | :white_check_mark: |
| LC-13 | No configuration exists at the requested path. | `load("/i18n/missing.properties")` (package-private overload used by `load()`) | Throws `IllegalStateException` with `"Missing language configuration: /i18n/missing.properties"`. | :white_check_mark: |

## Getter coverage

| Public method | Covered by |
|---|---|
| `getDefaultLanguage()` | LC-01, LC-02, LC-12 |
