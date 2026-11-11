package domain;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public final class PlayResult {
    private final PlayStatus status;
    private final PlayFailure failure;
    private final List<CardType> revealedCards;

    private PlayResult(
            PlayStatus status,
            PlayFailure failure,
            List<CardType> revealedCards
    ) {
        this.status = status;
        this.failure = failure;
        this.revealedCards = revealedCards;
    }

    public static PlayResult continueTurn(
            List<CardType> revealedCards
    ) {
        List<CardType> checkedCards = Objects.requireNonNull(
                revealedCards,
                "Revealed cards must not be null."
        );

        for (CardType card : checkedCards) {
            if (card == null) {
                throw new NullPointerException(
                        "Revealed cards must not contain null."
                );
            }
        }

        return new PlayResult(
                PlayStatus.CONTINUE_TURN,
                PlayFailure.NONE,
                new ArrayList<>(checkedCards)
        );
    }

    public PlayStatus getStatus() {
        return status;
    }

    public PlayFailure getFailure() {
        return failure;
    }

    public List<CardType> getRevealedCards() {
        return revealedCards;
    }

    public static PlayResult turnEnded() {
        return new PlayResult(
                PlayStatus.TURN_ENDED,
                PlayFailure.NONE,
                new ArrayList<>()
        );
    }

    public static PlayResult rejected(PlayFailure failure) {
        return new PlayResult(
                PlayStatus.REJECTED,
                failure,
                new ArrayList<>()
        );
    }

}