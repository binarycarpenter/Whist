package com.appspot.whist.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.appspot.whist.game.Player;

public class Dealer {
	
	private static Deck deck;

	private Dealer() {}
	
	private static List<Card> seen;
	
	public static void deal(Player[] players, List<Card> kitty) {
		deck = new Deck();
		seen = new ArrayList<Card>();
		
		
		//give 12 cards to each of the 4 players, 4 cards are left for the kitty
		for(int len = 52; len > 4; len--) 
		{
			players[len % 4].giveCard(deck.getRandomCard());	
		}
		
		//put the last 4 in the kitty
		for(int i = 0; i < 4; i++) 
		{
			kitty.add(deck.getRandomCard());
		}
	}
}
