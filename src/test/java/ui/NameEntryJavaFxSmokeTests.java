package ui;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "CS380_RUN_JAVAFX_SMOKE", matches = "1")
class NameEntryJavaFxSmokeTests {
    @BeforeAll
    static void startJavaFx() throws Exception {
        JavaFxToolkit.start();
    }

    @Test
    void TypeEmoji_DropsItAndExplains() throws Exception {
        JavaFxToolkit.run(() -> {
            GameSetupView view = nameStep();
            TextField field = firstName(view);

            field.replaceText(0, 0, "Ada😀");

            assertEquals("Ada", field.getText());
            assertEquals(Texts.get("setup.error.name-characters"), message(view));
        });
    }

    @Test
    void TypeCharacterMissingFromFont_DropsIt() throws Exception {
        JavaFxToolkit.run(() -> {
            TextField field = firstName(nameStep());

            field.replaceText(0, 0, "暹罗");

            assertEquals("罗", field.getText());
        });
    }

    @Test
    void PasteLongTextWithLineBreak_KeepsTwelveCharactersOnOneLine() throws Exception {
        JavaFxToolkit.run(() -> {
            GameSetupView view = nameStep();
            TextField field = firstName(view);

            field.replaceText(0, 0, "Ada\nLovelace Byron");

            assertEquals("Ada Lovelace", field.getText());
            assertEquals(12, field.getCaretPosition());
            assertFalse(message(view).equals(Texts.get("setup.error.name-characters")));
        });
    }

    @Test
    void TypeIntoFullField_KeepsName() throws Exception {
        JavaFxToolkit.run(() -> {
            TextField field = firstName(nameStep());
            field.replaceText(0, 0, "ABCDEFGHIJKL");

            field.replaceText(12, 12, "M");

            assertEquals("ABCDEFGHIJKL", field.getText());
        });
    }

    @Test
    void ZeroWidthSpace_IsDropped() throws Exception {
        JavaFxToolkit.run(() -> {
            GameSetupView view = nameStep();
            TextField field = firstName(view);

            field.replaceText(0, 0, "​");

            assertEquals("", field.getText());
            assertTrue(view.getPlayerNames().get(0).startsWith("Player"));
        });
    }

    private static GameSetupView nameStep() {
        Texts.use(Texts.languages().get(0));
        GameSetupView view = new GameSetupView();
        new Scene(view, 900, 760);
        view.showNameEntry(2);
        return view;
    }

    private static TextField firstName(GameSetupView view) {
        List<TextField> fields = view.lookupAll(".name-field").stream()
                .map(TextField.class::cast).collect(Collectors.toList());
        return fields.get(0);
    }

    private static String message(GameSetupView view) {
        return ((Label) view.lookup(".message-label")).getText();
    }
}
