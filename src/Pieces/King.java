package Pieces;

import Board.*;

public class King extends Piece
{
	public King(Player player, int position)
	{
		super(player, position);
	}

	@Override
	public ChessBitSet calculateLegalBitSet(ChessBitSet occupied, ChessBitSet team)
	{
		ChessBitSet[] directions = new ChessBitSet[4];
		directions[0] = new ChessBitSet(position + 8);
		directions[1] = new ChessBitSet(position - 8);
		directions[2] = new ChessBitSet(position + 1);
		directions[2].and(BitBoardTables.getNotAfile());
		directions[3] = new ChessBitSet(position - 1);
		directions[3].and(BitBoardTables.getNotHfile());
		ChessBitSet legalMoves = ChessBitSet.combine(directions);
		//king cant capture its own team
		ChessBitSet withoutTeam = ChessBitSet.xor(legalMoves, ChessBitSet.and(legalMoves, team));

		return withoutTeam;
	}

	@Override
	public int getScore()
	{
		return 20000;
	}

	@Override
	public String toString()
	{
		return "K";
	}
}
