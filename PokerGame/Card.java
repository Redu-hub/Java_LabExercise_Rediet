public class Card {
    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8),
        NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10), ACE(11);

        private final int value;
        Rank(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() { return rank; }
    public int getValue() { return rank.getValue(); }
    // Add this method inside your Card.java class
    public String getImageFileName() {
        String rankStr = switch (this.rank) {
            case ACE -> "A";
            case KING -> "K";
            case QUEEN -> "Q";
            case JACK -> "J";
            default -> String.valueOf(this.rank.getValue());
        };

        String suitStr = switch (this.suit) {
            case CLUBS -> "C";
            case DIAMONDS -> "D";
            case HEARTS -> "H";
            case SPADES -> "S";
        };

        return rankStr + "-" + suitStr + ".png";
    }

    @Override
    public String toString() { return rank + " of " + suit; }
}