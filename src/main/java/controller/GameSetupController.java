package controller;

import domain.*;

import java.util.ArrayList;
import java.util.List;

public class GameSetupController {
    private static final int MINIMUM_PLAYER_COUNT = 2;
    private static final int MAXIMUM_PLAYER_COUNT = 5;
    private static final int TOTAL_EXPLODING_KITTENS = 4;
    private static final int TOTAL_DEFUSES = 6;
    private static final int EXTRA_DEFUSES_FOR_SMALL_GAME = 2;
    private static final String PLAYER_COUNT_ERROR_MESSAGE =
            "Player count must be a whole number between 2 and 5.";

    public GameState startGame(String playerCountText) {
        int playerCount = parsePlayerCount(playerCountText);
        if (playerCount < MINIMUM_PLAYER_COUNT
                || playerCount > MAXIMUM_PLAYER_COUNT) {
            throw new IllegalArgumentException(
                    PLAYER_COUNT_ERROR_MESSAGE
            );
        }

        Deck deck = new Deck();

        List<Card> explodingKittens = deck.removeCards(
                CardType.EXPLODING_KITTEN,
                TOTAL_EXPLODING_KITTENS
        );
        List<Card> defuses = deck.removeCards(
                CardType.DEFUSE,
                TOTAL_DEFUSES
        );

        List<Player> players = createPlayers(playerCount);

        List<Card> playerDefuses = new ArrayList<>(
                defuses.subList(0, playerCount)
        );
        int extraDefuseCount = calculateExtraDefuseCount(playerCount);

        List<Card> extraDefuses = new ArrayList<>(
                defuses.subList(
                        playerCount,
                        playerCount + extraDefuseCount
                )
        );

        deck.addCards(extraDefuses);
        deck.shuffle();

        StartingHandDealer dealer = new StartingHandDealer();
        dealer.deal(players, deck, playerDefuses);

        List<Card> kittensForGame = new ArrayList<>(
                explodingKittens.subList(0, playerCount - 1)
        );
        deck.addCards(kittensForGame);
        deck.shuffle();

        return new GameState(players, deck);
    }
    private int parsePlayerCount(String playerCountText) {
        if (playerCountText == null) {
            throw new IllegalArgumentException(
                    PLAYER_COUNT_ERROR_MESSAGE
            );
        }

        try {
            return Integer.parseInt(playerCountText.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    PLAYER_COUNT_ERROR_MESSAGE,
                    exception
            );
        }
    }
    private int calculateExtraDefuseCount(int playerCount) {
        if (playerCount <= 3) {
            return EXTRA_DEFUSES_FOR_SMALL_GAME;
        }

        return TOTAL_DEFUSES - playerCount;
    }

    private List<Player> createPlayers(int playerCount) {
        List<Player> players = new ArrayList<>();

        for (int index = 0; index < playerCount; index++) {
            players.add(new Player("Player " + (index + 1)));
        }

        return players;
    }
}
