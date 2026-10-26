package controller;

import domain.GameState;

public class GameSetupController {

    public GameState startGame(String playerCountText) {
        int playerCount = Integer.parseInt(playerCountText);

        if (playerCount < 2) {
            throw new IllegalArgumentException(
                    "Player count must be between 2 and 5."
            );
        }

        throw new UnsupportedOperationException(
                "Valid game setup is not implemented yet."
        );
    }
}
