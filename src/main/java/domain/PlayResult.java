package domain;

import java.util.List;
import java.util.Objects;

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
        return new PlayResult(
                PlayStatus.CONTINUE_TURN,
                PlayFailure.NONE,
                Objects.requireNonNull(
                        revealedCards,
                        "Revealed cards must not be null."
                )
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


}