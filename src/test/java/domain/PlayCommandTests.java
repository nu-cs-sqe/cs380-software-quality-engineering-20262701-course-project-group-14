package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void Single_WithNegativeIndex_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PlayCommand.single(-1)
        );

        assertEquals(
                "Hand index must not be negative.",
                exception.getMessage()
        );
    }

    @Test
    public void Targeted_WithZeroIndices_PreservesCardAndTarget() {
        PlayCommand command = PlayCommand.targeted(0, 0);

        assertEquals(0, command.getFirstHandIndex());
        assertTrue(command.getSecondHandIndex().isEmpty());
        assertEquals(0, command.getTargetPlayerIndex().orElseThrow());
    }

    @Test
    public void Targeted_WithPositiveIndices_PreservesCardAndTarget() {
        PlayCommand command = PlayCommand.targeted(3, 2);

        assertEquals(3, command.getFirstHandIndex());
        assertTrue(command.getSecondHandIndex().isEmpty());
        assertEquals(2, command.getTargetPlayerIndex().orElseThrow());
    }

    @Test
    public void Targeted_WithNegativeHandIndex_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PlayCommand.targeted(-1, 0)
        );

        assertEquals(
                "Hand index must not be negative.",
                exception.getMessage()
        );
    }

    @Test
    public void Targeted_WithNegativeTarget_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PlayCommand.targeted(0, -1)
        );

        assertEquals(
                "Target player index must not be negative.",
                exception.getMessage()
        );
    }

    @Test
    public void Pair_WithDistinctIndices_PreservesBothCardsAndTarget() {
        PlayCommand command = PlayCommand.pair(0, 1, 2);

        assertEquals(0, command.getFirstHandIndex());
        assertEquals(1, command.getSecondHandIndex().orElseThrow());
        assertEquals(2, command.getTargetPlayerIndex().orElseThrow());
    }

    @Test
    public void Pair_WithReverseIndices_PreservesOriginalOrder() {
        PlayCommand command = PlayCommand.pair(4, 1, 0);

        assertEquals(4, command.getFirstHandIndex());
        assertEquals(1, command.getSecondHandIndex().orElseThrow());
        assertEquals(0, command.getTargetPlayerIndex().orElseThrow());
    }

    @Test
    public void Pair_WithDuplicateIndices_PreservesCommandForEngineValidation() {
        PlayCommand command = PlayCommand.pair(1, 1, 0);

        assertEquals(1, command.getFirstHandIndex());
        assertEquals(1, command.getSecondHandIndex().orElseThrow());
        assertEquals(0, command.getTargetPlayerIndex().orElseThrow());
    }

    @Test
    public void Pair_WithNegativeFirstIndex_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PlayCommand.pair(-1, 0, 0)
        );

        assertEquals(
                "Hand index must not be negative.",
                exception.getMessage()
        );
    }

    @Test
    public void Pair_WithNegativeSecondIndex_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PlayCommand.pair(0, -1, 0)
        );

        assertEquals(
                "Second hand index must not be negative.",
                exception.getMessage()
        );
    }
}
