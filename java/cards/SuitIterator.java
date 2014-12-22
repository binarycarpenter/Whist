package com.appspot.whist.cards;

import java.util.Iterator;
import java.util.List;

import com.appspot.whist.game.Utils;

/**
 * 
 * Iterates over a list of cards that in this context will be of a single suit, 
 * and ordered from lowest (starting at 2) to highest (ending at ACE). 
 * @author Greg Harris
 */
public class SuitIterator implements Iterator<Card>
{
	protected List<Card> cards;
	protected boolean isHigh;
	protected int cardsSeen;
	protected int index;
	protected boolean seenFirst;
	
	/**
	 * Constructs an Iterator over a list of cards. The Iterator will begin with 
	 * the best card (as determined by the given isHigh value), and return cards until
	 * reaching the worst card of the suit 
	 * @param cards The cards to iterate over
	 * @param isHigh Whether the 
	 */
	public SuitIterator(List<Card> cards, boolean isHigh)
	{
		this.isHigh = isHigh;
		this.cards = cards;
		cardsSeen = 0;
		seenFirst = false;
	}
	
	public void restart()
	{
		cardsSeen = 0;
		seenFirst = false;
	}
	
	public void reverse()
	{
		isHigh = !isHigh;
		restart();
	}
	
	public boolean hasNext() 
	{
		return cardsSeen < cards.size();
	}

	public Card next()
	{
		if(Utils.isEmptySafe(cards)) return null;
		
		int sz = cards.size();
		
		if(!seenFirst) 
		{
			seenFirst = true;
			index = isHigh? sz-1 : 0; 
			Card lastCard = cards.get(sz-1);
			if(lastCard.getRank() == Rank.ACE)
			{
				cardsSeen++;
				if(isHigh)
				{
					index--;
				}
				return lastCard;
			}	
		}
		cardsSeen++;
		Card ret = cards.get(index);
		index += (isHigh? -1 : 1);
		return ret;
	}

	/** return the worst Card in the list being iterated */
	public Card getWorst() {
		restart();
		Card worst = null;
		while(hasNext())
		{
			worst = next();
		}
		return worst;
	}
	
	/** return the best Card in the list being iterated - i.e. the first */
	public Card getBest()
	{
		restart();
		return next();
	}

	public void remove() {
		// TODO Auto-generated method stub
		System.out.println("HitIterator.remove() is not implemented. But somebody is calling it. Please investigate");
	}
}
