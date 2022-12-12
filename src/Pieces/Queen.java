package Pieces;

import Board.Direction;
import Board.Player;

public class Queen extends SlidingPiece
{
	public Queen(Player player, int position)
	{
		super(player, position, Direction.values());
	}

	@Override
	public int getScore()
	{
		return 900;
	}

	@Override
	public String toString()
	{
		return "Q";
	}
}
