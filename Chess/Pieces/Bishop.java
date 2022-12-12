package Pieces;

import Board.*;

public class Bishop extends SlidingPiece
{
	public Bishop(Player player, int position)
	{
		super(player, position,
				new Direction[]{Direction.NORTHWEST, Direction.NORTHEAST, Direction.SOUTHEAST, Direction.SOUTHWEST});
	}

	@Override
	public int getScore()
	{
		return 300;
	}

	@Override
	public String toString()
	{
		return "B";
	}
}
