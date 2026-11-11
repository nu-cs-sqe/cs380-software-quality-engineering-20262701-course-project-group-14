package controller;

import domain.CardType;
import domain.GameSnapshot;
import java.util.List;
import java.util.OptionalInt;

public interface GamePlayViewPort {
    void render(GameSnapshot snapshot);

    void showMessage(String messageKey);

    void showFutureCards(List<CardType> cards);

    OptionalInt requestDefusePosition(int minimum, int maximum);
}
