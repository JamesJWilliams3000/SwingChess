package Pieces;

import Board.*;

public class Rook extends SlidingPiece
{
	public Rook(Player player, int position)
	{
		super(player, position, new Direction[]{Direction.NORTH, Direction.WEST, Direction.EAST, Direction.SOUTH});
	}

	@Override
	public int getScore()
	{
		return 500;
	}

	@Override
	public String toString()
	{
		return "R";
	}
}
