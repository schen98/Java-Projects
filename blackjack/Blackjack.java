package blackjack;

import java.util.*;

/**
 * 
 * @author Simon
 *
 */
public class Blackjack implements BlackjackEngine {

	private ArrayList<Card> deck;
	private ArrayList<Card> playerCards;
	private ArrayList<Card> dealerCards;
	private int accountAmount;
	private int betAmount;
	private int numOfDecks;
	private int gameStatus;
	private Random randomNum;

	/**
	 * Constructor you must provide. Initializes the player's account to 200 and the
	 * initial bet to 5. Feel free to initialize any other fields. Keep in mind that
	 * the constructor does not define the deck(s) of cards.
	 * 
	 * @param randomGenerator
	 * @param numberOfDecks
	 */
	public Blackjack(Random randomGenerator, int numberOfDecks) {
		this.randomNum = randomGenerator;
		this.numOfDecks = numberOfDecks;
		this.accountAmount = 200;
		this.betAmount = 5;
	}

	/**
	 * returns the number of decks being used
	 */
	public int getNumberOfDecks() {
		return numOfDecks;
	}

	/**
	 * Creates and shuffles the card deck(s) using a random number generator
	 */
	public void createAndShuffleGameDeck() {
		deck = new ArrayList<Card>();

		for (int x = 0; x < numOfDecks; x++) {
			for (CardSuit suit : CardSuit.values()) {
				for (CardValue value : CardValue.values()) {
					deck.add(new Card(value, suit));
				}
			}
		}
		Collections.shuffle(deck, randomNum);
	}

	/**
	 * returns the current deck of cards
	 */
	public Card[] getGameDeck() {
		Card[] gameDeck = new Card[this.deck.size()];
		
		return deck.toArray(gameDeck);
	}

	/**
	 * Creates a new deck of cards and hands out a total of four cards in this
	 * order, player(face up), dealer(face up), player(face up), and dealer(face
	 * down). Game's status will then be GAME_IN_PROGRESS. Delete bet amount from
	 * account.
	 */
	public void deal() {
		createAndShuffleGameDeck();
		playerCards = new ArrayList<Card>();
		dealerCards = new ArrayList<Card>();

		playerCards.add(deck.get(0));
		deck.remove(0);

		Card cardDown = deck.get(0);
		deck.remove(0);
		cardDown.setFaceDown();
		dealerCards.add(cardDown);

		playerCards.add(deck.get(0));
		deck.remove(0);
		dealerCards.add(deck.get(0));
		deck.remove(0);

		gameStatus = BlackjackEngine.GAME_IN_PROGRESS;
		setAccountAmount(accountAmount -= betAmount);
	}

	/**
	 * returns dealer's cards
	 */
	public Card[] getDealerCards() {
		Card[] dealerHand = new Card[dealerCards.size()];
		return dealerCards.toArray(dealerHand);
	}

	/**
	 * returns an array representing possible values of dealer's cards if card
	 * values are less than or equal to 21
	 */
	public int[] getDealerCardsTotal() {
		return cardsTotal(dealerCards);
	}

	/**
	 * returns integer value that assumes values, LESS_THAN_21, HAS_21, BUST, or
	 * BLACKJACK if dealer has a ace with 10, jack, queen, or a king
	 */
	public int getDealerCardsEvaluation() {
		return cardsEval(getDealerCardsTotal(), dealerCards);

	}

	/**
	 * returns player's cards
	 */
	public Card[] getPlayerCards() {
		Card[] playerHand = new Card[playerCards.size()];
		return playerCards.toArray(playerHand);
	}

	/**
	 * returns an array representing possible values of player's cards if card
	 * values are less than or equal to 21
	 */
	public int[] getPlayerCardsTotal() {
		return cardsTotal(playerCards);
	}

	/**
	 * returns integer value that assumes values, LESS_THAN_21, HAS_21, BUST, or
	 * BLACKJACK if dealer has a ace with 10, jack, queen, or a king
	 */
	public int getPlayerCardsEvaluation() {
		return cardsEval(getPlayerCardsTotal(), playerCards);
	}

	/**
	 * Retrieves a card from the deck and assign it to the player's hand and is then
	 * evaluated. If player busts, game is over and game status is updated to
	 * DEALER_WON. Else game status to GAME_IN_PROGRESS.
	 */
	public void playerHit() {
		playerCards.add(deck.remove(0));

		if (getPlayerCardsEvaluation() == BlackjackEngine.BUST) {
			dealerCards.get(0).setFaceUp();
			gameStatus = BlackjackEngine.DEALER_WON;
		} else {
			gameStatus = BlackjackEngine.GAME_IN_PROGRESS;
		}
	}

	/**
	 * Flip dealer card face up and draws card from the deck if dealer doesn't bust
	 * and card sum is less than 16. Once dealer hand value is between 16 and 21, it
	 * will be compared to player's hand. If player hand is greater, status updated
	 * to PLAYER_WON, and add double the bet amount is added to account. If player
	 * hand is less, status updated to DEALER_WON. If its a draw, then status
	 * updated to DRAW.
	 */
	public void playerStand() {
		this.dealerCards.get(0).setFaceUp();
		int[] dealerTotal = getDealerCardsTotal();
		int[] playerTotal = getPlayerCardsTotal();
		int[] dealerBest = new int[1];
		int[] playerBest = new int[1];
		boolean underSixteen = true;

		while (underSixteen) {
			dealerTotal = getDealerCardsTotal();
			if (dealerTotal == null || dealerTotal[dealerTotal.length - 1] > 15) {
				underSixteen = false;
			} else {
				this.dealerCards.add(this.deck.remove(0));
			}
		}

		if (dealerTotal == null) {
			gameStatus = BlackjackEngine.PLAYER_WON;
			accountAmount += (2 * betAmount);
		} else if (playerTotal == null) {
			gameStatus = BlackjackEngine.DEALER_WON;
		} else {
			dealerBest[0] = dealerTotal[dealerTotal.length - 1];
			playerBest[0] = playerTotal[playerTotal.length - 1];

			if (playerBest[0] > dealerBest[0]) {
				gameStatus = BlackjackEngine.PLAYER_WON;
				accountAmount += (2 * betAmount);
			} else if (playerBest[0] < dealerBest[0]) {
				gameStatus = BlackjackEngine.DEALER_WON;
			} else {
				gameStatus = BlackjackEngine.DRAW;
				accountAmount += betAmount;
			}
		}
	}

	/**
	 * returns the integer representing the game status
	 */
	public int getGameStatus() {
		return gameStatus;
	}

	/**
	 * sets the player's bet amount
	 */
	public void setBetAmount(int amount) {
		this.betAmount = amount;
	}

	/**
	 * returns the player's bet amount
	 */
	public int getBetAmount() {
		return betAmount;
	}

	/**
	 * sets the player's account
	 */
	public void setAccountAmount(int amount) {
		this.accountAmount = amount;
	}

	/**
	 * returns player's account amount
	 */
	public int getAccountAmount() {
		return accountAmount;
	}

	/* Feel Free to add any private methods you might need */

	/**
	 * 
	 * @param hand array list of hand of cards
	 * @return integer array of hands total
	 */
	private int[] cardsTotal(ArrayList<Card> hand) {
		boolean hasAce = false;
		int aceOne = 0;
		int aceEleven = 0;
		int noAces = 0;

		for (int x = 0; x < hand.size(); x++) {
			if (hand.get(x).getValue() == CardValue.Ace) {
				hasAce = true;
			}
		}

		if (!hasAce) {
			for (int x = 0; x < hand.size(); x++) {
				noAces += hand.get(x).getValue().getIntValue();
			}
			if (noAces > 21) {
				return null;
			}
			int[] temp = {noAces};
			return temp;

		} else {
			for (int x = 0; x < hand.size(); x++) {
				aceOne += hand.get(x).getValue().getIntValue();
			}
			aceEleven = aceOne + 10;
			if (aceOne > 21) {
				return null;
			} else if (aceOne <= 21 && aceEleven > 21) {
				int[] temp = {aceOne};
				return temp;
			} else {
				int[] temp = {aceOne, aceEleven};
				return temp;
			}
		}

	}

	/**
	 * 
	 * @param cardEval integer array of cards
	 * @param hand     array list of hand of cards
	 * @return integer representing card standing
	 */
	private int cardsEval(int[] cardEval, ArrayList<Card> hand) {
		if (cardEval == null) {
			return BlackjackEngine.BUST;
		}

		if (cardEval.length == 2 && cardEval[1] == 21) {
			if (hand.size() == 2) {
				return BlackjackEngine.BLACKJACK;
			} else {
				return BlackjackEngine.HAS_21;
			}
		}

		return Blackjack.LESS_THAN_21;
	}

}