package i18n;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LanguageTests {

    @Test
    public void Constructor_WithEnglishTag_KeepsCodeNameAndLocale() {
        Language language = new Language("en", "English");

        assertEquals("en", language.getCode());
        assertEquals("English", language.getDisplayName());
        assertEquals("en", language.getLocale().getLanguage());
        assertEquals("", language.getLocale().getCountry());
    }
}
