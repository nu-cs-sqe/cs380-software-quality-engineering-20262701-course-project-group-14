package localization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MessagesBundleTests {

    @Test
    public void ForLanguage_BundledChinese_ReturnsChineseTextAndPixelFont() {
        Messages messages = Messages.forLanguage(language("zh-CN"));

        assertEquals("爆炸猫", messages.text("setup.title"));
        assertEquals("fallback", messages.text("ui.font"));
    }

    @Test
    public void EveryLanguageBundle_DefinesExactlyTheEnglishKeys() throws IOException {
        Set<String> englishKeys = keys("/i18n/labels.properties");
        LanguageCatalog catalog = LanguageCatalog.load();
        for (Language language : catalog.getLanguages()) {
            if (language != catalog.getDefaultLanguage()) {
                String resource = "/i18n/labels_" + language.getLocale() + ".properties";
                assertEquals(englishKeys, keys(resource), resource);
            }
        }
    }

    private static Set<String> keys(String resource) throws IOException {
        try (InputStream input = MessagesBundleTests.class.getResourceAsStream(resource)) {
            assertNotNull(input, resource);
            Properties properties = new Properties();
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            return properties.stringPropertyNames();
        }
    }

    private static Language language(String code) {
        return LanguageCatalog.load().getLanguages().stream()
                .filter(language -> language.getCode().equals(code))
                .findFirst()
                .orElseThrow();
    }
}
