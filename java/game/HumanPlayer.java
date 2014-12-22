package com.appspot.whist.game;

import java.util.List;

import com.appspot.whist.cards.Card;
import com.appspot.whist.cards.Suit;

public class HumanPlayer extends Player {

	protected HumanPlayer(int playerNum, String name) {
		super(playerNum, name);
	}

	@Override
	protected Trump discardAndCall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void makeBid(List<Bid> bids, WhistPrinter wp) {
		
	}
	
	

	@Override
	protected Card playCard(HandScenario hs, Trick trick) {
		// TODO Auto-generated method stub
		return null;
	}
}
