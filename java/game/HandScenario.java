package com.appspot.whist.game;

import java.util.ArrayList;
import java.util.List;

import com.appspot.whist.cards.Card;
import com.appspot.whist.cards.Deck;
import com.appspot.whist.cards.SuitIterator;
import com.appspot.whist.cards.Suit;

/**
 * 
 * @author Greg Harris
 * A grouping of useful information about the ongoing hand. Will be used heavily by the AI. 
 * This class will only deal with PUBLICLY KNOWN DATA!! Which cards have been played, what 
 * trump is, who is winning.  
 *
 */
public class HandScenario {

	/** All the cards played so far, arranged by suit */
	private List<Card>[] cardsPlayedBySuit = new List[4];
	
	/** The rank (1 is best, 13 is worst) of the auto winner in each suit */
	private int[] winnerRank = new int[4];
	
	/** Whether trump is high */
	private boolean isHigh;
	
	/** The trump suit */
	private Suit trumpSuit;
	
	/** The trump object */
	private Trump trump;
	
	/** Whether each suit has been trumped (always false for trump itself) */
	private boolean[] hasBeenTrumped = new boolean[4];

	private Player kittyTaker;
	
	private boolean[] hasTrumpLeft = new boolean[4];
	
	/** Constructor needs to know the trump suit and which player called it, and all the players' hands */
	public HandScenario(Trump trmp, Player kttyTkr)
	{
		for(int i = 0; i < 4; i++)
		{
			cardsPlayedBySuit[i] = new ArrayList<Card>();
			winnerRank[i] = 1;
			hasBeenTrumped[i] = false;
			hasTrumpLeft[i] = true;
		}
		trump = trmp;
		trumpSuit = trmp.getSuit();
		isHigh = trump.isHigh();
		kittyTaker = kttyTkr;
	}
	
	/** A simple pairing of a Card with the number of the player who threw it */
	public static class PlayedCard extends Card
	{
		public int playerNum;
		public PlayedCard(Card card, int playerNum)
		{
			super(card.getSuit(), card.getRank());
			this.playerNum = playerNum;
		}
	}
	
	/** what to do when a card is played */
	public void cardPlayed(PlayedCard card)
	{
		//add it to the cards we've seen
		int suitInd = card.getSuit().ordinal();
		cardsPlayedBySuit[suitInd].add(card);
		if(card.rankSpot(isHigh) == winnerRank[suitInd]) //adjust the winning card for the played suit
		{
			do { winnerRank[suitInd]--; } while(contains(cardsPlayedBySuit[suitInd], winnerRank[suitInd]));
		}
	}
	
	/** whether the list of cards of a specific suit contain the card with the given rank */
	public boolean contains(List<Card> cards, int rankSpot)
	{
		for(Card card : cards)
		{
			if(rankSpot == card.rankSpot(isHigh)) return true;
		}
		return false;
	}
	
	/** is the given card a winner? */
	public boolean isWinner(Card card)
	{
		int suitInd = card.getSuit().ordinal();
		return !hasBeenTrumped[suitInd] && card.rankSpot(isHigh) == winnerRank[suitInd];
	}
	
	/** The number of cards played so far in the given suit */
	public int nPlayed(Suit suit)
	{
		return cardsPlayedBySuit[suit.ordinal()].size();
	}

	public boolean amDefending(Player player) {
		return !player.isPartner(kittyTaker); 
	}

	public Suit getTrumpSuit() { return trumpSuit; }
	public Trump getTrump() { return trump; }
	public boolean isHigh() { return isHigh; }

	/**
	 * returns a Card that is a winner from the give list of cards which are 
	 * all of the same suit (comes from handBySuits). Takes into account whether 
	 * it is likely that the suit will be trumped(????)
	 */
	public Card getSuitWinner(List<Card> leadSuitCards, Suit leadSuit, int playerNum) 
	{
		for(SuitIterator sit = new SuitIterator(leadSuitCards, isHigh); sit.hasNext();)
		{
			Card card = sit.next();
			if(isWinner(card))
			{
				return card;
			}
		}
		return null;
	}
	
	/** 
	 * This method is called after each trick is played. 
	 * All of the data about the hand scenario is updated based on the cards that were played
	 */
	public void update() 
	{
		
	}

	/** was I the player who took the kitty? */
	public boolean amKittyTaker(Player player) 
	{
		return player.equals(kittyTaker);
	}

	/** should I draw trump? */
	public boolean shouldDrawTrump(ComputerPlayer computerPlayer) 
	{
		int trumpIhave = computerPlayer.getHand().howManyInSuit(trumpSuit);
		int trumpLeft = 13 - trumpIhave - nPlayed(trumpSuit);
		int firstOpponent = computerPlayer.getPlayerNum()%2 == 0? 1:0;
		boolean bothOpponentsOut = !hasTrumpLeft[firstOpponent] && !hasTrumpLeft[firstOpponent+2];
		return trumpIhave > 1 && trumpLeft > 0 && !bothOpponentsOut;
	}
}
