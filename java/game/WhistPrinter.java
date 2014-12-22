package com.appspot.whist.game;

import java.util.List;

import com.appspot.whist.cards.Card;
import com.appspot.whist.cards.Suit;
import com.appspot.whist.game.HandScenario.PlayedCard;

public class WhistPrinter {
	
	private static final int SINGLE_HAND_LEFT_MARGIN = 35;
	private static final int DOUBLE_HAND_LEFT_MARGIN = 5;
	private static final int DOUBLE_HAND_MID_MARGIN = 10;
	private static final int MAX_HAND_WIDTH = 40;
	private static final int SPACER_ROWS = 1;

	public void displayHands(Player[] players) {
		printSingleHand(players[0]);
		printLines(SPACER_ROWS);
		printDoubleHand(players[3], players[1]);
		printLines(SPACER_ROWS);
		printSingleHand(players[2]);
	}
	
	private void printSingleHand(Player player) {
		for(Suit suit: Suit.values()) {
			addSpace(SINGLE_HAND_LEFT_MARGIN);
			printSuit(suit, player);
			printLines(1);
		}
	}
	
	private void printDoubleHand(Player left, Player right) {
		for(Suit suit: Suit.values()) {
			addSpace(DOUBLE_HAND_LEFT_MARGIN);
			printSuit(suit, left);
			addSpace(DOUBLE_HAND_MID_MARGIN);
			printSuit(suit, right);
			printLines(1);
		}
	}
	
	private void printSuit(Suit suit, Player player) {
		StringBuffer sb = new StringBuffer();
		System.out.print(suit + ": ");
		addSpace(Suit.DIAMONDS.toString().length() - suit.toString().length());
		List<Card> suitCards = player.getSuitCards(suit);
		for(Card card: suitCards) {
			sb.append(card.toShortString() + " ");
		}
		System.out.print(sb.toString());
		addSpace(MAX_HAND_WIDTH - sb.length());
	}

	public void updateScores(int[] scores) {
		// TODO Auto-generated method stub
		
	}

	public void showBid(String name, int bid) {
		System.out.println(name +  " bid " + bid);
	}

	public void reDeal() 
	{
		
	}

	public void printLines(int nLines) {
		for(int i = 0; i < nLines; i++) {
			System.out.println();
		}
	}
	
	private void addSpace(int nSpaces) {
		for(int i = 0; i < nSpaces; i++) {
			System.out.print(" ");
		}
	}

	public void printTrump(Trump trump) {
		System.out.println((trump.isHigh()? "High " : "Low ") + trump.getSuit());
	}
	
	public void trickWon(PlayedCard played)
	{
		System.out.println("Player " + (played.playerNum + 1) + " wins the trick with the " + played);
	}

	public void cardPlayed(Card played, int playerNum) 
	{
		System.out.println("Player " + (playerNum + 1) + " plays the " + played);
	}
}
