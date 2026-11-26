package ui;

import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Refined pixel-art tavern stage:
 * - Warm cocoa-oak wall planks with checkerboard dithering, knots, iron nails & slow-breathing lantern
 * - Smooth beveled mahogany & oak plank tabletop with rich 2px wood grain & warm overhead light
 * - Inlaid rounded burgundy felt mat with double gold-leaf pinstripe, carved card cradles & paw compass
 * - Master-crafted scale-3 PixelData tavern stein & dagger-pinned wax-seal scroll matching the pixel cat
 */
final class TavernBackground extends Pane {

    private static final int PLANK = 96;
    private static final int LANTERN_SCALE = 4;
    private static final double COMPACT_BELOW = 860;

    private static final Color SKY_TOP = Color.web("#0B0819");
    private static final Color SKY_BOTTOM = Color.web("#221A52");
    private static final Color WINDOW_FRAME = Color.web("#160D09");
    private static final Color WINDOW_TRIM = Color.web("#6E4D37");
    private static final Color MOONLIGHT = Color.web("#7B68E0", 0.045);

    private static final Color SEAM_DARK = Color.web("#140B07", 0.34);
    private static final Color SEAM_LIGHT = Color.web("#FFE4B8", 0.075);
    private static final Color GRAIN_DARK = Color.web("#1A0E09", 0.14);
    private static final Color GRAIN_LIGHT = Color.web("#FFD9A0", 0.045);
    private static final Color NAIL_COLOR = Color.web("#140B07", 0.45);
    private static final Color NAIL_SHINE = Color.web("#E0AA58", 0.35);

    private final Canvas canvas = new Canvas();
    private final Canvas ambientCanvas = new Canvas();
    private final Image lanternA = PixelArt.sprite(PixelData.LANTERN_A, LANTERN_SCALE, PixelArt.TAVERN_PALETTE);
    private final Image lanternB = PixelArt.sprite(PixelData.LANTERN_B, LANTERN_SCALE, PixelArt.TAVERN_PALETTE);
    private final Image moonSprite = PixelArt.sprite(PixelData.MOON, 3);
    private final Image centerBomb = PixelArt.sprite(PixelData.BOMB, 3, PixelArt.TAVERN_PALETTE);
    private final Image sparkA = PixelArt.sprite(PixelData.SPARK_A, 3, PixelArt.TAVERN_PALETTE);
    private final Image sparkB = PixelArt.sprite(PixelData.SPARK_B, 3, PixelArt.TAVERN_PALETTE);
    private final Image wantedTitle = PixelArt.textImage("WANTED", 1,
            0xFF4A3222, 0xFF4A3222, 7, 0);
    private final Image posterBomb = PixelArt.mono(PixelData.BOMB, 1, 0xFF4A3222);
    private final Image feltPaw = PixelArt.mono(PixelData.PAW, 3, 0xFFD4A056);

    // Master-crafted scale-3 sprites matching the pixel cat's ink-outlined & shaded artistry
    private final Image steinSprite = PixelArt.sprite(PixelData.TAVERN_STEIN, 3, PixelArt.TAVERN_PALETTE);
    private final Image scrollSprite = PixelArt.sprite(PixelData.TAVERN_SCROLL, 3, PixelArt.TAVERN_PALETTE);

    private final Timeline ambientPulse;
    private final Timeline emberPulse;
    private boolean alternateAmbientFrame;
    private boolean alternateEmberFrame;
    private boolean danger;
    private boolean lowDeck;

    TavernBackground() {
        setMouseTransparent(true);
        getChildren().addAll(canvas, ambientCanvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        ambientCanvas.widthProperty().bind(widthProperty());
        ambientCanvas.heightProperty().bind(heightProperty());
        widthProperty().addListener(o -> draw());
        heightProperty().addListener(o -> draw());

        ambientPulse = new Timeline(
                new KeyFrame(Duration.millis(900), e -> {
                    alternateAmbientFrame = !alternateAmbientFrame;
                    drawAmbient();
                })
        );
        ambientPulse.setCycleCount(Animation.INDEFINITE);
        emberPulse = new Timeline(
                new KeyFrame(Duration.millis(650), e -> {
                    alternateEmberFrame = !alternateEmberFrame;
                    drawAmbient();
                })
        );
        emberPulse.setCycleCount(Animation.INDEFINITE);

        sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                ambientPulse.play();
            } else {
                ambientPulse.stop();
            }
            updateEmberPulse();
        });
    }

    void setTension(boolean danger, boolean lowDeck) {
        if (this.danger != danger || this.lowDeck != lowDeck) {
            this.danger = danger;
            this.lowDeck = lowDeck;
            updateEmberPulse();
            draw();
        }
    }

    private void updateEmberPulse() {
        if (getScene() != null && (danger || lowDeck)) {
            emberPulse.play();
        } else {
            emberPulse.stop();
            alternateEmberFrame = false;
        }
    }

    static double feltWidth(double width, boolean compact) {
        return snap2(Math.min(width - (compact ? 176 : 236), 640));
    }

    static double feltLeft(double width, double feltWidth) {
        return snap2((width - feltWidth) * 0.5);
    }

    @Override
    protected double computePrefWidth(double height) {
        return 0;
    }

    @Override
    protected double computePrefHeight(double width) {
        return 0;
    }

    private static double snap2(double v) {
        return Math.floor(v / 2.0) * 2.0;
    }

    private static Color blend(int from, int to, double t) {
        int r = (int) Math.round(((from >> 16) & 0xFF) * (1 - t) + ((to >> 16) & 0xFF) * t);
        int g = (int) Math.round(((from >> 8) & 0xFF) * (1 - t) + ((to >> 8) & 0xFF) * t);
        int b = (int) Math.round((from & 0xFF) * (1 - t) + (to & 0xFF) * t);
        return Color.rgb(r, g, b);
    }

    private void drawDitheredWood(GraphicsContext gc, double x, double y, double w, double h,
                                  int topColor, int bottomColor, int bands) {
        double bandHeight = Math.max(4, Math.ceil(h / bands / 4.0) * 4.0);
        Color previous = null;
        for (int i = 0; i < bands; i++) {
            double by = y + i * bandHeight;
            if (by >= y + h) {
                break;
            }
            double bh = Math.min(bandHeight, y + h - by);
            Color color = blend(topColor, bottomColor, i / (double) Math.max(1, bands - 1));
            gc.setFill(color);
            gc.fillRect(x, by, w, bh + 1);
            if (previous != null && bh >= 4) {
                gc.setFill(previous);
                for (double dx = x; dx < x + w; dx += 4) {
                    gc.fillRect(dx + 2, by, 2, 2);
                }
                gc.setFill(color);
                for (double dx = x; dx < x + w; dx += 4) {
                    gc.fillRect(dx, by - 2, 2, 2);
                }
            }
            previous = color;
        }
    }

    private void draw() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        boolean compact = w < COMPACT_BELOW;
        double wallH = 108;

        // 1. Dark timber shadow recess behind the tabletop corners
        gc.setFill(Color.web(danger ? "#14070A" : "#180E0A"));
        gc.fillRect(0, wallH, w, h - wallH);

        // 2. Upper warm cocoa-oak tavern wall with classic vertical planks & dithered shading
        int wallTop = danger ? 0xFF33141B : 0xFF5A4033;
        int wallBottom = danger ? 0xFF1F0B10 : 0xFF3F2B22;
        drawDitheredWood(gc, 0, 0, w, wallH, wallTop, wallBottom, 7);
        drawPlankGrain(gc, 0, 12, w, wallH - 12, 11, true);
        drawCeilingBeam(gc, w, compact);

        drawWantedPoster(gc, w);
        drawRefinedTable(gc, w, h, wallH, compact);
        drawAmbient();
    }

    private void drawAmbient() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        GraphicsContext gc = ambientCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);
        boolean compact = w < COMPACT_BELOW;

        // Keep animated wall light above the table's upper edge.
        gc.save();
        gc.beginPath();
        gc.rect(0, 0, w, 114);
        gc.clip();
        drawNightWindow(gc, w, compact);
        drawLantern(gc);
        gc.restore();
        drawCenterMedallion(gc, w, compact ? 256 : 272);
    }

    private void drawCeilingBeam(GraphicsContext gc, double w, boolean compact) {
        gc.setFill(Color.web("#23140E"));
        gc.fillRect(0, 0, w, 10);
        gc.setFill(Color.web("#6E4D37"));
        gc.fillRect(0, 8, w, 2);

        double bannerW = compact ? 480 : 540;
        double leftChainX = snap2(w * 0.5 - bannerW * 0.5 + 34);
        double rightChainX = snap2(w * 0.5 + bannerW * 0.5 - 38);
        for (double cx : new double[]{leftChainX, rightChainX}) {
            gc.setFill(Color.web("#140B07"));
            gc.fillRect(cx - 2, 4, 8, 18);
            gc.setFill(Color.web("#9C887B"));
            gc.fillRect(cx, 4, 4, 4);
            gc.fillRect(cx, 12, 4, 4);
            gc.setFill(Color.web("#D4A056"));
            gc.fillRect(cx, 8, 4, 4);
            gc.fillRect(cx, 16, 4, 4);
        }
    }

    private void drawPlankGrain(GraphicsContext gc, double areaX, double areaY,
                                double areaW, double areaH, long seed, boolean withNails) {
        if (areaW <= 0 || areaH <= 0) {
            return;
        }
        Random random = new Random(seed);
        int startPlank = (int) (Math.floor(areaX / PLANK) * PLANK) + PLANK;
        for (int x = startPlank; x < areaX + areaW + PLANK; x += PLANK) {
            double seamX = x;
            if (seamX > areaX + 4 && seamX < areaX + areaW - 4) {
                gc.setFill(SEAM_DARK);
                gc.fillRect(seamX - 2, areaY, 2, areaH);
                gc.setFill(SEAM_LIGHT);
                gc.fillRect(seamX, areaY, 2, areaH);
            }

            double plankLeft = Math.max(areaX + 4, x - PLANK + 4);
            double plankRight = Math.min(areaX + areaW - 4, x - 4);
            double plankSpan = plankRight - plankLeft;
            if (plankSpan <= 16) {
                continue;
            }

            for (int f = 0; f < 3; f++) {
                double fx = snap2(plankLeft + 8 + random.nextDouble() * Math.max(4, plankSpan - 16));
                double fy = snap2(areaY + 10 + random.nextDouble() * Math.max(8, areaH * 0.45));
                double fh = snap2(Math.min(areaH - (fy - areaY) - 8, 28 + random.nextDouble() * 54));
                if (fh > 8) {
                    gc.setFill(f % 2 == 0 ? GRAIN_DARK : GRAIN_LIGHT);
                    gc.fillRect(fx, fy, 2, fh);
                }
            }

            double ky = snap2(areaY + 16 + random.nextDouble() * Math.max(12, areaH - 32));
            double kx = snap2(plankLeft + 10 + random.nextDouble() * Math.max(4, plankSpan * 0.35));
            double kw = snap2(Math.min(plankRight - kx - 6, 26 + random.nextDouble() * 28));
            if (kw >= 14) {
                gc.setFill(GRAIN_DARK);
                gc.fillRect(kx, ky, kw, 2);
                gc.fillRect(kx + 4, ky + 4, Math.max(8, kw - 8), 2);
                gc.setFill(GRAIN_LIGHT);
                gc.fillRect(kx + 2, ky + 2, Math.max(10, kw - 4), 2);
            }

            if (withNails && areaH > 44) {
                double n1 = plankLeft + 10;
                double n2 = plankRight - 14;
                gc.setFill(NAIL_COLOR);
                gc.fillRect(n1, areaY + 10, 4, 4);
                gc.fillRect(n2, areaY + 10, 4, 4);
                gc.fillRect(n1, areaY + areaH - 14, 4, 4);
                gc.fillRect(n2, areaY + areaH - 14, 4, 4);
                gc.setFill(NAIL_SHINE);
                gc.fillRect(n1, areaY + 10, 2, 2);
                gc.fillRect(n2, areaY + 10, 2, 2);
                gc.fillRect(n1, areaY + areaH - 14, 2, 2);
                gc.fillRect(n2, areaY + areaH - 14, 2, 2);
            }
        }
    }

    private void drawWantedPoster(GraphicsContext gc, double w) {
        if (w < 740) {
            return;
        }
        double px = 88;
        double py = 18;
        double pw = 50;
        double ph = 62;

        gc.setFill(Color.web("#0D0705", 0.45));
        gc.fillRect(px + 3, py + 3, pw, ph);
        gc.setFill(Color.web("#DFC59B"));
        gc.fillRect(px, py, pw, ph);
        gc.setFill(Color.web("#F2DFC0"));
        gc.fillRect(px, py, pw - 4, 12);

        gc.setFill(Color.web("#24150E"));
        gc.fillRect(px + pw - 6, py + ph - 6, 6, 6);
        gc.setFill(Color.web("#B39264"));
        gc.fillRect(px + pw - 6, py + ph - 6, 4, 4);

        gc.setFill(Color.web("#D93848"));
        gc.fillRect(px + pw / 2 - 2, py - 2, 4, 4);

        gc.drawImage(wantedTitle, px + (pw - wantedTitle.getWidth()) / 2, py + 7);
        gc.setFill(Color.web("#B8986E"));
        gc.fillRect(px + 10, py + 16, pw - 20, 24);
        gc.drawImage(posterBomb, px + (pw - posterBomb.getWidth()) / 2, py + 17);

        gc.setFill(Color.web("#5C3D2E"));
        gc.fillRect(px + 10, py + 45, pw - 20, 3);
        gc.fillRect(px + 14, py + 53, pw - 28, 3);
    }

    private void drawNightWindow(GraphicsContext gc, double w, boolean compact) {
        if (w < 700) {
            return;
        }
        double winW = compact ? 124 : 144;
        double winH = 82;
        double winX = snap2(w - winW - 20);
        double winY = 16;

        if (!danger) {
            gc.setFill(MOONLIGHT);
            for (int i = 0; i < 4; i++) {
                double r = 74 + i * 20;
                gc.fillOval(winX + winW * 0.45 - r, winY + winH * 0.55 - r, r * 2, r * 2);
            }
        }

        gc.setFill(WINDOW_FRAME);
        gc.fillRect(winX, winY, winW, winH);
        gc.setFill(WINDOW_TRIM);
        gc.fillRect(winX + 4, winY + 4, winW - 8, 4);

        double glassX = winX + 8;
        double glassY = winY + 8;
        double glassW = winW - 16;
        double glassH = winH - 20;

        gc.setFill(danger ? Color.web("#1A0810") : SKY_TOP);
        gc.fillRect(glassX, glassY, glassW, glassH * 0.55);
        gc.setFill(danger ? Color.web("#36101D") : SKY_BOTTOM);
        gc.fillRect(glassX, glassY + snap2(glassH * 0.55), glassW, glassH - snap2(glassH * 0.55));

        gc.drawImage(moonSprite, glassX + 8, glassY + 8);
        gc.setFill(!alternateAmbientFrame ? Color.web("#FFF1D6") : Color.web("#B9A8FF", 0.75));
        gc.fillRect(glassX + 50, glassY + 14, 4, 4);
        gc.setFill(alternateAmbientFrame ? Color.web("#FFB84D") : Color.web("#FFF1D6", 0.7));
        gc.fillRect(glassX + 18, glassY + 40, 4, 4);
        if (glassW > 96) {
            gc.fillRect(glassX + 82, glassY + 18, 4, 4);
        }

        gc.setFill(WINDOW_FRAME);
        gc.fillRect(glassX + snap2(glassW * 0.48), glassY, 8, glassH);
        gc.fillRect(glassX, glassY + snap2(glassH * 0.48), glassW, 8);

        gc.setFill(WINDOW_TRIM);
        gc.fillRect(winX - 4, winY + winH - 12, winW + 8, 4);
        gc.setFill(WINDOW_FRAME);
        gc.fillRect(winX - 4, winY + winH - 8, winW + 8, 8);
    }

    private void drawLantern(GraphicsContext gc) {
        int flameFrame = alternateAmbientFrame ? 1 : 0;
        double cx = 48;
        double flameY = 12 + 7 * LANTERN_SCALE;
        gc.setFill(danger
                ? Color.web("#FF4D63", flameFrame == 0 ? 0.055 : 0.04)
                : Color.web("#FFB84D", flameFrame == 0 ? 0.045 : 0.034));
        for (int i = 0; i < 8; i++) {
            double r = 28 + i * 14;
            gc.fillOval(cx - r, flameY - r, r * 2, r * 2);
        }
        Image lantern = flameFrame == 0 ? lanternA : lanternB;
        gc.drawImage(lantern, snap2(cx - lantern.getWidth() / 2), 12);
    }

    private void drawRefinedTable(GraphicsContext gc, double w, double h, double wallH, boolean compact) {
        double topInset = compact ? 22 : 34;
        double tableTopY = wallH + 6;
        double tableBottomY = h - (compact ? 64 : 72);
        double tableH = Math.max(140, tableBottomY - tableTopY);

        double[] polyX = {topInset, w - topInset, w, 0};
        double[] polyY = {tableTopY, tableTopY, tableBottomY, tableBottomY};

        gc.setFill(Color.web("#140B07"));
        gc.fillPolygon(
                new double[]{topInset - 4, w - topInset + 4, w + 4, -4},
                new double[]{tableTopY - 3, tableTopY - 3, tableBottomY + 4, tableBottomY + 4},
                4
        );

        gc.save();
        gc.beginPath();
        gc.moveTo(polyX[0], polyY[0]);
        gc.lineTo(polyX[1], polyY[1]);
        gc.lineTo(polyX[2], polyY[2]);
        gc.lineTo(polyX[3], polyY[3]);
        gc.closePath();
        gc.clip();

        int woodTop = danger ? 0xFF3E1820 : 0xFF664838;
        int woodBottom = danger ? 0xFF240C11 : 0xFF422C21;
        drawDitheredWood(gc, 0, tableTopY, w, tableH, woodTop, woodBottom, 12);

        // Warm overhead tavern light pool
        gc.setFill(danger ? Color.web("#FF4D63", 0.04) : Color.web("#FFC27A", 0.042));
        for (int i = 0; i < 6; i++) {
            double k = 1.0 - i * 0.14;
            double rx = w * 0.48 * k;
            double ry = tableH * 0.56 * k;
            gc.fillOval(w * 0.5 - rx,
                    tableTopY + tableH * 0.46 - ry, rx * 2, ry * 2);
        }

        drawPlankGrain(gc, 0, tableTopY, w, tableH, 29, true);

        gc.setStroke(Color.web("#8F6E58", 0.65));
        gc.setLineWidth(2);
        gc.strokePolygon(
                new double[]{topInset + 8, w - topInset - 8, w - 10, 10},
                new double[]{tableTopY + 6, tableTopY + 6, tableBottomY - 6, tableBottomY - 6},
                4
        );
        gc.restore();

        gc.setStroke(danger ? Color.web("#A8525E") : Color.web("#B38867"));
        gc.setLineWidth(2);
        gc.strokeLine(topInset, tableTopY, w - topInset, tableTopY);
        gc.strokeLine(topInset, tableTopY, 0, tableBottomY);
        gc.strokeLine(w - topInset, tableTopY, w, tableBottomY);

        gc.setFill(Color.web("#9C7456"));
        gc.fillRect(0, tableBottomY, w, 3);
        gc.setFill(Color.web("#5A3E2E"));
        gc.fillRect(0, tableBottomY + 3, w, 7);
        gc.setFill(Color.web("#140B07"));
        gc.fillRect(0, tableBottomY + 10, w, 4);

        gc.setFill(Color.web("#D4A056"));
        gc.fillRect(topInset + 2, tableTopY + 1, 14, 4);
        gc.fillRect(w - topInset - 16, tableTopY + 1, 14, 4);

        // Recessed Rounded Burgundy Velvet Card Arena
        double feltW = feltWidth(w, compact);
        double feltH = snap2(Math.min(tableH - 54, compact ? 276 : 310));
        double feltX = feltLeft(w, feltW);
        double feltY = snap2(tableTopY + 28);

        gc.setFill(Color.web("#1A0E09", 0.72));
        gc.fillRoundRect(feltX - 3, feltY - 3, feltW + 6, feltH + 6, 20, 20);
        gc.setStroke(Color.web("#B38867", 0.55));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(feltX - 3, feltY - 3, feltW + 6, feltH + 6, 20, 20);

        gc.setFill(danger ? Color.web("#36101B") : Color.web("#3B1822"));
        gc.fillRoundRect(feltX, feltY, feltW, feltH, 16, 16);

        gc.setFill(danger ? Color.web("#FF4D63", 0.045) : Color.web("#FFB84D", 0.035));
        for (int i = 0; i < 4; i++) {
            double rx = feltW * (0.42 - i * 0.08);
            double ry = feltH * (0.44 - i * 0.08);
            gc.fillOval(w * 0.5 - rx,
                    feltY + feltH * 0.48 - ry, rx * 2, ry * 2);
        }

        gc.setStroke(Color.web("#D4A056", 0.58));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(feltX + 10, feltY + 10, feltW - 20, feltH - 20, 12, 12);
        gc.setStroke(Color.web("#D4A056", 0.25));
        gc.setLineWidth(1.0);
        gc.strokeRoundRect(feltX + 15, feltY + 15, feltW - 30, feltH - 30, 9, 9);

        // Carved Walnut & Brass Card Cradles under Draw & Discard piles
        double centerX = w * 0.5;
        double propGap = compact ? 52 : 66;
        double deckBoxW = (compact ? 92 : 104) + 16;
        double deckBoxH = (compact ? 128 : 144) + 16;
        double deckX = Math.round(centerX - propGap - deckBoxW);
        double deckY = compact ? 182 : 192;
        drawCardCradle(gc, deckX - 4, deckY - 4, deckBoxW + 8, deckBoxH + 8);

        double discardBoxW = compact ? 122 : 138;
        double discardBoxH = compact ? 142 : 158;
        double discardX = Math.round(centerX + propGap);
        double discardY = compact ? 176 : 186;
        drawCardCradle(gc, discardX + 4, discardY + 4, discardBoxW - 8, discardBoxH - 8);

        // Master-crafted scale-3 pixel-art tavern props on the oak side wings
        drawCraftedSideProps(gc, w, tableTopY, topInset, compact);
    }

    private void drawCardCradle(GraphicsContext gc, double x, double y, double w, double h) {
        gc.setFill(Color.web("#160B08", 0.55));
        gc.fillRoundRect(x, y, w, h, 12, 12);
        gc.setStroke(Color.web("#D4A056", 0.42));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(x, y, w, h, 12, 12);

        gc.setFill(Color.web("#E0AA58", 0.75));
        gc.fillRect(x + 6, y + 6, 6, 2);
        gc.fillRect(x + 6, y + 6, 2, 6);
        gc.fillRect(x + w - 12, y + 6, 6, 2);
        gc.fillRect(x + w - 8, y + 6, 2, 6);
        gc.fillRect(x + 6, y + h - 8, 6, 2);
        gc.fillRect(x + 6, y + h - 12, 2, 6);
        gc.fillRect(x + w - 12, y + h - 8, 6, 2);
        gc.fillRect(x + w - 8, y + h - 12, 2, 6);
    }

    private void drawCenterMedallion(GraphicsContext gc, double w, double cy) {
        double cx = snap2(w * 0.5);

        gc.setStroke(Color.web("#D4A056", 0.32));
        gc.setLineWidth(1.5);
        gc.strokeOval(cx - 34, cy - 34, 68, 68);
        gc.setStroke(Color.web("#D4A056", 0.18));
        gc.strokeOval(cx - 28, cy - 28, 56, 56);

        gc.setFill(Color.web("#D4A056", 0.45));
        gc.fillRect(cx - 1, cy - 40, 2, 6);
        gc.fillRect(cx - 1, cy + 34, 2, 6);
        gc.fillRect(cx - 40, cy - 1, 6, 2);
        gc.fillRect(cx + 34, cy - 1, 6, 2);

        if (danger || lowDeck) {
            double bx = snap2(cx - centerBomb.getWidth() * 0.5);
            double by = snap2(cy - centerBomb.getHeight() * 0.5);
            gc.setFill(Color.web("#120A07", 0.6));
            gc.fillOval(bx + 4, by + centerBomb.getHeight() - 8, centerBomb.getWidth() - 8, 8);
            gc.drawImage(centerBomb, bx, by);
            Image spark = alternateEmberFrame ? sparkB : sparkA;
            gc.drawImage(spark, bx + 18, by - 10);
        } else {
            gc.setGlobalAlpha(0.32);
            gc.drawImage(feltPaw, snap2(cx - feltPaw.getWidth() * 0.5), snap2(cy - feltPaw.getHeight() * 0.5));
            gc.setGlobalAlpha(1.0);
        }
    }

    /** Draws high-craft scale-3 pixel-art sprites matching the pixel cat's exact fidelity. */
    private void drawCraftedSideProps(GraphicsContext gc, double w, double tableTopY,
                                      double topInset, boolean compact) {
        if (w < 800) {
            return;
        }
        // Left oak wing (lower-mid): brass-banded oak milk-punch stein with drop shadow
        double steinX = snap2(topInset + (compact ? 14 : 24));
        double steinY = snap2(tableTopY + 126);
        gc.setFill(Color.web("#140B07", 0.52));
        gc.fillOval(steinX + 3, steinY + steinSprite.getHeight() - 9,
                steinSprite.getWidth() - 10, 12);
        gc.drawImage(steinSprite, steinX, steinY);

        // Right oak wing: dagger-pinned wax-seal parchment scroll with drop shadow
        double scrollX = snap2(w - topInset - scrollSprite.getWidth() - (compact ? 12 : 22));
        double scrollY = snap2(tableTopY + 42);
        gc.setFill(Color.web("#140B07", 0.48));
        gc.fillOval(scrollX + 4, scrollY + scrollSprite.getHeight() - 8,
                scrollSprite.getWidth() - 12, 10);
        gc.drawImage(scrollSprite, scrollX, scrollY);
    }
}
