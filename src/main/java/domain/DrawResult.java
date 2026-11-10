package domain;

import java.util.Optional;
import java.util.Objects;

public final class DrawResult {
    private final DrawStatus status;
    private final CardType drawnCardType;

    private DrawResult(
            DrawStatus status,
            CardType drawnCardType
    ) {
        this.status = status;
        this.drawnCardType = drawnCardType;
    }

    public static DrawResult normal(CardType drawnCardType) {
        return new DrawResult(
                DrawStatus.NORMAL_CARD_DRAWN,
                Objects.requireNonNull(
                        drawnCardType,
                        "Drawn card type must not be null."
                )
        );
    }

    public static DrawResult defuseRequired() {
        return new DrawResult(
                DrawStatus.DEFUSE_POSITION_REQUIRED,
                null
        );
    }

    public DrawStatus getStatus() {
        return status;
    }

    public Optional<CardType> getDrawnCardType() {
        return Optional.ofNullable(drawnCardType);
    }

    public static DrawResult playerEliminated() {
        return new DrawResult(
                DrawStatus.PLAYER_ELIMINATED,
                null
        );
    }

}