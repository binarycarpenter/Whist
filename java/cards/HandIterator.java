package com.appspot.whist.cards;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HandIterator implements Iterator<List<Card>> {

	private List<Card>[] handBySuits;
	private final static int NUM_SUITS = 4;
	private int suitIndex;
	private Set<Suit> suitsSeen;

	public HandIterator(List<Card>[] handBySuits)
	{
		initialize(handBySuits);
	}
	
	public void reset(List<Card>[] handBySuits)
	{
		initialize(handBySuits);
	}
	
	private void initialize(List<Card>[] handBySuits)
	{
		this.handBySuits = handBySuits;
		suitIndex = 0;
		suitsSeen = new HashSet<Suit>();
	}
	
	public boolean hasNext() {
		return suitsSeen.size() < NUM_SUITS;
	}

	public List<Card> next() {
		List<Card> ret = null;
		Suit nextSuit = null;
		int suitSize = Integer.MAX_VALUE;
		for(Suit suit : Suit.values())
		{
			if(suitsSeen.contains(suit)) continue;
			List<Card> suitCards = handBySuits[suit.ordinal()];
			if(suitCards.size() < suitSize)
			{
				suitSize = suitCards.size();
				nextSuit = suit;
				ret = suitCards;
				suitIndex = suit.ordinal();
			}
		}
		suitsSeen.add(nextSuit);
		return ret;
	}

	public void remove() {
		handBySuits[suitIndex].clear();
	}

}
