package Board;

import java.io.Serializable;

public class BitBoard implements Serializable
{
	public ChessBitSet board, whitePieces, blackPieces;

	public BitBoard(ChessBoard startingBoard)
	{
		board 		= ChessBitSet.toChessBitSet(startingBoard.getOccupiedTiles());
		whitePieces = ChessBitSet.toChessBitSet(startingBoard.getWhitePieces());
		blackPieces = ChessBitSet.toChessBitSet(startingBoard.getBlackPieces());
	}

	public ChessBitSet getOccupied()
	{
		return board;
	}

	public ChessBitSet getTeamPieces(Player player)
	{
		if(player == Player.WHITE) 	return whitePieces;
		else 						return blackPieces;
	}

	public void movePiece(int start, int end, Player player)
	{
		moveOnBitSet(start, end, board);

		ChessBitSet teamPieces = player == Player.WHITE ? whitePieces : blackPieces;
		moveOnBitSet(start, end, teamPieces);
	}

	public void capturePiece(int i, Player player)
	{
		ChessBitSet teamPieces = getTeamPieces(player);
		teamPieces.set(i, false);
		board.set(i, false);
	}

	public void unCapturePiece(int i, Player player)
	{
		ChessBitSet teamPieces = getTeamPieces(player);
		teamPieces.set(i, true);
		board.set(i, true);
	}

	private void moveOnBitSet(int start, int end, ChessBitSet b)
	{
		b.set(start, false);
		b.set(end, true);
	}

}

