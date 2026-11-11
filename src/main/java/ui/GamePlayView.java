package ui;

import controller.GamePlayViewPort;
import domain.CardType;
import domain.GameSnapshot;
import domain.TurnPhase;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class GamePlayView extends BorderPane
        implements GamePlayViewPort {
    private static final Map<String, String> MESSAGES = Map.ofEntries(
            Map.entry("draw.normal-card", "Card drawn. Next player's turn."),
            Map.entry("draw.empty-deck", "The draw pile is empty."),
            Map.entry(
                    "draw.pending-defuse",
                    "Choose where to return the Exploding Kitten first."
            ),
            Map.entry("draw.player-eliminated", "The player was eliminated."),
            Map.entry(
                    "draw.defuse-position-required",
                    "Defuse played. Choose a deck position for the Kitten."
            ),
            Map.entry("play.invalid-hand-index", "That hand position is invalid."),
            Map.entry("play.card-not-playable", "That card cannot be played yet."),
            Map.entry(
                    "play.precondition-not-met",
                    "The requirements for that card are not met."
            ),
            Map.entry(
                    "play.defuse-required",
                    "Resolve the Exploding Kitten before playing another card."
            ),
            Map.entry("defuse.success", "The Kitten was returned to the deck."),
            Map.entry("defuse.invalid-position", "Choose a position in the shown range."),
            Map.entry("defuse.not-awaiting", "There is no Kitten waiting to be returned."),
            Map.entry("defuse.not-found", "The current player has no Defuse card.")
    );

    private final Label currentPlayerLabel = new Label();
    private final Label deckLabel = new Label();
    private final Label discardLabel = new Label();
    private final Label alivePlayersLabel = new Label();
    private final Label messageLabel = new Label();
    private final Label futureCardsLabel = new Label();
    private final HBox handBox = new HBox(12);
    private final Button drawButton = new Button("Draw Card");
    private final Button choosePositionButton = new Button("Choose Kitten Position");

    private Runnable onDraw = () -> { };
    private Runnable onChooseDefusePosition = () -> { };
    private IntConsumer onPlayCard = index -> { };

    public GamePlayView() {
        getStyleClass().add("game-play-root");
        getStylesheets().add(
                Objects.requireNonNull(
                        GamePlayView.class.getResource("/ui/game-play.css"),
                        "Missing game-play.css"
                ).toExternalForm()
        );
        setPadding(new Insets(28));

        Label title = new Label("EXPLODING KITTENS");
        title.getStyleClass().add("game-title");
        currentPlayerLabel.getStyleClass().add("current-player");
        messageLabel.getStyleClass().add("game-message");
        messageLabel.setWrapText(true);
        futureCardsLabel.getStyleClass().add("future-cards");
        futureCardsLabel.setWrapText(true);

        HBox status = new HBox(
                22,
                deckLabel,
                discardLabel,
                alivePlayersLabel
        );
        status.setAlignment(Pos.CENTER);
        status.getStyleClass().add("status-bar");

        VBox header = new VBox(12, title, currentPlayerLabel, status);
        header.setAlignment(Pos.CENTER);
        setTop(header);

        handBox.setAlignment(Pos.CENTER);
        VBox center = new VBox(
                18,
                new Label("YOUR HAND"),
                handBox,
                futureCardsLabel,
                messageLabel
        );
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(30, 0, 30, 0));
        setCenter(center);

        drawButton.setOnAction(event -> onDraw.run());
        choosePositionButton.setOnAction(
                event -> onChooseDefusePosition.run()
        );
        HBox actions = new HBox(16, drawButton, choosePositionButton);
        actions.setAlignment(Pos.CENTER);
        setBottom(actions);
    }

    public void setOnDraw(Runnable action) {
        onDraw = action;
    }

    public void setOnPlayCard(IntConsumer action) {
        onPlayCard = action;
    }

    public void setOnChooseDefusePosition(Runnable action) {
        onChooseDefusePosition = action;
    }

    @Override
    public void render(GameSnapshot snapshot) {
        currentPlayerLabel.setText(
                "Current player: " + snapshot.getCurrentPlayerName()
        );
        deckLabel.setText("Draw pile: " + snapshot.getDeckSize());
        discardLabel.setText("Discard pile: " + snapshot.getDiscardPileSize());
        alivePlayersLabel.setText(
                "Players remaining: " + snapshot.getAlivePlayerCount()
        );
        renderHand(snapshot.getCurrentPlayerHand());
        updateAvailableActions(snapshot.getPhase());
    }

    @Override
    public void showMessage(String messageKey) {
        messageLabel.setText(MESSAGES.getOrDefault(messageKey, messageKey));
    }

    @Override
    public void showFutureCards(List<CardType> cards) {
        String cardNames = cards.stream()
                .map(this::displayName)
                .collect(Collectors.joining("  •  "));
        futureCardsLabel.setText("Top cards: " + cardNames);
    }

    @Override
    public OptionalInt requestDefusePosition(int minimum, int maximum) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Return Exploding Kitten");
        dialog.setHeaderText(
                "Choose a position from " + minimum + " to " + maximum
        );
        dialog.setContentText("Position:");
        Optional<String> response = dialog.showAndWait();
        if (response.isEmpty()) {
            return OptionalInt.empty();
        }
        return parsePosition(response.get(), minimum, maximum);
    }

    private void renderHand(List<CardType> hand) {
        handBox.getChildren().clear();
        for (int index = 0; index < hand.size(); index++) {
            int handIndex = index;
            Button cardButton = new Button(displayName(hand.get(index)));
            cardButton.getStyleClass().add("card-button");
            cardButton.setOnAction(event -> onPlayCard.accept(handIndex));
            handBox.getChildren().add(cardButton);
        }
    }

    private void updateAvailableActions(TurnPhase phase) {
        boolean waitingForPosition =
                phase == TurnPhase.AWAITING_DEFUSE_POSITION;
        drawButton.setDisable(waitingForPosition);
        handBox.setDisable(waitingForPosition);
        choosePositionButton.setVisible(waitingForPosition);
        choosePositionButton.setManaged(waitingForPosition);
    }

    private OptionalInt parsePosition(
            String input,
            int minimum,
            int maximum
    ) {
        try {
            int position = Integer.parseInt(input.trim());
            if (position >= minimum && position <= maximum) {
                return OptionalInt.of(position);
            }
        } catch (NumberFormatException exception) {
            // The view displays one consistent validation message below.
        }
        messageLabel.setText(
                "Enter a whole number from " + minimum + " to " + maximum + "."
        );
        return OptionalInt.empty();
    }

    private String displayName(CardType cardType) {
        String words = cardType.name().replace('_', ' ').toLowerCase();
        StringBuilder result = new StringBuilder(words.length());
        boolean capitalize = true;
        for (char character : words.toCharArray()) {
            result.append(capitalize
                    ? Character.toUpperCase(character)
                    : character);
            capitalize = character == ' ';
        }
        return result.toString();
    }
}
