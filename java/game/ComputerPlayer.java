package com.appspot.whist.game;

import java.util.ArrayList;
import java.util.List;

import sun.security.action.GetLongAction;

import com.appspot.whist.cards.Card;
import com.appspot.whist.cards.HandIterator;
import com.appspot.whist.cards.Suit;
import com.appspot.whist.cards.SuitIterator;
import com.appspot.whist.cards.Card.Worth;
import com.google.appengine.repackaged.com.google.common.base.Pair;

public class ComputerPlayer extends Player {
	
	private final static int PARTNER_TRICKS = 2;
	private final static int KITTY_TRICKS = 1;
	
	private HandScenario hs;
	private Trick trick;
	
	protected ComputerPlayer(int playerNum, String name) 
	{
		super(playerNum, name);
	}

	@Override
	protected void makeBid(List<Bid> bids, WhistPrinter wp) {
		int idealBid = getBid();
		int numBids = bids.size();
		Bid maxBid  = maxBid(bids); 
		int maxVal  = maxBid == null? 0 : maxBid.getVal();
		Bid bid = null;
		
		//if I'm the dealer...
		if(numBids == 3) {
			//if we don't have a good enough bid to play...
			if(maxVal < 4) { 
				//if my hand is good enough to play, take it
				if(idealBid >= 4)
				{
					bid = new Bid(playerNum, idealBid);
				}
				//if the bid's at 3, and it's my partner's, go for a 4
			    else if(idealBid == 3 && partnerBid3(bids)) {
					bid = new Bid(playerNum, 4);
				}
				//otherwise, fold the hand
				else {
					bid = new Bid(playerNum, -1); // folding signal
				}
			}
			
			else if(idealBid >= maxVal)
			{
				bid = new Bid(playerNum, idealBid);
			}
			//make my bid if it's as good or equal, and 
			else if(idealBid == maxVal-1 && partnerBid3(bids)) {
				bid = new Bid(playerNum, idealBid+1);
			}
			
			//my bid isn't as good as someone's playable bid, just pass
			else bid = new Bid(playerNum, 0);
		} 
		//I'm not the dealer...
		//never make a bid lower than 3
		else if(idealBid < 3) {
			bid = new Bid(playerNum, 0);
		}
		//make a bid if our bid is better than the winning
		else if(idealBid > maxVal) {
			bid = new Bid(playerNum, idealBid);
		}
		else {
			bid = new Bid(playerNum, 0);
		}
		bids.add(bid);
		wp.showBid(name, bid.getVal());
	}
	
	private Bid maxBid(List<Bid> bids) {
		Bid maxBid = null;
		int maxVal = 0;
		for(Bid bid: bids) {
			int val = bid.getVal();
			if(val > maxVal) {
				maxBid = bid;
				maxVal = val;
			}
		}
		return maxBid;
	}
	
	private boolean partnerBid3(List<Bid> bids) {
		for(Bid bid: bids) {
			if(bid.getVal() == 3 && isPartner(bid.getPlayerNum())) {
				return true;
			}
		}
		return false;
	}
	
	private int getBid() {
		HandInfo info = calcHandInfo();
		int tricks = info.getTricks();
		int lostTricks = 12 - tricks;
		int bid = 7 - lostTricks;
		
		if(bid == 6) bid = 5;
		else if(bid > 6 && bid < 10) bid = 6;
		else if(bid > 10) bid = 7;
		return bid;
	}
	
	private HandInfo calcHandInfo() 
	{
		int voids = 0;
		int trumpSuits = 0;
		int hiWinners = 0, loWinners = 0;
		int mostTrump = 0;
		List<Suit> lTrumpSuit = new ArrayList<Suit>();
		
		for(List<Card> suitCards: hand.getHandBySuits()) {
			int numSuit = suitCards.size();
			if(numSuit == 0) {
				voids++;
				continue;
			}
			
			Suit suit = suitCards.get(0).getSuit();
			if(numSuit >= 5) {
				if(mostTrump <= numSuit)
				{
					mostTrump = numSuit;
					lTrumpSuit.add(suit);
				}
				hiWinners += numSuit - 4;
				loWinners += numSuit - 4;
				trumpSuits++;
			}
						
			for(boolean isHigh = true; true; isHigh = !isHigh){
				if(virtualVoid(suitCards))
				{
					if(isHigh) hiWinners++;
					else loWinners++;
				}
				
				int bestRank = 1;
				int losersNeeded = 0;
				int lastRank = 0;
				SuitIterator handIt = new SuitIterator(suitCards, isHigh);
				while(handIt.hasNext()) {
					Card card = handIt.next();
					card.setWorth(Worth.NOTHING); // initialize
					int rankSpot = card.rankSpot(isHigh);
					
					if(rankSpot == bestRank)
					{
						card.setWorth(Worth.AUTO_WINNER);
						bestRank++;
						if(isHigh) hiWinners++; 
						else loWinners++;
					}
					else if(rankSpot <= numSuit && rankSpot <= 4) 
					{
						card.setWorth(Worth.MAYBE_WINNER);
						if(isHigh) hiWinners++; 
						else loWinners++;
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
				
				if(!isHigh) break;
			}
		}
		
		boolean isHigh = hiWinners != loWinners? hiWinners > loWinners : totalHandRank();
		Suit trumpSuit = null;
		switch(lTrumpSuit.size())
		{
			case 1: 
			{
				trumpSuit = lTrumpSuit.get(0); break;
			}
			
			case 2: 
			{
				trumpSuit = getTotalRank(hand.getSuitCards(lTrumpSuit.get(0)), isHigh) >
							getTotalRank(hand.getSuitCards(lTrumpSuit.get(1)), isHigh) ? 
									lTrumpSuit.get(1) : lTrumpSuit.get(0); break;
			}
			case 3: 
			{
				int suit1 = getTotalRank(hand.getSuitCards(lTrumpSuit.get(0)), isHigh);
				int suit2 = getTotalRank(hand.getSuitCards(lTrumpSuit.get(1)), isHigh);
				int suit3 = getTotalRank(hand.getSuitCards(lTrumpSuit.get(2)), isHigh);
				trumpSuit = suit1 < suit2? (suit1 < suit3? lTrumpSuit.get(0) : lTrumpSuit.get(2)) : 
				                   (suit2 < suit3? lTrumpSuit.get(1) : lTrumpSuit.get(2)); 
			}
		}
		return new HandInfo(hiWinners, loWinners, voids, trumpSuits, trumpSuit, isHigh);
	}
	
	private boolean totalHandRank()
	{
		return handRank(hand.getHandBySuits(), true) < handRank(hand.getHandBySuits(), false);
	}
	
	private int handRank(List<Card>[] suitHands, boolean isHigh)
	{
		int rank = 0;
		for(List<Card> suitCards: suitHands)
		{
			rank += getTotalRank(suitCards, isHigh);
		}
		return rank;
	}
	
	private boolean virtualVoid(List<Card> suitCards)
	{
		for(Card card: suitCards)
		{
			if(!card.isAutoWinner())
			{
				return false;
			}
		}
		return true;
	}
	
	private class HandInfo {
		public final int hiWinners, loWinners, voids, trumpSuits;
		public final Suit trumpSuit;
		public final boolean isHigh;
		public HandInfo(int hiWinners, int loWinners, int voids, int trumpSuits, Suit trump, boolean isHigh) {
			this.hiWinners  = hiWinners;
			this.loWinners  = loWinners;
			this.voids      = voids;
			this.trumpSuits = trumpSuits;
			this.trumpSuit  = trump;
			this.isHigh     = isHigh;
		}
		
		public int getTricks() {
			return Math.max(hiWinners, loWinners) + trumpSuits + voids + PARTNER_TRICKS + KITTY_TRICKS;
		}
	}
	
	@Override
	protected Trump discardAndCall() {
		HandInfo info = calcHandInfo();
		boolean isHigh = info.isHigh;
	
		int nToDiscard = 4;
		while(nToDiscard > 0)
		{
			int discarded = removeUselessSuit(nToDiscard);
			if(discarded > 0)
			{
				nToDiscard -= discarded;
			}
			else
			{
				break;
			}
		}

		HandIterator hit = new HandIterator(hand.getHandBySuits());
		while(hit.hasNext() && nToDiscard > 0)
		{
			List<Card> suitCards = hit.next();
			if(suitCards.isEmpty() || info.trumpSuit.equals(suitCards.get(0).getSuit())) continue;
			nToDiscard -= removeNonWinners(suitCards, nToDiscard);
		}

		for(Worth worth : Worth.values())
		{
			while(nToDiscard > 0)
			{
				if(removeCardByWorth(isHigh, info.trumpSuit, worth))
				{
					nToDiscard--;
				}
				else break;
			}
		}

		return getTrump(isHigh); 
	}
	
	private int removeNonWinners(List<Card> suitCards, int nToDiscard) {
		List<Card> toRemove = new ArrayList<Card>();
		for(Card card : suitCards)
		{
			if(!card.isAutoWinner()) toRemove.add(card);
		}
		int nToRemove = toRemove.size();
		if(nToRemove <= nToDiscard) 
		{
			removeCards(toRemove);
			return nToRemove;
		}
		else return 0;
	}

	private boolean removeCardByWorth(boolean isHigh, Suit trump, Worth worth) {
		for(List<Card> suitCards: hand.getHandBySuits())
		{
			SuitIterator sit = new SuitIterator(suitCards, !isHigh);
			while(sit.hasNext())
			{
				Card card = sit.next();
				if(card.getSuit().equals(trump)) break;
				if(card.getWorth().equals(worth))
				{
					removeCard(card);
					return true;
				}
			}
		}
		return false;
	}

	/** 
	 * This is used to discard a useless suit after taking the kitty 
	 * returns the number of cards in the useless suit, or -1 if there 
	 * is no suit that can be totally removed. 
	 */
	private int removeUselessSuit(int nToDiscard) {
		for(List<Card> cards: hand.getHandBySuits())
		{
			int nCards = cards.size();
			if(nCards > nToDiscard) continue;
			
			boolean allUseless = nCards > 0;
			int nCardsToToss = 0;
			for(Card card : cards) 
			{
				if(!card.isNothing())
				{
					allUseless = false;
					break;
				}
				else
				{
					nCardsToToss++;
				}
			}
			if(allUseless)
			{
				cards.clear();
				return nCards;
			}
		}
		return -1;
	}
	
	private Trump getTrump(boolean isHigh)
	{
		List<Suit> mostTrumpSuit = new ArrayList<Suit>();
		int mostTrump = 0;
		for(Suit suit: Suit.values())
		{
			int sz = hand.getSuitCards(suit).size();
			if(sz > mostTrump)
			{
				mostTrump = sz;
				mostTrumpSuit.clear();
				mostTrumpSuit.add(suit);
			}
			else if(sz == mostTrump) 
			{
				mostTrumpSuit.add(suit);
			}
		}
		int sz = mostTrumpSuit.size();
		if(sz == 1) return new Trump(mostTrumpSuit.get(0), isHigh);
		
		int lowRank = Integer.MAX_VALUE;
		Suit trump = null;
		for(Suit suit: mostTrumpSuit)
		{
			int rank = getTotalRank(hand.getSuitCards(suit), isHigh);
			if(rank < lowRank) 
			{
				lowRank = rank;
				trump = suit;
			}
		}
		return new Trump(trump, isHigh);
	}
	
	private int getTotalRank(List<Card> cards, boolean isHigh)
	{
		int totalRank = 0;
		for(Card card: cards) 
		{
			totalRank += card.rankSpot(isHigh);
		}
		return totalRank;
	}
	
	@Override
	/** return a Card to play */
	protected Card playCard(HandScenario hs, Trick trick) {
		Card toPlay = null;
		int cardsPlayed = trick.getNumCardsPlayed();
		this.hs = hs;
		this.trick = trick;
		switch(cardsPlayed)
		{
			case 0: toPlay = playFirstCard(); break;
			case 1: toPlay = playSecondCard(); break;
			case 2: toPlay = playThirdCard(); break;
			case 3: toPlay = playLastCard(); break;
			default: System.out.println("toPlay is null because trickCardsPlayed() returned " + cardsPlayed);
		}
		return toPlay;
	}

	/** return a Card to play when leading off the trick */
	private Card playFirstCard() {
		Card toPlay = null; // the card to play
		
		// if I am on the team that did no take the kitty...
		if(hs.amDefending(this))
		{
			// play a winner if you have one, but don't draw trump if we're defending
			toPlay = getNonTrumpWinner();
			if(toPlay == null)
			{   // don't have a winner...
				// try to draw out cards to make a possible winner a future winner
				toPlay = getDrawoutNonTrumpWinner();
				if(toPlay == null)
				{   // don't have any possible winners to try to make into winners
					// TODO: make this decision more intelligent
					toPlay = getWorstCard();
				}
			}
		}
		
		else // I am on offense
		{
			// if I should draw trump...
			if(hs.shouldDrawTrump(this))
			{
				toPlay = getLeadingTrumpCard();
			}
			// I should play a non-trump suit
			else
			{
				// play a winner if you have one
				toPlay = getNonTrumpWinner();
				if(toPlay == null)
				{   // I don't have a winner, try to draw out other winners
					toPlay = getDrawoutNonTrumpWinner();
					if(toPlay == null)
					{
						// I don't any possible winners, just throw a bad card away
						toPlay = getWorstCard();
					}
				}
			}
		}
		return toPlay;
	}

	/** return a Card to play when one card has been played so far */
	private Card playSecondCard() {
		Card toPlay = null;               // the card to play
		Suit leadSuit = trick.getLeadSuit(); // the suit that was lead 
		// the cards I hold for the led suit
		List<Card> leadSuitCards = hand.getSuitCards(leadSuit); 
		
		// if I have a card of the suit that was lead, I must play that suit...
		if(leadSuitCards.size() > 0)
		{
			// do I have the winner in this suit?
			toPlay = hand.getCardBySuitAndWorth(Worth.AUTO_WINNER, leadSuit);
			if(toPlay == null)
			{
				// don't have a winner, play second man low, get rid of a bad card 
				toPlay = getWorstSuitCard(leadSuit);
			}
		}
		
		// I can't follow suit, I can play any suit I want...
		else 
		{
			// if I'm on the team that didn't take the kitty...
			if(hs.amDefending(this))
			{
				Suit trump = hs.getTrumpSuit();
				// if there's no trump...
				if(trump == null)
				{
					// we can't follow suit and there's no trump, just waste our worst card
					toPlay = getWorstCard();
				}
				// there is a trump suit...
				else
				{
					// the trump cards I hold
					List<Card> trumpCards = hand.getSuitCards(trump);
					// if I have any trump...
					if(trumpCards.size() > 0)
					{
						// only play the trump if it's not guaranteed to win on its own 
						toPlay = getNonWinnerTrump();
						// if I don't have a non-winner trump card...
						if(toPlay == null)
						{
							// just waste a bad card 
							toPlay = getWorstCard();
						}
					}
					else // no trump, just waste a bad card
					{
						toPlay = getWorstCard();
					}
				}
			}
			else // I am on offense
			{
				Suit trump = hs.getTrumpSuit();
				List<Card> trumpCards = hand.getSuitCards(trump);
				
				// for now just always trump on offense
				// TODO: need better intelligence when deciding when to trump
				if(trumpCards.size() > 0)
				{
					toPlay = getWorstTrump();
				}
				else
				{
					toPlay = getWorstCard();
				}
			}
		}
		return toPlay;
	}

	/** return a Card to play when two cards have been played so far */
	private Card playThirdCard() {
		Card toPlay = null;               // the card to play
		Suit leadSuit = trick.getLeadSuit(); // the suit that was lead 
		// the cards I hold for the led suit
		List<Card> leadSuitCards = hand.getSuitCards(leadSuit); 
		boolean haveLeadSuit = !Utils.isEmptySafe(leadSuitCards);
		boolean wasTrumped = trick.wasTrumped();
		
		if(!wasTrumped)
		{
			// if the trick isn't trumped, and I can beat the leader, do so - unless my partner is winning
			// and my card only ranks one better - i.e. don't beat your partner's king with an ace.
			Card best = getBestSuitCard(leadSuit);
			if(best.beats(trick.getWinningCard(), hs.getTrump()) &&
			   !(isPartner(trick.getWinningPlayerNum()) 
					   && best.rankSpot(hs.isHigh()) - 1 != trick.getWinningCard().rankSpot(hs.isHigh())))
			{
				toPlay = best;
			}
			else
			{
				toPlay = getWorstSuitCard(leadSuit);
				if(toPlay == null) // we don't have to follow suit
				{
					// try to trump if we have one and our partner isn't winning
					Card worstTrump = getWorstTrump();
					if(worstTrump != null && !isPartner(trick.getWinningPlayerNum()))
					{
						toPlay = worstTrump;
					}
					else // just waste a bad card
					{
						toPlay = getWorstCard();
					}
				}
			}
		}
		else // this trick has been trumped
		{
			if(haveLeadSuit) // we have to follow suit, just waste the worst card
			{
				toPlay = getWorstSuitCard(leadSuit);
			}
			else // we can trump
			{
				toPlay = getWorstWinner(trick.getWinningCard()); // beat the winning trump with your lowest trump
				if(toPlay == null) // we couldn't beat their trump, just waste the worst card
				{
					toPlay = getWorstCard();
				}
			}
		}
		return toPlay;
	}
	
	/** return a Card to play when three cards have been played so far */
	private Card playLastCard() 
	{
		Card toPlay = null;
		Suit leadSuit = trick.getLeadSuit(); // the suit that was lead 
		// the cards I hold for the led suit
		List<Card> leadSuitCards = hand.getSuitCards(leadSuit); 
		boolean haveLeadSuit = !Utils.isEmptySafe(leadSuitCards);
		boolean wasTrumped = trick.wasTrumped();
		
		// if my partner's winning, 
		if(isPartner(trick.getWinningPlayerNum()))
		{
			if(haveLeadSuit)
			{
				toPlay = getWorstSuitCard(leadSuit);
			}
			else
			{
				toPlay = getWorstCard();
			}
		}
		else if(wasTrumped) // was trumped by an opponent
		{
			if(haveLeadSuit) // throw away useless card in trumped suit
			{
				toPlay = getWorstSuitCard(leadSuit);
			}
			else
			{
				toPlay = getWorstWinner(trick.getWinningCard()); // get the worst card that beats the trump
				if(toPlay == null) // can't win, just waste the worst card
				{
					toPlay = getWorstCard();
				}
			}
		}
		else // opponent leads, but trick isn't trumped
		{
			if(haveLeadSuit) // if I have to follow suit, try to win
			{
				toPlay = getWorstWinner(trick.getWinningCard());
				if(toPlay == null) // can't win, waste bad card
				{
					toPlay = getWorstSuitCard(leadSuit);
				}
			}
			else // I don't have to follow suit, try to trump
			{
				toPlay = getWorstTrump();
				if(toPlay == null) // can't win with trump, just waste a bad card
				{
					toPlay = getWorstCard();
				}
			}
		}
		return toPlay;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/*********************************HELPER FUNCTIONS**********************************/
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Is this card trump? */
	private boolean isTrump(Card card)
	{
		return card.getSuit().equals(hs.getTrumpSuit());
	}
	/**
	 *  Play either the worst or best card in the given list of Cards of a single suit.
	 */
	private Card getSuitCardDirection(Suit suit, boolean getWorst) 
	{
		List<Card> lSuitCards = hand.getSuitCards(suit);
		if(Utils.isEmptySafe(lSuitCards)) return null;
		SuitIterator sit = new SuitIterator(lSuitCards, hs.isHigh());
		return getWorst? sit.getWorst() : sit.getBest();
	}
	
	/**
	 * Get the best card for the given player's hand in the given suit
	 */
	public Card getBestSuitCard(Suit suit)
	{
		return getSuitCardDirection(suit, false);
	}
	
	/**
	 * Get the worst card for the given player's hand in the given suit
	 */
	public Card getWorstSuitCard(Suit suit)
	{
		return getSuitCardDirection(suit, true);
	}
	
	/**
	 * Get the best card for drawing trump for the given player
	 */
	public Card getLeadingTrumpCard()
	{
		return getCardToLead(hs.getTrumpSuit());
	}
	
	private Card getCardToLead(Suit suit)
	{
		for(Worth worth: Utils.leadOrdering)
		{
			Card card = hand.getCardBySuitAndWorth(worth, suit);
			if(card != null) return card;
		}
		return null;
	}
	
	/** return a Card that won't win, but may draw out some winners to make my cards winners */
	private Card getDrawoutNonTrumpWinner() 
	{
		boolean isHigh = hs.isHigh();
		Card bestMaybeWinner = null;
		Card drawout = null;
		for(Pair<Card, Card> cardPair: hand.getDrawoutWinnerCardPairs(isHigh))
		{
			Card maybeWinner = cardPair.first;
			if(bestMaybeWinner == null || 
			   bestMaybeWinner.rankSpot(isHigh) < maybeWinner.rankSpot(isHigh) ||
			   bestMaybeWinner.rankSpot(isHigh) == maybeWinner.rankSpot(isHigh) && 
			   hand.howManyInSuit(maybeWinner.getSuit()) > hand.howManyInSuit(bestMaybeWinner.getSuit()))
			{
				bestMaybeWinner = maybeWinner;
				drawout = cardPair.second;
			}
		}
		return drawout;
	}
	
	/** return the worst Card in the entire hand, we're not going to win this trick */
	private Card getWorstCard() 
	{
		for(List<Card> cardsByType: hand.getWorstToBest(hs.isHigh()))
		{
			Card worstCard = null;
			for(Card card: cardsByType)
			{
				if(isTrump(card))
				{
					continue;
				}
				if(worstCard == null || 
				   hand.howManyInSuit(card.getSuit()) < hand.howManyInSuit(worstCard.getSuit()))
				{
					worstCard = card;
				}
			}
			if(worstCard != null)
			{
				return worstCard;
			}
		}
		return null; // will only get here if hand was empty - should never happen
	}
	
	/** return a winner whose suit is not trump, or null if none exists */
	private Card getNonTrumpWinner() 
	{
		for(Card winner: hand.getCardsByWorth(Worth.AUTO_WINNER, hs.isHigh()))
		{
			if(!isTrump(winner))
			{
				return winner;
			}			
		}
		return null;
	}

	/** return a card from the trump suit which is not a winner, or null if none exists */
	private Card getNonWinnerTrump() 
	{
		for(Card trump: hand.getSuitCards(hs.getTrumpSuit()))
		{
			if(!trump.isAutoWinner())
			{
				return trump;
			}
		}
		return null;
	}
	
	/** return the worst Card in the trump suit */
	private Card getWorstTrump()
	{
		return getWorstSuitCard(hs.getTrumpSuit());
	}
	
	/** return the best Card in the trump suit */
	private Card getBestTrump()
	{
		return getBestSuitCard(hs.getTrumpSuit());
	}
	
	/** return the worst card that will still beat the given winning card in its own suit */
	private Card getWorstWinner(Card leader)
	{
		Card lowestWinner = null;
		for(SuitIterator sit = new SuitIterator(hand.getSuitCards(leader.getSuit()), hs.isHigh()); sit.hasNext();)
		{
			Card card = sit.next();
			if(card.beats(leader, hs.getTrump()))
			{
				lowestWinner = card;
			}
		}
		return lowestWinner;
	}
}