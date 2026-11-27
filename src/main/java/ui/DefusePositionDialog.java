package ui;

import java.util.OptionalInt;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Modal number picker for "where does the Exploding Kitten go?".
 * Position {@code minimum} is the top of the draw pile and {@code maximum} the bottom.
 * Cancel, Esc or closing the window returns an empty result; the caller keeps waiting.
 */
final class DefusePositionDialog {

    private static final int COLUMNS = 8;
    private static final int TILE_WIDTH = 60;
    private static final int TILE_HEIGHT = 46;
    private static final int TILE_GAP = 8;
    private static final int GRID_WIDTH = COLUMNS * TILE_WIDTH + (COLUMNS - 1) * TILE_GAP;

    private static final Color SLIVER = Color.web("#8F6E58");
    private static final Color MARKER = Color.web("#FF4D63");

    private int selected;
    private boolean confirmed;

    private DefusePositionDialog() {
    }

    static OptionalInt show(Window owner, String stylesheet, int minimum, int maximum) {
        return new DefusePositionDialog().open(owner, stylesheet, minimum, maximum);
    }

    private OptionalInt open(Window owner, String stylesheet, int minimum, int maximum) {
        selected = minimum;
        int cards = Math.max(0, maximum - minimum);

        // ---- header ----
        ImageView warn = PixelArt.view(PixelArt.sprite(PixelData.WARN, 3, PixelArt.TAVERN_PALETTE));
        ImageView title = PixelArt.textView(Texts.get("defuse.dialog.title"), 3, GameTheme.CREAM);
        HBox header = new HBox(14, warn, title);
        header.setAlignment(Pos.CENTER_LEFT);

        String help = minimum == maximum
                ? Texts.get("defuse.dialog.only-card")
                : Texts.get("defuse.dialog.help", minimum, maximum);
        Node description = PixelArt.paragraphView(help, 2, GameTheme.SAND, GRID_WIDTH);

        // ---- number tiles ----
        StackPane summary = new StackPane();
        summary.setAlignment(Pos.CENTER_LEFT);
        Canvas preview = new Canvas(GRID_WIDTH, 56);

        ToggleGroup group = new ToggleGroup();
        FlowPane grid = new FlowPane(TILE_GAP, TILE_GAP);
        grid.setPrefWrapLength(GRID_WIDTH);
        grid.setMaxWidth(GRID_WIDTH);
        grid.setPadding(new Insets(6, 6, 6, 6));
        for (int position = minimum; position <= maximum; position++) {
            ToggleButton tile = new ToggleButton(String.valueOf(position));
            tile.getStyleClass().add("tile");
            tile.setToggleGroup(group);
            tile.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
            tile.setMinSize(TILE_WIDTH, TILE_HEIGHT);
            tile.setMaxSize(TILE_WIDTH, TILE_HEIGHT);
            tile.setUserData(position);
            tile.setAccessibleText(Texts.get("defuse.dialog.position", position));
            tile.setSelected(position == minimum);
            grid.getChildren().add(tile);
        }
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                if (oldToggle != null) {
                    oldToggle.setSelected(true); // always keep one tile selected
                }
                return;
            }
            selected = (Integer) newToggle.getUserData();
            refreshPreview(preview, summary, selected - minimum, cards);
        });
        refreshPreview(preview, summary, 0, cards);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.getStyleClass().add("tile-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        int rows = (int) Math.ceil((maximum - minimum + 1) / (double) COLUMNS);
        int visibleRows = Math.min(rows, 4);
        scroll.setPrefViewportHeight(visibleRows * TILE_HEIGHT + (visibleRows - 1) * TILE_GAP + 12);
        scroll.setPrefViewportWidth(GRID_WIDTH + 12);

        // ---- buttons ----
        Stage stage = new Stage();
        Button confirm = actionButton(Texts.get("defuse.dialog.confirm"), "confirm-button");
        Button cancel = actionButton(Texts.get("defuse.dialog.cancel"), "cancel-button");
        confirm.setOnAction(e -> {
            confirmed = true;
            stage.close();
        });
        cancel.setOnAction(e -> stage.close());

        ImageView keys = PixelArt.textView(Texts.get("defuse.dialog.keys"), 2, GameTheme.MUTED);
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox buttons = new HBox(12, spacer, cancel, confirm);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(14, header, description, scroll, preview, summary, keys, buttons);
        root.getStyleClass().add("dialog-root");
        Texts.applyFont(root);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(26, 28, 24, 28));
        root.setPrefWidth(GRID_WIDTH + 12 + 56 + 8);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(stylesheet);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmed = scene.getFocusOwner() != cancel;
                stage.close();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
                event.consume();
            }
        });

        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(Texts.get("defuse.dialog.window"));
        stage.setScene(scene);
        stage.setOnShown(e -> {
            if (owner != null) {
                stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
                stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
            }
            grid.getChildren().get(0).requestFocus();
        });
        stage.showAndWait();

        return confirmed ? OptionalInt.of(selected) : OptionalInt.empty();
    }

    private static Button actionButton(String text, String styleClass) {
        Button button = new Button();
        button.getStyleClass().addAll("dialog-button", styleClass);
        button.setGraphic(PixelArt.textView(text, 2, styleClass.startsWith("confirm")
                ? GameTheme.PLATE : GameTheme.CREAM));
        button.setAccessibleText(text);
        button.setPrefSize(150, 48);
        return button;
    }

    /** Draws the draw pile as thin slivers, with the Kitten marker at the chosen slot. */
    private static void refreshPreview(Canvas canvas, StackPane summary, int slot, int cards) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);
        int columns = cards + 1;
        double step = Math.max(5, Math.floor(w / columns));
        double sliverWidth = Math.max(3, step - 3);
        double offset = Math.max(0, Math.floor((w - step * columns) / 2));
        for (int column = 0; column < columns; column++) {
            double x = offset + column * step;
            if (column == slot) {
                gc.setFill(MARKER);
                gc.fillRect(x, 6, sliverWidth, h - 12);
            } else {
                gc.setFill(SLIVER);
                gc.fillRect(x, 14, sliverWidth, h - 28);
            }
        }
        summary.getChildren().setAll(PixelArt.textView(Texts.get("defuse.dialog.above", slot), 2, GameTheme.CREAM));
    }
}
