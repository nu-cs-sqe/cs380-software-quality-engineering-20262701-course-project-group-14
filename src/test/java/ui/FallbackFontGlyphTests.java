package ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FallbackFontGlyphTests {
    @Test
    void CanDisplay_CharacterMissingFromFont_ReturnsFalse() {
        assertFalse(FallbackFont.canDisplay('暹'));
    }
    @Test
    void CanDisplay_Emoji_ReturnsFalse() {
        assertFalse(FallbackFont.canDisplay(0x1F600));
    }
    @Test
    void CanDisplay_CommonChineseCharacter_ReturnsTrue() {
        assertTrue(FallbackFont.canDisplay('橘'));
    }
    @Test
    void CanDisplay_LatinLetter_ReturnsTrue() {
        assertTrue(FallbackFont.canDisplay('A'));
    }
}
