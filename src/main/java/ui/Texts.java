package ui;

import localization.Language;
import localization.LanguageCatalog;
import localization.Messages;
import java.util.List;
import java.util.Locale;
import javafx.scene.Parent;

/**
 * Player-visible text in the language chosen on the setup screen. One game window shows one
 * language at a time, so the views share this choice instead of passing it through every
 * constructor.
 */
final class Texts {
    private static final LanguageCatalog CATALOG = LanguageCatalog.load();
    private static Messages messages = Messages.forLanguage(CATALOG.getDefaultLanguage());

    private Texts() {
    }

    static List<Language> languages() {
        return CATALOG.getLanguages();
    }

    static Language language() {
        return messages.getLanguage();
    }

    static Locale locale() {
        return messages.getLanguage().getLocale();
    }

    static void use(Language language) {
        messages = Messages.forLanguage(language);
    }

    static String get(String key, Object... arguments) {
        return messages.text(key, arguments);
    }

    /**
     * Gives labels and controls under {@code root} the bundled pixel font when the language
     * needs it; with the built-in bitmap font the stylesheet's font stays in place.
     */
    static void applyFont(Parent root) {
        if ("fallback".equals(get("ui.font"))) {
            root.setStyle("-fx-font-family: '" + FallbackFont.family() + "';");
        } else {
            root.setStyle("");
        }
    }
}
