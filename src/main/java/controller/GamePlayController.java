package controller;

import domain.DefuseResult;
import domain.DrawResult;
import domain.DrawStatus;
import domain.GameSnapshot;
import domain.PlayFailure;
import domain.PlayResult;
import domain.TurnActions;
import java.util.Objects;
import java.util.OptionalInt;

public final class GamePlayController {
    private final TurnActions actions;
    private final GamePlayViewPort view;

    public GamePlayController(
            TurnActions actions,
            GamePlayViewPort view
    ) {
        this.actions = Objects.requireNonNull(
                actions,
                "Turn actions must not be null."
        );
        this.view = Objects.requireNonNull(
                view,
                "Game play view must not be null."
        );
    }

    public void initialize() {
        view.render(actions.getSnapshot());
    }

    public void handleDraw() {
        DrawResult result = actions.draw();
        view.showMessage(drawMessageKey(result));
        GameSnapshot snapshot = actions.getSnapshot();
        view.render(snapshot);
        if (result.getStatus() == DrawStatus.DEFUSE_POSITION_REQUIRED) {
            requestDefusePosition(snapshot);
        }
    }

    public void handleChooseDefusePosition() {
        requestDefusePosition(actions.getSnapshot());
    }

    public void handlePlayCard(int handIndex) {
        PlayResult result = actions.playCard(handIndex);
        showRevealedCards(result);
        showPlayFailure(result.getFailure());
        view.render(actions.getSnapshot());
    }

    private void showRevealedCards(PlayResult result) {
        if (!result.getRevealedCards().isEmpty()) {
            view.showFutureCards(result.getRevealedCards());
        }
    }

    private void showPlayFailure(PlayFailure failure) {
        String messageKey = playFailureMessageKey(failure);
        if (messageKey != null) {
            view.showMessage(messageKey);
        }
    }

    private void requestDefusePosition(GameSnapshot snapshot) {
        OptionalInt position = view.requestDefusePosition(
                0,
                snapshot.getDeckSize()
        );
        if (position.isEmpty()) {
            return;
        }
        DefuseResult result = actions.resolveDefuse(position.getAsInt());
        view.showMessage(defuseMessageKey(result));
        view.render(actions.getSnapshot());
    }

    private String drawMessageKey(DrawResult result) {
        switch (result.getStatus()) {
            case EMPTY_DECK:
                return "draw.empty-deck";
            case REJECTED_PENDING_DEFUSE:
                return "draw.pending-defuse";
            case PLAYER_ELIMINATED:
                return "draw.player-eliminated";
            case DEFUSE_POSITION_REQUIRED:
                return "draw.defuse-position-required";
            case NORMAL_CARD_DRAWN:
                return "draw.normal-card";
            default:
                throw new IllegalStateException("Unknown draw status.");
        }
    }

    private String playFailureMessageKey(PlayFailure failure) {
        switch (failure) {
            case INVALID_HAND_INDEX:
                return "play.invalid-hand-index";
            case CARD_NOT_PLAYABLE:
                return "play.card-not-playable";
            case PRECONDITION_NOT_MET:
                return "play.precondition-not-met";
            case DEFUSE_RESOLUTION_REQUIRED:
                return "play.defuse-required";
            case NONE:
                return null;
            default:
                throw new IllegalStateException("Unknown play failure.");
        }
    }

    private String defuseMessageKey(DefuseResult result) {
        switch (result) {
            case DEFUSED:
                return "defuse.success";
            case INVALID_POSITION:
                return "defuse.invalid-position";
            case NOT_AWAITING_DEFUSE:
                return "defuse.not-awaiting";
            case DEFUSE_NOT_FOUND:
                return "defuse.not-found";
            default:
                throw new IllegalStateException("Unknown defuse result.");
        }
    }
}
