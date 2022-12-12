package Pieces;

import Board.*;

import java.io.Serializable;

public abstract class Piece implements Serializable
{
	protected final Player player;
	protected int position;
	protected int rank, file;
	private boolean isFirstMove = true;

	public Piece(Player player, int position)
	{
		this.player = player;
		this.position = position;
		rank = position / 8;
		file = position % 8;
	}

	public void setPosition(int position)
	{
		this.position = position;
		rank = position / 8;
		file = position % 8;
	}

	public int getPosition()
	{
		return position;
	}
	public boolean isFirstMove()
	{
		return isFirstMove;
	}
	public void setFirstMove(boolean firstMove)
	{
		isFirstMove = firstMove;
	}
	public Player getPlayer()
	{
		return player;
	}

	public String getImageFileName()
	{
		return String.format("images\\%s%s.png", player.toString(), this.toString());
	}
	public int getRank()
	{
		return rank;
	}

	public abstract int getScore();
	public abstract ChessBitSet calculateLegalBitSet(ChessBitSet occupied, ChessBitSet team);


}
