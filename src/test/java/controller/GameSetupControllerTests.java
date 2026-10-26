package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameSetupControllerTests {

    @Test
    public void StartGame_WithPlayerCountBelowMinimum_ThrowsIllegalArgumentException() {
        GameSetupController controller = new GameSetupController();

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.startGame("1")
        );
    }
}
