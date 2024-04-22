package vortigon.quadratum.game;
import vortigon.quadratum.game.Board.Cell;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
public class BotPlayer extends Player {
	private List<Cell> availableCells = new LinkedList<>();
	private Turn lastTurn = null;

	public BotPlayer() {
	}

	public static class NoAvailableTurnsException extends Exception {
		public NoAvailableTurnsException() {
			super();
		}

		public NoAvailableTurnsException(String message) {
			super(message);
		}
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

	public Turn makeTurn(Board board, int dice1, int dice2) throws NoAvailableTurnsException {
		availableCells = board.getAvailableCells(getId(), dice1, dice2);
		if (availableCells.isEmpty()) { throw new NoAvailableTurnsException("Bot couldn't make turn: No available cells for turns"); }
		lastTurn = getRandomTurn(board, dice1, dice2);
		return lastTurn;
	}

	private Turn getRandomTurn(Board board, int dice1, int dice2) {
		Cell beginCell = availableCells.get(ThreadLocalRandom.current().nextInt(0, availableCells.size()));
		Cell[] endCells = new Cell[8];
		endCells[0] = new Cell(beginCell.getRow() + dice1-1, beginCell.getCol() + dice2-1);
		endCells[1] = new Cell(beginCell.getRow() + dice1-1, beginCell.getCol() - dice2+1);
		endCells[2] = new Cell(beginCell.getRow() - dice1+1, beginCell.getCol() - dice2+1);
		endCells[3] = new Cell(beginCell.getRow() - dice1+1, beginCell.getCol() + dice2-1);
		endCells[4] = new Cell(beginCell.getRow() + dice2-1, beginCell.getCol() + dice1-1);
		endCells[5] = new Cell(beginCell.getRow() + dice2-1, beginCell.getCol() - dice1+1);
		endCells[6] = new Cell(beginCell.getRow() - dice2+1, beginCell.getCol() - dice1+1);
		endCells[7] = new Cell(beginCell.getRow() - dice2+1, beginCell.getCol() + dice1-1);

		for (int i = 0; i < endCells.length; ++i) {
			int endCell = ThreadLocalRandom.current().nextInt(0, endCells.length-i);
			lastTurn = new Turn(getId(), beginCell.getRow(), beginCell.getCol(),
					endCells[endCell].getRow(), endCells[endCell].getCol(), dice1, dice2);
			if (board.checkTurn(lastTurn)) { break; }
			if (endCell < endCells.length-1) {
				endCells[endCell] = endCells[endCells.length-i-1];
			}
		}

		return lastTurn;
	}
}
