package domain;

public enum CardType {
    EXPLODING_KITTEN,
    DEFUSE,
    ATTACK,
    SKIP,
    FAVOR,
    SHUFFLE,
    SEE_THE_FUTURE,
    NOPE,
    SNIFF_TEST,
    TACOCAT(true),
    CATTERMELON(true),
    HAIRY_POTATO_CAT(true),
    BEARD_CAT(true),
    RAINBOW_RALPHING_CAT(true);

    private final boolean catCard;

    CardType() {
        this(false);
    }

    CardType(boolean catCard) {
        this.catCard = catCard;
    }

    public boolean isCatCard() {
        return catCard;
    }
}
