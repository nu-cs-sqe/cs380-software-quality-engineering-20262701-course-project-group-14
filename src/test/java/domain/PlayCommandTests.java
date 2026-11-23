package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayCommandTests {

    @Test
    public void Single_WithIndexZero_PreservesIndexWithoutSelections() {
        PlayCommand command = PlayCommand.single(0);

        assertEquals(0, command.getFirstHandIndex());
        assertTrue(command.getSecondHandIndex().isEmpty());
        assertTrue(command.getTargetPlayerIndex().isEmpty());
    }

    @Test
    public void Single_WithPositiveIndex_PreservesIndexWithoutSelections() {
        PlayCommand command = PlayCommand.single(4);

        assertEquals(4, command.getFirstHandIndex());
        assertTrue(command.getSecondHandIndex().isEmpty());
        assertTrue(command.getTargetPlayerIndex().isEmpty());
    }
}
