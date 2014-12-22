package com.appspot.whist.cards;

public enum Suit {
	HEARTS, 
	SPADES, 
	DIAMONDS,
	CLUBS;
	
	public String toString(Suit suit) 
	{
		if(suit == null) return "No Trump";
		switch(suit)
		{
		case CLUBS: return "Clubs";
		case DIAMONDS: return "Diamonds";
		case HEARTS: return "Hearts";
		case SPADES: return "Spades";
		}
		return "Unknown";
	}
}
