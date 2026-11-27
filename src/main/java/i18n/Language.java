package i18n;

import java.util.Locale;
import java.util.Objects;

public final class Language {
    private final String code;
    private final String displayName;
    private final Locale locale;

    public Language(String code, String displayName) {
        this.code = requireText(code, "Language code");
        this.displayName = requireText(displayName, "Language display name");
        this.locale = Locale.forLanguageTag(code);
        if (locale.getLanguage().isEmpty()) {
            throw new IllegalArgumentException("Language code must be a language tag: " + code);
        }
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank.");
        }
        return value;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
