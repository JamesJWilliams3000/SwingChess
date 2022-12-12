package Pieces;

import Board.*;

public class Knight extends Piece
{
	public Knight(Player player, int position)
	{
		super(player, position);
	}

	@Override
	public ChessBitSet calculateLegalBitSet(ChessBitSet occupied, ChessBitSet team)
	{
		ChessBitSet set = BitBoardTables.getKnightMove(position);
		//knight cant capture its own team
		return ChessBitSet.xor(set, ChessBitSet.and(set, team));
	}

	@Override
	public int getScore()
	{
		return 300;
	}

	@Override
	public String toString()
	{
		return "N";
	}
}
