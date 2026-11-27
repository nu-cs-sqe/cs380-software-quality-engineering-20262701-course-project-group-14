package ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

/**
 * Pixel glyphs for text the bitmap font cannot draw, such as Chinese. Lines are rendered once
 * with the bundled Ark Pixel font (SIL Open Font License 1.1, see {@code fonts/ark-pixel-OFL.txt})
 * at its native 12-pixel size and thresholded to fully opaque pixels, so they scale up as
 * crisply as the bitmap font.
 */
final class FallbackFont {
    static final int SIZE = 12;
    private static final String RESOURCE = "/fonts/ark-pixel-12px-proportional-zh_hans.ttf";
    private static final int MAX_CACHED_LINES = 512;
    private static final Map<String, boolean[][]> MASKS = new LinkedHashMap<>(64, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, boolean[][]> eldest) {
            return size() > MAX_CACHED_LINES;
        }
    };
    private static Font font;

    private FallbackFont() {
    }

    static Font font() {
        if (font == null) {
            font = loadFont();
        }
        return font;
    }

    private static java.awt.Font glyphs;

    /** Whether a player name may contain this character: the bundled font has a glyph for it. */
    static synchronized boolean canDisplay(int codePoint) {
        if (glyphs == null) {
            glyphs = loadGlyphs();
        }
        return glyphs.canDisplay(codePoint);
    }

    private static java.awt.Font loadGlyphs() {
        try (InputStream input = FallbackFont.class.getResourceAsStream(RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Missing font: " + RESOURCE);
            }
            return java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, input);
        } catch (IOException | java.awt.FontFormatException exception) {
            throw new IllegalStateException("Unreadable font: " + RESOURCE, exception);
        }
    }

    static String family() {
        return font().getFamily();
    }

    /**
     * Scale factor for a bitmap-font scale. Small text keeps the readable native size; larger
     * text grows a little faster than the 7-pixel bitmap glyphs because CJK glyphs are denser.
     */
    static int factor(int bitmapScale) {
        return bitmapScale <= 2 ? 1 : Math.round(bitmapScale * 0.7f);
    }

    static int width(String line, int factor) {
        return mask(line)[0].length * factor;
    }

    /** Opaque pixels of one line at the native size, indexed {@code [row][column]}. */
    static boolean[][] mask(String line) {
        return MASKS.computeIfAbsent(line, FallbackFont::render);
    }

    private static boolean[][] render(String line) {
        Text text = new Text(line);
        text.setFont(font());
        text.setFill(Color.BLACK);
        text.setFontSmoothingType(FontSmoothingType.GRAY);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = text.snapshot(parameters, null);
        int width = Math.max(1, (int) image.getWidth());
        int height = Math.max(1, (int) image.getHeight());
        PixelReader reader = image.getPixelReader();
        boolean[][] mask = new boolean[height][width];
        for (int y = 0; y < height && y < image.getHeight(); y++) {
            for (int x = 0; x < width && x < image.getWidth(); x++) {
                mask[y][x] = (reader.getArgb(x, y) >>> 24) >= 128;
            }
        }
        return mask;
    }

    private static Font loadFont() {
        try (InputStream input = FallbackFont.class.getResourceAsStream(RESOURCE)) {
            Font loaded = input == null ? null : Font.loadFont(input, SIZE);
            return loaded != null ? loaded : Font.font(SIZE);
        } catch (IOException exception) {
            return Font.font(SIZE);
        }
    }
}
