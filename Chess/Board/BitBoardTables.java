package Board;

public class BitBoardTables
{
	private static final ChessBitSet AFile = ChessBitSet.toChessBitSet(new int[] {0, 8, 16, 24, 32, 40, 48, 56});
	private static final ChessBitSet Eighth_Rank = ChessBitSet.toChessBitSet(new int[] {0, 1, 2, 3, 4, 5, 6, 7});
	private static final ChessBitSet NotAfile = ChessBitSet.toChessBitSet(new int[] {0, 8, 16, 24, 32, 40, 48, 56}, false);
	private static final ChessBitSet NotBfile = ChessBitSet.toChessBitSet(new int[] {1, 9, 17, 25, 33, 41, 49, 57}, false);
	private static final ChessBitSet NotABfile = ChessBitSet.and(NotAfile, NotBfile);
	private static final ChessBitSet NotGfile = ChessBitSet.toChessBitSet(new int[] {6, 14, 22, 30, 38, 46, 54, 62}, false);
	private static final ChessBitSet NotHfile = ChessBitSet.toChessBitSet(new int[] {7, 15, 23, 31, 39, 47, 55, 63}, false);
	private static final ChessBitSet NotGHfile = ChessBitSet.and(NotGfile, NotHfile);
	private static final ChessBitSet[] diagonals = initDiagonals(new ChessBitSet[15], initDiagonal());
	private static final ChessBitSet[] antiDiagonals = initDiagonals(new ChessBitSet[15], initAntiDiagonal());

	//sliding pieces
	public static final ChessBitSet[]
			NORTH_WEST = BitBoardTables.populateDirectionalArrays(Direction.NORTHWEST),
			NORTH = BitBoardTables.populateDirectionalArrays(Direction.NORTH),
			NORTH_EAST = BitBoardTables.populateDirectionalArrays(Direction.NORTHEAST),
			WEST = BitBoardTables.populateDirectionalArrays(Direction.WEST),
			EAST = BitBoardTables.populateDirectionalArrays(Direction.EAST),
			SOUTH_WEST = BitBoardTables.populateDirectionalArrays(Direction.SOUTHWEST),
			SOUTH = BitBoardTables.populateDirectionalArrays(Direction.SOUTH),
			SOUTH_EAST = BitBoardTables.populateDirectionalArrays(Direction.SOUTHEAST);

	//non-sliding pieces
	public static final ChessBitSet[]
			knightMoves = calcKnightsMoves(),
			whitePawnMoves = calcPawnMoves(Player.WHITE),
			blackPawnMoves = calcPawnMoves(Player.BLACK),
			whitePawnAttackMoves = calcPawnAttackMoves(Player.WHITE),
			blackPawnAttackMoves = calcPawnAttackMoves(Player.BLACK);

	public static ChessBitSet[] populateDirectionalArrays(Direction direction)
	{
		ChessBitSet[] rays = new ChessBitSet[ChessBoard.BOARD_SIZE];

		//calculates the ray for each position on the board
		for (int j = 0; j < ChessBoard.BOARD_SIZE; j++)
		{
			rays[j] = getDirectionalRay(direction, j, j / 8, j % 8);
		}

		return rays;
	}

	/**
	 * Privae method used to calculate the static ChessBitSet arrays.
	 */
	private static ChessBitSet getDirectionalRay(Direction direction, int pos, int rank, int file)
	{
		switch (direction)
		{
			case NORTH:
				return getFile(file).getRay(pos, Direction.NORTH);
			case SOUTH:
				return getFile(file).getRay(pos, Direction.SOUTH);
			case WEST:
				return getRank(rank).getRay(pos, Direction.WEST);
			case EAST:
				return getRank(rank).getRay(pos, Direction.EAST);
			case NORTHWEST:
				return getAntiDiagonal(rank, file).getRay(pos, Direction.NORTH);
			case NORTHEAST:
				return getDiagonal(rank, file).getRay(pos, Direction.NORTH);
			case SOUTHWEST:
				return getDiagonal(rank, file).getRay(pos, Direction.SOUTH);
			case SOUTHEAST:
				return getAntiDiagonal(rank, file).getRay(pos, Direction.SOUTH);
		}

		return new ChessBitSet();
	}

	/**
	 * Public method to retrieve the values from the ChessBitSet arrays
	 * @param pos the position of the piece on the board
	 * @return returns a ChessBitSet where the indexes in a given direction are set to true.
	 */
	public static ChessBitSet getRay(Direction direction, int pos)
	{
		switch (direction)
		{
			case NORTH:
				return (ChessBitSet) NORTH[pos].clone();
			case SOUTH:
				return (ChessBitSet) SOUTH[pos].clone();
			case WEST:
				return (ChessBitSet) WEST[pos].clone();
			case EAST:
				return (ChessBitSet) EAST[pos].clone();
			case NORTHWEST:
				return (ChessBitSet) NORTH_WEST[pos].clone();
			case NORTHEAST:
				return (ChessBitSet) NORTH_EAST[pos].clone();
			case SOUTHWEST:
				return (ChessBitSet) SOUTH_WEST[pos].clone();
			case SOUTHEAST:
				return (ChessBitSet) SOUTH_EAST[pos].clone();
		}

		return new ChessBitSet();
	}

	private static ChessBitSet getFile(int file)
	{
		return AFile.shift(file, Direction.EAST);
	}

	private static ChessBitSet getRank(int rank)
	{
		return Eighth_Rank.shift(rank, Direction.SOUTH);
	}

	/**
	 * Creates all diagonal lines by taking the long diagonal in the middle and shifting it either up or down.
	 */
	private static ChessBitSet[] initDiagonals(ChessBitSet[] set, ChessBitSet standard)
	{
		//the full anti/diagonal line in the middle
		set[7] = standard;

		int j = 0;
		for (int i = 7; i >= 0; i--)
		{
			set[j] = standard.shift(i, Direction.NORTH);
			j++;
		}

		for (int i = 1; i <= 7; i++)
		{
			set[j] = standard.shift(i, Direction.SOUTH);
			j++;
		}
		return set;
	}

	/**
	 * @return returns the middle diagonal line
	 */
	private static ChessBitSet initDiagonal()
	{
		ChessBitSet diagonal = new ChessBitSet();
		for(int r = 0, c = ChessBoard.DIMENSIONS - 1; r < ChessBoard.DIMENSIONS; r++, c--)
		{
			diagonal.set(ChessBoard.DIMENSIONS * r + c);
		}

		return diagonal;
	}

	/**
	 * @return returns the middle anti-diagonal line
	 */
	private static ChessBitSet initAntiDiagonal()
	{
		ChessBitSet diagonal = new ChessBitSet();
		for(int r = 0, c = 0; r < ChessBoard.DIMENSIONS; r++, c++)
		{
			diagonal.set(ChessBoard.DIMENSIONS * r + c);
		}

		return diagonal;
	}

	/**
	 * @return returns a ChessBitSet where the indexes of a diagonal at a given rank and file are set to true.
	 */
	private static ChessBitSet getDiagonal(int rank, int file)
	{
		return diagonals[rank + file];
	}

	/**
	 * @return returns a ChessBitSet where the indexes of an anti-diagonal at a given rank and file are set to true.
	 */
	private static ChessBitSet getAntiDiagonal(int rank, int file)
	{
		return antiDiagonals[7 + rank - file];
	}

	private static ChessBitSet[] calcPawnMoves(Player player)
	{
		ChessBitSet[] bitSet = new ChessBitSet[ChessBoard.BOARD_SIZE];
		for (int i = 0; i < bitSet.length; i++)
		{
			int pos = player == Player.WHITE ? i - 8 : i + 8;
			ChessBitSet bitset = new ChessBitSet();
			if(pos > 0 && pos < ChessBoard.BOARD_SIZE) bitset = new ChessBitSet(pos);
			bitSet[i] = bitset;
		}

		return bitSet;
	}

	private static ChessBitSet[] calcPawnAttackMoves(Player player)
	{
		ChessBitSet[] bitSet = new ChessBitSet[ChessBoard.BOARD_SIZE];
		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++)
		{
			int leftAttack = player == Player.WHITE ? i - 9 : i + 9;
			int rightAttack = player == Player.WHITE ? i - 7 : i + 7;

			ChessBitSet attack = new ChessBitSet();
			if (leftAttack >= 0 && leftAttack < ChessBoard.BOARD_SIZE) attack.set(leftAttack);
			if (rightAttack >= 0 && rightAttack < ChessBoard.BOARD_SIZE) attack.set(rightAttack);

			int file = i % 8;

			//removes any overflow from the A/H files
			if(file == 0)
				attack.and(getNotHfile());
			else if(file == 7)
				attack.and(getNotAfile());

			bitSet[i] = attack;
		}
		return bitSet;
	}

	private static ChessBitSet[] calcKnightsMoves()
	{
		ChessBitSet[] moves = new ChessBitSet[ChessBoard.BOARD_SIZE];
		for (int position = 0; position < moves.length; position++){
			ChessBitSet set = ChessBitSet.toChessBitSet(new int[]{
					position - 17, position - 15, position - 10, position - 6,
					position + 17, position + 15, position + 10, position + 6});

			int file = position % 8;

			if(file < 2)
				set.and(getNotGHfile());
			else if(file > 5)
				set.and(getNotABfile());

			moves[position] = set;
		}

		return moves;
	}

	public static ChessBitSet getNotAfile()
	{
		return NotAfile;
	}

	public static ChessBitSet getNotABfile()
	{
		return NotABfile;
	}

	public static ChessBitSet getNotHfile()
	{
		return NotHfile;
	}

	/**
	 * @return returns a ChessBitSet where all values are set to true except for the G and H files.
	 * Used for masking any overflow if a piece can move horizontally.
	 */
	public static ChessBitSet getNotGHfile()
	{
		return NotGHfile;
	}

	public static ChessBitSet getKnightMove(int pos)
	{
		return knightMoves[pos];
	}

	public static ChessBitSet getPawnMoves(int pos, Player player)
	{
		return player == Player.WHITE ?
				(ChessBitSet) whitePawnMoves[pos].clone() :
				(ChessBitSet) blackPawnMoves[pos].clone();
	}

	public static ChessBitSet getPawnAttackMoves(int pos, Player player)
	{
		return player == Player.WHITE ?
				(ChessBitSet) whitePawnAttackMoves[pos].clone() :
				(ChessBitSet) blackPawnAttackMoves[pos].clone();
	}
}
