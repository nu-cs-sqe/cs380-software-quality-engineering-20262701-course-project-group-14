package ui;

/**
 * Colour tokens of the upgraded "cocoa tavern" gameplay theme (ARGB).
 * Keep matching hex values in game-play.css in sync.
 */
final class GameTheme {

    // darkened tavern walls & tabletop surfaces (high contrast against cards)
    static final int WALL_TOP = 0xFF281812;
    static final int WALL_BOTTOM = 0xFF170E0A;
    static final int TABLE_TOP = 0xFF3A241A;
    static final int TABLE_BOTTOM = 0xFF22140E;
    static final int GLOW = 0xFF5C3D2E;
    static final int PANEL = 0xFF2A1B14;
    static final int RAISED = 0xFF3F2B21;
    static final int TRAY = 0xFF261812;
    static final int PLATE = 0xFF160D09;
    static final int LINE = 0xFF9C785E;

    // burnished brass & iron
    static final int BRASS = 0xFFE0AA58;
    static final int BRASS_BRIGHT = 0xFFFFE08C;
    static final int BRASS_DARK = 0xFF7A5022;

    // text
    static final int CREAM = 0xFFFFF1D6;
    static final int SAND = 0xFFE8CFA8;
    static final int MUTED = 0xFFBCA284;

    // accents
    static final int AMBER = 0xFFFFB84D;
    static final int DANGER = 0xFFFF4D63;

    // card material palettes
    static final int PARCHMENT = 0xFFF3E3C3;
    static final int PARCHMENT_LIGHT = 0xFFFFF8E8;
    static final int PARCHMENT_SHADE = 0xFFDEC69B;
    static final int VELLUM = 0xFFECD9B4;
    static final int EMERALD_FILL = 0xFF1B4D3E;
    static final int EMERALD_LIGHT = 0xFF2B6E59;
    static final int CRIMSON_FILL = 0xFF3D1220;
    static final int CRIMSON_LIGHT = 0xFF591B2F;
    static final int CARD_BACK_FIELD = 0xFF381822;

    // card roles
    static final int ROLE_DANGER = 0xFFFF4D63;
    static final int ROLE_SAFETY = 0xFF5FD3A0;
    static final int ROLE_ATTACK = 0xFFFF9B3D;
    static final int ROLE_SKIP = 0xFFFFD166;
    static final int ROLE_FUTURE = 0xFF7CC4FF;
    static final int ROLE_SHUFFLE = 0xFF9AA5FF;
    static final int ROLE_FAVOR = 0xFFE59BFF;
    static final int ROLE_NOPE = 0xFFB7B1A6;
    static final int ROLE_CAT = 0xFFD8C3A0;

    // "not wired yet" card look
    static final int SOON_BORDER = 0xFF6B5344;
    static final int SOON_BAND = 0xFF4F3C30;
    static final int SOON_FACE = 0xFF33241D;
    static final int SOON_ICON = 0xFF8C7362;

    private GameTheme() {
    }

    /** "#RRGGBB" for use in inline CSS. */
    static String css(int argb) {
        return String.format("#%06X", argb & 0xFFFFFF);
    }
}
