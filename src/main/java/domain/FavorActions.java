package domain;

import java.util.List;

public interface FavorActions {
    List<CardType> getFavorChoices(int respondingPlayerIndex);

    PlayResult submitFavorGift(
            int respondingPlayerIndex,
            int giftHandIndex
    );
}
