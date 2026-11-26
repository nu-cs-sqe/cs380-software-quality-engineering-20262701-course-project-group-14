package ui;

import controller.CatPairViewPort;
import controller.GameOverViewPort;
import domain.CardType;
import domain.GameSnapshot;
import domain.PlayerSummary;
import domain.TurnPhase;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.IntConsumer;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Bold diegetic tavern card-table screen:
 * - Iron-chained turn signboard at the top
 * - Pettable sleeping/alert calico tavern cat mascot on the oak wing (matching MASCOT_OPEN craft)
 * - Clickable Draw Deck with Fuse-Tension gauge, inlaid compass medallion, and hover-fan Discard Pile
 * - Physical flying card arcs when playing or drawing cards
 * - Cat-pair combo lift & contextual unfurling parchment rule scroll
 */
public final class GamePlayView extends StackPane
        implements CatPairViewPort, GameOverViewPort {
    private static final ResourceBundle GAME_OVER_TEXT =
            ResourceBundle.getBundle("ui.GameOverMessages");
    private static final ResourceBundle SNIFF_TEST_TEXT =
            ResourceBundle.getBundle("ui.SniffTestMessages");
    private static final ResourceBundle CHRONICLE_TEXT =
            ResourceBundle.getBundle("ui.ChronicleMessages");
    private static final int CHRONICLE_LINES = 2;
    private static final Map<String, String> MESSAGES = Map.ofEntries(
            Map.entry("game.over", GAME_OVER_TEXT.getString("closed")),
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
            Map.entry("defuse.not-found", "The current player has no Defuse card."),
            Map.entry("sniff.kitten-moved", SNIFF_TEST_TEXT.getString("kitten-moved")),
            Map.entry("sniff.safe", SNIFF_TEST_TEXT.getString("safe"))
    );

    private static final Set<String> WARNING_MESSAGES = Set.of(
            "draw.pending-defuse",
            "draw.player-eliminated",
            "draw.defuse-position-required",
            "play.defuse-required"
    );

    private static final String[] CAT_QUIPS = {
            "Purrr... the tavern calico approves of your poker face.",
            "The calico twitches an ear. Something furry is ticking in that deck...",
            "Mrrp! Scratching behind the ears grants +10% Defuse confidence.",
            "The tavern cat kneads the oak wood and eyes the discard pile."
    };

    private static final String[] PROP_QUIPS = {
            "Spiced milk-punch in an oak-and-brass stein. Steady your nerves!",
            "Tavern Rule: Match any two Cat cards to pilfer from a rival.",
            "Bounty Notice: Keep a Defuse close when the deck grows thin."
    };

    private static final PseudoClass DANGER = PseudoClass.getPseudoClass("danger");
    private static final PseudoClass CURRENT = PseudoClass.getPseudoClass("current");
    private static final PseudoClass OUT = PseudoClass.getPseudoClass("out");
    private static final PseudoClass PAIR_FIRST = PseudoClass.getPseudoClass("pair-first");
    private static final PseudoClass PAIR_MATCH = PseudoClass.getPseudoClass("pair-match");
    private static final PseudoClass PAIR_UNAVAILABLE = PseudoClass.getPseudoClass("pair-unavailable");
    private static final double COMPACT_BELOW = 860;
    private static final int LOW_DECK_THRESHOLD = 3;
    private static final int DIM_TEXT = 0xFF8F7A68;

    private static final int INK_DARK = 0xFF24150E;
    private static final int INK_ACCENT = 0xFF8C4A1C;
    private static final int INK_DANGER = 0xFFB81D35;
    private static final int INK_FADED = 0xFF7A5B43;
    private static final double[] TOSS_ANGLES = {-11.0, 8.0, -5.0, 3.0};
    private static final int DISPLAYED_DISCARDS = TOSS_ANGLES.length;
    private static final double[] PEEK_ANGLES = {-22.0, -7.0, 8.0, 22.0};

    private enum Status { READY, AUTO, SOON, LOCKED }

    private final String stylesheetUrl;
    private final TavernBackground background = new TavernBackground();

    // ---- top hanging signboard ----
    private final HBox banner = new HBox(14);
    private final StackPane nameHolder = new StackPane();
    private final HBox phaseChip = new HBox(8);

    // ---- tavern chronicle parchment (latest events + current prompt) & seat plaques ----
    private final HBox messageRibbon = new HBox(10);
    private final ImageView messageIcon = PixelArt.view(
            PixelArt.sprite(PixelData.WARN, 3, PixelArt.TAVERN_PALETTE));
    private final StackPane messageHolder = new StackPane();
    private final HBox seatRow = new HBox(8);

    // ---- interactive tavern tabletop props (pettable cat, stein, scroll, poster) ----
    private final Button catMascotButton = new Button();
    private final ImageView catMascotView = PixelArt.view(
            PixelArt.sprite(PixelData.TABLE_CAT_SLEEP_A, 3, PixelArt.TAVERN_PALETTE));
    private final WritableImage catSleepA =
            PixelArt.sprite(PixelData.TABLE_CAT_SLEEP_A, 3, PixelArt.TAVERN_PALETTE);
    private final WritableImage catSleepB =
            PixelArt.sprite(PixelData.TABLE_CAT_SLEEP_B, 3, PixelArt.TAVERN_PALETTE);
    private final WritableImage catAlert =
            PixelArt.sprite(PixelData.TABLE_CAT_ALERT, 3, PixelArt.TAVERN_PALETTE);
    private final WritableImage heartSprite =
            PixelArt.sprite(PixelData.HEART, 3, PixelArt.TAVERN_PALETTE);
    private Timeline catBreathTimeline;
    private int catPhase;
    private int quipIndex;

    private final Region posterHotspot = new Region();
    private final Region steinHotspot = new Region();
    private final Region scrollHotspot = new Region();

    // ---- centerpiece arena: Clickable Draw Pile + Fuse Gauge + Hover-Fan Discard Pile ----
    private final StackPane deckStackHolder = new StackPane();
    private final StackPane deckCountHolder = new StackPane();
    private final HBox fuseGauge = new HBox(3);
    private final Button drawButton = new Button();
    private final Button returnToSetupButton = new Button();
    private final StackPane drawHintHolder = new StackPane();

    private final Button choosePositionButton = new Button();
    private final VBox futureOverlay = new VBox(8);
    private final StackPane futureTitleHolder = new StackPane();
    private final HBox futureChips = new HBox(8);

    private final StackPane discardPileHolder = new StackPane();
    private final StackPane discardCountBadge = new StackPane();
    private boolean discardHovered;

    // ---- contextual unfurling parchment scroll (card rules) ----
    private final HBox parchmentScroll = new HBox(14);

    // ---- foreground fanned poker hand ----
    private final Pane handFanPane = new Pane();
    private final List<Button> cardButtons = new ArrayList<>();
    private final List<Double> baseAngles = new ArrayList<>();
    private final HBox handLabelPill = new HBox(8);
    private final HBox handChip = new HBox();
    private final HBox keyHintPill = new HBox();

    // ---- root layers ----
    private final Pane stageLayer = new Pane();
    private final Pane fxLayer = new Pane();
    private final List<Animation> activeEffects = new ArrayList<>();
    private final Region dimOverlay = new Region();
    private StackPane handoffOverlay;
    private StackPane handoffNameHolder;
    private Button handoffButton;
    private Timeline handoffBlink;
    private Timeline shakeTimeline;
    private Runnable onHandoffReady = () -> { };

    // ---- state ----
    private boolean compact;
    private String playerName = "";
    private String winnerMessage;
    private int deckSize;
    private int initialDeckMax = 20;
    private int discardSize;
    private int aliveCount;
    private List<CardType> hand = List.of();
    private List<CardType> futureCards = List.of();
    private final List<CardType> recentDiscards = new ArrayList<>();
    private boolean danger;
    private boolean turnActionsDisabled;
    private String currentMessage;
    private final List<String> chronicle = new ArrayList<>();
    private List<PlayerSummary> seats = List.of();
    private int currentSeat = -1;
    private int remainingTurns = 1;
    private String lastTargetName = "";
    private boolean currentWarning;
    private CardCatalog.Info shownInfo;
    private Status shownStatus = Status.READY;
    private int activeCardIndex = -1;
    private HandPairSelection handPairSelection;

    private Runnable onDraw = () -> { };
    private Runnable onReturnToSetup = () -> { };
    private Runnable onChooseDefusePosition = () -> { };
    private IntConsumer onPlayCard = index -> { };

    public GamePlayView() {
        getStyleClass().add("game-play-root");
        stylesheetUrl = Objects.requireNonNull(
                GamePlayView.class.getResource("/ui/game-play.css"),
                "Missing game-play.css"
        ).toExternalForm();
        getStylesheets().add(stylesheetUrl);

        buildBanner();
        buildTopTableStrip();
        messageRibbon.setId("gameplay-message");
        buildTavernProps();
        buildArenaStations();
        discardPileHolder.setId("discard-pile");
        buildParchmentScroll();
        buildHandHud();

        fxLayer.setMouseTransparent(true);

        stageLayer.getChildren().addAll(
                banner,
                messageRibbon,
                seatRow,
                posterHotspot,
                steinHotspot,
                scrollHotspot,
                catMascotButton,
                deckStackHolder,
                deckCountHolder,
                fuseGauge,
                drawButton,
                returnToSetupButton,
                drawHintHolder,
                discardPileHolder,
                discardCountBadge,
                choosePositionButton,
                futureOverlay,
                parchmentScroll,
                handFanPane,
                handLabelPill,
                keyHintPill
        );

        dimOverlay.getStyleClass().add("dim-overlay");
        dimOverlay.setVisible(false);
        handoffOverlay = buildHandoffOverlay();

        getChildren().addAll(background, stageLayer, fxLayer, handoffOverlay, dimOverlay);
        stageLayer.setPickOnBounds(false);
        handFanPane.setPickOnBounds(false);
        sceneProperty().addListener((property, previousScene, scene) -> {
            if (scene == null) {
                handPairSelection = null;
                clearEffects();
                hideHandoff();
            }
        });

        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE && handPairSelection != null) {
                clearHandPairSelection();
                event.consume();
            }
        });

        widthProperty().addListener((obs, oldW, newW) -> layoutStage());
        heightProperty().addListener((obs, oldH, newH) -> layoutStage());

        startCatIdleAnimation();
        updateFuture();
        layoutStage();
    }

    // ------------------------------------------------------------------
    // public API (unchanged)
    // ------------------------------------------------------------------

    public void setOnDraw(Runnable action) {
        onDraw = action;
    }

    void disableTurnActions() {
        turnActionsDisabled = true;
        updateActions();
        updateHand();
        layoutStage();
    }

    public void setOnPlayCard(IntConsumer action) {
        onPlayCard = action;
    }

    @Override
    public void setOnReturnToSetup(Runnable action) {
        onReturnToSetup = action;
    }

    public void setOnChooseDefusePosition(Runnable action) {
        onChooseDefusePosition = action;
    }

    @Override
    public void render(GameSnapshot snapshot) {
        handPairSelection = null;
        String newName = snapshot.getCurrentPlayerName();
        if (!Objects.equals(newName, playerName)) {
            futureCards = List.of();
            updateFuture();
        }
        playerName = newName;
        deckSize = snapshot.getDeckSize();
        initialDeckMax = Math.max(initialDeckMax, deckSize);
        int newDiscardSize = snapshot.getDiscardPileSize();
        if (newDiscardSize != discardSize) {
            recentDiscards.clear();
        }
        discardSize = newDiscardSize;
        aliveCount = snapshot.getAlivePlayerCount();
        seats = snapshot.getPlayers();
        currentSeat = snapshot.getCurrentPlayerIndex();
        remainingTurns = snapshot.getRemainingTurns();
        hand = List.copyOf(snapshot.getCurrentPlayerHand());
        boolean wasDanger = danger;
        danger = snapshot.getPhase() == TurnPhase.AWAITING_DEFUSE_POSITION;
        turnActionsDisabled = snapshot.getPhase() == TurnPhase.GAME_OVER;
        boolean newlyFinished = turnActionsDisabled && winnerMessage == null;
        winnerMessage = turnActionsDisabled
                ? MessageFormat.format(GAME_OVER_TEXT.getString("winner"), playerName)
                : null;
        if (newlyFinished) {
            addChronicle(chronicleLine("game.winner", playerName));
        }
        if (turnActionsDisabled) {
            currentWarning = false;
        }

        background.setTension(danger, deckSize <= LOW_DECK_THRESHOLD);
        refreshCatSprite();
        refreshAll();
        if (danger && !wasDanger) {
            shakeTable();
        }
    }

    @Override
    public void showMessage(String messageKey) {
        boolean warning = WARNING_MESSAGES.contains(messageKey);
        if (CHRONICLE_TEXT.containsKey(messageKey)) {
            addChronicle(chronicleLine(messageKey, playerName));
            currentMessage = warning ? MESSAGES.getOrDefault(messageKey, messageKey) : null;
        } else {
            currentMessage = MESSAGES.getOrDefault(messageKey, messageKey);
        }
        currentWarning = warning;
        layoutStage();
    }

    private static String chronicleLine(String key, Object... arguments) {
        return MessageFormat.format(CHRONICLE_TEXT.getString(key), arguments);
    }

    private void addChronicle(String line) {
        chronicle.add(line);
        if (chronicle.size() > CHRONICLE_LINES) {
            chronicle.remove(0);
        }
    }

    @Override
    public void showFutureCards(List<CardType> cards) {
        futureCards = List.copyOf(cards);
        updateFuture();
        layoutStage();
    }

    @Override
    public OptionalInt requestDefusePosition(int minimum, int maximum) {
        Window owner = getScene() == null ? null : getScene().getWindow();
        dimOverlay.setVisible(true);
        try {
            return DefusePositionDialog.show(owner, stylesheetUrl, minimum, maximum);
        } finally {
            dimOverlay.setVisible(false);
        }
    }

    @Override
    public void requestSecondCard(int firstHandIndex, List<Integer> matchingHandIndices,
                                  IntConsumer onMatchSelected) {
        handPairSelection = new HandPairSelection(firstHandIndex, matchingHandIndices, onMatchSelected);
        layoutStage();
    }

    @Override
    public OptionalInt requestTargetPlayer(List<PlayerSummary> candidates) {
        Map<Integer, String> options = new LinkedHashMap<>();
        for (PlayerSummary player : candidates) {
            options.put(player.getPlayerIndex(), CatPairChoiceDialog.text("target.option",
                    player.getName(), player.getHandSize()));
        }
        OptionalInt choice = requestCatPairChoice("target.title", options);
        lastTargetName = "";
        choice.ifPresent(seat -> candidates.stream()
                .filter(player -> player.getPlayerIndex() == seat)
                .findFirst()
                .ifPresent(player -> lastTargetName = player.getName()));
        return choice;
    }

    private OptionalInt requestCatPairChoice(String titleKey, Map<Integer, String> options) {
        Window owner = getScene() == null ? null : getScene().getWindow();
        dimOverlay.setVisible(true);
        try {
            return CatPairChoiceDialog.show(owner, stylesheetUrl, titleKey, options);
        } finally {
            dimOverlay.setVisible(false);
        }
    }

    public void showHandoff(String nextPlayerName, Runnable onReady) {
        clearHandPairSelection();
        futureCards = List.of();
        updateFuture();
        showEmptyInfo();
        onHandoffReady = onReady == null ? () -> { } : onReady;
        handoffNameHolder.getChildren().setAll(nameNode(nextPlayerName, 5, 480, GameTheme.AMBER));
        clearEffects();
        stageLayer.setVisible(false);
        handoffOverlay.setVisible(true);
        handoffBlink.playFromStart();
        handoffButton.requestFocus();
    }

    public void hideHandoff() {
        handoffOverlay.setVisible(false);
        handoffBlink.stop();
        stageLayer.setVisible(true);
    }

    // ------------------------------------------------------------------
    // construction
    // ------------------------------------------------------------------

    private void buildBanner() {
        banner.getStyleClass().add("turn-banner");
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(10, 14, 10, 14));

        StackPane avatarSlot = new StackPane(PixelArt.view(PixelArt.sprite(
                PixelData.HEAD_OPEN, 2, PixelArt.TAVERN_PALETTE)));
        avatarSlot.getStyleClass().add("avatar-slot");
        fixSize(avatarSlot, 58, 58);

        nameHolder.setAlignment(Pos.CENTER_LEFT);
        VBox who = new VBox(4, PixelArt.textView("NOW PLAYING", 2, GameTheme.SAND), nameHolder);
        who.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        phaseChip.getStyleClass().add("phase-chip");
        phaseChip.setAlignment(Pos.CENTER);

        banner.getChildren().addAll(avatarSlot, who, spacer, phaseChip);
    }

    private void buildTopTableStrip() {
        messageRibbon.getStyleClass().add("chronicle");
        messageRibbon.setAlignment(Pos.CENTER_LEFT);
        messageRibbon.setPadding(new Insets(12, 20, 12, 20));
        messageRibbon.setMouseTransparent(true);
        messageIcon.setVisible(false);
        messageIcon.setManaged(false);
        messageHolder.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(messageHolder, Priority.ALWAYS);
        messageRibbon.getChildren().addAll(messageIcon, messageHolder);

        seatRow.setAlignment(Pos.CENTER);
        seatRow.setPickOnBounds(false);
    }

    /** Interactive pixel-art props: pettable tavern calico cat, oak stein, dagger scroll & wanted flyer. */
    private void buildTavernProps() {
        catMascotButton.getStyleClass().add("prop-button");
        catMascotButton.setGraphic(catMascotView);
        catMascotButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        catMascotButton.setPadding(Insets.EMPTY);
        catMascotButton.setFocusTraversable(false);
        catMascotButton.setAccessibleText("Pet Tavern Cat");
        fixSize(catMascotButton, catSleepA.getWidth(), catSleepA.getHeight());

        catMascotButton.setOnMouseEntered(e -> {
            catMascotButton.setTranslateY(-3);
            if (!danger && !currentWarning) {
                showFlavorQuip("Click to pet the sleeping tavern calico.");
            }
        });
        catMascotButton.setOnMouseExited(e -> catMascotButton.setTranslateY(0));
        catMascotButton.setOnAction(e -> {
            spawnFloatingHeart(
                    catMascotButton.getLayoutX() + catSleepA.getWidth() * 0.45,
                    catMascotButton.getLayoutY() - 6
            );
            if (!danger && !currentWarning) {
                showFlavorQuip(CAT_QUIPS[quipIndex % CAT_QUIPS.length]);
                quipIndex++;
            }
        });

        setupPropHotspot(steinHotspot, () -> {
            spawnFloatingSparkle(
                    steinHotspot.getLayoutX() + 26,
                    steinHotspot.getLayoutY() + 6
            );
            if (!danger && !currentWarning) {
                showFlavorQuip(PROP_QUIPS[0]);
            }
        });
        setupPropHotspot(scrollHotspot, () -> {
            if (!danger && !currentWarning) {
                showFlavorQuip(PROP_QUIPS[1]);
            }
        });
        setupPropHotspot(posterHotspot, () -> {
            if (!danger && !currentWarning) {
                showFlavorQuip(PROP_QUIPS[2]);
            }
        });
    }

    private void setupPropHotspot(Region hotspot, Runnable onClick) {
        hotspot.getStyleClass().add("prop-hotspot");
        hotspot.setOnMouseClicked(e -> onClick.run());
    }

    private void showFlavorQuip(String quip) {
        currentMessage = quip;
        layoutStage();
    }

    private void startCatIdleAnimation() {
        catBreathTimeline = new Timeline(
                new KeyFrame(Duration.millis(900), e -> {
                    catPhase = (catPhase + 1) % 2;
                    refreshCatSprite();
                })
        );
        catBreathTimeline.setCycleCount(Animation.INDEFINITE);
        sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                catBreathTimeline.play();
            } else {
                catBreathTimeline.stop();
            }
        });
    }

    private void refreshCatSprite() {
        boolean lowDeck = deckSize <= LOW_DECK_THRESHOLD;
        if (danger || lowDeck) {
            catMascotView.setImage(catAlert);
        } else {
            catMascotView.setImage(catPhase == 0 ? catSleepA : catSleepB);
        }
    }

    private void buildArenaStations() {
        deckStackHolder.setAlignment(Pos.CENTER);
        deckStackHolder.setAccessibleText("Draw Card");
        deckStackHolder.getStyleClass().add("deck-hotspot");
        deckStackHolder.setOnMouseEntered(e -> {
            if (!danger && !turnActionsDisabled && deckSize > 0) {
                deckStackHolder.setTranslateY(-4);
            }
        });
        deckStackHolder.setOnMouseExited(e -> deckStackHolder.setTranslateY(0));
        deckStackHolder.setOnMouseClicked(e -> {
            if (!danger && !turnActionsDisabled && deckSize > 0) {
                triggerDrawWithFlight();
            }
        });

        deckCountHolder.setAlignment(Pos.CENTER);
        fuseGauge.getStyleClass().add("fuse-gauge");
        fuseGauge.setAlignment(Pos.CENTER);

        drawButton.getStyleClass().add("draw-button");
        drawButton.setId("draw-card");
        drawButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        drawButton.setAccessibleText("Draw Card");
        drawButton.setOnAction(event -> triggerDrawWithFlight());

        returnToSetupButton.setId("return-to-setup");
        returnToSetupButton.getStyleClass().add("draw-button");
        returnToSetupButton.setText(GAME_OVER_TEXT.getString("return-to-setup"));
        returnToSetupButton.setOnAction(event -> onReturnToSetup.run());
        returnToSetupButton.setVisible(false);

        drawHintHolder.setAlignment(Pos.CENTER);

        discardPileHolder.setAlignment(Pos.CENTER);
        discardPileHolder.setOnMouseEntered(e -> {
            if (discardSize > 0) {
                discardHovered = true;
                updateDiscard();
            }
        });
        discardPileHolder.setOnMouseExited(e -> {
            if (discardHovered) {
                discardHovered = false;
                updateDiscard();
            }
        });
        discardCountBadge.setAlignment(Pos.CENTER);

        choosePositionButton.getStyleClass().add("danger-button");
        choosePositionButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        choosePositionButton.setAccessibleText("Choose Kitten Position");
        HBox centered = new HBox(10,
                PixelArt.view(PixelArt.sprite(PixelData.WARN, 2, PixelArt.TAVERN_PALETTE)),
                PixelArt.textView("CHOOSE KITTEN POSITION", 2, GameTheme.PLATE));
        centered.setAlignment(Pos.CENTER);
        choosePositionButton.setGraphic(centered);
        choosePositionButton.setOnAction(event -> onChooseDefusePosition.run());
        choosePositionButton.setVisible(false);

        futureOverlay.getStyleClass().add("fortune-panel");
        futureOverlay.setPadding(new Insets(8, 10, 10, 10));
        futureOverlay.setMouseTransparent(true);
        futureTitleHolder.setAlignment(Pos.CENTER);
        futureChips.setAlignment(Pos.CENTER);
        futureOverlay.getChildren().addAll(futureTitleHolder, futureChips);
        futureOverlay.setVisible(false);
    }

    private void buildParchmentScroll() {
        parchmentScroll.getStyleClass().add("parchment-note");
        parchmentScroll.setAlignment(Pos.CENTER_LEFT);
        parchmentScroll.setPadding(new Insets(12, 18, 12, 18));
        parchmentScroll.setMouseTransparent(true);
        parchmentScroll.setVisible(false);
    }

    private void buildHandHud() {
        handChip.getStyleClass().add("chip");
        handChip.setAlignment(Pos.CENTER);
        handLabelPill.getStyleClass().add("corner-plaque");
        handLabelPill.setAlignment(Pos.CENTER);
        handLabelPill.setPadding(new Insets(5, 10, 5, 10));
        handLabelPill.getChildren().addAll(
                PixelArt.textView("HAND", 2, GameTheme.CREAM), handChip);

        keyHintPill.getStyleClass().add("corner-plaque");
        keyHintPill.setAlignment(Pos.CENTER);
        keyHintPill.setPadding(new Insets(5, 10, 5, 10));
        keyHintPill.getChildren().add(
                PixelArt.textView("ARROWS BROWSE  ENTER PLAYS", 2, GameTheme.SAND));
    }

    private StackPane buildHandoffOverlay() {
        WritableImage open = PixelArt.sprite(PixelData.HEAD_OPEN, 5, PixelArt.TAVERN_PALETTE);
        WritableImage blink = PixelArt.sprite(PixelData.HEAD_BLINK, 5, PixelArt.TAVERN_PALETTE);
        ImageView cat = PixelArt.view(blink);

        handoffBlink = new Timeline(
                new KeyFrame(Duration.ZERO, e -> cat.setImage(blink)),
                new KeyFrame(Duration.millis(1100), e -> cat.setImage(open)),
                new KeyFrame(Duration.millis(1450), e -> cat.setImage(blink)),
                new KeyFrame(Duration.millis(3200))
        );
        handoffBlink.setCycleCount(Animation.INDEFINITE);

        ImageView seal = PixelArt.textView("TAVERN CURTAIN DRAWN", 2, GameTheme.BRASS);
        ImageView title = PixelArt.textView("PASS THE COMPUTER", 4, GameTheme.CREAM);
        ImageView next = PixelArt.textView("NEXT UP", 2, GameTheme.SAND);
        handoffNameHolder = new StackPane();

        handoffButton = new Button();
        handoffButton.getStyleClass().add("draw-button");
        handoffButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        handoffButton.setGraphic(PixelArt.textView("I'M READY", 3, GameTheme.PLATE));
        handoffButton.setAccessibleText("I'm ready");
        fixSize(handoffButton, 280, 60);
        handoffButton.setOnAction(event -> {
            onHandoffReady.run();
            hideHandoff();
        });

        ImageView note = PixelArt.textView(
                "YOUR HAND STAYS HIDDEN UNTIL YOU CONTINUE", 2, GameTheme.MUTED);

        VBox panel = new VBox(14, seal, cat, title, next, handoffNameHolder, handoffButton, note);
        panel.getStyleClass().add("handoff-panel");
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(32, 38, 32, 38));
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        StackPane overlay = new StackPane(panel);
        overlay.getStyleClass().add("handoff-root");
        overlay.setVisible(false);
        return overlay;
    }

    // ------------------------------------------------------------------
    // spatial stage layout
    // ------------------------------------------------------------------

    private void layoutStage() {
        double w = getWidth() > 0 ? getWidth() : 900;
        double h = getHeight() > 0 ? getHeight() : 760;
        boolean nowCompact = w < COMPACT_BELOW;
        if (nowCompact != compact) {
            compact = nowCompact;
            refreshAll();
            return;
        }

        // 1. Hanging turn signboard at top center
        double bannerW = compact ? 480 : 540;
        double bannerH = compact ? 76 : 80;
        banner.resizeRelocate(Math.round((w - bannerW) * 0.5), 16, bannerW, bannerH);

        // 2. Top tabletop strip aligned over the inlaid velvet mat
        double feltW = TavernBackground.feltWidth(w, compact);
        double leftEdge = TavernBackground.feltLeft(w, feltW);
        double rightEdge = leftEdge + feltW;

        seatRow.resizeRelocate(leftEdge, 112, feltW, 46);

        // 2b. Interactive tavern props positioned cleanly on the oak wings
        double topInset = compact ? 22 : 34;
        double tableTopY = 114;
        boolean showSideProps = w >= 800;
        catMascotButton.setVisible(showSideProps);
        steinHotspot.setVisible(showSideProps);
        scrollHotspot.setVisible(showSideProps);
        posterHotspot.setVisible(w >= 740);

        posterHotspot.resizeRelocate(86, 16, 54, 66);
        catMascotButton.resizeRelocate(
                Math.round(topInset + (compact ? 10 : 18)),
                Math.round(tableTopY + 46),
                catSleepA.getWidth(),
                catSleepA.getHeight()
        );
        steinHotspot.resizeRelocate(
                Math.round(topInset + (compact ? 14 : 24)),
                Math.round(tableTopY + 126),
                54,
                60
        );
        scrollHotspot.resizeRelocate(
                Math.round(w - topInset - 60 - (compact ? 12 : 22)),
                Math.round(tableTopY + 42),
                60,
                54
        );

        // 3. Centerpiece Arena: Draw Pile (left cradle) & Hover-Fan Discard Pile (right cradle)
        double centerX = w * 0.5;
        double propGap = compact ? 52 : 66;

        int deckW = compact ? 92 : 104;
        int deckH = compact ? 128 : 144;
        double deckBoxW = deckW + 16;
        double deckBoxH = deckH + 16;
        double deckX = Math.round(centerX - propGap - deckBoxW);
        double deckY = compact ? 182 : 192;
        deckStackHolder.resizeRelocate(deckX, deckY, deckBoxW, deckBoxH);

        double countW = 136;
        deckCountHolder.resizeRelocate(Math.round(deckX + (deckBoxW - countW) * 0.5), deckY + deckBoxH + 2, countW, 20);
        fuseGauge.resizeRelocate(Math.round(deckX + (deckBoxW - 108) * 0.5), deckY + deckBoxH + 23, 108, 8);

        double drawBtnW = compact ? 128 : 142;
        double drawBtnH = compact ? 44 : 48;
        drawButton.resizeRelocate(Math.round(deckX + (deckBoxW - drawBtnW) * 0.5), deckY + deckBoxH + 35, drawBtnW, drawBtnH);
        drawHintHolder.resizeRelocate(Math.round(deckX + (deckBoxW - 180) * 0.5), deckY + deckBoxH + 35 + drawBtnH + 4, 180, 20);

        double discardBoxW = compact ? 122 : 138;
        double discardBoxH = compact ? 142 : 158;
        double discardX = Math.round(centerX + propGap);
        double discardY = compact ? 176 : 186;
        discardPileHolder.resizeRelocate(discardX, discardY, discardBoxW, discardBoxH);
        discardCountBadge.resizeRelocate(Math.round(discardX + (discardBoxW - 124) * 0.5), discardY + discardBoxH + 6, 124, 26);

        // 4. Contextual overlays on the table
        double defuseBtnW = 310;
        double defuseBtnH = 50;
        choosePositionButton.resizeRelocate(Math.round(centerX - defuseBtnW * 0.5), deckY + deckBoxH + 28, defuseBtnW, defuseBtnH);
        returnToSetupButton.resizeRelocate(Math.round(centerX - 110), deckY + deckBoxH + 28, 220, 48);

        double futureW = 248;
        futureOverlay.resizeRelocate(deckX - 104, deckY - 20,
                futureW, futureOverlay.prefHeight(futureW));

        // 5. Contextual Unfurling Parchment Scroll
        double scrollW = Math.min(w - (compact ? 100 : 150), 660);
        double scrollH = compact ? 92 : 98;
        double scrollY = h - (compact ? 304 : 322);
        parchmentScroll.resizeRelocate(Math.round(centerX - scrollW * 0.5), scrollY, scrollW, scrollH);
        renderMessage(scrollW);
        double chronicleH = Math.max(scrollH, messageRibbon.prefHeight(scrollW));
        messageRibbon.resizeRelocate(Math.round(centerX - scrollW * 0.5),
                scrollY + scrollH - chronicleH, scrollW, chronicleH);
        messageRibbon.setVisible(!parchmentScroll.isVisible());

        // 6. Foreground Fanned Poker Hand + bottom corner plaques
        double handAreaY = h - (compact ? 204 : 220);
        handFanPane.resizeRelocate(0, handAreaY, w, h - handAreaY);
        layoutHandFan(w);

        double labelW = handLabelPill.prefWidth(-1);
        handLabelPill.resizeRelocate(16, h - 42, labelW, 30);

        double hintW = keyHintPill.prefWidth(-1);
        keyHintPill.resizeRelocate(w - hintW - 16, h - 42, hintW, 30);
    }

    // ------------------------------------------------------------------
    // refresh
    // ------------------------------------------------------------------

    private void refreshAll() {
        updateBanner();
        updateDeck();
        updateDiscard();
        updateSeats();
        updateActions();
        updateHand();
        layoutStage();
    }

    private void renderMessage(double ribbonWidth) {
        Insets insets = messageRibbon.getInsets();
        double warningWidth = currentWarning
                ? messageIcon.getImage().getWidth() + messageRibbon.getSpacing() : 0;
        double maxTextW = ribbonWidth - insets.getLeft() - insets.getRight() - warningWidth;
        String text = messageText();
        messageRibbon.setAccessibleText(text);
        VBox lines = new VBox(4);
        lines.setAlignment(Pos.CENTER_LEFT);
        lines.getChildren().add(PixelArt.textView(
                CHRONICLE_TEXT.getString("title").toUpperCase(Locale.ROOT), 1, INK_ACCENT));
        List<String> history = chronicle.isEmpty()
                ? List.of(CHRONICLE_TEXT.getString("empty")) : chronicle;
        for (String line : history) {
            if (!line.equals(text)) {
                lines.getChildren().add(bodyText(line, 1, INK_FADED, maxTextW, "chronicle-past"));
            }
        }
        lines.getChildren().add(bodyText(text, 2, currentWarning ? INK_DANGER : INK_DARK,
                maxTextW, "chronicle-now"));
        messageHolder.getChildren().setAll(lines);
        messageRibbon.pseudoClassStateChanged(DANGER, currentWarning);
        messageIcon.setVisible(currentWarning);
        messageIcon.setManaged(currentWarning);
    }

    private String messageText() {
        if (winnerMessage != null) {
            return winnerMessage;
        }
        if (handPairSelection != null) {
            return CatPairChoiceDialog.text("hand.select-match");
        }
        return currentMessage != null ? currentMessage : chronicleLine("prompt", playerName);
    }

    private void updateBanner() {
        banner.pseudoClassStateChanged(DANGER, danger);
        phaseChip.pseudoClassStateChanged(DANGER, danger);
        double available = (compact ? 480 : 540) - 270;
        nameHolder.getChildren().setAll(nameNode(playerName, 4, available, GameTheme.CREAM));

        phaseChip.getChildren().clear();
        if (danger) {
            phaseChip.getChildren().addAll(
                    PixelArt.view(PixelArt.sprite(PixelData.WARN, 2, PixelArt.TAVERN_PALETTE)),
                    PixelArt.textView("PLACE KITTEN", 2, GameTheme.CREAM));
        } else if (remainingTurns > 1) {
            phaseChip.getChildren().addAll(
                    PixelArt.view(CardCatalog.icon(CardCatalog.lookup("ATTACK"), true)),
                    PixelArt.textView(chronicleLine("turns.left", remainingTurns), 2,
                            GameTheme.ROLE_ATTACK));
        } else {
            phaseChip.getChildren().add(PixelArt.textView("DRAW OR PLAY", 2, GameTheme.AMBER));
        }
    }

    private Node nameNode(String name, int maxScale, double available, int color) {
        String text = name == null ? "" : name.trim().toUpperCase(Locale.ROOT);
        if (PixelArt.canRender(text)) {
            for (int scale = maxScale; scale >= 2; scale--) {
                if (PixelArt.textWidth(text, scale) <= available) {
                    return PixelArt.textView(text, scale, color);
                }
            }
        }
        Label fallback = new Label(name == null ? "" : name);
        fallback.getStyleClass().add("fallback-name");
        fallback.setMaxWidth(available);
        return fallback;
    }

    private void updateDeck() {
        int w = compact ? 92 : 104;
        int h = compact ? 128 : 144;
        deckStackHolder.getChildren().clear();
        if (deckSize <= 0) {
            StackPane empty = new StackPane(PixelArt.textView("EMPTY", 2, GameTheme.DANGER));
            empty.getStyleClass().add("empty-coaster");
            fixSize(empty, w, h);
            deckStackHolder.getChildren().add(empty);
        } else {
            int layers = Math.min(4, 1 + deckSize / 6);
            WritableImage back = PixelArt.cardBack(w, h);
            Pane stack = new Pane();
            fixSize(stack, w + 4 * layers, h + 4 * layers);
            for (int i = layers; i >= 0; i--) {
                ImageView layer = PixelArt.view(back);
                layer.setLayoutX(i * 4);
                layer.setLayoutY((layers - i) * 4);
                stack.getChildren().add(layer);
            }
            deckStackHolder.getChildren().add(stack);
        }

        int color = deckSize <= 0 ? GameTheme.DANGER
                : deckSize <= LOW_DECK_THRESHOLD ? GameTheme.AMBER : GameTheme.CREAM;
        String label = deckSize == 1 ? "1 CARD LEFT" : deckSize + " CARDS LEFT";
        deckCountHolder.getChildren().setAll(PixelArt.textView(label, 2, color));

        // 10-pip Fuse Tension Gauge beneath the draw pile
        fuseGauge.getChildren().clear();
        int totalPips = 10;
        int filledPips = deckSize <= 0 ? 0
                : Math.max(1, (int) Math.ceil((deckSize / (double) Math.max(1, initialDeckMax)) * totalPips));
        String pipColor = deckSize <= LOW_DECK_THRESHOLD ? "#FF4D63" : deckSize <= 7 ? "#FFB84D" : "#5FD3A0";
        for (int i = 0; i < totalPips; i++) {
            Region pip = new Region();
            fixSize(pip, 8, 6);
            pip.setStyle(i < filledPips
                    ? "-fx-background-color: " + pipColor + "; -fx-border-color: #140B07; -fx-border-width: 1;"
                    : "-fx-background-color: #1A0E09; -fx-border-color: #4D3527; -fx-border-width: 1;");
            fuseGauge.getChildren().add(pip);
        }
    }

    private void updateDiscard() {
        int boxW = compact ? 122 : 138;
        int boxH = compact ? 142 : 158;
        discardPileHolder.getChildren().clear();

        if (discardSize <= 0) {
            StackPane empty = new StackPane(PixelArt.textView("DISCARD", 2, GameTheme.MUTED));
            empty.getStyleClass().add("empty-coaster");
            fixSize(empty, compact ? 92 : 104, compact ? 128 : 144);
            discardPileHolder.getChildren().add(empty);
            discardCountBadge.getChildren().setAll(
                    PixelArt.textView("0 PLAYED", 2, GameTheme.MUTED));
            return;
        }

        Pane tossPile = new Pane();
        fixSize(tossPile, boxW, boxH);
        int cardW = CardCatalog.cardWidth(true);
        int cardH = CardCatalog.cardHeight(true);
        double baseX = (boxW - cardW) * 0.5;
        double baseY = (boxH - cardH) * 0.5;

        int knownCount = recentDiscards.size();
        int fillerCount = Math.min(DISPLAYED_DISCARDS, discardSize) - knownCount;
        int totalVisible = fillerCount + knownCount;
        int angleOffset = Math.max(0, TOSS_ANGLES.length - totalVisible);
        double[] angles = discardHovered ? PEEK_ANGLES : TOSS_ANGLES;
        double spreadX = discardHovered ? 13.0 : 5.0;

        WritableImage back = PixelArt.cardBack(cardW, cardH);
        for (int i = 0; i < fillerCount; i++) {
            ImageView filler = PixelArt.view(back);
            int slot = i;
            filler.setLayoutX(baseX + (slot - totalVisible * 0.5) * spreadX);
            filler.setLayoutY(baseY + (1 - i) * 3);
            filler.setRotate(angles[(angleOffset + slot) % angles.length]);
            tossPile.getChildren().add(filler);
        }

        for (int i = 0; i < knownCount; i++) {
            CardCatalog.Info info = CardCatalog.lookup(recentDiscards.get(i).name());
            ImageView face = PixelArt.view(CardCatalog.face(info, true, CardCatalog.Face.READY));
            face.getStyleClass().add("discard-face");
            face.setAccessibleText(info.name);
            int slot = fillerCount + i;
            face.setLayoutX(baseX + (slot - totalVisible * 0.5) * spreadX);
            face.setLayoutY(baseY - i * 3);
            face.setRotate(angles[(angleOffset + slot) % angles.length]);
            tossPile.getChildren().add(face);
        }

        discardPileHolder.getChildren().add(tossPile);
        if (discardHovered && !recentDiscards.isEmpty()) {
            CardCatalog.Info topInfo = CardCatalog.lookup(
                    recentDiscards.get(recentDiscards.size() - 1).name());
            discardCountBadge.getChildren().setAll(
                    PixelArt.textView(topInfo.name.toUpperCase(Locale.ROOT), 2, topInfo.role));
        } else {
            discardCountBadge.getChildren().setAll(
                    PixelArt.textView(discardSize + " DISCARDED", 2, GameTheme.SAND));
        }
    }

    private void updateSeats() {
        seatRow.getChildren().clear();
        if (seats.isEmpty()) {
            return;
        }
        double feltW = TavernBackground.feltWidth(getWidth() > 0 ? getWidth() : 900, compact);
        double plaqueW = Math.min(170, (feltW - seatRow.getSpacing() * (seats.size() - 1)) / seats.size());
        WritableImage head = PixelArt.sprite(PixelData.HEAD_OPEN, 1, PixelArt.TAVERN_PALETTE);
        WritableImage ghost = PixelArt.mono(PixelData.HEAD_OPEN, 1, 0xFF5A4636);
        for (PlayerSummary seat : seats) {
            boolean current = seat.getPlayerIndex() == currentSeat && !seat.isEliminated();
            boolean out = seat.isEliminated();
            ImageView face = PixelArt.view(out ? ghost : head);
            int nameColor = out ? DIM_TEXT : current ? GameTheme.BRASS_BRIGHT : GameTheme.CREAM;
            double textW = plaqueW - 22 - 30;
            Node name = seatName(seat.getName(), textW, nameColor);
            String detail = out ? chronicleLine("seat.out")
                    : chronicleLine("seat.cards", seat.getHandSize());
            int detailColor = out ? GameTheme.DANGER : current ? GameTheme.AMBER : GameTheme.MUTED;
            VBox text = new VBox(3, name, PixelArt.textView(detail, 1, detailColor));
            text.setAlignment(Pos.CENTER_LEFT);
            HBox plaque = new HBox(8, face, text);
            plaque.getStyleClass().add("seat-plaque");
            plaque.pseudoClassStateChanged(CURRENT, current);
            plaque.pseudoClassStateChanged(OUT, out);
            plaque.setAlignment(Pos.CENTER_LEFT);
            plaque.setPadding(new Insets(6, 10, 6, 10));
            fixSize(plaque, plaqueW, 42);
            plaque.setAccessibleText(seat.getName() + ", " + detail.toLowerCase(Locale.ROOT)
                    + (current ? ", current turn" : ""));
            if (current) {
                StackPane marker = new StackPane(plaque, turnTag());
                StackPane.setAlignment(marker.getChildren().get(1), Pos.TOP_RIGHT);
                StackPane.setMargin(marker.getChildren().get(1), new Insets(-7, 6, 0, 0));
                seatRow.getChildren().add(marker);
            } else {
                seatRow.getChildren().add(plaque);
            }
        }
    }

    private Node turnTag() {
        StackPane tag = new StackPane(PixelArt.textView(chronicleLine("seat.turn"), 1, GameTheme.PLATE));
        tag.getStyleClass().add("seat-turn-tag");
        tag.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return tag;
    }

    private Node seatName(String rawName, double available, int color) {
        String text = rawName == null ? "" : rawName.trim().toUpperCase(Locale.ROOT);
        if (PixelArt.canRender(text)) {
            for (int scale = 2; scale >= 1; scale--) {
                if (PixelArt.textWidth(text, scale) <= available) {
                    return PixelArt.textView(text, scale, color);
                }
            }
        }
        Label fallback = new Label(rawName == null ? "" : rawName);
        fallback.getStyleClass().add("seat-name");
        fallback.setMaxWidth(available);
        return fallback;
    }

    private void updateActions() {
        boolean blocked = danger || turnActionsDisabled;
        drawButton.setDisable(blocked);
        drawButton.setVisible(!danger);
        deckStackHolder.setDisable(blocked);
        deckStackHolder.setTranslateY(0);
        drawButton.setGraphic(PixelArt.textView("DRAW", 3, blocked ? DIM_TEXT : GameTheme.PLATE));

        drawHintHolder.getChildren().clear();
        if (danger) {
            drawHintHolder.getChildren().add(
                    PixelArt.textView("PLACE KITTEN FIRST", 2, GameTheme.DANGER));
        }
        drawHintHolder.setVisible(danger);
        choosePositionButton.setDisable(turnActionsDisabled);
        choosePositionButton.setVisible(danger);
        returnToSetupButton.setVisible(winnerMessage != null);
    }

    private void updateFuture() {
        boolean any = !futureCards.isEmpty();
        futureOverlay.setVisible(any);
        futureChips.getChildren().clear();
        if (!any) {
            return;
        }
        futureTitleHolder.getChildren().setAll(
                PixelArt.textView("TOP " + futureCards.size() + " IN DECK", 2, GameTheme.SAND));
        String[] ordinals = {"1ST", "2ND", "3RD"};
        for (int i = 0; i < futureCards.size(); i++) {
            CardCatalog.Info info = CardCatalog.lookup(futureCards.get(i).name());
            String ord = i < ordinals.length ? ordinals[i] : "#" + (i + 1);
            ImageView mini = PixelArt.view(CardCatalog.face(info, true, CardCatalog.Face.READY));
            mini.setFitWidth(28);
            mini.setPreserveRatio(true);
            double offset = i - (futureCards.size() - 1) / 2.0;
            mini.setRotate(offset * 12);
            mini.setTranslateY(Math.abs(offset) * 4);
            Label name = new Label(info.name);
            name.getStyleClass().add("chip-name");
            name.setWrapText(true);
            name.setTextAlignment(TextAlignment.CENTER);

            VBox chip = new VBox(3, mini, PixelArt.textView(ord, 1, info.role), name);
            chip.setAlignment(Pos.TOP_CENTER);
            chip.getStyleClass().add("future-chip");
            chip.setAccessibleText(ord + ": " + info.name);
            chip.setPrefWidth(70);
            chip.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(chip, Priority.ALWAYS);
            futureChips.getChildren().add(chip);
        }
    }

    // ------------------------------------------------------------------
    // physical card flight & micro-interaction animations
    // ------------------------------------------------------------------

    private void triggerDrawWithFlight() {
        if (danger || turnActionsDisabled) {
            return;
        }
        clearHandPairSelection();
        int previousDeckSize = deckSize;
        int previousDiscardSize = discardSize;
        onDraw.run();
        if (deckSize == previousDeckSize - 1 && !danger && discardSize == previousDiscardSize) {
            launchDrawCardFlight();
        }
    }

    private void launchDrawCardFlight() {
        int cardW = CardCatalog.cardWidth(compact);
        int cardH = CardCatalog.cardHeight(compact);
        ImageView phantom = PixelArt.view(PixelArt.cardBack(cardW, cardH));
        double startX = deckStackHolder.getLayoutX() + (deckStackHolder.getWidth() - cardW) * 0.5;
        double startY = deckStackHolder.getLayoutY() + (deckStackHolder.getHeight() - cardH) * 0.5;
        double endX = (getWidth() > 0 ? getWidth() : 900) * 0.5 - cardW * 0.5;
        double endY = (getHeight() > 0 ? getHeight() : 760) - cardH * 0.85;

        phantom.setLayoutX(startX);
        phantom.setLayoutY(startY);
        fxLayer.getChildren().add(phantom);

        TranslateTransition move = new TranslateTransition(Duration.millis(230), phantom);
        move.setToX(endX - startX);
        move.setToY(endY - startY);
        move.setInterpolator(Interpolator.EASE_OUT);

        RotateTransition spin = new RotateTransition(Duration.millis(230), phantom);
        spin.setFromAngle(-4);
        spin.setToAngle(6);

        FadeTransition fade = new FadeTransition(Duration.millis(230), phantom);
        fade.setFromValue(0.95);
        fade.setToValue(0.0);

        playEffect(new ParallelTransition(move, spin, fade), phantom);
    }

    private void launchPlayCardFlight(CardCatalog.Info info, double startX, double startY, double angle) {
        int cardW = CardCatalog.cardWidth(compact);
        int cardH = CardCatalog.cardHeight(compact);
        ImageView phantom = PixelArt.view(CardCatalog.face(info, compact, CardCatalog.Face.HOVER));

        double endX = discardPileHolder.getLayoutX() + (discardPileHolder.getWidth() - cardW) * 0.5;
        double endY = discardPileHolder.getLayoutY() + (discardPileHolder.getHeight() - cardH) * 0.5;

        phantom.setLayoutX(startX);
        phantom.setLayoutY(startY);
        phantom.setRotate(angle);
        fxLayer.getChildren().add(phantom);

        TranslateTransition toss = new TranslateTransition(Duration.millis(240), phantom);
        toss.setToX(endX - startX);
        toss.setToY(endY - startY);
        toss.setInterpolator(Interpolator.EASE_OUT);

        RotateTransition spin = new RotateTransition(Duration.millis(240), phantom);
        spin.setToAngle(TOSS_ANGLES[recentDiscards.size() % TOSS_ANGLES.length]);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(240), phantom);
        shrink.setToX(0.88);
        shrink.setToY(0.88);

        FadeTransition fade = new FadeTransition(Duration.millis(240), phantom);
        fade.setFromValue(1.0);
        fade.setToValue(0.15);

        playEffect(new ParallelTransition(toss, spin, shrink, fade), phantom);
    }

    private void spawnFloatingHeart(double x, double y) {
        ImageView heart = PixelArt.view(heartSprite);
        heart.setLayoutX(x);
        heart.setLayoutY(y);
        fxLayer.getChildren().add(heart);

        TranslateTransition rise = new TranslateTransition(Duration.millis(750), heart);
        rise.setByY(-28);
        FadeTransition fade = new FadeTransition(Duration.millis(750), heart);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        playEffect(new ParallelTransition(rise, fade), heart);
    }

    private void spawnFloatingSparkle(double x, double y) {
        ImageView spark = PixelArt.view(PixelArt.sprite(PixelData.STAR_PLUS, 3, PixelArt.TAVERN_PALETTE));
        spark.setLayoutX(x);
        spark.setLayoutY(y);
        fxLayer.getChildren().add(spark);

        TranslateTransition rise = new TranslateTransition(Duration.millis(600), spark);
        rise.setByY(-20);
        FadeTransition fade = new FadeTransition(Duration.millis(600), spark);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        playEffect(new ParallelTransition(rise, fade), spark);
    }

    private void playEffect(Animation animation, Node graphic) {
        activeEffects.add(animation);
        animation.setOnFinished(event -> {
            fxLayer.getChildren().remove(graphic);
            activeEffects.remove(animation);
        });
        animation.play();
    }

    private void clearEffects() {
        for (Animation animation : activeEffects) {
            animation.stop();
        }
        activeEffects.clear();
        fxLayer.getChildren().clear();
    }

    private void playCard(int handIndex, CardType type, CardCatalog.Info info, Button source) {
        HandPairSelection selection = handPairSelection;
        if (selection == null) {
            playWithDiscardFeedback(type, info, source, () -> onPlayCard.accept(handIndex));
        } else if (handIndex == selection.firstIndex) {
            clearHandPairSelection();
        } else if (selection.matches.contains(handIndex)) {
            playWithDiscardFeedback(type, info, source, () -> {
                clearHandPairSelection();
                selection.onMatchSelected.accept(handIndex);
            });
        }
    }

    private void clearHandPairSelection() {
        if (handPairSelection != null) {
            handPairSelection = null;
            layoutStage();
        }
    }

    private void playWithDiscardFeedback(CardType type, CardCatalog.Info info, Button source,
                                         Runnable submit) {
        int previousDiscardSize = discardSize;
        List<CardType> previousDiscards = List.copyOf(recentDiscards);
        String actor = playerName;
        int chronicleMark = chronicle.size();
        double startX = handFanPane.getLayoutX() + source.getLayoutX() + source.getTranslateX();
        double startY = handFanPane.getLayoutY() + source.getLayoutY() + source.getTranslateY();
        double angle = source.getRotate();
        submit.run();
        int discarded = discardSize - previousDiscardSize;
        if (discarded == 1 || (discarded == 2 && type.isCatCard())) {
            narratePlay(actor, type, info, chronicleMark);
            showPlayedCards(previousDiscards, type, discarded);
            launchPlayCardFlight(info, startX, startY, angle);
        }
    }

    private void narratePlay(String actor, CardType type, CardCatalog.Info info, int mark) {
        String line;
        if (type.isCatCard()) {
            line = chronicleLine("played.pair", actor, info.name, lastTargetName);
        } else if (CHRONICLE_TEXT.containsKey("played." + type.name())) {
            line = chronicleLine("played." + type.name(), actor);
        } else if (type == CardType.SNIFF_TEST) {
            return;
        } else {
            line = chronicleLine("played.card", actor, info.name);
        }
        chronicle.add(Math.max(0, Math.min(mark, chronicle.size())), line);
        while (chronicle.size() > CHRONICLE_LINES) {
            chronicle.remove(0);
        }
        layoutStage();
    }

    private static final class HandPairSelection {
        private final int firstIndex;
        private final Set<Integer> matches;
        private final IntConsumer onMatchSelected;

        private HandPairSelection(int firstIndex, List<Integer> matches, IntConsumer onMatchSelected) {
            this.firstIndex = firstIndex;
            this.matches = Set.copyOf(matches);
            this.onMatchSelected = onMatchSelected;
        }
    }

    private void showPlayedCards(List<CardType> previousDiscards, CardType type, int count) {
        recentDiscards.addAll(previousDiscards);
        recentDiscards.addAll(Collections.nCopies(count, type));
        int excess = recentDiscards.size() - DISPLAYED_DISCARDS;
        if (excess > 0) {
            recentDiscards.subList(0, excess).clear();
        }
        updateDiscard();
    }

    // ------------------------------------------------------------------
    // foreground fanned poker hand & unfurling parchment rule scroll
    // ------------------------------------------------------------------

    private void updateHand() {
        handFanPane.getChildren().clear();
        cardButtons.clear();
        baseAngles.clear();
        activeCardIndex = -1;

        for (int index = 0; index < hand.size(); index++) {
            Button btn = createCardButton(hand.get(index), index);
            cardButtons.add(btn);
            handFanPane.getChildren().add(btn);
        }

        boolean locked = danger || turnActionsDisabled;
        String chipText = locked ? "LOCKED" : String.valueOf(hand.size());
        handChip.getChildren().setAll(PixelArt.textView(
                chipText, 2, locked ? GameTheme.DANGER : GameTheme.AMBER));
        handChip.pseudoClassStateChanged(DANGER, locked);

        showEmptyInfo();
        layoutHandFan(getWidth() > 0 ? getWidth() : 900);
    }

    private void layoutHandFan(double totalWidth) {
        int n = cardButtons.size();
        baseAngles.clear();
        if (n == 0) {
            return;
        }

        double w = CardCatalog.cardWidth(compact);
        double h = CardCatalog.cardHeight(compact);
        double maxSpan = Math.max(240, totalWidth - (compact ? 170 : 210));
        double naturalStep = w + 10;
        double step = n == 1 ? 0 : Math.min(naturalStep, (maxSpan - w) / (n - 1));
        double spanW = w + (n - 1) * step;
        double startX = (totalWidth - spanW) * 0.5;
        double apexY = compact ? 18 : 22;

        for (int i = 0; i < n; i++) {
            Button btn = cardButtons.get(i);
            double t = n == 1 ? 0.0 : (2.0 * i / (n - 1)) - 1.0;
            double angle = t * Math.min(11.0, n * 1.5);
            double topY = apexY + (t * t) * Math.min(20.0, n * 2.4);

            baseAngles.add(angle);

            btn.resizeRelocate(Math.round(startX + i * step), Math.round(topY), w, h);
        }
        applyFanElevation();
    }

    private boolean isCatPairTwin(int i) {
        if (activeCardIndex < 0 || activeCardIndex >= hand.size() || i == activeCardIndex) {
            return false;
        }
        CardType activeType = hand.get(activeCardIndex);
        CardCatalog.Info activeInfo = CardCatalog.lookup(activeType.name());
        return "CAT".equals(activeInfo.tag) && hand.get(i) == activeType;
    }

    private void applyFanElevation() {
        int n = cardButtons.size();
        if (n == 0 || baseAngles.size() != n) {
            return;
        }
        for (int i = 0; i < n; i++) {
            Button btn = cardButtons.get(i);
            boolean selecting = handPairSelection != null;
            boolean first = selecting && i == handPairSelection.firstIndex;
            boolean match = selecting && handPairSelection.matches.contains(i);
            boolean active = selecting ? first : i == activeCardIndex;
            boolean twin = selecting ? match : isCatPairTwin(i);
            btn.pseudoClassStateChanged(PAIR_FIRST, first);
            btn.pseudoClassStateChanged(PAIR_MATCH, match);
            btn.pseudoClassStateChanged(PAIR_UNAVAILABLE, selecting && !first && !match);
            btn.setViewOrder(active ? -n : match ? -n + 0.5 : -i);
            if (active) {
                btn.setRotate(0);
                btn.setTranslateY(-28);
                btn.setTranslateX(0);
                btn.setScaleX(1.07);
                btn.setScaleY(1.07);
            } else if (twin) {
                // Matching Cat pair lifts together to signal combo synergy!
                btn.setRotate(baseAngles.get(i) * 0.45);
                btn.setTranslateY(-14);
                btn.setTranslateX(0);
                btn.setScaleX(1.03);
                btn.setScaleY(1.03);
            } else {
                btn.setRotate(baseAngles.get(i));
                btn.setTranslateY(0);
                btn.setScaleX(1.0);
                btn.setScaleY(1.0);
                if (activeCardIndex >= 0 && Math.abs(i - activeCardIndex) == 1) {
                    btn.setTranslateX(i < activeCardIndex ? -12 : 12);
                } else {
                    btn.setTranslateX(0);
                }
            }
        }
    }

    private Button createCardButton(CardType type, int handIndex) {
        CardCatalog.Info info = CardCatalog.lookup(type.name());
        boolean wired = CardCatalog.isWired(info);
        boolean locked = danger || turnActionsDisabled;
        Status status = !wired ? Status.SOON : locked ? Status.LOCKED
                : CardCatalog.isAutomatic(info) ? Status.AUTO : Status.READY;
        CardCatalog.Face rest = status == Status.SOON ? CardCatalog.Face.SOON
                : status == Status.LOCKED ? CardCatalog.Face.LOCKED : CardCatalog.Face.READY;
        CardCatalog.Face raised = status == Status.READY ? CardCatalog.Face.HOVER : rest;

        int w = CardCatalog.cardWidth(compact);
        int h = CardCatalog.cardHeight(compact);
        ImageView face = PixelArt.view(CardCatalog.face(info, compact, rest));

        Label name = new Label(info.name.replace("-", "-\n"));
        name.getStyleClass().add("card-name");
        if (compact) {
            name.getStyleClass().add("compact");
        }
        if (status == Status.SOON || status == Status.LOCKED) {
            name.getStyleClass().add("dim");
        }
        name.setWrapText(true);
        name.setTextAlignment(TextAlignment.CENTER);
        name.setAlignment(Pos.CENTER);
        name.setPrefSize(w - 10, 48);
        name.setMinSize(w - 10, 48);
        name.setMaxSize(w - 10, 48);
        StackPane.setAlignment(name, Pos.BOTTOM_CENTER);
        StackPane.setMargin(name, new Insets(0, 0, 5, 0));

        StackPane card = new StackPane(face, name);
        fixSize(card, w, h);

        if (status != Status.READY) {
            int badgeColor = status == Status.LOCKED ? GameTheme.DANGER
                    : status == Status.AUTO ? GameTheme.ROLE_SAFETY : GameTheme.AMBER;
            StackPane badge = new StackPane(PixelArt.textView(status.name(), 1, badgeColor));
            badge.getStyleClass().add("card-badge");
            badge.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            StackPane.setAlignment(badge, Pos.TOP_RIGHT);
            StackPane.setMargin(badge, new Insets(5, 5, 0, 0));
            card.getChildren().add(badge);
        }

        Button button = new Button();
        button.getStyleClass().add("card-button");
        button.setId("hand-card-" + handIndex);
        button.setGraphic(card);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setPadding(Insets.EMPTY);
        fixSize(button, w, h);
        button.setAccessibleText(info.name + ", " + info.tag.toLowerCase(Locale.ROOT) + " card"
                + (status == Status.SOON ? ", not available yet"
                : status == Status.LOCKED ? ", locked"
                : status == Status.AUTO ? ", used automatically" : ""));
        button.setAccessibleHelp(info.effect);

        button.setOnMouseEntered(event -> {
            face.setImage(CardCatalog.face(info, compact, raised));
            activeCardIndex = handIndex;
            applyFanElevation();
            showInfo(info, status);
        });
        button.setOnMouseExited(event -> {
            face.setImage(CardCatalog.face(info, compact, button.isFocused() ? raised : rest));
            if (activeCardIndex == handIndex && !button.isFocused()) {
                activeCardIndex = -1;
                applyFanElevation();
                showEmptyInfo();
            }
        });
        button.focusedProperty().addListener((obs, was, isFocused) -> {
            face.setImage(CardCatalog.face(info, compact, isFocused || button.isHover() ? raised : rest));
            if (isFocused) {
                activeCardIndex = handIndex;
                applyFanElevation();
                showInfo(info, status);
            } else if (activeCardIndex == handIndex && !button.isHover()) {
                activeCardIndex = -1;
                applyFanElevation();
                showEmptyInfo();
            }
        });
        button.setOnAction(event -> {
            if (status == Status.READY) {
                playCard(handIndex, type, info, button);
            } else {
                showInfo(info, status);
            }
        });
        button.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                button.fire();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT && !cardButtons.isEmpty()) {
                int prev = (handIndex - 1 + cardButtons.size()) % cardButtons.size();
                cardButtons.get(prev).requestFocus();
                event.consume();
            } else if (event.getCode() == KeyCode.RIGHT && !cardButtons.isEmpty()) {
                int next = (handIndex + 1) % cardButtons.size();
                cardButtons.get(next).requestFocus();
                event.consume();
            }
        });
        return button;
    }

    private void showEmptyInfo() {
        shownInfo = null;
        parchmentScroll.setVisible(false);
    }

    private void showInfo(CardCatalog.Info info, Status status) {
        shownInfo = info;
        shownStatus = status;
        renderInfo();
        parchmentScroll.setVisible(true);
    }

    private void renderInfo() {
        if (shownInfo == null) {
            parchmentScroll.setVisible(false);
            return;
        }
        CardCatalog.Info info = shownInfo;
        double w = getWidth() > 0 ? getWidth() : 900;
        double scrollW = Math.min(w - (compact ? 100 : 150), 660);
        double textW = Math.max(200, scrollW - 130);

        StackPane iconSlot = new StackPane(PixelArt.view(CardCatalog.icon(info, true)));
        iconSlot.setStyle(notchedStyle(info.role, GameTheme.PLATE));
        fixSize(iconSlot, 52, 52);

        Node name = bodyText(info.name, 3, INK_DARK, textW - 150, "parchment-title");

        HBox tag = new HBox(PixelArt.textView(info.tag, 2, info.role));
        tag.setAlignment(Pos.CENTER);
        tag.setMaxWidth(Region.USE_PREF_SIZE);
        tag.setStyle(notchedStyle(info.role, GameTheme.PLATE, 2));

        HBox titleLine = new HBox(10, name, tag);
        titleLine.setAlignment(Pos.CENTER_LEFT);

        // Highlight matching Cat-Pair combo readiness directly on the parchment!
        if ("CAT".equals(info.tag) && activeCardIndex >= 0 && activeCardIndex < hand.size()) {
            CardType activeType = hand.get(activeCardIndex);
            long matchCount = hand.stream().filter(c -> c == activeType).count();
            if (matchCount >= 2) {
                HBox pairBadge = new HBox(PixelArt.textView("PAIR X" + matchCount, 2, GameTheme.AMBER));
                pairBadge.setAlignment(Pos.CENTER);
                pairBadge.setMaxWidth(Region.USE_PREF_SIZE);
                pairBadge.setStyle(notchedStyle(GameTheme.AMBER, GameTheme.PLATE, 2));
                titleLine.getChildren().add(pairBadge);
            }
        }

        Node effect = bodyText(info.effect, 2, INK_DARK, textW, "parchment-body");

        HBox statusRow = new HBox(8);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        if (shownStatus == Status.READY) {
            statusRow.getChildren().addAll(
                    PixelArt.view(PixelArt.mono(PixelData.ARROW, 2, INK_ACCENT)),
                    PixelArt.textView("CLICK OR ENTER TO PLAY", 2, INK_ACCENT));
        } else if (shownStatus == Status.SOON) {
            statusRow.getChildren().add(
                    PixelArt.textView("NOT AVAILABLE YET", 2, 0xFF6E5641));
        } else if (shownStatus == Status.AUTO) {
            statusRow.getChildren().add(
                    PixelArt.textView("USED AUTOMATICALLY WHEN A KITTEN IS DRAWN", 2, 0xFF2E7D5B));
        } else {
            statusRow.getChildren().add(
                    PixelArt.textView(danger ? "LOCKED: PLACE KITTEN FIRST" : "LOCKED", 2, INK_DANGER));
        }

        VBox copy = new VBox(5, titleLine, effect, statusRow);
        copy.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(copy, Priority.ALWAYS);

        parchmentScroll.getChildren().setAll(iconSlot, copy);
    }

    // ------------------------------------------------------------------
    // small helpers
    // ------------------------------------------------------------------

    private Node bodyText(String text, int scale, int color, double maxWidth, String fallbackClass) {
        String safe = text == null ? "" : text;
        if (PixelArt.canRender(safe.toUpperCase(Locale.ROOT))) {
            return PixelArt.paragraphView(safe, scale, color, (int) Math.max(60, maxWidth));
        }
        Label fallback = new Label(safe);
        fallback.getStyleClass().add(fallbackClass);
        fallback.setWrapText(true);
        fallback.setMinHeight(Region.USE_PREF_SIZE);
        fallback.setMaxWidth(maxWidth);
        return fallback;
    }

    private void shakeTable() {
        if (shakeTimeline != null) {
            shakeTimeline.stop();
        }
        shakeTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(stageLayer.translateXProperty(), 0, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(55), new KeyValue(stageLayer.translateXProperty(), -8, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(110), new KeyValue(stageLayer.translateXProperty(), 8, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(165), new KeyValue(stageLayer.translateXProperty(), -4, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(220), new KeyValue(stageLayer.translateXProperty(), 4, Interpolator.DISCRETE)),
                new KeyFrame(Duration.millis(275), new KeyValue(stageLayer.translateXProperty(), 0, Interpolator.DISCRETE))
        );
        shakeTimeline.play();
    }

    private static void fixSize(Region region, double width, double height) {
        region.setPrefSize(width, height);
        region.setMinSize(width, height);
        region.setMaxSize(width, height);
    }

    private static String notchedStyle(int borderArgb, int fillArgb) {
        return notchedStyle(borderArgb, fillArgb, 4);
    }

    private static String notchedStyle(int borderArgb, int fillArgb, int notch) {
        String border = GameTheme.css(borderArgb);
        String fill = GameTheme.css(fillArgb);
        int n = notch;
        return "-fx-background-color: " + border + ", " + border + ", " + fill + ", " + fill + ";"
                + " -fx-background-insets: 0 " + n + " 0 " + n + ", " + n + " 0 " + n + " 0, "
                + n + " " + 2 * n + " " + n + " " + 2 * n + ", "
                + 2 * n + " " + n + " " + 2 * n + " " + n + ";"
                + " -fx-background-radius: 0;"
                + " -fx-padding: " + (n == 4 ? "8 12 8 12;" : "3 8 3 8;");
    }
}
