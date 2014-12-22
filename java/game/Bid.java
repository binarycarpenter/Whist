package com.appspot.whist.game;

public class Bid {

	private int playerNum; 
	private int bid;
	
	public Bid(int playerNum, int bid) {
		this.playerNum = playerNum;
		this.bid = bid;
	}
	
	public int getPlayerNum() { return playerNum; }
	public int getVal()       { return bid;       }
}

