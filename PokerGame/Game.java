public class Game {
    private final Deck deck = new Deck();
    private final Hand playerHand = new Hand();
    private final Hand dealerHand = new Hand();

    public void startNewRound() {
        playerHand.clear();
        dealerHand.clear();

        // Deal initial cards
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
    }

    public void playerHit() {
        if (!playerHand.isBusted()) {
            playerHand.addCard(deck.drawCard());
        }
    }

    // Automated Dealer Rule: Dealer must hit until they reach at least 17
    public void playDealerTurn() {
        while (dealerHand.calculateTotal() < 17) {
            dealerHand.addCard(deck.drawCard());
        }
    }

    public String determineWinner() {
        int playerTotal = playerHand.calculateTotal();
        int dealerTotal = dealerHand.calculateTotal();

        if (playerHand.isBusted()) return "Player Busts! Dealer Wins.";
        if (dealerHand.isBusted()) return "Dealer Busts! Player Wins.";
        if (playerTotal > dealerTotal) return "Player Wins!";
        if (dealerTotal > playerTotal) return "Dealer Wins!";
        return "It's a Push (Tie)!";
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public Hand getPlayerHand() {
        return playerHand;
    }
}