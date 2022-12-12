package AI;

import Board.ChessBitSet;
import Board.ChessBoard;
import Pieces.Piece;

import java.util.ArrayList;

public class Move
{
	private Piece piece, capturedPiece = null;
	private int startPosition, endPosition;
	private boolean firstMove;

	public Move(Piece piece, int startPosition, int endPosition)
	{
		this.piece = piece;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.firstMove = piece.isFirstMove();
	}

	public Move(Piece piece, int startPosition, int endPosition, Piece captured)
	{
		this(piece, startPosition, endPosition);
		capturedPiece = captured;
	}

	public int getStartPosition()
	{
		return startPosition;
	}

	public int getEndPosition()
	{
		return endPosition;
	}

	public Piece getCapturedPiece()
	{
		return capturedPiece;
	}

	public Piece getPiece()
	{
		return piece;
	}

	public boolean isFirstMove()
	{
		return firstMove;
	}

	public static ArrayList<Move> createMoves(ChessBitSet legalMoves, Piece p, ChessBoard board)
	{
		ArrayList<Move> moves = new ArrayList<>();
		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++)
		{
			if (legalMoves.get(i))
			{
				if (board.hasPieceAt(i)) moves.add(new Move(p, p.getPosition(), i, board.getPiece(i)));
				else moves.add(new Move(p, p.getPosition(), i));
			}
		}
		return moves;
	}

	@Override
	public String toString()
	{
		return String.format("%s: %s to %s", piece, Squares.getPosition(startPosition),
				Squares.getPosition(endPosition));
	}

	public enum Squares
	{
		a8, b8, c8, d8, e8, f8, g8, h8,
		a7, b7, c7, d7, e7, f7, g7, h7,
		a6, b6, c6, d6, e6, f6, g6, h6,
		a5, b5, c5, d5, e5, f5, g5, h5,
		a4, b4, c4, d4, e4, f4, g4, h4,
		a3, b3, c3, d3, e3, f3, g3, h3,
		a2, b2, c2, d2, e2, f2, g2, h2,
		a1, b1, c1, d1, e1, f1, g1, h1;

		public static Squares getPosition(int i)
		{
			return Squares.values()[i];
		}
	};
}