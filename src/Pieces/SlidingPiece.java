package Pieces;

import Board.*;

public abstract class SlidingPiece extends Piece
{
	protected Direction[] directions;
	private ChessBitSet[] rays;

	protected SlidingPiece(Player player, int position, Direction[] directions)
	{
		super(player, position);
		this.directions = directions;
		rays = new ChessBitSet[directions.length];
		for (int i = 0; i < directions.length; i++)
		{
			rays[i] = BitBoardTables.getRay(directions[i], position);
		}
	}

	@Override
	public void setPosition(int position)
	{
		super.setPosition(position);

		//the unblocked ChessBitSets only change when the piece moves, so to cut down on time
		//it gets the unblocked ChessBitSets whenever its position changes
		for (int i = 0; i < directions.length; i++)
		{
			rays[i] = BitBoardTables.getRay(directions[i], position);
		}
	}

	@Override
	public ChessBitSet calculateLegalBitSet(ChessBitSet occupied, ChessBitSet team)
	{
		ChessBitSet[] legalRays = new ChessBitSet[directions.length];
		int i =0;
		for (Direction direction : directions)
		{
			//rays[i] is cloned so that .and/.xor can alter the set
			ChessBitSet legalMoves = (ChessBitSet)rays[i].clone();
			ChessBitSet potentialBlockingPieces = ChessBitSet.and(legalMoves, occupied);

			//find the index of the piece blocking the way
			int blockingIndex;
			if(direction.isPrevious())
				blockingIndex = potentialBlockingPieces.previousSetBit(position);
			else
				blockingIndex = potentialBlockingPieces.nextSetBit(position);

			ChessBitSet blockingRay = new ChessBitSet();
			if(blockingIndex != -1)
			{
				//this gets all the tiles bocked by the piece
				blockingRay = BitBoardTables.getRay(direction, blockingIndex);
			}
			//remove the tiles blocked
			legalMoves.xor(blockingRay);

			//remove the player's team piece, because they cant be captured
			legalMoves.xor(ChessBitSet.and(legalMoves, team));
			legalRays[i] = legalMoves;
			i++;
		}

		return ChessBitSet.combine(legalRays);
	}

	/**
	 * If the king can be captured by a sliding piece, this returns the direction that the piece is attacking
	 * from, so that the player in check can block the attack.
	 */
	public ChessBitSet getRayCreatingCheck(int pos)
	{
		for (ChessBitSet ray : rays)
		{
			if (ray.get(pos)) return ray;
		}

		return null;
	}
}
