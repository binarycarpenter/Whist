package com.appspot.whist.game;

import com.appspot.whist.cards.Suit;

public class Trump {

	private boolean isHigh;
	private Suit suit;
	
	public Trump(Suit suit, boolean isHigh) {
		this.suit = suit;
		this.isHigh = isHigh;
	}
	
	public boolean isNoTrump() {
		return null == suit;
	}
	public Suit getSuit()   { return suit;   }
	public boolean isHigh() { return isHigh; }
}
