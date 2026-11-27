package ui;

import i18n.Language;
import i18n.LanguageCatalog;
import i18n.Messages;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FallbackFontCoverageTests {

    @Test
    public void FallbackLanguages_UseOnlyCharactersInTheBundledFont() throws IOException, FontFormatException {
        Font font = bundledFont();
        List<String> missing = new ArrayList<>();
        for (Language language : LanguageCatalog.load().getLanguages()) {
            if ("fallback".equals(Messages.forLanguage(language).text("ui.font"))) {
                Properties texts = bundle(language);
                for (String key : texts.stringPropertyNames()) {
                    texts.getProperty(key).codePoints()
                            .filter(codePoint -> codePoint > 127 && !font.canDisplay(codePoint))
                            .forEach(codePoint -> missing.add(language.getCode() + " " + key + ": "
                                    + new String(Character.toChars(codePoint))));
                }
            }
        }
        assertEquals(List.of(), missing);
    }

    private static Font bundledFont() throws IOException, FontFormatException {
        try (InputStream input = FallbackFontCoverageTests.class.getResourceAsStream(
                "/fonts/ark-pixel-12px-proportional-zh_hans.ttf")) {
            assertNotNull(input);
            return Font.createFont(Font.TRUETYPE_FONT, input);
        }
    }

    private static Properties bundle(Language language) throws IOException {
        String resource = "/i18n/labels_" + language.getLocale() + ".properties";
        try (InputStream input = FallbackFontCoverageTests.class.getResourceAsStream(resource)) {
            assertNotNull(input, resource);
            Properties properties = new Properties();
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            return properties;
        }
    }
}
