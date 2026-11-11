package controller;

import domain.DrawResult;
import domain.DrawStatus;
import domain.GameSnapshot;
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

    private void requestDefusePosition(GameSnapshot snapshot) {
        OptionalInt position = view.requestDefusePosition(
                0,
                snapshot.getDeckSize()
        );
        if (position.isPresent()) {
            actions.resolveDefuse(position.getAsInt());
        }
    }

    private String drawMessageKey(DrawResult result) {
        if (result.getStatus() == DrawStatus.EMPTY_DECK) {
            return "draw.empty-deck";
        }
        if (result.getStatus() == DrawStatus.REJECTED_PENDING_DEFUSE) {
            return "draw.pending-defuse";
        }
        if (result.getStatus() == DrawStatus.PLAYER_ELIMINATED) {
            return "draw.player-eliminated";
        }
        if (result.getStatus() == DrawStatus.DEFUSE_POSITION_REQUIRED) {
            return "draw.defuse-position-required";
        }
        return "draw.normal-card";
    }
}
