package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

/**
 * Tiny pixel-art toolkit. Sprites are described as strings (see PixelData),
 * every character is one "pixel" and is turned into a crisp, pre-scaled image.
 */
final class PixelArt {

    // ---- setup-page colours (ARGB) ----
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
    /** Tavern variant: same cats, but the warning red matches the game theme. */
    static final Map<Character, Integer> TAVERN_PALETTE = new HashMap<>();

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

        TAVERN_PALETTE.putAll(DEFAULT_PALETTE);
        TAVERN_PALETTE.put('r', GameTheme.DANGER);
    }

    private PixelArt() {
    }

    static Map<Character, Integer> withColor(Map<Character, Integer> base, char key, int argb) {
        Map<Character, Integer> copy = new HashMap<>(base);
        copy.put(key, argb);
        return copy;
    }

    static Map<Character, Integer> withColors(Map<Character, Integer> base, String keys, int... argb) {
        Map<Character, Integer> copy = new HashMap<>(base);
        for (int i = 0; i < keys.length(); i++) {
            copy.put(keys.charAt(i), argb[i]);
        }
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

    /** Every non-transparent pixel of the sprite is painted with one colour. */
    static WritableImage mono(String[] rows, int scale, int argb) {
        Buffer b = new Buffer(rows[0].length() * scale, rows.length * scale);
        b.mono(rows, argb, 0, 0, scale);
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

    static ImageView textView(String s, int scale, int color) {
        return view(textImage(s, scale, color, color, 7, 0));
    }

    static int textWidth(String s, int scale) {
        return Math.max(1, s.length() * 6 - 1) * scale;
    }

    /** True when every character can be drawn with the bitmap font (spaces are fine). */
    static boolean canRender(String upperCaseText) {
        if (upperCaseText == null || upperCaseText.isBlank()) {
            return false;
        }
        for (int i = 0; i < upperCaseText.length(); i++) {
            char c = upperCaseText.charAt(i);
            if (c != ' ' && !PixelData.GLYPHS.containsKey(c)) {
                return false;
            }
        }
        return true;
    }

    /** Word-wrapped bitmap-font paragraph (upper case). Lines are at most {@code maxWidth} pixels wide. */
    static WritableImage paragraph(String text, int scale, int color, int maxWidth) {
        String upper = text.toUpperCase(Locale.ROOT);
        int maxChars = Math.max(1, (int) ((maxWidth / (double) scale + 1) / 6));
        List<String> lines = wrapWords(upper, maxChars);
        int lineHeight = 7 * scale;
        int gap = 2 * scale;
        int width = 1;
        for (String line : lines) {
            width = Math.max(width, textWidth(line, scale));
        }
        int height = lines.size() * lineHeight + (lines.size() - 1) * gap;
        Buffer b = new Buffer(width, Math.max(1, height));
        for (int i = 0; i < lines.size(); i++) {
            b.text(lines.get(i), 0, i * (lineHeight + gap), scale, color, color, 7);
        }
        return b.toImage();
    }

    static ImageView paragraphView(String text, int scale, int color, int maxWidth) {
        ImageView view = view(paragraph(text, scale, color, maxWidth));
        view.setAccessibleText(text);
        return view;
    }

    private static List<String> wrapWords(String text, int maxChars) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.trim().split("\\s+")) {
            while (word.length() > maxChars) {
                if (line.length() > 0) {
                    lines.add(line.toString());
                    line.setLength(0);
                }
                lines.add(word.substring(0, maxChars));
                word = word.substring(maxChars);
            }
            if (line.length() == 0) {
                line.append(word);
            } else if (line.length() + 1 + word.length() <= maxChars) {
                line.append(' ').append(word);
            } else {
                lines.add(line.toString());
                line.setLength(0);
                line.append(word);
            }
        }
        if (line.length() > 0 || lines.isEmpty()) {
            lines.add(line.toString());
        }
        return lines;
    }

    /** Ornate tarot-inspired brass & burgundy card back with central paw medallion. */
    static WritableImage cardBack(int w, int h) {
        Buffer b = new Buffer(w, h);

        // Outer beveled brass frame
        b.notched(0, 0, w, h, GameTheme.BRASS, 4);
        b.rect(4, 0, w - 8, 2, GameTheme.BRASS_BRIGHT);
        b.rect(4, h - 2, w - 8, 2, GameTheme.BRASS_DARK);

        // Dark ink recess & deep burgundy field
        b.notched(4, 4, w - 8, h - 8, GameTheme.PLATE, 4);
        b.notched(6, 6, w - 12, h - 12, GameTheme.CARD_BACK_FIELD, 4);

        // Subtle geometric lattice inside the burgundy field
        int lattice = 0xFF4A202D;
        for (int y = 14; y < h - 14; y += 8) {
            for (int x = 14; x < w - 14; x += 8) {
                if (((x + y) / 8) % 2 == 0) {
                    b.rect(x, y, 4, 4, lattice);
                }
            }
        }

        // Inner double-line brass filigree border
        b.rect(10, 10, w - 20, 2, GameTheme.BRASS);
        b.rect(10, h - 12, w - 20, 2, GameTheme.BRASS);
        b.rect(10, 10, 2, h - 20, GameTheme.BRASS);
        b.rect(w - 12, 10, 2, h - 20, GameTheme.BRASS);

        // Corner diamond rosettes
        int[][] corners = {{12, 12}, {w - 18, 12}, {12, h - 18}, {w - 18, h - 18}};
        for (int[] c : corners) {
            b.rect(c[0] + 2, c[1], 2, 6, GameTheme.BRASS_BRIGHT);
            b.rect(c[0], c[1] + 2, 6, 2, GameTheme.BRASS_BRIGHT);
        }

        // Central notched brass & ink medallion
        int medW = 54;
        int medH = 54;
        int mx = (w - medW) / 2;
        int my = (h - medH) / 2;
        b.notched(mx, my, medW, medH, GameTheme.BRASS, 6);
        b.notched(mx + 2, my + 2, medW - 4, medH - 4, GameTheme.PLATE, 6);

        Map<Character, Integer> pal = new HashMap<>();
        pal.put('g', GameTheme.BRASS_BRIGHT);
        pal.put('k', GameTheme.BRASS_DARK);
        int scale = 3;
        int pw = PixelData.PAW[0].length() * scale;
        int ph = PixelData.PAW.length * scale;
        b.sprite(PixelData.PAW, pal, (w - pw) / 2, (h - ph) / 2, scale);
        return b.toImage();
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

        /** A rectangle whose four corners are cut away by {@code n} pixels. */
        void notched(int x, int y, int w, int h, int argb, int n) {
            rect(x + n, y, w - 2 * n, h, argb);
            rect(x, y + n, w, h - 2 * n, argb);
        }

        /** Dashed 4px frame drawn along the edge of a rectangle. */
        void dashed(int x, int y, int w, int h, int argb) {
            int seg = 8;
            for (int i = x + 4; i < x + w - 4; i += seg * 2) {
                int len = Math.min(seg, x + w - 4 - i);
                rect(i, y, len, 4, argb);
                rect(i, y + h - 4, len, 4, argb);
            }
            for (int j = y + 4; j < y + h - 4; j += seg * 2) {
                int len = Math.min(seg, y + h - 4 - j);
                rect(x, j, 4, len, argb);
                rect(x + w - 4, j, 4, len, argb);
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

        void mono(String[] rows, int argb, int x, int y, int scale) {
            for (int r = 0; r < rows.length; r++) {
                String row = rows[r];
                for (int c = 0; c < row.length(); c++) {
                    if (row.charAt(c) != '.') {
                        rect(x + c * scale, y + r * scale, scale, scale, argb);
                    }
                }
            }
        }

        /** Renders a monochrome icon with a crisp dark outline and top-left pixel highlight. */
        void outlinedMono(String[] rows, int fillArgb, int outlineArgb, int highlightArgb,
                          int x, int y, int scale) {
            int h = rows.length;
            int w = rows[0].length();
            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int r = 0; r < h; r++) {
                for (int c = 0; c < w; c++) {
                    if (rows[r].charAt(c) == '.') {
                        continue;
                    }
                    for (int[] d : dirs) {
                        int nr = r + d[0];
                        int nc = c + d[1];
                        if (nr < 0 || nr >= h || nc < 0 || nc >= w || rows[nr].charAt(nc) == '.') {
                            rect(x + nc * scale, y + nr * scale, scale, scale, outlineArgb);
                        }
                    }
                }
            }
            for (int r = 0; r < h; r++) {
                for (int c = 0; c < w; c++) {
                    if (rows[r].charAt(c) == '.') {
                        continue;
                    }
                    boolean topExposed = (r == 0 || rows[r - 1].charAt(c) == '.');
                    int pxColor = (highlightArgb != 0 && topExposed) ? highlightArgb : fillArgb;
                    rect(x + c * scale, y + r * scale, scale, scale, pxColor);
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

        /** Mixes every visible pixel with {@code argb} (alpha is the mix amount, 0..1). */
        void blend(int argb, double amount) {
            int tr = (argb >> 16) & 0xFF;
            int tg = (argb >> 8) & 0xFF;
            int tb = argb & 0xFF;
            for (int i = 0; i < px.length; i++) {
                int p = px[i];
                if ((p >>> 24) == 0) {
                    continue;
                }
                int r = (int) Math.round(((p >> 16) & 0xFF) * (1 - amount) + tr * amount);
                int g = (int) Math.round(((p >> 8) & 0xFF) * (1 - amount) + tg * amount);
                int b = (int) Math.round((p & 0xFF) * (1 - amount) + tb * amount);
                px[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
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
