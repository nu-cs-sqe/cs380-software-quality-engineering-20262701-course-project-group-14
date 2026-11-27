package ui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

/** Starts the JavaFX toolkit once per test JVM; it cannot be restarted after it exits. */
final class JavaFxToolkit {
    private static boolean started;

    private JavaFxToolkit() {
    }

    static synchronized void start() throws Exception {
        if (started) {
            return;
        }
        CompletableFuture<Void> ready = new CompletableFuture<>();
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            ready.complete(null);
        });
        ready.get(10, TimeUnit.SECONDS);
        started = true;
    }

    static void run(Runnable exercise) throws Exception {
        CompletableFuture<Void> finished = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                exercise.run();
                finished.complete(null);
            } catch (Throwable failure) {
                finished.completeExceptionally(failure);
            }
        });
        finished.get(20, TimeUnit.SECONDS);
    }
}
