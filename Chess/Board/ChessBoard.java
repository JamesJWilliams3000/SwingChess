package Board;

import AI.Move;
import Pieces.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class ChessBoard implements Serializable
{
    public static final int BOARD_SIZE = 64;
    public static final int DIMENSIONS = 8;
    public static final int INITIAL_PIECE_COUNT = 16;
    private Piece[] board;
    private int[] whitePieces = new int[INITIAL_PIECE_COUNT];
    private int[] blackPieces = new int[INITIAL_PIECE_COUNT];
    private int[] occupiedTiles = new int[INITIAL_PIECE_COUNT * 2];
    public BitBoard bitBoard;
	public Player currentPlayer;
	King whiteKing, blackKing;
    private Piece attackingPiece = null;
    private boolean playerinCheck = false;
    private boolean gameOver = false;
    private ChessBitSet attackingRay = null;
    private boolean whiteWins, blackWins;

    public ChessBoard()
    {
        this.board = getStartingBoard();

        for (int i = 0, b = 0, w = 0, o = 0; i < board.length; i++)
        {
            if(hasPieceAt(i))
            {
               if(board[i].getPlayer() == Player.BLACK) blackPieces[b++] = i;
               if(board[i].getPlayer() == Player.WHITE) whitePieces[w++] = i;
               occupiedTiles[o++] = i;
            }
        }

        currentPlayer = Player.WHITE;
        bitBoard = new BitBoard(this);
    }

    public Piece[] getStartingBoard()
    {
        Piece[] startingBoard = new Piece[BOARD_SIZE];

        Player player = Player.BLACK;
        setOthers(startingBoard, 0, player);
        setPawns(startingBoard, 8, player);

        player = Player.WHITE;
        setPawns(startingBoard, 48, player);
        setOthers(startingBoard, 56, player);

        return startingBoard;
    }

    private void setOthers(Piece[] b, int i, Player player)
    {
        b[i] = new Rook(player, i++);
        b[i] = new Knight(player, i++);
        b[i] = new Bishop(player, i++);
        b[i] = new Queen(player, i++);
        b[i] = new King(player, i++);
        if(player == Player.WHITE)    whiteKing = (King)b[i-1];
        else                          blackKing = (King)b[i-1];
        b[i] = new Bishop(player, i++);
        b[i] = new Knight(player, i++);
        b[i] = new Rook(player, i);
    }

    private  static void setPawns(Piece[] b, int i, Player player)
    {
        for (int file = 0; file < ChessBoard.DIMENSIONS; file++)
        {
            b[i] = new Pawn(player, i++);
        }
    }

    public int[] getWhitePieces()
    {
        return whitePieces;
    }

    public int[] getBlackPieces()
    {
        return blackPieces;
    }

    public int[] getOccupiedTiles()
    {
        return occupiedTiles;
    }

    public boolean hasPieceAt(int i)
    {
        return i < board.length && board[i] != null;
    }

    public boolean playerHasPieceAt(int i)
    {
        return board[i] != null && board[i].getPlayer() == currentPlayer;
    }

    public Piece getPiece(int i)
    {
        return board[i];
    }

    public Piece[] getAllPieces()
    {
        return board;
    }

    //returns an IntStream for ChessJPanel to paint the selected tiles.
    public IntStream getLegalTiles(int i)
    {
        return getLegalBitSet(i).stream();
    }

    private ChessBitSet getLegalBitSet(int i)
    {
        return board[i].calculateLegalBitSet(bitBoard.getOccupied(), bitBoard.getTeamPieces(board[i].getPlayer()));
    }

    public ArrayList<Move> getAllLegalMoves()
    {
        ArrayList<Move> moves = new ArrayList<>();

        for (Piece piece : board)
        {
            if(piece != null && piece.getPlayer() == currentPlayer)
            {
                ChessBitSet legalSet = getLegalBitSet(piece.getPosition());
                moves.addAll(Move.createMoves(legalSet, piece, this));
            }
        }
        return moves;
    }

    public void move(int start, int end)
    {
        if(board[end] != null)
        {
            capturePiece(end, board[end].getPlayer());
        }

        board[end] = board[start];
        board[start] = null;
        board[end].setPosition(end);

        bitBoard.movePiece(start, end, board[end].getPlayer());
        board[end].setFirstMove(false);

        currentPlayer = Player.changePlayer(currentPlayer);
    }

    public boolean move(Move move)
    {
        King king = getCurrentPlayersKing();

        move(move.getStartPosition(), move.getEndPosition());

        boolean inCheck = false;
        Player opposingPlayer = Player.changePlayer(currentPlayer);
        for (Piece piece : board)
        {
            //does moving leave our king in check
            if(piece != null && piece.getPlayer() == currentPlayer)
            {
                ChessBitSet legalSet = getLegalBitSet(piece.getPosition());
                if (legalSet.get(king.getPosition()))
                    return false;
            }
            //does making the move put the other king in check
            else if (piece != null && piece.getPlayer() == opposingPlayer)
            {
                King oppKing = getCurrentPlayersKing();
                ChessBitSet legalSet = getLegalBitSet(piece.getPosition());
                if (legalSet.get(oppKing.getPosition()))
                {
                    playerinCheck = true;
                    inCheck = true;
                    attackingPiece = piece;
                    if (SlidingPiece.class.isAssignableFrom(attackingPiece.getClass()))
                    {
                        attackingRay = (ChessBitSet)((SlidingPiece)attackingPiece)
                                        .getRayCreatingCheck(oppKing.getPosition()).clone();
                    }
                }
            }
        }

        if (!inCheck)
        {
            playerinCheck = false;
            attackingPiece = null;
            attackingRay = null;
        }

        //move does not leave player in check
        return true;
    }

    public void unMove(Move move)
    {
        move(move.getEndPosition(), move.getStartPosition());
        move.getPiece().setFirstMove(move.isFirstMove());

        if(move.getCapturedPiece() != null)
        {
            board[move.getEndPosition()] = move.getCapturedPiece();
            unCapturePiece(move.getEndPosition(), move.getCapturedPiece().getPlayer());
        }
    }

    private void capturePiece(int i, Player player)
    {
        bitBoard.capturePiece(i, player);
        if (board[i] instanceof King)
        {
            gameOver = true;
            if (currentPlayer == Player.WHITE) whiteWins = true;
            else blackWins = true;
        }
    }

    private void unCapturePiece(int i, Player player)
    {
        bitBoard.unCapturePiece(i, player);

        if (board[i] instanceof King)
        {
            gameOver = false;
            whiteWins = false;
            blackWins = false;
        }
    }

    public boolean playerIsInCheck()
    {
        return playerinCheck;
    }

    public King getCurrentPlayersKing()
    {
        return currentPlayer == Player.WHITE ? whiteKing : blackKing;
    }

    //checkmate if the king cannot move to a safe tile
    public boolean checkMate()
    {
        if (!playerIsInCheck()) return false;
        ArrayList<Move> legalMovesInCheck = getLegalMovesInCheck();
        if (legalMovesInCheck.size() > 0) return false;

        return true;
    }

    public boolean isGameOver()
    {
        return gameOver;
    }

    public boolean isHumanWinner()
    {
        return whiteWins;
    }

    /**
     * If the player is in check, the only possible moves are to capture the threatening piece,
     * block its attack, or move the king out of danger.
     */
    public ArrayList<Move> getLegalMovesInCheck()
    {
        King king = getCurrentPlayersKing();
        if (board[king.getPosition()] != king) return new ArrayList<>();
        ArrayList<Move> legalMovesInCheck = new ArrayList<>();
        ArrayList<Move> kingsMoves = Move.createMoves(getLegalBitSet(king.getPosition()), king, this);

        //look at all the current moves and only keep the ones possible in check
        ArrayList<Move> legalMoves = getAllLegalMoves();
        for (Move move : legalMoves)
        {
            //capture the attacking piece
            if (move.getEndPosition() == attackingPiece.getPosition()) legalMovesInCheck.add(move);

            //intercept the attacking piece
            if(attackingRay != null
                    && attackingRay.get(move.getEndPosition())
                    && !(move.getPiece() instanceof King)) legalMovesInCheck.add(move);
        }

        //move the king out of danger
        for (Move kingsMove : kingsMoves)
        {
            if (!getLegalBitSet(attackingPiece.getPosition()).get(kingsMove.getEndPosition()))
                legalMovesInCheck.add(kingsMove);
        }

        return legalMovesInCheck;
    }

    //king is at checkmate
    public Move getRandomKingMove()
    {
        ChessBitSet legalSet = getLegalBitSet(getCurrentPlayersKing().getPosition());
        return Move.createMoves(legalSet, getCurrentPlayersKing(), this).get(0);
    }

    public int getWhiteMobility()
    {
        return getMobility(Player.WHITE);
    }

    public int getBlackMobility()
    {
        return getMobility(Player.BLACK);
    }

    /**
     * Since the all possible moves are set to true on a ChessBitSet,
     * to find out how many possible moves a piece has, just get the number of indexes set to true (aka cardinality).
     */
    private int getMobility(Player player)
    {
        int mobility = 0;
        for (Piece piece : board)
        {
            if (piece != null && player == piece.getPlayer())
            {
                mobility += getLegalBitSet(piece.getPosition()).cardinality();
            }
        }
        return mobility;
    }

    /**
     * Same concept as mobility, expect here we AND the opposite team's BitSet and all possible moves, which leaves
     * only the tiles that can capture a piece.
     */
    public int getCapturableTiles(Player player)
    {
        ChessBitSet opposingTeam = bitBoard.getTeamPieces(Player.changePlayer(player));
        int attack = 0;
        for (Piece piece : board)
        {
            if (piece != null && player == piece.getPlayer())
            {
                attack += ChessBitSet.and(getLegalBitSet(piece.getPosition()), opposingTeam).cardinality();
            }
        }
        return attack;
    }

    public void promotePiece(Piece piece)
    {
        board[piece.getPosition()] = piece;
    }

    /**
     * Represents a 8x8 board of pieces, where '-' is empty and pieces are represented by a letter.
     */
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < board.length; i++)
        {
            if(board[i] != null) 		str.append(String.format(" %s ", board[i].toString()));
            else 		                str.append(" - ");

            if ((i + 1) % ChessBoard.DIMENSIONS == 0) str.append("\n");
        }

        return str.toString();
    }
}


