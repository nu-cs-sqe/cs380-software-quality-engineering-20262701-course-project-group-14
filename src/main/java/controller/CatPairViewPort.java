package controller;

import domain.PlayerSummary;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

public interface CatPairViewPort extends GamePlayViewPort {
    void requestSecondCard(int firstHandIndex, List<Integer> matchingHandIndices,
                           IntConsumer onMatchSelected);

    OptionalInt requestTargetPlayer(List<PlayerSummary> candidates);
}
