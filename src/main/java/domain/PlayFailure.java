package domain;

public enum PlayFailure {
    NONE,
    INVALID_HAND_INDEX,
    CARD_NOT_PLAYABLE,
    PRECONDITION_NOT_MET,
    DEFUSE_RESOLUTION_REQUIRED
}
