package ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.image.WritableImage;

/** Pixel cat faces players choose on the setup screen; one recolored head sprite each. */
final class CatAvatar {
    /** Patch, patch shadow, face, chin: the head sprite colors each coat replaces. */
    private static final List<int[]> COATS = List.of(
            new int[] {0xFFFF9B3D, 0xFFD96A2B, 0xFFFFF1D6, 0xFFE6CBA2},
            new int[] {0xFF8E8AA0, 0xFF66627A, 0xFFDAD6E6, 0xFFB5B0C6},
            new int[] {0xFF2E2A3A, 0xFF1E1B28, 0xFF453F58, 0xFF34304A},
            new int[] {0xFF3A3346, 0xFF2A2433, 0xFFFFF7EE, 0xFFFFB066},
            new int[] {0xFF6B4A3A, 0xFF4A3328, 0xFFF3E3C8, 0xFFC9A27E},
            new int[] {0xFFF4F0FF, 0xFFD6CFE8, 0xFFFFFFFF, 0xFFE4DEF0});
    private static final List<String> KEYS = List.of(
            "avatar.ginger", "avatar.tabby", "avatar.black",
            "avatar.calico", "avatar.siamese", "avatar.white");

    private CatAvatar() {
    }

    static int count() {
        return COATS.size();
    }

    /** Wraps any index into the available coats, so seat numbers can be used as defaults. */
    static int normalize(int avatar) {
        return Math.floorMod(avatar, COATS.size());
    }

    static String nameKey(int avatar) {
        return KEYS.get(normalize(avatar));
    }

    static WritableImage image(int avatar, int scale) {
        int[] coat = COATS.get(normalize(avatar));
        Map<Character, Integer> palette = new HashMap<>(PixelArt.TAVERN_PALETTE);
        palette.put('o', coat[0]);
        palette.put('d', coat[1]);
        palette.put('c', coat[2]);
        palette.put('s', coat[3]);
        return PixelArt.sprite(PixelData.HEAD_OPEN, scale, palette);
    }
}
