import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.io.InputStream;

public class Main extends Application {

    private final Game game = new Game();

    // UI Containers for holding the dynamically generated card image nodes
    private final HBox dealerCardContainer = new HBox(10);
    private final HBox playerCardContainer = new HBox(10);

    // UI Labels for text updates
    private final Label dealerScoreLabel = new Label("Score: 0");
    private final Label playerScoreLabel = new Label("Score: 0");
    private final Label statusLabel = new Label("Welcome to Blackjack! Press Deal to start.");

    private Button hitButton;
    private Button standButton;
    private Button dealButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("University Blackjack Project");

        //  Styling Text Labels
        Font labelFont = Font.font("Arial", FontWeight.BOLD, 16);
        dealerScoreLabel.setFont(labelFont);
        dealerScoreLabel.setStyle("-fx-text-fill: white;");
        playerScoreLabel.setFont(labelFont);
        playerScoreLabel.setStyle("-fx-text-fill: white;");

        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        statusLabel.setStyle("-fx-text-fill: #ffeb3b;"); // Bright casino yellow

        //  Grouping Dealer and Player Areas
        VBox dealerArea = new VBox(10,
                new Label("DEALER") {{ setFont(Font.font("Arial", FontWeight.BLACK, 14)); setStyle("-fx-text-fill: #b2dfdb;"); }},
                dealerCardContainer,
                dealerScoreLabel
        );
        dealerArea.setAlignment(Pos.CENTER);

        VBox playerArea = new VBox(10,
                new Label("PLAYER") {{ setFont(Font.font("Arial", FontWeight.BLACK, 14)); setStyle("-fx-text-fill: #b2dfdb;"); }},
                playerCardContainer,
                playerScoreLabel
        );
        playerArea.setAlignment(Pos.CENTER);

        // Configuring Control Buttons
        hitButton = new Button("Hit");
        standButton = new Button("Stand");
        dealButton = new Button("Deal New Hand");

        String buttonStyle = "-fx-font-size: 14px; -fx-padding: 8 20 8 20; -fx-font-weight: bold;";
        hitButton.setStyle(buttonStyle);
        standButton.setStyle(buttonStyle);
        dealButton.setStyle(buttonStyle);

        // Link buttons to event handlers
        hitButton.setOnAction(e -> handleHit());
        standButton.setOnAction(e -> handleStand());
        dealButton.setOnAction(e -> handleDeal());

        // Disable gameplay controls until game setup is initiated via "Deal"
        hitButton.setDisable(true);
        standButton.setDisable(true);

        HBox buttonControlPanel = new HBox(20, dealButton, hitButton, standButton);
        buttonControlPanel.setAlignment(Pos.CENTER);

        //  Base Layout Assembly
        VBox tableRoot = new VBox(40, dealerArea, statusLabel, playerArea, buttonControlPanel);
        tableRoot.setAlignment(Pos.CENTER);
        tableRoot.setPadding(new Insets(30));
        // Classic green felt casino background styling
        tableRoot.setStyle("-fx-background-color: #1b5e20; -fx-border-color: #0d47a1; -fx-border-width: 5;");

        // Render Stage
        Scene scene = new Scene(tableRoot, 700, 550); // Slightly wider to support multiple side-by-side card graphics
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    //  Action Handlers

    private void handleDeal() {
        game.startNewRound();

        statusLabel.setText("Your turn! Hit or Stand?");
        dealButton.setDisable(true);
        hitButton.setDisable(false);
        standButton.setDisable(false);

        updateTableDisplay(false); // Round starts: dealer's first card stays face-down
    }

    private void handleHit() {
        game.playerHit();

        if (game.determineWinner().contains("Player Busts")) {
            statusLabel.setText(game.determineWinner());
            endGameplayRound();
            updateTableDisplay(true); // Player busted: reveal the dealer's face-down card
        } else {
            updateTableDisplay(false);
        }
    }

    private void handleStand() {
        // Player stands: trigger automated house dealer AI rules
        game.playDealerTurn();
        statusLabel.setText(game.determineWinner());

        endGameplayRound();
        updateTableDisplay(true); // Reveal everything for evaluation comparison
    }

    private void endGameplayRound() {
        dealButton.setDisable(false);
        hitButton.setDisable(true);
        standButton.setDisable(true);
    }

    // UI Synchronizer Rendering Methods

    private void updateTableDisplay(boolean revealDealerWholeHand) {
        playerCardContainer.getChildren().clear();
        dealerCardContainer.getChildren().clear();

        // 1. Render Player Hand
        for (Card card : game.getPlayerHand().getCards()) {
            playerCardContainer.getChildren().add(createVisualCardNode(card.getImageFileName()));
        }
        playerScoreLabel.setText("Score: " + game.getPlayerHand().calculateTotal());

        // 2. Render Dealer Hand
        var dealerCards = game.getDealerHand().getCards();
        if (!dealerCards.isEmpty()) {
            if (revealDealerWholeHand) {
                for (Card card : dealerCards) {
                    dealerCardContainer.getChildren().add(createVisualCardNode(card.getImageFileName()));
                }
                dealerScoreLabel.setText("Score: " + game.getDealerHand().calculateTotal());
            } else {
                // Mask the dealer's primary card using the card-back graphic
                dealerCardContainer.getChildren().add(createVisualCardNode("hidden.png"));

                // Show subsequent face-up dealer cards normally
                for (int i = 1; i < dealerCards.size(); i++) {
                    dealerCardContainer.getChildren().add(createVisualCardNode(dealerCards.get(i).getImageFileName()));
                }
                dealerScoreLabel.setText("Score: ?");
            }
        }
    }

    /**
     * Node Factory: Looks up the file name from project resources and outputs an ImageView component.
     */
    private ImageView createVisualCardNode(String fileName) {
        try {
            InputStream stream = getClass().getResourceAsStream("/cards/" + fileName);
            if (stream == null) {
                System.out.println("Missing asset resource mapping: " + fileName);
                return new ImageView();
            }

            Image cardImage = new Image(stream);
            ImageView imageView = new ImageView(cardImage);

            // Constrain constraints
            imageView.setFitWidth(90);
            imageView.setFitHeight(130);
            imageView.setPreserveRatio(true);

            return imageView;
        } catch (Exception e) {
            e.printStackTrace();
            return new ImageView();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}