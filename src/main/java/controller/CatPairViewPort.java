package controller;

import domain.PlayerSummary;
import java.util.List;
import java.util.OptionalInt;

public interface CatPairViewPort extends GamePlayViewPort {
    OptionalInt requestSecondCard(List<Integer> matchingHandIndices);

    OptionalInt requestTargetPlayer(List<PlayerSummary> candidates);
}
