package domain;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;

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
        return Collections.unmodifiableList(revealedCards);
    }

    public static PlayResult turnEnded() {
        return new PlayResult(
                PlayStatus.TURN_ENDED,
                PlayFailure.NONE,
                new ArrayList<>()
        );
    }

    public static PlayResult rejected(PlayFailure failure) {
        PlayFailure checkedFailure = Objects.requireNonNull(
                failure,
                "Play failure must not be null."
        );

        if (checkedFailure == PlayFailure.NONE) {
            throw new IllegalArgumentException(
                    "Rejected result requires a failure reason."
            );
        }

        return new PlayResult(
                PlayStatus.REJECTED,
                checkedFailure,
                new ArrayList<>()
        );
    }

}