package vortigon.quadratum.game;

public class Turn {
	private final int madeByPlayerId;
	private final int beginCellRow, beginCellColumn, endCellRow, endCellColumn;
	private final int dice1, dice2;

	public Turn(int id, int row1, int col1, int row2, int col2, int dice1, int dice2) {
		madeByPlayerId = id;
		beginCellRow = row1;
		beginCellColumn = col1;
		endCellRow = row2;
		endCellColumn = col2;
		this.dice1 = dice1;
		this.dice2 = dice2;
	}

	@Override
	public String toString() {
		return "ID: " + getPlayerId() + " " + beginCellRow + ":" + beginCellColumn + "->" + endCellRow + ":" + endCellColumn + " on " + dice1 + "x" + dice2;
	}

	public int getBeginCellColumn() {
		return beginCellColumn;
	}

	public int getBeginCellRow() {
		return beginCellRow;
	}

	public int getEndCellColumn() {
		return endCellColumn;
	}

	public int getEndCellRow() {
		return endCellRow;
	}

	public int getPlayerId() {
		return madeByPlayerId;
	}

	public int getDice1() {
		return dice1;
	}

	public int getDice2() {
		return dice2;
	}
}
