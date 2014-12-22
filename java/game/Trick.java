package com.appspot.whist.game;

import java.util.ArrayList;
import java.util.List;

import com.appspot.whist.cards.Card;
import com.appspot.whist.cards.Suit;
import com.appspot.whist.game.HandScenario.PlayedCard;

public class Trick 
{
	/** The cards played so far in the current trick */
	private List<PlayedCard> cardsPlayed;
	
	/** The suit that was lead in the current trick */
	private Suit suitLead;
	
	/** The card leading the current trick */
	private PlayedCard winningCard;
	
	/** whether the trick has been trumped (always false if trump was led */
	private boolean wasTrumped;
	
	/** The trump suit */
	private Trump trump;
	
	public Trick(Trump trump)
	{
		cardsPlayed = new ArrayList<PlayedCard>();
		wasTrumped = false;
		winningCard = null;
		this.trump = trump;
	}
	
	public void cardPlayed(PlayedCard card)
	{
		if(cardsPlayed.isEmpty())
		{
			suitLead = card.getSuit();
			winningCard = card;
		}
		// if the played card is trump, and trump was not led
		else if(card.getSuit().equals(trump.getSuit()) && 
				!suitLead.equals(trump.getSuit()))
		{
			wasTrumped = true;
		}
		
		cardsPlayed.add(card);
		if(card.beats(winningCard, trump))
		{
			winningCard = card;
		}
	}
	
	public boolean wasTrumped()
	{
		return wasTrumped;
	}
	
	public int getWinningPlayerNum()
	{
		return winningCard.playerNum;
	}
	
	public PlayedCard getWinningCard()
	{
		return winningCard;
	}

	public int getNumCardsPlayed() 
	{
		return cardsPlayed.size();
	}
	
	public Suit getLeadSuit()
	{
		return suitLead;
	}
}
