package i18n;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessagesTests {
    private static final String TEST_BUNDLE = "i18n.test-messages";
    private static final Language ENGLISH = new Language("en", "English");
    private static final Language CHINESE = new Language("zh-CN", "简体中文");

    @Test
    public void Text_EnglishKeyInBaseBundle_ReturnsEnglishText() {
        assertEquals("Hello", english().text("greeting"));
    }

    @Test
    public void Text_ChineseKeyInChineseBundle_ReturnsChineseText() {
        Messages messages = Messages.forLanguage(TEST_BUNDLE, CHINESE);

        assertEquals("你好", messages.text("greeting"));
        assertEquals(CHINESE, messages.getLanguage());
    }

    private static Messages english() {
        return Messages.forLanguage(TEST_BUNDLE, ENGLISH);
    }
}
