package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTests {

    // PL-01
    @Test
    public void Create_WithNonEmptyName_HasNameAndEmptyHand() {
        Player player = new Player("Player 1");

        assertEquals("Player 1", player.getName());
        assertTrue(player.getHand().isEmpty());
    }

    // PL-02
    @Test
    public void Create_WithEmptyName_ThrowsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Player("")
        );
    }

    // PL-03
    @Test
    public void Create_WithBlankName_ThrowsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Player("   ")
        );
    }

    // PL-04
    @Test
    public void Create_WithNullName_ThrowsNullPointerException() {
        assertThrows(
                NullPointerException.class,
                () -> new Player(null)
        );
    }

    // PL-05
    @Test
    public void AddCard_ToEmptyHand_HandContainsThatCard() {
        Player player = new Player("Player 1");
        Card skip = new Card(CardType.SKIP);

        player.addCard(skip);

        assertEquals(List.of(skip), player.getHand());
    }

    // PL-06
    @Test
    public void AddCard_ToHandWithOneCard_AppendsCardAtEnd() {
        Player player = new Player("Player 1");
        Card tacocat = new Card(CardType.TACOCAT);
        Card attack = new Card(CardType.ATTACK);
        player.addCard(tacocat);

        player.addCard(attack);

        assertEquals(List.of(tacocat, attack), player.getHand());
    }

}