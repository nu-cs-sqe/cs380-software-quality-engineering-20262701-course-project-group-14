package i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public final class LanguageCatalog {
    private final List<Language> languages;
    private final Language defaultLanguage;

    private LanguageCatalog(List<Language> languages, Language defaultLanguage) {
        this.languages = List.copyOf(languages);
        this.defaultLanguage = defaultLanguage;
    }

    public static LanguageCatalog fromProperties(Properties properties) {
        Objects.requireNonNull(properties, "Language configuration must not be null.");
        String list = properties.getProperty("languages");
        if (list == null || list.isBlank()) {
            throw new IllegalArgumentException("Language list must not be empty.");
        }
        List<Language> languages = new ArrayList<>();
        for (String entry : list.split(",")) {
            String code = entry.trim();
            if (languages.stream().anyMatch(language -> language.getCode().equals(code))) {
                throw new IllegalArgumentException("Duplicate language: " + code);
            }
            String name = properties.getProperty("name." + code);
            if (name == null) {
                throw new IllegalArgumentException("Missing display name for language: " + code);
            }
            languages.add(new Language(code, name));
        }
        String defaultCode = properties.getProperty("default");
        Language defaultLanguage = null;
        for (Language language : languages) {
            if (language.getCode().equals(defaultCode)) {
                defaultLanguage = language;
            }
        }
        if (defaultLanguage == null) {
            throw new IllegalArgumentException(
                    "Default language must be one of the supported languages: " + defaultCode);
        }
        return new LanguageCatalog(languages, defaultLanguage);
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }
}
