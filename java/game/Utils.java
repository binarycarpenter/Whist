package com.appspot.whist.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.appspot.whist.cards.Card;
import com.appspot.whist.cards.Card.Worth;

public class Utils {

	public static Collection<Integer> fromXtoY(int x, int y)
	{
		Collection<Integer> collection = new ArrayList<Integer>();
		for(int i = x; i <= y; i++)
		{
			collection.add(i);
		}
		return collection;
	}
	
	public static Collection<Integer> from0to3()   { return fromXtoY(0, 3); }
	public static Collection<Integer> playerNums() { return fromXtoY(0, 3); }

	public static boolean isEmptySafe(Collection<?> c) 
	{
		return c == null || c.isEmpty();
	}
	
	public static final Worth[] leadOrdering = {Worth.AUTO_WINNER, 
												Worth.BAD_BUT_USEFUL,
												Worth.NOTHING,
												Worth.MAYBE_WINNER};
}
