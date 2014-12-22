package com.appspot.whist.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.appspot.whist.cards.Card;
import com.appspot.whist.cards.HandIterator;
import com.appspot.whist.cards.Suit;
import com.appspot.whist.cards.SuitIterator;
import com.appspot.whist.cards.Card.Worth;
import com.google.appengine.repackaged.com.google.common.base.Pair;

public class Hand 
{
	List<Card>[] handBySuits;
	
	public Hand()
	{
		init();
	}
	
	public void init()
	{
		handBySuits = new List[4];
		for(int i : Utils.playerNums())
		{
			handBySuits[i] = new ArrayList<Card>();
		}
	}
	
	/** get all the cards, listed by suit */
	public List<Card>[] getHandBySuits()
	{
		return handBySuits;
	}
	/** get the list of cards for a given suit */
	public List<Card> getSuitCards(Suit suit)
	{
		return handBySuits[suit.ordinal()];
	}
	
	/** remove the given card from the hand */
	public void removeCard(Card toRemove)
	{
		boolean wasRemoved = handBySuits[toRemove.getSuit().ordinal()].remove(toRemove);
		if(!wasRemoved)
		{
			System.out.println("Tried to remove a card that wasn't in the hand: " + toRemove);
		}
	}
	
	public List<List<Card>> getWorstToBest(boolean isHigh)
	{
		List<List<Card>> llCards = new ArrayList<List<Card>>();
		llCards.add(getCardsByWorth(Worth.NOTHING, isHigh));
		llCards.add(getCardsByWorth(Worth.BAD_BUT_USEFUL, isHigh));
		llCards.add(getCardsByWorth(Worth.MAYBE_WINNER, isHigh));
		llCards.add(getCardsByWorth(Worth.AUTO_WINNER, isHigh));
		return llCards;
	}
	
	public List<List<Card>> getBestToWorst(boolean isHigh)
	{
		List<List<Card>> llCards = new ArrayList<List<Card>>();
		llCards.add(getCardsByWorth(Worth.AUTO_WINNER, isHigh));
		llCards.add(getCardsByWorth(Worth.MAYBE_WINNER, isHigh));
		llCards.add(getCardsByWorth(Worth.BAD_BUT_USEFUL, isHigh));
		llCards.add(getCardsByWorth(Worth.NOTHING, isHigh));
		return llCards;
	}
	
	public List<Card> getCardsByWorth(Worth worth, boolean isHigh)
	{
		List<Card> lResults = new ArrayList<Card>();
		for(List<Card> lSuitCards: handBySuits)
		{
			for(SuitIterator sit = new SuitIterator(lSuitCards, isHigh); sit.hasNext();)
			{
				Card card = sit.next();
				if(card.getWorth().equals(worth))
				{
					lResults.add(card);
				}
			}
		}
		return lResults;
	}

	public Card getCardBySuitAndWorth(Worth worth, Suit suit)
	{
		for(Card card: getSuitCards(suit))
		{
			if(card.getWorth().equals(worth))
			{
				return card;
			}
		}
		return null;
	}
	
	public int howManyInSuit(Suit suit) 
	{
		return getSuitCards(suit).size();
	}

	public List<Pair<Card, Card>> getDrawoutWinnerCardPairs(boolean high) 
	{
		List<Pair<Card, Card>> drawoutWinnerPairs = new ArrayList<Pair<Card, Card>>();
		for(List<Card> lSuitCards: handBySuits)
		{
			Card maybeWinner = null;
			Card drawout = null;
			for(Card card: lSuitCards)
			{
				if(maybeWinner == null && card.isMaybeWinner())
				{
					maybeWinner = card;
				}
				if(drawout == null && card.isBadButUseful())
				{
					drawout = card;
				}
			}
			if(maybeWinner != null && drawout != null)
			{
				drawoutWinnerPairs.add(new Pair<Card, Card>(maybeWinner, drawout));
			}
		}
		return drawoutWinnerPairs;
	}

	public void clear()
	{
		init();
	}
}
