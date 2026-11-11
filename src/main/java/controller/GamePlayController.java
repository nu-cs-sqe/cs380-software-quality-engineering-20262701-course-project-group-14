package controller;

import domain.DrawResult;
import domain.DrawStatus;
import domain.TurnActions;
import java.util.Objects;

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
        view.render(actions.getSnapshot());
    }

    private String drawMessageKey(DrawResult result) {
        if (result.getStatus() == DrawStatus.EMPTY_DECK) {
            return "draw.empty-deck";
        }
        return "draw.normal-card";
    }
}
