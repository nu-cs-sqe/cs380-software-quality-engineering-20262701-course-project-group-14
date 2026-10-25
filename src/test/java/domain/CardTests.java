package domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTests {

    @ParameterizedTest(name = "Card01: type={0}")
    @EnumSource(CardType.class)
    public void Create_WithValidType_GetTypeReturnsSuppliedType(
            CardType type) {
        Card card = new Card(type);

        assertEquals(type, card.getType());
    }
}