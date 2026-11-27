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

    @Test
    public void Constructor_WithChineseTag_KeepsCodeNameAndLocale() {
        Language language = new Language("zh-CN", "简体中文");

        assertEquals("zh-CN", language.getCode());
        assertEquals("简体中文", language.getDisplayName());
        assertEquals("zh", language.getLocale().getLanguage());
        assertEquals("CN", language.getLocale().getCountry());
    }
}
