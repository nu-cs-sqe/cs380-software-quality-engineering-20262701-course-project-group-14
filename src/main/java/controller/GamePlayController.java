package controller;

import domain.CardType;
import domain.CommandPlayActions;
import domain.DefuseResult;
import domain.DrawResult;
import domain.DrawStatus;
import domain.GameSnapshot;
import domain.PlayCommand;
import domain.PlayFailure;
import domain.PlayResult;
import domain.PlayerSummary;
import domain.TurnActions;
import domain.TurnPhase;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        view.setOnDraw(this::handleDraw);
        view.setOnPlayCard(this::handlePlayCard);
        view.setOnChooseDefusePosition(this::handleChooseDefusePosition);
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
        if (requestCatPair(handIndex)) {
            return;
        }
        presentPlayResult(actions.playCard(handIndex));
    }

    private void presentPlayResult(PlayResult result) {
        showRevealedCards(result);
        showPlayFailure(result.getFailure());
        view.render(actions.getSnapshot());
    }

    private boolean requestCatPair(int handIndex) {
        if (!(view instanceof CatPairViewPort) || !(actions instanceof CommandPlayActions)) {
            return false;
        }
        GameSnapshot snapshot = actions.getSnapshot();
        List<CardType> hand = snapshot.getCurrentPlayerHand();
        if (snapshot.getPhase() != TurnPhase.ACTIONS_ALLOWED
                || handIndex < 0 || handIndex >= hand.size()
                || !hand.get(handIndex).isCatCard()) {
            return false;
        }
        List<Integer> matchingIndices = matchingCards(hand, handIndex);
        if (matchingIndices.isEmpty()) {
            return false;
        }
        chooseCatPair(snapshot, handIndex, matchingIndices);
        return true;
    }

    private void chooseCatPair(GameSnapshot snapshot, int firstIndex, List<Integer> matching) {
        CatPairViewPort choiceView = (CatPairViewPort) view;
        OptionalInt second = choiceView.requestSecondCard(matching);
        if (second.isEmpty() || !matching.contains(second.getAsInt())) {
            return;
        }
        List<PlayerSummary> targets = eligibleTargets(snapshot);
        if (targets.isEmpty()) {
            return;
        }
        OptionalInt target = choiceView.requestTargetPlayer(targets);
        if (target.isEmpty() || !includesSeat(targets, target.getAsInt())) {
            return;
        }
        CommandPlayActions commandActions = (CommandPlayActions) actions;
        PlayCommand command = PlayCommand.pair(firstIndex, second.getAsInt(), target.getAsInt());
        presentPlayResult(commandActions.playCard(command));
    }

    private static boolean includesSeat(List<PlayerSummary> targets, int seat) {
        return targets.stream().anyMatch(player -> player.getPlayerIndex() == seat);
    }

    private static List<PlayerSummary> eligibleTargets(GameSnapshot snapshot) {
        return snapshot.getPlayers().stream()
                .filter(player -> player.getPlayerIndex() != snapshot.getCurrentPlayerIndex())
                .filter(player -> !player.isEliminated() && player.getHandSize() > 0)
                .collect(Collectors.toUnmodifiableList());
    }

    private static List<Integer> matchingCards(List<CardType> hand, int selectedIndex) {
        CardType selected = hand.get(selectedIndex);
        return IntStream.range(0, hand.size())
                .filter(index -> index != selectedIndex && hand.get(index) == selected)
                .boxed().collect(Collectors.toUnmodifiableList());
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
            case INVALID_SELECTION:
            case INVALID_TARGET:
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
