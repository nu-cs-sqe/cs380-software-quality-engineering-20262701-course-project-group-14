package localization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public final class LanguageCatalog {
    private static final String RESOURCE = "/i18n/languages.properties";
    private final List<Language> languages;
    private final Language defaultLanguage;

    private LanguageCatalog(List<Language> languages, Language defaultLanguage) {
        this.languages = List.copyOf(languages);
        this.defaultLanguage = defaultLanguage;
    }

    public static LanguageCatalog load() {
        return load(RESOURCE);
    }

    static LanguageCatalog load(String resource) {
        try (InputStream input = LanguageCatalog.class.getResourceAsStream(resource)) {
            if (input == null) {
                throw new IllegalStateException("Missing language configuration: " + resource);
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            return fromProperties(properties);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    public static LanguageCatalog fromProperties(Properties properties) {
        Objects.requireNonNull(properties, "Language configuration must not be null.");
        List<Language> languages = readLanguages(properties);
        return new LanguageCatalog(languages, findDefault(languages, properties.getProperty("default")));
    }

    private static List<Language> readLanguages(Properties properties) {
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
        return languages;
    }

    private static Language findDefault(List<Language> languages, String defaultCode) {
        return languages.stream()
                .filter(language -> language.getCode().equals(defaultCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Default language must be one of the supported languages: " + defaultCode));
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }
}
