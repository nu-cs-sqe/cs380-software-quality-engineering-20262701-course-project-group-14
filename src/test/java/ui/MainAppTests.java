package ui;

import domain.GameState;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainAppTests {

    @Test
    public void StartGame_WithValidCount_OpensGameplayWithInitializedGame() {
        List<GameState> openedGames = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        MainApp.startGame("2", openedGames::add, errors::add);

        assertEquals(1, openedGames.size());
        GameState game = openedGames.get(0);
        assertEquals(2, game.getPlayers().size());
        assertEquals("Player 1", game.getCurrentPlayer().getName());
        assertEquals(8, game.getCurrentPlayer().getHand().size());
        assertEquals(List.of(), errors);
    }

    @Test
    public void CheckPlayerCount_WithThree_ReportsCountForNameStep() {
        List<Integer> counts = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        MainApp.checkPlayerCount("3", counts::add, errors::add);

        assertEquals(List.of(3), counts);
        assertEquals(List.of(), errors);
    }

    @Test
    public void CheckPlayerCount_WithSix_ReportsErrorWithoutCount() {
        List<Integer> counts = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        MainApp.checkPlayerCount("6", counts::add, errors::add);

        assertEquals(List.of(), counts);
        assertEquals(List.of("Player count must be a whole number between 2 and 5."), errors);
    }

    @Test
    public void StartNamedGame_WithTwoNames_OpensGameplayWithNamedPlayers() {
        List<GameState> openedGames = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        MainApp.startNamedGame(List.of("Ada", "Bo"), openedGames::add, errors::add);

        assertEquals(1, openedGames.size());
        assertEquals("Ada", openedGames.get(0).getCurrentPlayer().getName());
        assertEquals(List.of(), errors);
    }

    @Test
    public void StartNamedGame_WithDuplicateNames_ReportsErrorWithoutOpeningGameplay() {
        List<GameState> openedGames = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        MainApp.startNamedGame(List.of("Ada", "ada"), openedGames::add, errors::add);

        assertEquals(List.of(), openedGames);
        assertEquals(List.of("Two players cannot share a name."), errors);
    }

    @Test
    public void StartGame_WithCountBelowMinimum_ReportsErrorWithoutOpeningGameplay() {
        List<GameState> openedGames = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        MainApp.startGame("1", openedGames::add, errors::add);

        assertEquals(List.of(), openedGames);
        assertEquals(List.of("Player count must be a whole number between 2 and 5."), errors);
    }
}
