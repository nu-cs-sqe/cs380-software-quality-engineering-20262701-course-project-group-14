package domain;

public interface CardEffect {
    boolean canPlay(Player player, Deck deck);

    EffectOutcome play(Player player, Deck deck);
}
