package edu.uni.rgz.game;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BotPlayer extends Player {
	private class Cell {
		private int row, col;

		public Cell(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public int getCol() {
			return col;
		}

		public int getRow() {
			return row;
		}
	}

	private ArrayList<Cell> availableCells = new ArrayList<>();
	private Turn lastTurn = null;


	public BotPlayer() {
	}

	public void setupUpLeftPlayer() {
		availableCells.add(new Cell(0,0));
	}

	public void setupBottomRightPlayer(int x, int y) {
		availableCells.add(new Cell(x ,y));
	}

	public Turn getLastTurn() {
		return lastTurn;
	}

	public void updateInfo(Board board, int dice1, int dice2) {
		availableCells = new ArrayList<>();
		for (int row = 0; row < board.getRows(); ++row) {
			for (int col = 0; col < board.getColumns(); ++col) {
				if (board.cellHasTurns(row, col, getId(), dice1, dice2)) {
					availableCells.add(new Cell(row, col));
				}
			}
		}
	}

	public Turn makeTurn(Board board, int dice1, int dice2) {
		Cell beginCell = availableCells.get(ThreadLocalRandom.current().nextInt(0, availableCells.size()));
		Cell[] endCells = new Cell[8];
		endCells[0] = new Cell(beginCell.row+dice1-1, beginCell.col+dice2-1);
		endCells[1] = new Cell(beginCell.row+dice1-1, beginCell.col-dice2+1);
		endCells[2] = new Cell(beginCell.row-dice1+1, beginCell.col-dice2+1);
		endCells[3] = new Cell(beginCell.row-dice1+1, beginCell.col+dice2-1);
		endCells[4] = new Cell(beginCell.row+dice2-1, beginCell.col+dice1-1);
		endCells[5] = new Cell(beginCell.row+dice2-1, beginCell.col-dice1+1);
		endCells[6] = new Cell(beginCell.row-dice2+1, beginCell.col-dice1+1);
		endCells[7] = new Cell(beginCell.row-dice2+1, beginCell.col+dice1-1);

		for (int i = 0; i < endCells.length; ++i) {
			int endCell = ThreadLocalRandom.current().nextInt(0, endCells.length-i);
			lastTurn = new Turn(getId(), beginCell.row, beginCell.col,
					endCells[endCell].row, endCells[endCell].col, dice1, dice2);
			if (board.checkTurn(lastTurn)) {

				break;
			} else {
				if (endCell < endCells.length-1) {
					endCells[endCell] = endCells[endCells.length-i-1];
				}
			}
		}
		return lastTurn;
	}
}
