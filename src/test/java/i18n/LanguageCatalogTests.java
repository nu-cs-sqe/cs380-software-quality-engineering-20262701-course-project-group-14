package i18n;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LanguageCatalogTests {

    @Test
    public void FromProperties_WithTwoLanguages_KeepsOrderNamesAndDefault() {
        LanguageCatalog catalog = LanguageCatalog.fromProperties(
                config("en,zh-CN", "en", "en", "English", "zh-CN", "简体中文"));

        assertEquals(List.of("en", "zh-CN"), codes(catalog));
        assertEquals(List.of("English", "简体中文"), catalog.getLanguages().stream()
                .map(Language::getDisplayName).collect(Collectors.toList()));
        assertEquals("en", catalog.getDefaultLanguage().getCode());
    }

    @Test
    public void FromProperties_WithChineseDefault_UsesChineseAsDefault() {
        LanguageCatalog catalog = LanguageCatalog.fromProperties(
                config("en,zh-CN", "zh-CN", "en", "English", "zh-CN", "简体中文"));

        assertEquals("zh-CN", catalog.getDefaultLanguage().getCode());
    }

    @Test
    public void FromProperties_WithSpacesAroundCodes_TrimsCodes() {
        LanguageCatalog catalog = LanguageCatalog.fromProperties(
                config(" en , zh-CN ", "en", "en", "English", "zh-CN", "简体中文"));

        assertEquals(List.of("en", "zh-CN"), codes(catalog));
    }

    @Test
    public void FromProperties_WithoutLanguageList_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> LanguageCatalog.fromProperties(config(null, "en", "en", "English")));

        assertEquals("Language list must not be empty.", exception.getMessage());
    }

    @Test
    public void FromProperties_WithBlankLanguageList_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> LanguageCatalog.fromProperties(config("", "en", "en", "English")));

        assertEquals("Language list must not be empty.", exception.getMessage());
    }

    private static Properties config(String languages, String defaultCode, String... names) {
        Properties properties = new Properties();
        if (languages != null) {
            properties.setProperty("languages", languages);
        }
        if (defaultCode != null) {
            properties.setProperty("default", defaultCode);
        }
        for (int i = 0; i < names.length; i += 2) {
            properties.setProperty("name." + names[i], names[i + 1]);
        }
        return properties;
    }

    private static List<String> codes(LanguageCatalog catalog) {
        return catalog.getLanguages().stream().map(Language::getCode).collect(Collectors.toList());
    }
}
