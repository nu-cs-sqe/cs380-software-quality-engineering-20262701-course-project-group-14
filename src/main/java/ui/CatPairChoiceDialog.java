package ui;

import java.text.MessageFormat;
import java.util.Map;
import java.util.OptionalInt;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

final class CatPairChoiceDialog {
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("ui.CatPairMessages");

    private CatPairChoiceDialog() {
    }

    static String text(String key, Object... values) {
        return new MessageFormat(MESSAGES.getString(key), MESSAGES.getLocale()).format(values);
    }

    static OptionalInt show(Window owner, String stylesheet, String titleKey,
                            Map<Integer, String> options) {
        if (options.isEmpty()) {
            return OptionalInt.empty();
        }

        Dialog<Integer> dialog = new Dialog<>();
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(text(titleKey));
        DialogPane pane = dialog.getDialogPane();
        pane.setId("cat-pair-dialog");
        pane.getStylesheets().add(stylesheet);
        pane.getStyleClass().add("cat-pair-dialog");

        ListView<Option> choices = new ListView<>();
        choices.setId("cat-pair-options");
        choices.setAccessibleText(text(titleKey));
        options.forEach((value, label) -> choices.getItems().add(new Option(value, label)));
        choices.setPrefHeight(46 * Math.min(options.size(), 5) + 4);
        choices.setFixedCellSize(46);

        Label title = new Label(text(titleKey));
        title.getStyleClass().add("cat-pair-title");
        pane.setContent(new VBox(12, title, choices));
        configureButtons(dialog, choices);
        dialog.setOnShown(event -> choices.requestFocus());
        return dialog.showAndWait().map(OptionalInt::of).orElseGet(OptionalInt::empty);
    }

    private static void configureButtons(Dialog<Integer> dialog, ListView<Option> choices) {
        ButtonType confirm = new ButtonType(text("confirm"), ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(text("cancel"), ButtonData.CANCEL_CLOSE);
        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(cancel, confirm);
        Button confirmButton = (Button) pane.lookupButton(confirm);
        confirmButton.disableProperty().bind(choices.getSelectionModel().selectedItemProperty().isNull());
        dialog.setResultConverter(button -> button == confirm
                ? choices.getSelectionModel().getSelectedItem().value : null);
    }

    private static final class Option {
        private final int value;
        private final String label;

        private Option(int value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
