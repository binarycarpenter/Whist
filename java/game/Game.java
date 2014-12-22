package com.appspot.whist.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.appspot.whist.cards.Card;
import com.appspot.whist.cards.Dealer;
import com.appspot.whist.cards.Suit;
import com.appspot.whist.game.HandScenario.PlayedCard;

public class Game {
	
	private static final int NUM_PLAYERS = 4;
	private static final int SEC_INTERVAL = 10;
	
	private Player[] players = new Player[NUM_PLAYERS];
	private List<Card> kitty = new ArrayList<Card>();
	
	private int scores[] = new int[2];
	private int tricks[] = new int[2];
	private final static int PLAY_TO = 21;
	
	private Random rand = new Random();
	private Player dealer;
	
	private Bid winningBid;
	private Player winningPlayer;
	private Trump trump;
	private WhistPrinter wp;
	
	/** start the game! */
	public static void main(String[] args) 
	{
		new Game();
	}
	
	/** play hands until someone scores enough to win the game */
	public Game() 
	{
		wp = new WhistPrinter();
		
		// TODO: this sets up 4 computer players, we'll need to figure out here how many humans are playing
		for(int nPlayerNum : Utils.from0to3()) 
		{
			players[nPlayerNum] = new ComputerPlayer(nPlayerNum, "Player " + (nPlayerNum+1));
		}
		dealer = players[rand.nextInt(NUM_PLAYERS)];
		
		//while(scores[0] < PLAY_TO && scores[1] < PLAY_TO) {
			playHand();
		//}
		doWinner();
	}
	
	/** play a hand - deal, bid, play the hand, update scores */
	private void playHand() 
	{
		deal();
		bid();
		play();
		updateScores();
	}
	
	/** give each player 12 cards, and put 4 in the kitty */
	private void deal() 
	{
		kitty.clear();
		Dealer.deal(players, kitty);
		wp.displayHands(players);
	}
	
	private void bid() {
		
		//get the bids
		List<Bid> bids = new ArrayList<Bid>();
		for(Player player : playersInBidOrder()) 
		{
			// players need to see previous bids, will then add theirs to the list
			player.makeBid(bids, wp);
		}
		
		//dealer sets the last bid to -1 when choosing to redeal
		int lastBid = bids.get(3).getVal();
		if(lastBid < 0) // no winning bid, redeal with the same dealer
		{
			wp.reDeal();
			for(Player p: players)
			{
				p.clear();
			}
			deal();
			bid();
		}
		else // there was a winning bid
		{
			// find the value of the winning bid, and the Player who bid it
			int maxBid = 0;
			for(Bid bid: bids) {
				int val = bid.getVal();
				if(val >= maxBid) 
				{
					winningPlayer = players[bid.getPlayerNum()];
					winningBid = bid;
					maxBid = val;
				}
			}
			
			// the winning player takes the kitty
			winningPlayer.takeKitty(kitty);
			//stall(SEC_INTERVAL);
			wp.displayHands(players);
			//stall(SEC_INTERVAL);
			
			// the winning player has to discard and call trump
			trump = winningPlayer.discardAndCall();
			for(Player player: players)
			{
				player.initHandWorth(trump.isHigh());
			}
			wp.printLines(3);
			wp.displayHands(players);
			wp.printTrump(trump);
			rotateDealer();
		}
	}
	
	private void rotateDealer() 
	{
		dealer = playerAfter(dealer);
	}
	
	private Player playerAfter(Player p) 
	{
		return players[(p.getPlayerNum() + 1) % NUM_PLAYERS];
	}
	
	private void play() 
	{
		tricks[0] = 0; tricks[1] = 0;
		Player goesFirst = players[winningBid.getPlayerNum()];
		HandScenario hs = new HandScenario(trump, goesFirst);

		for(int trick = 0; trick < 12; trick++) 
		{
			Player winner = playTrick(goesFirst, hs);
			tricks[winner.getPlayerNum() % 2]++;
			goesFirst = winner;
			hs.update();
		}
	}
	
	
	
	/**
	 * plays a trick, starting with the given player who goes first
	 * returns the player number of the winner of the trick
	 * @return
	 */
	private Player playTrick(Player goesFirst, HandScenario hs) 
	{
		Trick trick = new Trick(hs.getTrump());
		for(Player player: allPlayersStartingWith(goesFirst))
		{
			Card played = player.playCard(hs, trick);
			trick.cardPlayed(new PlayedCard(played, player.playerNum));
			wp.cardPlayed(played, player.playerNum);
		}
		wp.trickWon(trick.getWinningCard());
		wp.printLines(3);
		wp.displayHands(players);
		return players[trick.getWinningPlayerNum()];
	}
	
	private void updateScores() {
		int offense = winningBid.getPlayerNum() % 2;
		int defense = offense == 0? 1 : 0;
		int bid = winningBid.getVal();
		int winner;
		int points = bid;
		
		
		if(bid + tricks[defense] >= 8) {
			winner = defense;
			
			int extras = (bid + tricks[defense]) - 9; 
			points += extras;
		}
		else {
			winner = offense;
			if(tricks[defense] == 0) {
				points = 7;
			}
		}
		if(trump.isNoTrump()) {
			points *= 2;
		}
		
		scores[winner] += points;
		wp.updateScores(scores);
	}
	
	private void doWinner() {
		
	}
	
	private void stall(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private List<Player> allPlayersStartingWith(Player goesFirst)
	{
		List<Player> lPlayers = new ArrayList<Player>();
		int playersAdded = 0;
		for(Player curPlayer = goesFirst; playersAdded < NUM_PLAYERS; 
		    playersAdded++, curPlayer = playerAfter(curPlayer))
		{
			lPlayers.add(curPlayer);
		}
		return lPlayers;
	}
	
	private List<Player> playersInBidOrder()
	{
		return allPlayersStartingWith(playerAfter(dealer));
	}
}
