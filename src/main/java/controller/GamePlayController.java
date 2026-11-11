package controller;

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
}
