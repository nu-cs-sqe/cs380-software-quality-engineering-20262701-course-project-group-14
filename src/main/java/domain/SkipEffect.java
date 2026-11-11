package domain;

public final class SkipEffect implements CardEffect {

    @Override
    public boolean canPlay(Player player, Deck deck) {
        return true;
    }

    @Override
    public EffectOutcome play(Player player, Deck deck) {
        throw new UnsupportedOperationException(
                "Skip effect is not implemented."
        );
    }
}
