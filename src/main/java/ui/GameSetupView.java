package ui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public final class GameSetupView extends StackPane {

    private static final int CARD_WIDTH = 360;

    private final TextField playerCountInput = new TextField();
    private final Button startGameButton = new Button();
    private final Label messageLabel = new Label();
    private final StackPane cardWrap;
    private Timeline shake;
    private boolean introPlayed;

    public GameSetupView() {
        getStyleClass().add("game-setup-root");

        // ---- animated pixel background (decoration only) ----
        BackgroundLayer background = new BackgroundLayer();

        // ---- mascot: a calico kitten with a lit fuse ----
        WritableImage mascotOpen = PixelArt.sprite(PixelData.MASCOT_OPEN, 5);
        WritableImage mascotBlink = PixelArt.sprite(PixelData.MASCOT_BLINK, 5);
        WritableImage sparkA = PixelArt.sprite(PixelData.SPARK_A, 5);
        WritableImage sparkB = PixelArt.sprite(PixelData.SPARK_B, 5);

        ImageView mascotView = PixelArt.view(mascotOpen);
        ImageView sparkView = PixelArt.view(sparkA);
        sparkView.setLayoutX(53);
        sparkView.setLayoutY(-7);
        Group mascotBody = new Group(mascotView, sparkView);
        Group mascotHolder = new Group(mascotBody);

        // ---- title and subtitle (bitmap font) ----
        ImageView title = PixelArt.view(PixelArt.textImage(
                "EXPLODING KITTENS", 5, PixelArt.CREAM, PixelArt.AMBER, 4, PixelArt.VIOLET_SHADOW));

        ImageView heartLeft = PixelArt.view(PixelArt.sprite(PixelData.HEART, 3));
        ImageView heartRight = PixelArt.view(PixelArt.sprite(PixelData.HEART, 3));
        ImageView subtitleText = PixelArt.view(PixelArt.textImage(
                "SET UP A NEW GAME", 2, PixelArt.LILAC, PixelArt.LILAC, 7, 0xFF0B0819));
        HBox subtitle = new HBox(14, heartLeft, subtitleText, heartRight);
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setMaxWidth(Region.USE_PREF_SIZE);

        // ---- setup card (same controls as before) ----
        ImageView instruction = PixelArt.view(PixelArt.textImage(
                "NUMBER OF PLAYERS", 2, PixelArt.CREAM, PixelArt.CREAM, 7, 0));

        playerCountInput.setPromptText("Enter 2-5");
        playerCountInput.setMaxWidth(Double.MAX_VALUE);

        startGameButton.setGraphic(PixelArt.view(PixelArt.textImage(
                "START GAME", 3, PixelArt.INK, PixelArt.INK, 7, 0)));
        startGameButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        startGameButton.setAccessibleText("Start Game");
        startGameButton.setMaxWidth(Double.MAX_VALUE);
        startGameButton.setDefaultButton(true);

        messageLabel.getStyleClass().add("message-label");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);

        VBox setupCard = new VBox(
                16,
                instruction,
                playerCountInput,
                startGameButton,
                messageLabel
        );
        setupCard.setPadding(new Insets(30, 30, 26, 30));
        setupCard.getStyleClass().add("setup-card");

        // cat ears sitting on top of the card
        Pane ears = createCardEars();
        StackPane.setAlignment(ears, Pos.TOP_LEFT);
        ears.setTranslateY(-16);

        cardWrap = new StackPane(setupCard, ears);
        cardWrap.setPrefWidth(CARD_WIDTH);
        cardWrap.setMaxWidth(CARD_WIDTH);

        // ---- layout ----
        VBox content = new VBox(12, mascotHolder, title, subtitle, cardWrap);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(60, 32, 72, 32));
        VBox.setMargin(title, new Insets(6, 0, 0, 0));
        VBox.setMargin(cardWrap, new Insets(30, 0, 0, 0));

        getChildren().addAll(background, content);

        // ---- entrance (stepped, like a retro game) ----
        Node[] intro = {mascotHolder, title, subtitle, cardWrap};
        for (Node node : intro) {
            node.setOpacity(0);
        }
        Animation entrance = new ParallelTransition(
                stepIn(mascotHolder, 150, -80),
                stepIn(title, 320, 60),
                stepIn(subtitle, 460, 48),
                stepIn(cardWrap, 600, 100)
        );
        sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null && !introPlayed) {
                introPlayed = true;
                entrance.play();
            }
        });

        startIdleAnimations(mascotBody, mascotView, sparkView, mascotOpen, mascotBlink, sparkA, sparkB);
    }

    public String getPlayerCountInput() {
        return playerCountInput.getText();
    }

    public void setOnStart(Runnable action) {
        startGameButton.setOnAction(event -> action.run());
    }

    public void showMessage(String message) {
        messageLabel.setText(message);
        if (message != null && !message.isBlank()) {
            shakeCard();
        }
    }

    // ------------------------------------------------------------------
    // decoration helpers
    // ------------------------------------------------------------------

    private static Pane createCardEars() {
        WritableImage left = PixelArt.sprite(PixelData.EAR_L, 4);
        WritableImage right = PixelArt.sprite(PixelArt.mirror(PixelData.EAR_L), 4);

        ImageView leftView = PixelArt.view(left);
        ImageView rightView = PixelArt.view(right);
        leftView.setLayoutX(24);
        rightView.setLayoutX(CARD_WIDTH - 24 - right.getWidth());

        Pane pane = new Pane(leftView, rightView);
        pane.setPrefSize(CARD_WIDTH, 20);
        pane.setMinSize(CARD_WIDTH, 20);
        pane.setMaxSize(CARD_WIDTH, 20);
        pane.setMouseTransparent(true);
        return pane;
    }

    // ------------------------------------------------------------------
    // animation helpers
    // ------------------------------------------------------------------

    /** Slides a node into place in a handful of hard steps (no smooth tweening). */
    private static Animation stepIn(Node node, double delayMillis, double fromY) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 0, Interpolator.DISCRETE),
                new KeyValue(node.translateYProperty(), fromY, Interpolator.DISCRETE)));
        int steps = 6;
        for (int i = 0; i <= steps; i++) {
            double remaining = 1.0 - (double) i / steps;
            double y = Math.round(fromY * remaining * remaining / 4.0) * 4.0;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(delayMillis + 55.0 * i),
                    new KeyValue(node.opacityProperty(), 1, Interpolator.DISCRETE),
                    new KeyValue(node.translateYProperty(), y, Interpolator.DISCRETE)));
        }
        return timeline;
    }

    private void startIdleAnimations(Group mascotBody, ImageView mascotView, ImageView sparkView,
                                     WritableImage open, WritableImage blink,
                                     WritableImage sparkA, WritableImage sparkB) {
        // mascot hops one pixel row up and down
        Timeline bob = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(mascotBody.translateYProperty(), 0, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(450),
                        new KeyValue(mascotBody.translateYProperty(), -5, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(900),
                        new KeyValue(mascotBody.translateYProperty(), 0, Interpolator.DISCRETE))
        );
        bob.setCycleCount(Animation.INDEFINITE);
        bob.play();

        // blink every few seconds
        Timeline blinkTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> mascotView.setImage(open)),
                new KeyFrame(Duration.millis(2800), e -> mascotView.setImage(blink)),
                new KeyFrame(Duration.millis(2950), e -> mascotView.setImage(open)),
                new KeyFrame(Duration.millis(3600))
        );
        blinkTimeline.setCycleCount(Animation.INDEFINITE);
        blinkTimeline.play();

        // the fuse spark flickers between two frames
        Timeline sparkTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> sparkView.setImage(sparkA)),
                new KeyFrame(Duration.millis(130), e -> sparkView.setImage(sparkB)),
                new KeyFrame(Duration.millis(260))
        );
        sparkTimeline.setCycleCount(Animation.INDEFINITE);
        sparkTimeline.play();
    }

    /** A short horizontal shake of the card whenever a validation message appears. */
    private void shakeCard() {
        if (shake != null) {
            shake.stop();
        }
        shake = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(cardWrap.translateXProperty(), 0, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(60), new KeyValue(cardWrap.translateXProperty(), -8, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(120), new KeyValue(cardWrap.translateXProperty(), 8, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(180), new KeyValue(cardWrap.translateXProperty(), -4, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(240), new KeyValue(cardWrap.translateXProperty(), 4, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(300), new KeyValue(cardWrap.translateXProperty(), 0, Interpolator.DISCRETE))
        );
        shake.play();
    }
}
