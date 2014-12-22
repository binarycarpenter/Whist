package com.appspot.whist.cards;

import com.appspot.whist.game.Trump;

/**
 * 
 * @author Greg Harris
 *
 */
public class Card {

	private Suit suit;
	private Rank rank;
	private Worth worth;
	
	public enum Worth {
		NOTHING,
		BAD_BUT_USEFUL,
		MAYBE_WINNER,
		AUTO_WINNER
	}
	
	public Card(Suit suit, Rank hiRank) 
	{
		this.suit = suit;
		this.rank = hiRank;
		worth = Worth.NOTHING;
	}

	public Suit  getSuit()  { return suit;  }
	public Rank  getRank()  { return rank;  }
	public Worth getWorth() { return worth; }
	
	public boolean isAutoWinner() 
	{ 
		return worth.equals(Worth.AUTO_WINNER); 
	}
	
	public boolean isMaybeWinner()      
	{ 
		return worth.equals(Worth.MAYBE_WINNER); 
	}
	
	public boolean isBadButUseful()         
	{ 
		return worth.equals(Worth.BAD_BUT_USEFUL); 
	}
	
	public boolean isNothing()        
	{ 
		return worth.equals(Worth.NOTHING); 
	}
	
	public void setWorth(Worth worth)    { 
		this.worth = worth;
	}
	
	public String toShortString() { 
		switch(rank) {
		case ACE:   return "A";
		case TWO:   return "2";
		case THREE: return "3";
		case FOUR:  return "4";
		case FIVE:  return "5";
		case SIX:   return "6";
		case SEVEN: return "7";
		case EIGHT: return "8";
		case NINE:  return "9";
		case TEN:   return "10";
		case JACK:  return "J";
		case QUEEN: return "Q";
		case KING:  return "K";
		default:    return "Unknown";
		}
	}
	
	@Override public String toString()
	{
		return toShortString() + " of " + suit;
	}
	
	@Override public boolean equals(Object o) {
		if(!(o instanceof Card)) return false;
		
		Card card = (Card) o;
		return (card.rank == this.rank && card.suit == this.suit);
	}

	public boolean isHigher(Card card) {
		return this.rank.ordinal() > card.getRank().ordinal();
	}
	
	/**
	 * 
	 * @param isHigh
	 * @return the 'place' of the card's rank - best is 1, next is 2, etc...
	 */
	public int rankSpot(boolean isHigh) {
		if(isHigh) {
			int len = Rank.values().length;
			int ord = rank.ordinal();
			return len - ord;
		}
		else {
			return rank == Rank.ACE? 1 : rank.ordinal() + 2;
		}
	}

	/**
	 * whether this card beats the given card that was led, with the given trump
	 */
	public boolean beats(Card lead, Trump trump) {
		if(!lead.getSuit().equals(suit)) // didn't follow suit
		{
			return suit.equals(trump.getSuit()); // did will throw trump?
		}	
		else
		{
			boolean isHigh = trump.isHigh();
			return rankSpot(isHigh) < lead.rankSpot(isHigh);
		}
	}
}
