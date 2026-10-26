package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Decorative pixel-art night sky. Everything is painted onto a single canvas
 * about 20 times per second, with all movement snapped to the pixel grid so the
 * animation feels like an old console game. It never takes mouse input and
 * contains no controls.
 */
final class BackgroundLayer extends Pane {

    private static final int UNIT = 4;
    private static final int BAND_H = 44;
    private static final int GROUND_H = 52;
    private static final long FRAME_NANOS = 50_000_000L;

    private static final Color BAND_BG = Color.web("#0B0819");
    private static final Color BAND_LINE = Color.web("#5B47B8");
    private static final Color GROUND_TOP = Color.web("#7B68E0");
    private static final Color GROUND_BODY = Color.web("#2B2262");
    private static final Color GROUND_LINE = Color.web("#221A52");

    private static final String[] STAR_BASE = {"#FFF1D6", "#B9A8FF", "#FFB84D"};
    private static final double[] STAR_ALPHA = {0.25, 0.6, 1.0};
    private static final Color[][] STAR_COLORS = new Color[3][3];

    static {
        for (int k = 0; k < 3; k++) {
            for (int a = 0; a < 3; a++) {
                STAR_COLORS[k][a] = Color.web(STAR_BASE[k], STAR_ALPHA[a]);
            }
        }
    }

    private static final class Star {
        double fx;
        double fy;
        double phase;
        double period;
        int kind;
        boolean plus;
    }

    private static final class Cloud {
        Image image;
        double fy;
        double speed;
        double offset;
    }

    private static final class Sticker {
        Image image;
        Image blink;
        double fx;
        double fy;
        double phase;
        double period;
        double appearAt;
        int amp;
        Image[] sparkFrames;
        int sparkUx;
        int sparkUy;
        int sparkScale;
    }

    private final Canvas canvas = new Canvas();
    private final AnimationTimer timer;
    private final List<Star> stars = new ArrayList<>();
    private final List<Cloud> clouds = new ArrayList<>();
    private final List<Sticker> stickers = new ArrayList<>();
    private final Random random = new Random(11);
    private final Image walkA = PixelArt.sprite(PixelData.WALK_A, 4);
    private final Image walkB = PixelArt.sprite(PixelData.WALK_B, 4);
    private final Image marqueeUnit = createMarqueeUnit();
    private long startNanos = -1;
    private long lastDrawNanos;
    private int stickerCount;

    BackgroundLayer() {
        setMouseTransparent(true);
        getChildren().add(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        createStars();
        createClouds();
        createStickers();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (startNanos < 0) {
                    startNanos = now;
                }
                if (now - lastDrawNanos < FRAME_NANOS) {
                    return;
                }
                lastDrawNanos = now;
                draw((now - startNanos) / 1_000_000_000.0);
            }
        };

        sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                timer.start();
            } else {
                timer.stop();
            }
        });
    }

    // The layer must never influence the size of its parent.
    @Override
    protected double computePrefWidth(double height) {
        return 0;
    }

    @Override
    protected double computePrefHeight(double width) {
        return 0;
    }

    // ------------------------------------------------------------------
    // content
    // ------------------------------------------------------------------

    private void createStars() {
        for (int i = 0; i < 44; i++) {
            Star s = new Star();
            s.fx = 0.02 + random.nextDouble() * 0.96;
            s.fy = 0.09 + random.nextDouble() * 0.78;
            s.phase = random.nextDouble();
            s.period = 1.8 + random.nextDouble() * 2.6;
            s.kind = random.nextInt(3);
            s.plus = i % 4 == 0;
            stars.add(s);
        }
    }

    private void createClouds() {
        addCloud(PixelData.CLOUD_A, 6, 0.19, 12, 0);
        addCloud(PixelData.CLOUD_B, 8, 0.50, 8, 300);
        addCloud(PixelData.CLOUD_A, 5, 0.70, 16, 700);
    }

    private void addCloud(String[] rows, int scale, double fy, double speed, double offset) {
        Cloud c = new Cloud();
        c.image = PixelArt.sprite(rows, scale);
        c.fy = fy;
        c.speed = speed;
        c.offset = offset;
        clouds.add(c);
    }

    private void createStickers() {
        Image headOpen = PixelArt.sprite(PixelData.HEAD_OPEN, 3);
        Image headBlink = PixelArt.sprite(PixelData.HEAD_BLINK, 3);
        Image nightOpen = PixelArt.sprite(PixelData.HEAD_OPEN, 3, PixelArt.NIGHT_PALETTE);
        Image nightBlink = PixelArt.sprite(PixelData.HEAD_BLINK, 3, PixelArt.NIGHT_PALETTE);
        Image bomb = PixelArt.sprite(PixelData.BOMB, 3);
        Image[] sparks = {
                PixelArt.sprite(PixelData.SPARK_A, 3),
                PixelArt.sprite(PixelData.SPARK_B, 3)
        };
        Image paw = PixelArt.sprite(PixelData.PAW, 3);
        Image pawAmber = PixelArt.sprite(
                PixelData.PAW, 3, PixelArt.withColor(PixelArt.DEFAULT_PALETTE, 'g', 0xFFFFB84D));
        Image heart = PixelArt.sprite(PixelData.HEART, 4);
        Image fish = PixelArt.sprite(PixelData.FISH, 3);
        Image plus = PixelArt.sprite(PixelData.STAR_PLUS, 3);
        Image moon = PixelArt.sprite(PixelData.MOON, 5);

        // left side
        addSticker(headOpen, headBlink, 0.09, 0.26, 2, 3.2, null);
        addSticker(heart, null, 0.18, 0.14, 1, 2.2, null);
        addSticker(paw, null, 0.07, 0.50, 1, 3.0, null);
        addSticker(plus, null, 0.19, 0.50, 1, 2.0, null);
        addSticker(bomb, null, 0.14, 0.69, 2, 3.6, sparks);
        addSticker(fish, null, 0.09, 0.84, 1, 2.8, null);

        // right side
        addSticker(moon, null, 0.86, 0.14, 1, 4.0, null);
        addSticker(bomb, null, 0.93, 0.32, 2, 3.4, sparks);
        addSticker(nightOpen, nightBlink, 0.89, 0.58, 2, 3.0, null);
        addSticker(pawAmber, null, 0.80, 0.45, 1, 2.6, null);
        addSticker(heart, null, 0.82, 0.74, 1, 2.4, null);
        addSticker(fish, null, 0.91, 0.84, 1, 3.1, null);
        addSticker(plus, null, 0.80, 0.26, 1, 2.3, null);
    }

    private void addSticker(Image image, Image blink, double fx, double fy,
                            int amp, double period, Image[] sparks) {
        Sticker s = new Sticker();
        s.image = image;
        s.blink = blink;
        s.fx = fx;
        s.fy = fy;
        s.amp = amp;
        s.period = period;
        s.phase = random.nextDouble();
        s.appearAt = 0.3 + 0.08 * stickerCount++;
        if (sparks != null) {
            s.sparkFrames = sparks;
            s.sparkUx = 10;
            s.sparkUy = 1;
            s.sparkScale = 3;
        }
        stickers.add(s);
    }

    private static Image createMarqueeUnit() {
        String[] words = {"EXPLODING KITTENS", "MEOW", "DEFUSE", "BOOM", "NOPE"};
        int[] starColors = {PixelArt.AMBER, PixelArt.PINK, PixelArt.LILAC, PixelArt.AMBER, PixelArt.PINK};
        int scale = 2;
        int gap = 16;
        int starSize = 5 * scale;

        int width = 0;
        for (String w : words) {
            width += PixelArt.textWidth(w, scale) + gap + starSize + gap;
        }
        PixelArt.Buffer b = new PixelArt.Buffer(width, 7 * scale);
        int x = 0;
        for (int i = 0; i < words.length; i++) {
            b.text(words[i], x, 0, scale, PixelArt.CREAM, PixelArt.CREAM, 7);
            x += PixelArt.textWidth(words[i], scale) + gap;
            java.util.Map<Character, Integer> pal = new java.util.HashMap<>();
            pal.put('y', starColors[i]);
            pal.put('w', 0xFFFFFFFF);
            b.sprite(PixelData.STAR_PLUS, pal, x, 2, scale);
            x += starSize + gap;
        }
        return b.toImage();
    }

    // ------------------------------------------------------------------
    // drawing
    // ------------------------------------------------------------------

    private static double snap(double v) {
        return Math.round(v / UNIT) * UNIT;
    }

    /** Remaining slide-in distance, in whole pixel steps. */
    private static double stepIn(double t, double start, double duration, double distance) {
        double p = Math.min(1, Math.max(0, (t - start) / duration));
        return Math.floor(distance * (1 - p) / UNIT) * UNIT;
    }

    private void draw(double t) {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        drawStars(gc, w, h, t);
        drawClouds(gc, w, h, t);
        drawStickers(gc, w, h, t);
        drawGround(gc, w, h, t);
        drawTopBand(gc, w, t);
    }

    private void drawStars(GraphicsContext gc, double w, double h, double t) {
        for (Star s : stars) {
            int level = Math.floorMod((int) Math.floor((t / s.period + s.phase) * 3), 3);
            gc.setFill(STAR_COLORS[s.kind][level]);
            double x = snap(w * s.fx);
            double y = snap(h * s.fy);
            if (s.plus) {
                gc.fillRect(x + UNIT, y, UNIT, UNIT);
                gc.fillRect(x, y + UNIT, UNIT, UNIT);
                gc.fillRect(x + UNIT, y + UNIT, UNIT, UNIT);
                gc.fillRect(x + 2 * UNIT, y + UNIT, UNIT, UNIT);
                gc.fillRect(x + UNIT, y + 2 * UNIT, UNIT, UNIT);
            } else {
                gc.fillRect(x, y, UNIT, UNIT);
            }
        }
    }

    private void drawClouds(GraphicsContext gc, double w, double h, double t) {
        for (Cloud c : clouds) {
            double span = w + c.image.getWidth();
            double pos = (t * c.speed + c.offset) % span;
            gc.drawImage(c.image, snap(w - pos), snap(h * c.fy));
        }
    }

    private void drawStickers(GraphicsContext gc, double w, double h, double t) {
        for (Sticker s : stickers) {
            double elapsed = t - s.appearAt;
            if (elapsed < 0) {
                continue;
            }
            if (elapsed < 0.3 && ((int) (elapsed / 0.075)) % 2 == 0) {
                continue; // flicker in
            }
            double bob = Math.round(Math.sin(2 * Math.PI * (t / s.period + s.phase)) * s.amp) * UNIT;
            double x = snap(w * s.fx - s.image.getWidth() / 2);
            double y = snap(h * s.fy - s.image.getHeight() / 2) + bob;

            Image image = s.image;
            if (s.blink != null && ((t + s.phase * 4) % 4.0) < 0.15) {
                image = s.blink;
            }
            gc.drawImage(image, x, y);

            if (s.sparkFrames != null) {
                Image spark = s.sparkFrames[((int) (t * 8)) % 2];
                double sx = x + s.sparkUx * s.sparkScale - Math.floor(spark.getWidth() / 2);
                double sy = y + s.sparkUy * s.sparkScale - Math.floor(spark.getHeight() / 2);
                gc.drawImage(spark, sx, sy);
            }
        }
    }

    private void drawGround(GraphicsContext gc, double w, double h, double t) {
        double gy = h - GROUND_H + stepIn(t, 0.15, 0.5, GROUND_H);

        gc.setFill(GROUND_TOP);
        gc.fillRect(0, gy, w, UNIT);
        gc.setFill(GROUND_BODY);
        gc.fillRect(0, gy + UNIT, w, GROUND_H - UNIT);

        gc.setFill(GROUND_LINE);
        for (int row = 0; row < 4; row++) {
            double y0 = gy + UNIT + row * 12;
            gc.fillRect(0, y0 + 10, w, 2);
            double offset = (row % 2 == 0) ? 0 : 16;
            for (double x = offset; x < w; x += 32) {
                gc.fillRect(x, y0, 2, 10);
            }
        }

        // a little cat strolling along the ground
        double walkT = Math.max(0, t - 1.0);
        Image cat = (((int) (t * 6)) % 2 == 0) ? walkA : walkB;
        double span = w + cat.getWidth();
        double x = snap((walkT * 72) % span - cat.getWidth());
        gc.drawImage(cat, x, gy + UNIT - cat.getHeight());
    }

    private void drawTopBand(GraphicsContext gc, double w, double t) {
        double by = -stepIn(t, 0.15, 0.5, BAND_H);

        gc.setFill(BAND_BG);
        gc.fillRect(0, by, w, BAND_H);
        gc.setFill(BAND_LINE);
        gc.fillRect(0, by + BAND_H - UNIT, w, UNIT);

        double unitWidth = marqueeUnit.getWidth();
        double scroll = Math.floor(t * 48 / UNIT) * UNIT;
        double x0 = -(scroll % unitWidth);
        double ty = by + 12;
        for (double x = x0; x < w; x += unitWidth) {
            gc.drawImage(marqueeUnit, x, ty);
        }
    }
}
