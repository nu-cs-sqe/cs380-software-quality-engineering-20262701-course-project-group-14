package i18n;

import java.util.Locale;
import java.util.Objects;

public final class Language {
    private final String code;
    private final String displayName;
    private final Locale locale;

    public Language(String code, String displayName) {
        this.code = Objects.requireNonNull(code, "Language code must not be null.");
        this.displayName = displayName;
        this.locale = Locale.forLanguageTag(code);
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
}
