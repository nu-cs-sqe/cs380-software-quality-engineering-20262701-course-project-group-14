package i18n;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void Constructor_WithNullCode_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> new Language(null, "English"));

        assertEquals("Language code must not be null.", exception.getMessage());
    }

    @Test
    public void Constructor_WithBlankCode_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Language("  ", "English"));

        assertEquals("Language code must not be blank.", exception.getMessage());
    }

    @Test
    public void Constructor_WithInvalidTag_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Language("not a tag", "English"));

        assertEquals("Language code must be a language tag: not a tag", exception.getMessage());
    }

    @Test
    public void Constructor_WithNullDisplayName_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> new Language("en", null));

        assertEquals("Language display name must not be null.", exception.getMessage());
    }

    @Test
    public void Constructor_WithBlankDisplayName_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Language("en", ""));

        assertEquals("Language display name must not be blank.", exception.getMessage());
    }
}
