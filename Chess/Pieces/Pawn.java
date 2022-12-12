package Pieces;

import Board.*;

public class Pawn extends Piece
{
	public Pawn(Player player, int position)
	{
		super(player, position);
	}

	@Override
	public ChessBitSet calculateLegalBitSet(ChessBitSet occupied, ChessBitSet team)
	{
		ChessBitSet attack = BitBoardTables.getPawnAttackMoves(position, player);
		ChessBitSet legalMoves = BitBoardTables.getPawnMoves(position, player);

		//can only attack the other team
		attack.and(ChessBitSet.xor(team, occupied));
		//cannot move forward if another piece is in the way
		legalMoves.xor(ChessBitSet.and(legalMoves, occupied));
		ChessBitSet withoutTeams = legalMoves;

		//if nothing is blocking the pawn, it can move 2 tiles ahead
		if (isFirstMove() && withoutTeams.cardinality() > 0)
		{
			int pos = player == Player.WHITE ? position - 16 : position + 16;
			if(!occupied.get(pos)) withoutTeams.or(new ChessBitSet(pos));
		}

		//combine the attack and forward bitsets
		attack.or(withoutTeams);
		return attack;
	}

	@Override
	public int getScore()
	{
		return 100;
	}

	@Override
	public String toString()
	{
		return "P";
	}

}
