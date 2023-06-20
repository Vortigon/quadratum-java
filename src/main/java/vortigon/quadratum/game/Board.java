package vortigon.quadratum.game;

import java.util.Arrays;

public class Board {
	private int rows, columns;
	private int[][] board;
	private final int upLeftPlayerID, bottomRightPlayerID;

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

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public boolean checkCellBounds(int row, int col) {
		return row >= 0 && col >= 0 && row < rows && col < columns;
	}

	public boolean checkCell(int row, int col, int playerID) {
		if (playerID != upLeftPlayerID && playerID != bottomRightPlayerID)
		{ throw new RuntimeException("Invalid playerID"); }
		if (row == rows-1 && col == columns-1 && playerID == bottomRightPlayerID) { return true; }
		if (row == 0 && col == 0 && playerID == upLeftPlayerID) { return true; }

		return (row > 0 && board[row - 1][col] == playerID)
				|| (row < rows - 1 && board[row + 1][col] == playerID)
				|| (col > 0 && board[row][col - 1] == playerID)
				|| (col < columns - 1 && board[row][col + 1] == playerID);
	}

	public boolean checkTurn(Turn turn) {
		if (!checkCellBounds(turn.getBeginCellRow(), turn.getBeginCellColumn())) { return false; }
		if (!checkCellBounds(turn.getEndCellRow(), turn.getEndCellColumn())) { return false; }

		int cellsX = turn.getBeginCellColumn() - turn.getEndCellColumn();
		if (cellsX < 0) { cellsX = -cellsX; }
		cellsX += 1;

		int cellsY = turn.getBeginCellRow() - turn.getEndCellRow();
		if (cellsY < 0) { cellsY = -cellsY; }
		cellsY += 1;

		if (!((cellsX == turn.getDice1() && cellsY == turn.getDice2())
				|| (cellsX == turn.getDice2() && cellsY == turn.getDice1()))
		) { return false; }

		boolean cellCheckPassed = false;

		int col1 = Math.min(turn.getBeginCellColumn(), turn.getEndCellColumn());
		int col2 = Math.max(turn.getBeginCellColumn(), turn.getEndCellColumn());

		int row1 = turn.getBeginCellRow();
		int row2 = turn.getEndCellRow();
		while (col1 <= col2) {
			if (board[row1][col1] != -1 || board[row2][col1] != -1) { return false; }
			if (!cellCheckPassed) {
				if (checkCell(row1, col1, turn.getPlayerId()) || checkCell(row2, col1, turn.getPlayerId())) {
					cellCheckPassed = true;
				}
			}
			++col1;
		}

		row1 = Math.min(turn.getBeginCellRow(), turn.getEndCellRow());
		row2 = Math.max(turn.getBeginCellRow(), turn.getEndCellRow());

		col1 = turn.getBeginCellColumn();
		col2 = turn.getEndCellColumn();

		while (row1 <= row2) {
			if (board[row1][col1] != -1 || board[row1][col2] != -1) { return false; }
			if (!cellCheckPassed) {
				if (checkCell(row1, col1, turn.getPlayerId()) || checkCell(row1, col2, turn.getPlayerId())) {
					cellCheckPassed = true;
				}
			}
			++row1;
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
		if (dice1 == 1 && dice2 == 1 && board[row][col] == -1) { return true; }

		Turn turn;
		if (dice1 == dice2) {
			turn = new Turn(playerID, row, col, row+dice1-1, col+dice2-1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row+dice1-1, col-dice2+1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row-dice1+1, col-dice2+1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row-dice1+1, col+dice2-1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
		} else if (dice1 == 1 || dice2 == 1) {
			int dice = Math.max(dice1, dice2);

			turn = new Turn(playerID, row, col, row+dice-1, col, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row-dice+1, col, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row, col+dice-1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row, col-dice+1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
		} else {
			turn = new Turn(playerID, row, col, row+dice1-1, col+dice2-1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row+dice1-1, col-dice2+1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row-dice1+1, col-dice2+1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row-dice1+1, col+dice2-1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row+dice2-1, col+dice1-1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row+dice2-1, col-dice1+1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row-dice2+1, col-dice1+1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
			turn = new Turn(playerID, row, col, row-dice2+1, col+dice1-1, dice1, dice2);
			if (checkTurn(turn)) { return true; }
		}
		return false;
	}

	public boolean hasAvailableTurns(int playerID, int dice1, int dice2) {
		if (playerID != upLeftPlayerID && playerID != bottomRightPlayerID)
			{ throw new RuntimeException("Invalid playerID"); }

		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < columns; ++col) {
				if (!checkCellBounds(row, col)) { return false; }
				if (dice1 == 1 && dice2 == 1 && board[row][col] == -1) { return true; }

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
