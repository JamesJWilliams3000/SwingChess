package Board;

import java.util.BitSet;

public class ChessBitSet extends BitSet implements Cloneable
{
	public ChessBitSet(int pieceIndex)
	{
		super(ChessBoard.BOARD_SIZE);
		if (pieceIndex >= 0 && pieceIndex < ChessBoard.BOARD_SIZE) set(pieceIndex);
	}

	public ChessBitSet()
	{
		super(ChessBoard.BOARD_SIZE);
	}

	/**
	 * @param pieceIndexes a int[] containing board indexes
	 * @return returns a ChessBitSet where each index in pieceIndexes are set to true.
	 */
	public static ChessBitSet toChessBitSet(int[] pieceIndexes)
	{
		ChessBitSet bitset = new ChessBitSet();
		for (int pieceIndex : pieceIndexes)
		{
			if (pieceIndex >=0 && pieceIndex < ChessBoard.BOARD_SIZE) bitset.set(pieceIndex);
		}
		return bitset;
	}

	/**
	 * @param pieceIndexes a int[] containing board indexes
	 * @param value the value to set the indexes to.
	 * @return returns a ChessBitSet where each index in pieceIndexes are set to 'value'.
	 */
	public static ChessBitSet toChessBitSet(int[] pieceIndexes, boolean value)
	{
		if(!value)
		{
			ChessBitSet full = new ChessBitSet();
			full.set(0, full.size(), true);

			return ChessBitSet.xor(full, toChessBitSet(pieceIndexes));
		}
		else
		{
			return toChessBitSet(pieceIndexes);
		}
	}

	/**
	 * @param pos The position of the piece
	 * @return Returns a BitBoard of just the ray in the indicated direction, excluding the chess piece position.
	 */
	public ChessBitSet getRay(int pos, Direction direction)
	{
		ChessBitSet ray = (ChessBitSet)this.clone();

		if (direction == Direction.WEST || direction == Direction.NORTH)
		{
			ray.clear(pos, ray.size());
		}
		else if (direction == Direction.EAST || direction == Direction.SOUTH)
		{
			ray.clear(0, pos +1);
		}

		return ray;
	}

	/**
	 * Shifts all the values of a ChessBitSet in a given direction.
	 */
	public ChessBitSet shift(int dv, Direction dir)
	{
		ChessBitSet board = this;
		int shift;
		if (dir.isVertical())
		{
			shift = dv * ChessBoard.DIMENSIONS;
		}
		else
		{
			shift = dv;
		}

		return shift(shift, dir, board);
	}

	private static ChessBitSet shift(int shift, Direction dir, ChessBitSet board)
	{
		long[] bits = board.toLongArray();

		for (int i = 0; i < bits.length; i++)
		{
			if(dir == Direction.NORTH || dir == Direction.WEST)
			{
				bits[i] = bits[i] >>> shift;
			}
			else
			{
				bits[i] = bits[i] << shift;
			}
		}

		ChessBitSet shifted = new ChessBitSet();
		shifted.or(BitSet.valueOf(bits));

		return shifted;
	}

	/**
	 * BitSet .and/.or only modifies an existing bitset and doesnt return a new one,
	 * which can be unwanted, so this returns a new bitset without modifing a bitset
	 * we dont want modified.
	 */
	public static ChessBitSet and(ChessBitSet set, ChessBitSet set2)
	{
		ChessBitSet chessBitSet = (ChessBitSet)set2.clone();
		chessBitSet.and(set);
		return chessBitSet;
	}

	public static ChessBitSet or(ChessBitSet set, ChessBitSet set2)
	{
		ChessBitSet chessBitSet = (ChessBitSet)set2.clone();
		chessBitSet.or(set);
		return chessBitSet;
	}

	public static ChessBitSet xor(ChessBitSet set, ChessBitSet set2)
	{
		ChessBitSet chessBitSet = (ChessBitSet)set2.clone();
		chessBitSet.xor(set);
		return chessBitSet;
	}

	/**
	 * Takes an array of ChessBitSet and combines all the values into a single ChessBitSet.
	 */
	public static ChessBitSet combine(ChessBitSet[] bits)
	{
		ChessBitSet combined = new ChessBitSet();

		for (int i = 0; i < bits.length; i++)
		{
			//using the .or from bitSet this time because we actually want to modify this one and not just
			//make a bunch of them
			combined.or(bits[i]);
		}

		return combined;
	}

	/**
	 * represents a 8x8 string representation of the bitset, with '1' as true and '-' as false
	 */
	@Override
	public String toString()
	{
		StringBuilder board = new StringBuilder();
		for(int i = 0; i < size(); i++)
		{
			if(get(i))  board.append(" 1 ");
			else 		board.append(" - ");

			if ((i + 1) % ChessBoard.DIMENSIONS == 0) board.append("\n");
		}

		return board.toString();
	}


	/**
	 * The same toString(), except the piece in question is marked separately
	 * @param p the index of where that piece is located
	 * @param piece the toString of a particular piece
	 * @return
	 */
	public String toString(int p, String piece)
	{
		StringBuilder board = new StringBuilder();
		for(int i = 0; i < size(); i++)
		{
			if(i == p) 		board.append(" " + piece + " ");
			else if(get(i))  board.append(" 1 ");
			else 		board.append(" - ");

			if ((i + 1) % ChessBoard.DIMENSIONS == 0) board.append("\n");
		}

		return board.toString();
	}
}
