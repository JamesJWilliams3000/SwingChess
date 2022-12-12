package Board;

public enum Direction
{
	NORTHWEST(false, true), NORTH(true, true),
	NORTHEAST(false, true), WEST (false, true),
	EAST(false, false), SOUTHWEST(false, false),
	SOUTH(true, false), SOUTHEAST(false, false);

	private final boolean IS_VERTICAL, IS_PREVIOUS, IS_NEXT;

	Direction(boolean isVertical, boolean isPrevious)
	{
		IS_VERTICAL = isVertical;

		if (isPrevious)
		{
			IS_PREVIOUS = true;
			IS_NEXT = false;
		} else
		{
			IS_NEXT = true;
			IS_PREVIOUS = false;
		}
	}

	/**
	 * Used for shifting a ChessBitSet
	 */
	public boolean isVertical()
	{
		return IS_VERTICAL;
	}

	/**
	 * Used for calculating the sliding piece ChessBitSets, when finding the next/previous set bit of a BitSet.
	 */
	public boolean isPrevious()
	{
		return IS_PREVIOUS;
	}
}
