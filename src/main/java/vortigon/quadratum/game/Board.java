package vortigon.quadratum.game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Board {
	private int rows, columns;
	private int[][] board;
	private final int upLeftPlayerID, bottomRightPlayerID;

	public enum Corner {
		UP_LEFT,
		UP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}

	public Board(int rows, int columns, int upLeftPlayerID, int bottomRightPlayerID) {
		this.rows = rows;
		this.columns = columns;
		board = new int[rows][columns];
		for (int i = 0; i < rows; ++i) {
			Arrays.fill(board[i], -1);
		}
		this.upLeftPlayerID = upLeftPlayerID;
		this.bottomRightPlayerID = bottomRightPlayerID;
	}

	public static class Cell {
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

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public boolean checkCellBounds(int row, int col) {
		return row >= 0 && col >= 0 && row < rows && col < columns;
	}

	public boolean checkCellOccupied(int row, int col) { return board[row][col] != -1; }

	public boolean checkCellNeighbours(int row, int col, int playerID) {
		if (playerID != upLeftPlayerID && playerID != bottomRightPlayerID)
		{ throw new RuntimeException("Invalid playerID"); }
		if ((playerID == bottomRightPlayerID && row == rows-1 && col == columns-1)
				|| (playerID == upLeftPlayerID && row == 0 && col == 0))
		{ return true; }

		return (row > 0 && board[row - 1][col] == playerID)
				|| (row < rows - 1 && board[row + 1][col] == playerID)
				|| (col > 0 && board[row][col - 1] == playerID)
				|| (col < columns - 1 && board[row][col + 1] == playerID);
	}

	public boolean checkTurn(Turn turn) {
		if (!checkCellBounds(turn.getBeginCellRow(), turn.getBeginCellColumn())) { return false; }
		if (!checkCellBounds(turn.getEndCellRow(), turn.getEndCellColumn())) { return false; }

		int col1 = Math.min(turn.getBeginCellColumn(), turn.getEndCellColumn());
		int col2 = Math.max(turn.getBeginCellColumn(), turn.getEndCellColumn());

		int row1 = Math.min(turn.getBeginCellRow(), turn.getEndCellRow());
		int row2 = Math.max(turn.getBeginCellRow(), turn.getEndCellRow());

		int cellsX = col2 - col1 + 1;
		int cellsY = row2 - row1 + 1;

		if (!((cellsX == turn.getDice1() && cellsY == turn.getDice2())
				|| (cellsX == turn.getDice2() && cellsY == turn.getDice1()))
		) { return false; }

		boolean cellCheckPassed = false;

		int iter = col1;
		while (iter <= col2) {
			if (checkCellOccupied(row1, iter) || checkCellOccupied(row2, iter)) { return false; }
			if (!cellCheckPassed) {
				if (checkCellNeighbours(row1, iter, turn.getPlayerId())
						|| (row1 != row2 && checkCellNeighbours(row2, iter, turn.getPlayerId()))) {
					cellCheckPassed = true;
				}
			}
			++iter;
		}

		iter = row1;
		while (iter <= row2) {
			if (checkCellOccupied(iter, col1) || checkCellOccupied(iter, col2)) { return false; }
			if (!cellCheckPassed) {
				if (checkCellNeighbours(iter, col1, turn.getPlayerId())
						|| (col1 != col2 && checkCellNeighbours(iter, col2, turn.getPlayerId()))) {
					cellCheckPassed = true;
				}
			}
			++iter;
		}
		return cellCheckPassed;
	}

	public void makeTurn(Turn turn) {
		int row1 = Math.min(turn.getBeginCellRow(), turn.getEndCellRow());
		int row2 = Math.max(turn.getBeginCellRow(), turn.getEndCellRow());
		int col1 = Math.min(turn.getBeginCellColumn(), turn.getEndCellColumn());
		int col2 = Math.max(turn.getBeginCellColumn(), turn.getEndCellColumn());

		for (int row =row1; row <= row2; ++row) {
			for (int col = col1; col <= col2; ++col) {
				board[row][col] = turn.getPlayerId();
			}
		}
	}

	public boolean cellHasTurns(int row, int col, int playerID, int dice1, int dice2) {
		if (!checkCellBounds(row, col)) { return false; }
		if (dice1 == 1 && dice2 == 1 && !checkCellOccupied(row, col) && checkCellNeighbours(row, col, playerID)) {
			return true;
		}

		final int[] deltas = {-1, 1};

		if (dice1 == dice2) {
			for (int rowDelta : deltas) {
				for (int colDelta : deltas) {
					int newRow = row + dice1 * rowDelta - rowDelta,
						newCol = col + dice2 * colDelta - colDelta;

					if (checkTurn(new Turn(playerID, row, col, newRow, newCol, dice1, dice2))) {
						return true;
					}
				}
			}
 		}

		if (dice1 == 1 || dice2 == 1) {
			for (int delta : deltas) {
				int newRow = row + Math.max(dice1, dice2) * delta - delta;
				int newCol = newRow - row + col;

				if (checkTurn(new Turn(playerID, row, col, newRow, col, dice1, dice2))
						|| checkTurn(new Turn(playerID, row, col, row, newCol, dice1, dice2))) {
					return true;
				}
			}
		}

		for (int rowDelta : deltas) {
			for (int colDelta : deltas) {
				int newRow1 = row + dice1 * rowDelta - rowDelta;
				int newRow2 = row + dice2 * rowDelta - rowDelta;
				int newCol1 = col + dice1 * colDelta - colDelta;
				int newCol2 = col + dice2 * colDelta - colDelta;

				if (checkTurn(new Turn(playerID, row, col, newRow1, newCol2, dice1, dice2))
						|| checkTurn(new Turn(playerID, row, col, newRow2, newCol1, dice1, dice2))) {
					return true;
				}
			}
		}

		return false;
	}

	public List<Cell> getAvailableCells(int playerID, int dice1, int dice2) {
		LinkedList<Cell> availableCells = new LinkedList<>();

		if (playerID != upLeftPlayerID && playerID != bottomRightPlayerID)
		{ return availableCells; }

		for (int row = 0; row < rows; ++row) {
			int newRow1 = row-dice1+1;
			int newRow2 = row-dice2+1;

			for (int col = 0; col < columns; ++col) {
				if (!checkCellBounds(row, col)) { continue; }
				if (dice1 == 1 && dice2 == 1 && !checkCellOccupied(row, col) && checkCellNeighbours(row, col, playerID)) {
					availableCells.add(new Cell(row, col));
				} else if (dice1 == dice2) {
					if (checkTurn(new Turn(playerID, row, col, newRow1, col+dice2-1, dice1, dice2))) {
						availableCells.add(new Cell(row, col));
					}
				} else if (dice1 == 1 || dice2 == 1) {
					int newRow = dice1 > dice2 ? newRow1 : newRow2;
					int newCol = newRow - row + col;
					if (checkTurn(new Turn(playerID, row, col, newRow, col, dice1, dice2))
							|| checkTurn(new Turn(playerID, row, col, row, newCol, dice1, dice2))) {
						availableCells.add(new Cell(row, col));
					}
				} else {
					if (checkTurn(new Turn(playerID, row, col, newRow1, col+dice2-1, dice1, dice2))
							|| checkTurn(new Turn(playerID, row, col, newRow2, col+dice1-1, dice1, dice2))) {
						availableCells.add(new Cell(row, col));
					}
				}
			}
		}

		return availableCells;
	}

	public boolean playerHasAvailableTurns(int playerID, int dice1, int dice2) {
		if (playerID != upLeftPlayerID && playerID != bottomRightPlayerID)
			{ throw new RuntimeException("Invalid playerID"); }

		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < columns; ++col) {
				if (!checkCellBounds(row, col)) { return false; }
				if (dice1 == 1 && dice2 == 1 && !checkCellOccupied(row, col) && checkCellNeighbours(row, col, playerID)) {
					return true;
				}

				Turn turn;
				if (dice1 == dice2) {
					turn = new Turn(playerID, row, col, row-dice1+1, col+dice2-1, dice1, dice2);
					if (checkTurn(turn)) {
						return true;
					}
				} else if (dice1 == 1 || dice2 == 1) {
					int dice = Math.max(dice1, dice2);
					turn = new Turn(playerID, row, col, row-dice+1, col, dice1, dice2);
					if (checkTurn(turn)) {
						return true;
					}

					turn = new Turn(playerID, row, col, row, col+dice-1, dice1, dice2);
					if (checkTurn(turn)) {
						return true;
					}
				} else {
					turn = new Turn(playerID, row, col, row-dice1+1, col+dice2-1, dice1, dice2);
					if (checkTurn(turn)) {
						return true;
					}
					turn = new Turn(playerID, row, col, row-dice2+1, col+dice1-1, dice1, dice2);
					if (checkTurn(turn)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
