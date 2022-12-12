package Board;

public enum Player
{
	BLACK ("b"), WHITE ("w");

	private final String name;

	Player(String name)
	{
		this.name = name;
	}

	public static Player changePlayer(Player currentPlayer)
	{
		return currentPlayer == WHITE ? BLACK : WHITE;
	}

	public static boolean isHumanPlayer(Player player)
	{
		return player == WHITE;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
