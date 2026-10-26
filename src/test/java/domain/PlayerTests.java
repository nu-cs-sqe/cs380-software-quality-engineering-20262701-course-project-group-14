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

    // PL-07
    @Test
    public void AddCard_ToHandWithSevenCardsIncludingDuplicates_KeepsAllCards() {
        Player player = new Player("Player 1");
        Card tacocat1 = new Card(CardType.TACOCAT);
        Card tacocat2 = new Card(CardType.TACOCAT);
        Card beardCat1 = new Card(CardType.BEARD_CAT);
        Card beardCat2 = new Card(CardType.BEARD_CAT);
        Card cattermelon = new Card(CardType.CATTERMELON);
        Card tacocat3 = new Card(CardType.TACOCAT);
        Card defuse1 = new Card(CardType.DEFUSE);
        Card defuse2 = new Card(CardType.DEFUSE);
        player.addCard(tacocat1);
        player.addCard(tacocat2);
        player.addCard(beardCat1);
        player.addCard(beardCat2);
        player.addCard(cattermelon);
        player.addCard(tacocat3);
        player.addCard(defuse1);

        player.addCard(defuse2);

        assertEquals(
                List.of(tacocat1, tacocat2, beardCat1, beardCat2,
                        cattermelon, tacocat3, defuse1, defuse2),
                player.getHand());
    }

    // PL-08
    @Test
    public void AddCard_WithNullCard_ThrowsNullPointerExceptionAndHandStaysEmpty() {
        Player player = new Player("Player 1");

        assertThrows(NullPointerException.class, () -> player.addCard(null));
        assertTrue(player.getHand().isEmpty());
    }

}