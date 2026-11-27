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

## Languages
English (default) and Simplified Chinese, chosen on the setup screen. See
`docs/design/i18n-plan.md` for how to add a language.

## Third-party assets
- [Ark Pixel Font](https://github.com/TakWolf/ark-pixel-font) 12px proportional
  (Simplified Chinese) by TakWolf, licensed under the SIL Open Font License 1.1
  (`src/main/resources/fonts/ark-pixel-OFL.txt`). Used to draw Chinese text in
  the game's pixel style.

## Exceptions
### Code Coverage
N/A
### Mutation Testing
N/A
### Others
- `GameSetupController` does not have isolated unit tests and is
  covered by integration tests only. See the Week 6 Instructor
  Code Review Feedback for details.
