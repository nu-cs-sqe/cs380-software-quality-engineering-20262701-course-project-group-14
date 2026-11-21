package domain;

public final class AttackEffect implements CardEffect {

    @Override
    public boolean canPlay(Player player, Deck deck) {
        return true;
    }

    @Override
    public EffectOutcome play(Player player, Deck deck) {
        throw new UnsupportedOperationException(
                "Attack play is not implemented yet."
        );
    }
}