package com.appspot.whist.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck
{
	private List<Card> cards = new ArrayList<Card>();
	private List<List<Card>> cardsBySuit = new ArrayList<List<Card>>();
	private static final Random rand = new Random();
	
	public Deck()
	{
		for(Suit suit: Suit.values()) {
			List<Card> lCardsBySuit = new ArrayList<Card>();
			for(Rank rank: Rank.values()) {
				Card card =  new Card(suit, rank);
				lCardsBySuit.add(card);
				cards.add(card);
			}
			cardsBySuit.add(lCardsBySuit);
		}
	}
	
	public Card getRandomCard()
	{
		return cards.remove(rand.nextInt(cards.size()));
	}
	
	public void removeCard(Card card)
	{
		cards.remove(card);
		for(List<Card> lCards: cardsBySuit)
		{
			lCards.remove(card);
		}
	}
}
