package ui;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javafx.scene.image.WritableImage;

/**
 * Everything the view knows about the 14 card types: display name, role colour,
 * effect text and icon, rendered as tactile parchment-and-brass tavern cards.
 */
final class CardCatalog {

    enum Icon {
        BOMB, SCISSORS, CLAW, SKIP, GIFT, SHUFFLE, EYE, NOPE, PAW,
        CAT_TACO, CAT_MELON, CAT_POTATO, CAT_BEARD, CAT_RAINBOW
    }

    enum Face { READY, HOVER, SOON, LOCKED }

    static final class Info {
        final String key;
        final String name;
        final String tag;
        final int role;
        final String effect;
        final Icon icon;

        Info(String key, String name, String tag, int role, String effect, Icon icon) {
            this.key = key;
            this.name = name;
            this.tag = tag;
            this.role = role;
            this.effect = effect;
            this.icon = icon;
        }
    }

    static final Set<String> NOT_WIRED_YET = Set.of("FAVOR", "NOPE");

    private static final String CAT_TEXT =
            "No effect on its own. Play a matching pair to steal a random card.";
    private static final Map<String, Info> INFO = new HashMap<>();

    static {
        add("EXPLODINGKITTEN", "Exploding Kitten", "DANGER", GameTheme.ROLE_DANGER, Icon.BOMB,
                "Draw it and you explode, unless you can play a Defuse.");
        add("DEFUSE", "Defuse", "SAFETY", GameTheme.ROLE_SAFETY, Icon.SCISSORS,
                "Cancels an Exploding Kitten. Secretly put the Kitten back anywhere in the draw pile.");
        add("ATTACK", "Attack", "TEMPO", GameTheme.ROLE_ATTACK, Icon.CLAW,
                "End your turn without drawing. The next player takes two turns in a row.");
        add("SKIP", "Skip", "TEMPO", GameTheme.ROLE_SKIP, Icon.SKIP,
                "End your current turn without drawing a card.");
        add("FAVOR", "Favor", "PLAYER", GameTheme.ROLE_FAVOR, Icon.GIFT,
                "Pick a player. They choose one card from their hand and give it to you.");
        add("SHUFFLE", "Shuffle", "DECK", GameTheme.ROLE_SHUFFLE, Icon.SHUFFLE,
                "Shuffle the draw pile. Nobody learns what is in it.");
        add("SEETHEFUTURE", "See The Future", "DECK", GameTheme.ROLE_FUTURE, Icon.EYE,
                "Privately look at the top 3 cards of the draw pile. The order stays the same.");
        add("NOPE", "Nope", "PLAYER", GameTheme.ROLE_NOPE, Icon.NOPE,
                "Cancel an action. It does not work on Exploding Kitten or Defuse.");
        add("SNIFFTEST", "Sniff Test", "DECK", GameTheme.ROLE_SNIFF, Icon.PAW,
                "Sniff the top card. If it is an Exploding Kitten, move it to the bottom. "
                        + "Your turn continues.");
        add("TACOCAT", "Tacocat", "CAT", GameTheme.ROLE_CAT, Icon.CAT_TACO, CAT_TEXT);
        add("CATTERMELON", "Cattermelon", "CAT", GameTheme.ROLE_CAT, Icon.CAT_MELON, CAT_TEXT);
        add("HAIRYPOTATOCAT", "Hairy Potato Cat", "CAT", GameTheme.ROLE_CAT, Icon.CAT_POTATO, CAT_TEXT);
        add("BEARDCAT", "Beard Cat", "CAT", GameTheme.ROLE_CAT, Icon.CAT_BEARD, CAT_TEXT);
        add("RAINBOWRALPHINGCAT", "Rainbow-Ralphing Cat", "CAT", GameTheme.ROLE_CAT,
                Icon.CAT_RAINBOW, CAT_TEXT);
    }

    private static final Map<String, WritableImage> FACES = new HashMap<>();
    private static final Map<String, WritableImage> ICONS = new HashMap<>();

    private CardCatalog() {
    }

    private static void add(String key, String name, String tag, int role, Icon icon, String effect) {
        INFO.put(key, new Info(key, name, tag, role, effect, icon));
    }

    static Info lookup(String enumName) {
        String key = enumName.replaceAll("[^A-Za-z]", "").toUpperCase(Locale.ROOT);
        Info known = INFO.get(key);
        if (known != null) {
            return known;
        }
        return new Info(key, titleCase(enumName), "CARD", GameTheme.ROLE_CAT,
                "No description is available for this card yet.", Icon.CAT_BEARD);
    }

    static boolean isWired(Info info) {
        return !NOT_WIRED_YET.contains(info.key);
    }

    private static String titleCase(String enumName) {
        String words = enumName.replace('_', ' ').toLowerCase(Locale.ROOT);
        StringBuilder out = new StringBuilder(words.length());
        boolean capital = true;
        for (char c : words.toCharArray()) {
            out.append(capital ? Character.toUpperCase(c) : c);
            capital = c == ' ';
        }
        return out.toString();
    }

    // ------------------------------------------------------------------
    // rendering
    // ------------------------------------------------------------------

    static int cardWidth(boolean compact) {
        return compact ? 86 : 98;
    }

    static int cardHeight(boolean compact) {
        return compact ? 134 : 150;
    }

    /** Tactile pixel card with role header, beveled vignette art box, and brass rivets. */
    static WritableImage face(Info info, boolean compact, Face state) {
        String cacheKey = info.key + "|" + compact + "|" + state;
        WritableImage cached = FACES.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        int w = cardWidth(compact);
        int h = cardHeight(compact);
        PixelArt.Buffer b = new PixelArt.Buffer(w, h);

        boolean soon = state == Face.SOON;
        int outerInk = 0xFF120A07;
        int border = info.role;
        int band = info.role;
        int fill = 0xFF36241B;
        if (soon) {
            border = GameTheme.SOON_BORDER;
            band = GameTheme.SOON_BAND;
            fill = GameTheme.SOON_FACE;
        } else if (state == Face.HOVER) {
            border = GameTheme.CREAM;
            fill = 0xFF452E23;
        }

        int bandHeight = 16;
        int plateHeight = 48;

        // Crisp 2px dark outer silhouette + notched role frame
        b.notched(0, 0, w, h, outerInk, 4);
        b.notched(2, 2, w - 4, h - 4, border, 4);
        b.notched(6, 6, w - 12, h - 12, fill, 2);

        // Beveled colour header band
        b.rect(6, 6, w - 12, bandHeight, band);
        b.rect(6, 6, w - 12, 2, mix(band, 0xFFFFFFFF, 0.38));
        b.rect(6, 6 + bandHeight - 2, w - 12, 2, mix(band, 0xFF000000, 0.38));

        // Art well with subtle warm top light & side vignette
        int artTop = 6 + bandHeight;
        int artHeight = h - 12 - bandHeight - plateHeight;
        b.rect(6, artTop, w - 12, artHeight, mix(fill, 0xFFFFFFFF, 0.05));
        b.rect(6, artTop, w - 12, artHeight / 3, mix(fill, 0xFFFFFFFF, 0.12));
        b.rect(6, artTop + artHeight / 3, w - 12, artHeight / 3, mix(fill, 0xFFFFFFFF, 0.07));
        // Side shadow pillars inside art box
        b.rect(6, artTop, 3, artHeight, mix(fill, 0xFF000000, 0.22));
        b.rect(w - 9, artTop, 3, artHeight, mix(fill, 0xFF000000, 0.22));

        // Ornate corner filigree brackets + brass rivets
        int bracket = mix(soon ? GameTheme.SOON_ICON : info.role, fill, 0.45);
        int inset = 5;
        int top = artTop + inset;
        int bottom = artTop + artHeight - inset - 2;
        int left = 6 + inset;
        int right = w - 6 - inset - 2;
        for (int i = 0; i < 6; i++) {
            b.rect(left + i, top, 1, 2, bracket);
            b.rect(right - i, top, 1, 2, bracket);
            b.rect(left + i, bottom, 1, 2, bracket);
            b.rect(right - i, bottom, 1, 2, bracket);
        }
        b.rect(left, top, 2, 6, bracket);
        b.rect(right, top, 2, 6, bracket);
        b.rect(left, bottom - 4, 2, 6, bracket);
        b.rect(right, bottom - 4, 2, 6, bracket);

        // Dark leather name plate with role accent divider
        b.rect(6, h - 6 - plateHeight, w - 12, plateHeight, 0xFF1A100C);
        b.rect(6, h - 6 - plateHeight, w - 12, 2, mix(0xFF1A100C, soon ? GameTheme.SOON_ICON : info.role, 0.6));

        drawIcon(b, info.icon, w / 2, artTop + artHeight / 2, compact,
                soon ? GameTheme.SOON_ICON : info.role, soon, mix(fill, 0xFF000000, 0.55));

        if (soon) {
            b.dashed(2, 2, w - 4, h - 4, GameTheme.LINE);
        }
        if (state == Face.LOCKED) {
            b.blend(0xFF1E130E, 0.52);
        }
        WritableImage image = b.toImage();
        FACES.put(cacheKey, image);
        return image;
    }

    private static int mix(int a, int b, double t) {
        int r = (int) Math.round(((a >> 16) & 0xFF) * (1 - t) + ((b >> 16) & 0xFF) * t);
        int g = (int) Math.round(((a >> 8) & 0xFF) * (1 - t) + ((b >> 8) & 0xFF) * t);
        int bl = (int) Math.round((a & 0xFF) * (1 - t) + (b & 0xFF) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }

    static WritableImage icon(Info info, boolean small) {
        String cacheKey = info.key + "|icon|" + small;
        WritableImage cached = ICONS.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        String[] rows = rowsFor(info.icon);
        int scale = scaleFor(info.icon, small);
        PixelArt.Buffer b = new PixelArt.Buffer(rows[0].length() * scale, rows.length * scale);
        paint(b, info.icon, rows, scale, 0, 0, info.role, false, 0);
        WritableImage image = b.toImage();
        ICONS.put(cacheKey, image);
        return image;
    }

    private static void drawIcon(PixelArt.Buffer b, Icon icon, int cx, int cy,
                                 boolean compact, int color, boolean flat, int shadow) {
        String[] rows = rowsFor(icon);
        int scale = scaleFor(icon, compact);
        int x = cx - rows[0].length() * scale / 2;
        int y = cy - rows.length * scale / 2;
        paint(b, icon, rows, scale, x, y, color, flat, flat ? 0 : shadow);
    }

    private static void paint(PixelArt.Buffer b, Icon icon, String[] rows, int scale,
                              int x, int y, int color, boolean flat, int shadow) {
        if (shadow != 0) {
            b.mono(rows, shadow, x + scale, y + scale, scale);
        }
        if (flat) {
            b.mono(rows, color, x, y, scale);
        } else if (icon == Icon.BOMB) {
            b.sprite(rows, PixelArt.TAVERN_PALETTE, x, y, scale);
        } else if (isCat(icon)) {
            b.sprite(rows, catPalette(icon), x, y, scale);
        } else {
            b.mono(rows, color, x, y, scale);
        }
    }

    private static boolean isCat(Icon icon) {
        return icon == Icon.CAT_TACO || icon == Icon.CAT_MELON || icon == Icon.CAT_POTATO
                || icon == Icon.CAT_BEARD || icon == Icon.CAT_RAINBOW;
    }

    private static int scaleFor(Icon icon, boolean small) {
        if (icon == Icon.BOMB || isCat(icon)) {
            return small ? 2 : 3;
        }
        return small ? 3 : 4;
    }

    private static String[] rowsFor(Icon icon) {
        switch (icon) {
            case BOMB:
                return PixelData.BOMB;
            case SCISSORS:
                return PixelData.ICON_SCISSORS;
            case CLAW:
                return PixelData.ICON_CLAW;
            case SKIP:
                return PixelData.ICON_SKIP;
            case GIFT:
                return PixelData.ICON_GIFT;
            case SHUFFLE:
                return PixelData.ICON_SHUFFLE;
            case EYE:
                return PixelData.ICON_EYE;
            case NOPE:
                return PixelData.ICON_NOPE;
            case PAW:
                return PixelData.ICON_PAW;
            default:
                return PixelData.HEAD_OPEN;
        }
    }

    private static Map<Character, Integer> catPalette(Icon icon) {
        Map<Character, Integer> base = PixelArt.TAVERN_PALETTE;
        switch (icon) {
            case CAT_MELON:
                return PixelArt.withColors(base, "csod",
                        0xFFFFB3C6, 0xFFE5688A, 0xFF7FD99A, 0xFF4FAE72);
            case CAT_POTATO:
                return PixelArt.withColors(base, "csod",
                        0xFFD9B58A, 0xFFB38E62, 0xFF8A6A47, 0xFF6B4F33);
            case CAT_BEARD:
                return PixelArt.withColors(base, "csod",
                        0xFFD5CFF0, 0xFFA9A2CC, 0xFF8F86C9, 0xFF6E65A8);
            case CAT_RAINBOW:
                return PixelArt.withColors(base, "csod",
                        0xFFFFF1D6, 0xFFE6CBA2, 0xFFFF8FAB, 0xFF7CC4FF);
            default:
                return base;
        }
    }
}
