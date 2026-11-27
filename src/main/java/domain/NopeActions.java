package domain;

public interface NopeActions {
    boolean canPlayNope(int respondingPlayerIndex);

    PlayResult respondToNope(
            int respondingPlayerIndex,
            boolean playNope
    );
}
