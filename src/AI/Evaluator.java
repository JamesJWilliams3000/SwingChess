package AI;

import Board.ChessBoard;
import Board.Player;
import Pieces.*;

import java.util.ArrayList;

public class Evaluator extends Thread
{
	private Move bestMove;
	private ChessBoard board;
	int depth;

	public Evaluator(ChessBoard board, int depth)
	{
		super();
		this.board = board;
		this.depth = depth;
	}

	public Move getBestMove()
	{
		return bestMove == null ? board.getRandomKingMove() : bestMove;
	}
	public void run()
	{
		System.out.println("Thinking....");
		long startTime = System.currentTimeMillis();
		int max = Integer.MIN_VALUE;
		Move best = null;
		ArrayList<Move> allLegalMoves = board.getAllLegalMoves();
		for (Move move : allLegalMoves)
		{
			boolean legalMove = board.move(move);
			if (legalMove)
			{
				int score = alphaBetaMax(board, depth, Integer.MIN_VALUE +1, Integer.MAX_VALUE);
				board.unMove(move);
				if (score >= max)
				{
					max = score;
					best = move;
				}
			}
			else
			{
				board.unMove(move);
			}
		}

		long time = System.currentTimeMillis() - startTime;
		System.out.printf("Time: %d:%d \n", ((time / 1000) / 60), ((time / 1000) % 60));

		bestMove = best;
	}

	private static int alphaBetaMax(ChessBoard board, int depth, int alpha, int beta)
	{
		if (depth == 0 || board.checkMate())
			return evaluate(board);

		ArrayList<Move> allLegalMoves = board.getAllLegalMoves();
		if(board.playerIsInCheck()) allLegalMoves = board.getLegalMovesInCheck();
		for (Move move : allLegalMoves)
		{
			boolean legalMove = board.move(move);
			if (legalMove)
			{
				int score = alphaBetaMin(board, depth -1, alpha, beta);
				board.unMove(move);
				if (score >= beta)
				{
					return beta;
				}
				if (score > alpha)
				{
					alpha = score;
				}
			}
			else
			{
				board.unMove(move);
			}
		}
		return alpha;
	}

	private static int alphaBetaMin(ChessBoard board, int depth, int alpha, int beta)
	{
		if (depth == 0 || board.checkMate())
			return -evaluate(board);

		ArrayList<Move> allLegalMoves = board.getAllLegalMoves();
		if(board.playerIsInCheck()) allLegalMoves = board.getLegalMovesInCheck();
		for (Move move : allLegalMoves)
		{
			boolean legalMove = board.move(move);
			if (legalMove)
			{
				int score = alphaBetaMax(board, depth -1, alpha, beta);
				board.unMove(move);
				if (score <= alpha)
				{
					return alpha;
				}
				if (score < beta)
				{
					beta = score;
				}
			}
			else
			{
				board.unMove(move);
			}
		}

		return beta;
	}

	private static int evaluate(ChessBoard board)
	{
		int materialScore = scorePieces(board, Player.WHITE) - scorePieces(board, Player.BLACK);
		int mobilityScore = 100 * (board.getWhiteMobility() - board.getBlackMobility());
		int pieceSquareScore = 10 * (scorePieceSquares(board, Player.WHITE) - scorePieceSquares(board, Player.BLACK));
		int capturingScore = 100 * (board.getCapturableTiles(Player.WHITE) - board.getCapturableTiles(Player.BLACK));
		return materialScore + mobilityScore + pieceSquareScore + capturingScore;
	}

	private static int scorePieces(ChessBoard board, Player player)
	{
		int score = 0;
		for (Piece piece : board.getAllPieces())
		{
			if(piece != null && piece.getPlayer() == player) score += piece.getScore();
		}

		return score;
	}

	private static int scorePieceSquares(ChessBoard board, Player player)
	{
		int score = 0;
		for (Piece piece : board.getAllPieces())
		{
			if(piece != null && piece.getPlayer() == player)
			{
				int i = piece.getPosition();
				if (piece instanceof Pawn)
				{
					if (player == Player.WHITE)	 score += pawnWhiteSquareTable[i];
					else						 score += pawnBlackSquareTable[i];
				}
				else if (piece instanceof Knight)
				{
					score += knightSquareTable[i];
				}
				else if (piece instanceof Bishop)
				{
					if (player == Player.WHITE) score += bishopWhiteSquareTable[i];
					else						score += bishopBlackSquareTable[i];
				}
				else if (piece instanceof Queen)
				{
					score += queenSquareTable[i];
				}
				else if (piece instanceof King)
				{
					if (player == Player.WHITE) score += kingWhiteSquareTable[i];
					else					 	score += kingBlackSquareTable[i];
				}
			}
		}
		return score;
	}

	//https://www.chessprogramming.org/Simplified_Evaluation_Function
	private static final int[] pawnWhiteSquareTable =
			{
					0,  0,  0,  0,  0,  0,  0,  0,
					50, 50, 50, 50, 50, 50, 50, 50,
					10, 10, 20, 30, 30, 20, 10, 10,
					5,  5, 10, 25, 25, 10,  5,  5,
					0,  0,  0, 20, 20,  0,  0,  0,
					5, -5,-10,  0,  0,-10, -5,  5,
					5, 10, 10,-20,-20, 10, 10,  5,
					0,  0,  0,  0,  0,  0,  0,  0
			};

	private static final int[] pawnBlackSquareTable =
			{
					 0,  0,  0,  0,  0,  0,  0,  0,
					 5, 10, 10,-20,-20, 10, 10,  5,
					 5, -5,-10,  0,  0,-10, -5,  5,
					 0,  0,  0, 20, 20,  0,  0,  0,
					 5,  5, 10, 25, 25, 10,  5,  5,
					10, 10, 20, 30, 30, 20, 10, 10,
					50, 50, 50, 50, 50, 50, 50, 50,
					 0,  0,  0,  0,  0,  0,  0,  0
			};

	private static final int[] knightSquareTable =
			{
					-50,-40,-30,-30,-30,-30,-40,-50,
					-40,-20,  0,  0,  0,  0,-20,-40,
					-30,  0, 10, 15, 15, 10,  0,-30,
					-30,  5, 15, 20, 20, 15,  5,-30,
					-30,  0, 15, 20, 20, 15,  0,-30,
					-30,  5, 10, 15, 15, 10,  5,-30,
					-40,-20,  0,  5,  5,  0,-20,-40,
					-50,-40,-30,-30,-30,-30,-40,-50,
			};

	private static final int[] bishopWhiteSquareTable =
			{
					-20,-10,-10,-10,-10,-10,-10,-20,
					-10,  0,  0,  0,  0,  0,  0,-10,
					-10,  0,  5, 10, 10,  5,  0,-10,
					-10,  5,  5, 10, 10,  5,  5,-10,
					-10,  0, 10, 10, 10, 10,  0,-10,
					-10, 10, 10, 10, 10, 10, 10,-10,
					-10,  5,  0,  0,  0,  0,  5,-10,
					-20,-10,-10,-10,-10,-10,-10,-20,
			};

	private static final int[] bishopBlackSquareTable =
			{
					-20,-10,-10,-10,-10,-10,-10,-20,
					-10,  5,  0,  0,  0,  0,  5,-10,
					-10, 10, 10, 10, 10, 10, 10,-10,
					-10,  0, 10, 10, 10, 10,  0,-10,
					-10,  5,  5, 10, 10,  5,  5,-10,
					-10,  0,  5, 10, 10,  5,  0,-10,
					-10,  0,  0,  0,  0,  0,  0,-10,
					-20,-10,-10,-10,-10,-10,-10,-20,
			};

	private static final int[] queenSquareTable =
			{
					-20,-10,-10, -5, -5,-10,-10,-20,
					-10,  0,  0,  0,  0,  0,  0,-10,
					-10,  0,  5,  5,  5,  5,  0,-10,
					 -5,  0,  5,  5,  5,  5,  0, -5,
					  0,  0,  5,  5,  5,  5,  0,  0,
					-10,  0,  5,  5,  5,  5,  0,-10,
					-10,  0,  0,  0,  0,  0,  0,-10,
					-20,-10,-10, -5, -5,-10,-10,-20
			};

	private static final int[] kingWhiteSquareTable =
	{
					-30,-40,-40,-50,-50,-40,-40,-30,
					-30,-40,-40,-50,-50,-40,-40,-30,
					-30,-40,-40,-50,-50,-40,-40,-30,
					-30,-40,-40,-50,-50,-40,-40,-30,
					-20,-30,-30,-40,-40,-30,-30,-20,
					-10,-20,-20,-20,-20,-20,-20,-10,
					 20, 20,  0,  0,  0,  0, 20, 20,
					 20, 30, 10,  0,  0, 10, 30, 20
	};

	private static final int[] kingBlackSquareTable =
			{
					 20, 30, 10,  0,  0, 10, 30, 20,
					 20, 20,  0,  0,  0,  0, 20, 20,
					-10,-20,-20,-20,-20,-20,-20,-10,
					-20,-30,-30,-40,-40,-30,-30,-20,
					-30,-40,-40,-50,-50,-40,-40,-30,
					-30,-40,-40,-50,-50,-40,-40,-30,
					-30,-40,-40,-50,-50,-40,-40,-30,
					-30,-40,-40,-50,-50,-40,-40,-30,
			};
}
