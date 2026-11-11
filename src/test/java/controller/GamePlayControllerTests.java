package controller;

import domain.CardType;
import domain.GameSnapshot;
import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GamePlayControllerTests {

    @Test
    public void Constructor_WithNullActions_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new GamePlayController(null, new RecordingView())
        );

        assertEquals(
                "Turn actions must not be null.",
                exception.getMessage()
        );
    }

    private static final class RecordingView implements GamePlayViewPort {
        @Override
        public void render(GameSnapshot snapshot) {
        }

        @Override
        public void showMessage(String messageKey) {
        }

        @Override
        public void showFutureCards(List<CardType> cards) {
        }

        @Override
        public OptionalInt requestDefusePosition(
                int minimum,
                int maximum
        ) {
            return OptionalInt.empty();
        }
    }
}
