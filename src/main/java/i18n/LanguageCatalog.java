package i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class LanguageCatalog {
    private final List<Language> languages;
    private final Language defaultLanguage;

    private LanguageCatalog(List<Language> languages, Language defaultLanguage) {
        this.languages = languages;
        this.defaultLanguage = defaultLanguage;
    }

    public static LanguageCatalog fromProperties(Properties properties) {
        String list = properties.getProperty("languages");
        if (list == null || list.isBlank()) {
            throw new IllegalArgumentException("Language list must not be empty.");
        }
        List<Language> languages = new ArrayList<>();
        for (String entry : list.split(",")) {
            String code = entry.trim();
            languages.add(new Language(code, properties.getProperty("name." + code)));
        }
        String defaultCode = properties.getProperty("default");
        Language defaultLanguage = null;
        for (Language language : languages) {
            if (language.getCode().equals(defaultCode)) {
                defaultLanguage = language;
            }
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
