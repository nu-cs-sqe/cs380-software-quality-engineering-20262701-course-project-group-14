package ui;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

/**
 * Tiny pixel-art toolkit. Sprites are described as strings (see PixelData),
 * every character is one "pixel" and is turned into a crisp, pre-scaled image.
 */
final class PixelArt {

    // ---- UI colours (ARGB) ----
    static final int INK = 0xFF141026;
    static final int CREAM = 0xFFFFF1D6;
    static final int AMBER = 0xFFFFB84D;
    static final int VIOLET = 0xFF7B68E0;
    static final int LILAC = 0xFFB9A8FF;
    static final int PINK = 0xFFFF8FAB;
    static final int VIOLET_SHADOW = 0xFF5B47B8;

    /** Default sprite palette: cream cat with amber patch. */
    static final Map<Character, Integer> DEFAULT_PALETTE = new HashMap<>();
    /** Night variant: violet cat with yellow patch. */
    static final Map<Character, Integer> NIGHT_PALETTE = new HashMap<>();

    static {
        DEFAULT_PALETTE.put('k', 0xFF141026);
        DEFAULT_PALETTE.put('c', 0xFFFFF1D6);
        DEFAULT_PALETTE.put('s', 0xFFE6CBA2);
        DEFAULT_PALETTE.put('o', 0xFFFF9B3D);
        DEFAULT_PALETTE.put('d', 0xFFD96A2B);
        DEFAULT_PALETTE.put('p', 0xFFFF8FAB);
        DEFAULT_PALETTE.put('q', 0xFFE5688A);
        DEFAULT_PALETTE.put('w', 0xFFFFFFFF);
        DEFAULT_PALETTE.put('y', 0xFFFFD166);
        DEFAULT_PALETTE.put('r', 0xFFFF5D4A);
        DEFAULT_PALETTE.put('v', 0xFF4F3FAE);
        DEFAULT_PALETTE.put('u', 0xFF7B68E0);
        DEFAULT_PALETTE.put('n', 0xFF2B2257);
        DEFAULT_PALETTE.put('g', 0xFF8F7BE0);
        DEFAULT_PALETTE.put('a', 0xFF3A2F7A);
        DEFAULT_PALETTE.put('b', 0xFF4B3C94);
        DEFAULT_PALETTE.put('m', 0xFFFFE9A8);
        DEFAULT_PALETTE.put('t', 0xFFC9A27A);

        NIGHT_PALETTE.putAll(DEFAULT_PALETTE);
        NIGHT_PALETTE.put('c', 0xFF9C8CF0);
        NIGHT_PALETTE.put('s', 0xFF7766D6);
        NIGHT_PALETTE.put('o', 0xFFFFD166);
        NIGHT_PALETTE.put('d', 0xFFE0A93C);
    }

    private PixelArt() {
    }

    static Map<Character, Integer> withColor(Map<Character, Integer> base, char key, int argb) {
        Map<Character, Integer> copy = new HashMap<>(base);
        copy.put(key, argb);
        return copy;
    }

    static String[] mirror(String[] rows) {
        String[] out = new String[rows.length];
        for (int i = 0; i < rows.length; i++) {
            out[i] = new StringBuilder(rows[i]).reverse().toString();
        }
        return out;
    }

    // ------------------------------------------------------------------
    // images
    // ------------------------------------------------------------------

    static WritableImage sprite(String[] rows, int scale) {
        return sprite(rows, scale, DEFAULT_PALETTE);
    }

    static WritableImage sprite(String[] rows, int scale, Map<Character, Integer> palette) {
        Buffer b = new Buffer(rows[0].length() * scale, rows.length * scale);
        b.sprite(rows, palette, 0, 0, scale);
        return b.toImage();
    }

    /**
     * Bitmap-font text. Rows above {@code split} use {@code top}, the others {@code bottom}.
     * A non-zero {@code shadow} adds a one-pixel extruded drop shadow.
     */
    static WritableImage textImage(String s, int scale, int top, int bottom, int split, int shadow) {
        int extra = shadow != 0 ? scale : 0;
        Buffer b = new Buffer(textWidth(s, scale) + extra, 7 * scale + extra);
        if (shadow != 0) {
            b.text(s, extra, extra, scale, shadow, shadow, 7);
            b.text(s, 0, extra, scale, shadow, shadow, 7);
            b.text(s, extra, 0, scale, shadow, shadow, 7);
        }
        b.text(s, 0, 0, scale, top, bottom, split);
        return b.toImage();
    }

    static int textWidth(String s, int scale) {
        return Math.max(1, s.length() * 6 - 1) * scale;
    }

    static ImageView view(WritableImage image) {
        ImageView view = new ImageView(image);
        view.setSmooth(false);
        view.setMouseTransparent(true);
        return view;
    }

    // ------------------------------------------------------------------
    // pixel buffer
    // ------------------------------------------------------------------

    static final class Buffer {
        final int width;
        final int height;
        final int[] px;

        Buffer(int width, int height) {
            this.width = width;
            this.height = height;
            this.px = new int[width * height];
        }

        void rect(int x, int y, int w, int h, int argb) {
            for (int yy = Math.max(0, y); yy < Math.min(height, y + h); yy++) {
                for (int xx = Math.max(0, x); xx < Math.min(width, x + w); xx++) {
                    px[yy * width + xx] = argb;
                }
            }
        }

        void sprite(String[] rows, Map<Character, Integer> palette, int x, int y, int scale) {
            for (int r = 0; r < rows.length; r++) {
                String row = rows[r];
                for (int c = 0; c < row.length(); c++) {
                    Integer color = palette.get(row.charAt(c));
                    if (color != null) {
                        rect(x + c * scale, y + r * scale, scale, scale, color);
                    }
                }
            }
        }

        void text(String s, int x, int y, int scale, int top, int bottom, int split) {
            for (int i = 0; i < s.length(); i++) {
                String[] glyph = PixelData.GLYPHS.get(Character.toUpperCase(s.charAt(i)));
                if (glyph == null) {
                    continue;
                }
                for (int r = 0; r < glyph.length; r++) {
                    int color = r < split ? top : bottom;
                    for (int c = 0; c < glyph[r].length(); c++) {
                        if (glyph[r].charAt(c) == '1') {
                            rect(x + (i * 6 + c) * scale, y + r * scale, scale, scale, color);
                        }
                    }
                }
            }
        }

        WritableImage toImage() {
            WritableImage image = new WritableImage(width, height);
            image.getPixelWriter().setPixels(
                    0, 0, width, height, PixelFormat.getIntArgbInstance(), px, 0, width);
            return image;
        }
    }
}
