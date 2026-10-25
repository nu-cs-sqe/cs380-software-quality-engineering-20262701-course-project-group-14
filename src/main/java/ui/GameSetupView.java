package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public final class GameSetupView extends VBox {

    private final TextField playerCountInput = new TextField();
    private final Button startGameButton = new Button("Start Game");
    private final Label messageLabel = new Label();

    public GameSetupView() {
        Label title = new Label("Exploding Kittens");
        Label instruction = new Label("Enter the number of players (2-5)");

        playerCountInput.setPromptText("2-5");
        playerCountInput.setMaxWidth(180);

        setAlignment(Pos.CENTER);
        setSpacing(14);
        setPadding(new Insets(32));

        getChildren().addAll(
                title,
                instruction,
                playerCountInput,
                startGameButton,
                messageLabel
        );
    }

    public String getPlayerCountInput() {
        return playerCountInput.getText();
    }

    public void setOnStart(Runnable action) {
        startGameButton.setOnAction(event -> action.run());
    }

    public void showMessage(String message) {
        messageLabel.setText(message);
    }
}
