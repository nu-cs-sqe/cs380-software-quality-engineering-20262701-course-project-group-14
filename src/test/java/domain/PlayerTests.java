package domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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

    // PL-14
    @Test
    public void HasCard_EmptyHand_ReturnsFalse() {
        Player player = new Player("Player 1");

        assertFalse(player.hasCard(CardType.DEFUSE));
    }

    // PL-15
    @Test
    public void HasCard_HandContainsOneCardOfType_ReturnsTrue() {
        Player player = new Player("Player 1");
        player.addCard(new Card(CardType.SKIP));
        player.addCard(new Card(CardType.DEFUSE));

        assertTrue(player.hasCard(CardType.DEFUSE));
    }

    // PL-16
    @Test
    public void HasCard_HandContainsTwoCardsOfType_ReturnsTrue() {
        Player player = new Player("Player 1");
        player.addCard(new Card(CardType.DEFUSE));
        player.addCard(new Card(CardType.SKIP));
        player.addCard(new Card(CardType.DEFUSE));

        assertTrue(player.hasCard(CardType.DEFUSE));
    }

    // PL-17
    @Test
    public void HasCard_HandWithoutType_ReturnsFalse() {
        Player player = new Player("Player 1");
        player.addCard(new Card(CardType.SKIP));

        assertFalse(player.hasCard(CardType.DEFUSE));
    }

    // PL-18
    @Test
    public void HasCard_NullType_ThrowsNullPointerException() {
        Player player = new Player("Player 1");
        player.addCard(new Card(CardType.SKIP));

        assertThrows(NullPointerException.class, () -> player.hasCard(null));
    }

    // PL-13
    @Test
    public void GetHand_ModifyReturnedHand_ThrowsAndHandUnchanged() {
        Player player = new Player("Player 1");
        Card skip = new Card(CardType.SKIP);
        player.addCard(skip);

        assertThrows(
                UnsupportedOperationException.class,
                () -> player.getHand().add(new Card(CardType.DEFUSE))
        );
        assertEquals(List.of(skip), player.getHand());
    }

    // PL-19
    @Test
    public void RemoveOne_EmptyHand_ReturnsEmptyAndHandUnchanged() {
        Player player = new Player("Player 1");

        Optional<Card> removed = player.removeOne(CardType.DEFUSE);

        assertTrue(removed.isEmpty());
        assertTrue(player.getHand().isEmpty());
    }

    // PL-20
    @Test
    public void RemoveOne_OnlyCardOfType_ReturnsItAndRemovesIt() {
        Player player = new Player("Player 1");
        Card skip = new Card(CardType.SKIP);
        Card defuse = new Card(CardType.DEFUSE);
        player.addCard(skip);
        player.addCard(defuse);

        Optional<Card> removed = player.removeOne(CardType.DEFUSE);

        assertEquals(Optional.of(defuse), removed);
        assertEquals(List.of(skip), player.getHand());
    }

    // PL-21
    @Test
    public void RemoveOne_TwoCardsOfType_RemovesOnlyOne() {
        Player player = new Player("Player 1");
        Card firstDefuse = new Card(CardType.DEFUSE);
        Card skip = new Card(CardType.SKIP);
        Card secondDefuse = new Card(CardType.DEFUSE);
        player.addCard(firstDefuse);
        player.addCard(skip);
        player.addCard(secondDefuse);

        Optional<Card> removed = player.removeOne(CardType.DEFUSE);

        assertEquals(Optional.of(firstDefuse), removed);
        assertEquals(List.of(skip, secondDefuse), player.getHand());
    }

    // PL-22
    @Test
    public void RemoveOne_TypeNotInHand_ReturnsEmptyAndHandUnchanged() {
        Player player = new Player("Player 1");
        Card skip = new Card(CardType.SKIP);
        player.addCard(skip);

        Optional<Card> removed = player.removeOne(CardType.DEFUSE);

        assertTrue(removed.isEmpty());
        assertEquals(List.of(skip), player.getHand());
    }

    // PL-23
    @Test
    public void RemoveOne_NullType_ThrowsAndHandUnchanged() {
        Player player = new Player("Player 1");
        Card skip = new Card(CardType.SKIP);
        player.addCard(skip);

        assertThrows(NullPointerException.class, () -> player.removeOne(null));
        assertEquals(List.of(skip), player.getHand());
    }

    // PL-24
    @Test
    public void RemoveAllCards_EmptyHand_ReturnsEmptyList() {
        Player player = new Player("Player 1");

        List<Card> removed = player.removeAllCards();

        assertTrue(removed.isEmpty());
        assertTrue(player.getHand().isEmpty());
    }

}