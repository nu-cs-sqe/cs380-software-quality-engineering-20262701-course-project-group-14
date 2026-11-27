package i18n;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessagesBundleTests {

    @Test
    public void ForLanguage_BundledChinese_ReturnsChineseTextAndPixelFont() {
        Messages messages = Messages.forLanguage(language("zh-CN"));

        assertEquals("爆炸猫", messages.text("setup.title"));
        assertEquals("fallback", messages.text("ui.font"));
    }

    private static Language language(String code) {
        return LanguageCatalog.load().getLanguages().stream()
                .filter(language -> language.getCode().equals(code))
                .findFirst()
                .orElseThrow();
    }
}
