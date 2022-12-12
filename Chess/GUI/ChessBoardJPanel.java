package GUI;

import AI.Evaluator;
import Board.ChessBoard;
import AI.Move;
import Board.Player;
import Pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class ChessBoardJPanel extends JPanel implements ActionListener
{
	public TileJButton[] tiles = new TileJButton[ChessBoard.BOARD_SIZE];
	public ArrayList<Integer> legalTileIndexes = new ArrayList<>();
	ChessBoard board = new ChessBoard();
	private boolean canPlay = true;
	private boolean showLegalTiles = true;

	public ChessBoardJPanel()
	{
		setLayout(new GridLayout(ChessBoard.DIMENSIONS+2, ChessBoard.DIMENSIONS+2));
		initBoard();
	}

	public ChessBoardJPanel(ChessBoard chessBoard)
	{
		setLayout(new GridLayout(ChessBoard.DIMENSIONS+2, ChessBoard.DIMENSIONS+2));
		board = chessBoard;
		initBoard();
	}

	private void initBoard()
	{
		int i = 0;
		char col = 'A';
		int row = 8;

		//top row
		add(makeLabel("", true));
		for(int j = 0; j < ChessBoard.DIMENSIONS; j++)
		{
			add(makeLabel(Character.toString((char)(col + j)), true));
		}
		add(makeLabel("", true));

		int rightCol = ChessBoard.DIMENSIONS+1;
		for (int r = 0; r < ChessBoard.DIMENSIONS; r++)
		{
			for (int c = 0; c < ChessBoard.DIMENSIONS+2; c++)
			{
				//left col
				if(c == 0){
					add(makeLabel(Integer.toString(row - r), true));
				}
				else if(c == rightCol)
				{
					add(makeLabel("", true));
				}
				else
				{
					TileJButton tile;
					if(board.hasPieceAt(i)) 	tile = new TileJButton(i, r, c, board.getPiece(i).getImageFileName());
					else						tile = new TileJButton(i, r, c, null);

					tile.addActionListener(this);
					add(tile);
					tiles[i] = tile;

					i++;
				}
			}
		}

		//bottom row
		for(int j = 0; j < ChessBoard.DIMENSIONS; j++)
		{
			add(makeLabel("", true));
		}
	}

	public JLabel makeLabel(String text, boolean top)
	{
		JLabel l = new JLabel(text, SwingConstants.RIGHT);
		l.setOpaque(true);

		if(top)
		{
			l.setHorizontalAlignment(SwingConstants.CENTER);
			l.setVerticalAlignment(SwingConstants.BOTTOM);
		}

		return l;
	}

	@Override
	public final Dimension getPreferredSize()
	{
		Component p = getParent();
		//fit the panel to the parent as a square
		if (p.getWidth() > this.getWidth() || p.getWidth() > this.getWidth())
		{
			int size = Math.min(p.getWidth(), p.getHeight());
			return new Dimension(size, size);
		}
		else
		{
			return super.getPreferredSize();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//prevent the player from clicking buttons when it is game over/the computer is playing
		if (canPlay)
		{
			TileJButton tile = (TileJButton)e.getSource();

			for (Integer i : legalTileIndexes)
			{
				tiles[i].setasLegalTile(false);
			}

			if(Player.isHumanPlayer(board.currentPlayer))
			{
				//not a blank tile, a piece hasnt been already selected, and the piece belongs to the player
				if (board.playerHasPieceAt(tile.getCoord()) && TileJButton.getSelectedButton() == null)
				{
					//if it wasn't the last tile selected
					if(!tile.isSelected())
					{
						tile.select();
						//get the possible tiles the player can move to
						setLegalTiles(tile);
					}
					else
					{
						tile.deselect();
					}
				}
				//a tile was already selected, player clicked another tile to move to
				else if(TileJButton.getSelectedButton() != null)
				{
					//player can only make legal moves
					if(legalTileIndexes.contains(tile.getCoord()))
					{
						movePiece(TileJButton.getSelectedButton().getCoord(), tile.getCoord());
						legalTileIndexes.clear();
					}

					TileJButton.getSelectedButton().deselect();
				}
			}

			if(board.isGameOver()) gameOver();

			if(board.currentPlayer == Player.BLACK && !board.isGameOver())
			{
				//evaulator is run in a different thread so that the gui thread can update first
				SwingWorker<Move, Void> worker = new SwingWorker<>() {
					@Override
					protected Move doInBackground() {
						canPlay = false;
						Evaluator AI = new Evaluator(board, 3);
						AI.run();

						return AI.getBestMove();
					}

					protected void done() {
						try {
							Move bestMove = get();
							movePiece(bestMove.getStartPosition(), bestMove.getEndPosition());
							canPlay = true;
							if(board.isGameOver()) gameOver();
						} catch (InterruptedException | ExecutionException ignored) { }
					}
				};

				worker.execute();
			}
		}
	}

	private void gameOver()
	{
		String message = board.isHumanWinner() ? "You Win! New game?" : "Checkmate. New Game?";
		int n = JOptionPane
				.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION);

		if(n == JOptionPane.YES_OPTION)
		{
			removeAll();
			board = new ChessBoard();
			initBoard();
			validate();
		}
		else
		{
			canPlay = false;
		}
	}

	public void setLegalTiles(TileJButton tile)
	{
		IntStream legalTilesStream = board.getLegalTiles(tile.getCoord());
		legalTilesStream.parallel().forEach(i ->
		{
			if( i != tile.getCoord() )
			{
				legalTileIndexes.add(i);
				if(showLegalTiles) tiles[i].setasLegalTile(true);
			}
		});
	}

	public ChessBoard getBoard()
	{
		return board;
	}

	private void movePiece(int start, int end)
	{
		Player currentPlayer = board.currentPlayer;
		board.move(start, end);
		tiles[start].setImage(null);
		tiles[end].setImage(board.getPiece(end).getImageFileName());

		//pawn promotion
		if (board.getPiece(end) instanceof Pawn)
		{
			Player player = board.getPiece(end).getPlayer();
			int promotionRank = player == Player.BLACK ? 7 :  0;
			if (board.getPiece(end).getRank() == promotionRank)
			{
				Piece promotedPiece;
				if (!Player.isHumanPlayer(currentPlayer))
					promotedPiece = new Queen(player, board.getPiece(end).getPosition());
				else
				{
					//give player option
					String[] buttons = {"Rook", "Bishop", "Knight", "Queen"};
					int choice =JOptionPane.showOptionDialog(this,
							"Which piece will you promote your pawn to?",
							"Pawn Promotion",
							JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
							null,
							buttons,
							buttons[3]	);

					promotedPiece = createPromotedPiece(board.getPiece(end),choice);
				}

				board.promotePiece(promotedPiece);
				tiles[end].setImage(board.getPiece(end).getImageFileName());
			}
		}
	}

	public Piece createPromotedPiece(Piece pawn, int choice)
	{
		switch (choice)
		{
			case 0:
				return new Rook(pawn.getPlayer(),pawn.getPosition());
			case 1:
				return new Bishop(pawn.getPlayer(),pawn.getPosition());
			case 2:
				return new Knight(pawn.getPlayer(),pawn.getPosition());
			case 3:
				return new Queen(pawn.getPlayer(),pawn.getPosition());
			default:
				return pawn;
		}
	}

	public void toggleLegalTiles()
	{
		showLegalTiles = !showLegalTiles;
	}
}
