package domain;

public interface TurnActions {
    DrawResult draw();

    PlayResult playCard(int handIndex);

    DefuseResult resolveDefuse(int position);

    TurnPhase getPhase();

    GameSnapshot getSnapshot();
}
