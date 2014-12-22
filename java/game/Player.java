package com.appspot.whist.game;

import java.util.Collection;
import java.util.List;

import com.appspot.whist.cards.Card;
import com.appspot.whist.cards.Suit;
import com.appspot.whist.cards.SuitIterator;
import com.appspot.whist.cards.Card.Worth;

@SuppressWarnings("unchecked")
public abstract class Player {

	protected Hand hand;
	protected int playerNum;
	protected String name;
	
	protected Player(int playerNum, String name) 
	{
		hand = new Hand();
		this.playerNum = playerNum;
		this.name = name;
	}
	
	public void giveCard(Card newCard) 
	{
		int ind;
		List<Card> cards = hand.getSuitCards(newCard.getSuit());
		int size = cards.size();
		for(ind = 0; ind < size && newCard.isHigher(cards.get(ind)); ind++)
			;
		cards.add(ind, newCard);
	}
	
	public void giveCards(Collection<Card> cards)
	{
		for(Card card: cards)
		{
			giveCard(card);
		}
	}
	
	public void removeCard(Card toRemove) 
	{
		hand.removeCard(toRemove);
	}
	
	public void removeCards(Collection<Card> toRemove)
	{
		for(Card card : toRemove)
		{
			removeCard(card);
		}
	}
	
	public boolean isPartner(Player player)
	{
		return isPartner(player.getPlayerNum());
	}
	
	public boolean isPartner(int partnerNum) {
		//partners have playerNums with the same even-odd-ness, 
		//therefore their sum is an even number
		return (partnerNum + playerNum) % 2 == 0;
	}
	
	public List<Card> getSuitCards(Suit suit)
	{
		return hand.getSuitCards(suit);
	}
	
	protected abstract void makeBid(List<Bid> bids, WhistPrinter wp);
	
	public void takeKitty(List<Card> kitty) 
	{
		giveCards(kitty);
	}
	
	/**
	 * After taking the kitty, choose which cards to discard from your hand,
	 * then call trump.
	 * Implemented by AI to algorithmically make these choices
	 * Implemented by Human to prompt user for their decisions
	 * @return
	 */
	protected abstract Trump discardAndCall();
	
	/** 
	 *  Play a card! 
	 *  Implemented by AI to choose the proper card to play
	 *  Implemented by Human to prompt user for their card choice
	 */
	protected abstract Card playCard(HandScenario hs, Trick trick);
	
	@Override 
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof Player))
		{
			return false; 
		}
		Player other = (Player)obj;
		return other.getPlayerNum() == this.getPlayerNum();
	}
	
	public String getName() { return name; }
	public int getPlayerNum() { return playerNum; }
	public Hand getHand() { return hand; }

	public void clear()
	{
		hand.clear();
	}

	public void initHandWorth(boolean isHigh)
	{
		for(List<Card> lSuitCards: hand.handBySuits)
		{
			int bestRank = 1;
			int losersNeeded = 0;
			int lastRank = 0;
			int numSuit = lSuitCards.size();
			SuitIterator handIt = new SuitIterator(lSuitCards, isHigh);
			while(handIt.hasNext()) {
				Card card = handIt.next();
				card.setWorth(Worth.NOTHING); // initialize
				int rankSpot = card.rankSpot(isHigh);
				
				if(rankSpot == bestRank)
				{
					card.setWorth(Worth.AUTO_WINNER);
					bestRank++;
				}
				else if(rankSpot <= numSuit && rankSpot <= 4) 
				{
					card.setWorth(Worth.MAYBE_WINNER);
					losersNeeded += (rankSpot - (lastRank + 1));
				}
				lastRank = rankSpot;
			}
			handIt.restart();
			while(handIt.hasNext() && losersNeeded > 0)
			{
				Card card = handIt.next();
				if(card.isNothing())
				{
					card.setWorth(Worth.BAD_BUT_USEFUL);
					losersNeeded--;
				}
			}	
		}
	}
}
