package i18n;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.ResourceBundle;

public final class Messages {
    private static final String BUNDLE = "i18n.messages";
    private final Language language;
    private final ResourceBundle bundle;

    private Messages(Language language, ResourceBundle bundle) {
        this.language = language;
        this.bundle = bundle;
    }

    public static Messages forLanguage(Language language) {
        return forLanguage(BUNDLE, language);
    }

    static Messages forLanguage(String bundleName, Language language) {
        return new Messages(language, ResourceBundle.getBundle(bundleName, language.getLocale()));
    }

    public Language getLanguage() {
        return language;
    }

    public String text(String key, Object... arguments) {
        Objects.requireNonNull(key, "Message key must not be null.");
        if (!bundle.containsKey(key)) {
            return key;
        }
        String pattern = bundle.getString(key);
        return arguments.length == 0
                ? pattern
                : new MessageFormat(pattern, language.getLocale()).format(arguments);
    }
}
