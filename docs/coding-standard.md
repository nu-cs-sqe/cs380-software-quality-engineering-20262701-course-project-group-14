# Group 14 Java Coding Standard

This standard adapts the Oracle and Google Java style examples to the conventions
already used by this project. Checkstyle enforces the mechanically verifiable rules;
SpotBugs checks compiled code for likely defects.

## Formatting

- Use UTF-8, Unix line endings, four spaces for indentation, and no tab characters.
- Keep lines at or below 140 characters. Package declarations, imports, and URLs are
  exempt from the line-length check.
- Put one statement on each line and use braces for control-flow bodies.
- Do not leave trailing whitespace. Every file must end with a newline.

## Naming

- Use lowercase package names, `UpperCamelCase` type names, and `lowerCamelCase`
  method, field, parameter, and local-variable names.
- Existing JUnit scenario methods may retain their descriptive `PascalCase` names.

## Imports and declarations

- Do not use wildcard class imports. Static wildcard imports are permitted in tests
  for JUnit assertions.
- Remove unused, duplicate, and redundant imports.
- Follow Java modifier order and do not write redundant modifiers.
- Prefer private fields. Package-private and protected members are allowed when they
  form a deliberate collaboration or testing seam.
- Methods should normally accept no more than five parameters. Prefer a cohesive
  object when a method would otherwise need more. Low-level pixel drawing primitives
  and the immutable `GameSnapshot` transfer object are documented exceptions.

## Correctness and control flow

- Do not use empty statements or declare multiple variables in one statement.
- Keep array brackets with the type.
- When overriding `equals`, also override `hashCode`.
- Prevent accidental switch fall-through and provide a default branch where needed.
- Compare strings by value rather than by reference.
- Avoid unnecessarily complex boolean expressions and returns.

## Verification

Run the complete local quality gate before requesting review:

```bash
./gradlew clean check
```

The `check` task runs unit tests, Checkstyle, and SpotBugs. Reports are generated in:

- `build/reports/checkstyle/`
- `build/reports/spotbugs/`

The checks fail the build when they find a violation. Suppressions require a narrow,
documented justification in `config/checkstyle/suppressions.xml`; they must not be
used merely to make the build green.

`config/spotbugs/exclude.xml` records two reviewed design exceptions: constructor
argument validation in classes that do not participate in finalization, and the
intentionally shared `GameState` draw pile. Mutable player and player-list exposure
is not excluded and must be fixed in production code.
