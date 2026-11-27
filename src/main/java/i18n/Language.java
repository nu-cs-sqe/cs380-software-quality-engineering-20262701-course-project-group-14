package i18n;

import java.util.Locale;
import java.util.Objects;

public final class Language {
    private final String code;
    private final String displayName;
    private final Locale locale;

    public Language(String code, String displayName) {
        this.code = Objects.requireNonNull(code, "Language code must not be null.");
        if (code.isBlank()) {
            throw new IllegalArgumentException("Language code must not be blank.");
        }
        this.displayName = Objects.requireNonNull(displayName, "Language display name must not be null.");
        if (displayName.isBlank()) {
            throw new IllegalArgumentException("Language display name must not be blank.");
        }
        this.locale = Locale.forLanguageTag(code);
        if (locale.getLanguage().isEmpty()) {
            throw new IllegalArgumentException("Language code must be a language tag: " + code);
        }
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
