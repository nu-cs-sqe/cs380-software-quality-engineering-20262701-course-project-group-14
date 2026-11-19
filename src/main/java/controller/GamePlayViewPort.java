package controller;

import domain.CardType;
import domain.GameSnapshot;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

public interface GamePlayViewPort {
    void setOnDraw(Runnable action);

    void setOnPlayCard(IntConsumer action);

    void setOnChooseDefusePosition(Runnable action);

    void render(GameSnapshot snapshot);

    void showMessage(String messageKey);

    void showFutureCards(List<CardType> cards);

    OptionalInt requestDefusePosition(int minimum, int maximum);
}