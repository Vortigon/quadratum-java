package edu.uni.rgz.controllers;

import edu.uni.rgz.Main;
import edu.uni.rgz.game.*;
import edu.uni.rgz.view.Grid;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;

public class GameController {
	private Game gameInstance;
	@FXML private AnchorPane gameRoot;
	@FXML private Pane paneWithGrid;
	@FXML private Label turnLabel;
	@FXML private Label diceLabel;
	@FXML private Label player1ScoreLabel, player2ScoreLabel;
	@FXML private Label winnerLabel;
	private Grid grid;

	private Rectangle drawingRectangle = null;
	private Turn drawingRectangleTurn = null;
	private double drawingStartX, drawingStartY;
	private int drawingStartColumn, drawingStartRow;
	private int drawingColumn, drawingRow;

	public GameController() {}

	private boolean checkDrawingRectangleTurn() {
		if (drawingRectangle == null) { return false; }

		int cellsX = drawingColumn - drawingStartColumn;
		if (cellsX < 0) cellsX = -cellsX;
		++cellsX;
		int cellsY = drawingRow - drawingStartRow;
		if (cellsY < 0) cellsY = -cellsY;
		++cellsY;

		int col1 = drawingStartColumn;
		int row1 = drawingStartRow;
		int col2 = drawingColumn > drawingStartColumn ?
				drawingStartColumn + cellsX - 1 :
				drawingStartColumn - cellsX + 1;
		int row2 = drawingRow > drawingStartRow ?
				drawingStartRow + cellsY - 1 :
				drawingStartRow - cellsY + 1;

		drawingRectangleTurn = new Turn(gameInstance.getCurrentPlayer().getId(), row1, col1, row2, col2,
				gameInstance.getDice1(), gameInstance.getDice2());
		return gameInstance.getBoard().checkTurn(drawingRectangleTurn);
	}

	public void setupMouseHandling() {
		gameRoot.getScene().setOnMousePressed((MouseEvent event) -> {
			if (gameInstance.isGameEnded() || !(gameInstance.getCurrentPlayer() instanceof LocalPlayer)) { return; }

			drawingStartRow = grid.getCellRow(event.getY());
			drawingStartColumn = grid.getCellColumn(event.getX());
			drawingRow = drawingStartRow;
			drawingColumn = drawingStartColumn;

			drawingStartX = grid.getCellX(drawingStartColumn);
			drawingStartY = grid.getCellY(drawingStartRow);
			drawingRectangle = new Rectangle(drawingStartX, drawingStartY, grid.getCellWidth(), grid.getCellHeight());
			drawingRectangle.setFill(Color.RED);
			drawingRectangle.setOpacity(0.5);
			paneWithGrid.getChildren().add(drawingRectangle);

		});

		gameRoot.getScene().setOnMouseReleased((MouseEvent event) -> {
			if (gameInstance.isGameEnded()) { return; }
			if (checkDrawingRectangleTurn()) {
				drawingRectangle.setOpacity(0.8);
				drawingRectangle.setFill(gameInstance.getCurrentPlayer().getColor());
				gameInstance.makeTurn(drawingRectangleTurn);
			} else {
				paneWithGrid.getChildren().remove(drawingRectangle);
			}
			drawingRectangle = null;
		});

		gameRoot.getScene().setOnMouseDragged((MouseEvent event) -> {
			if (gameInstance.isGameEnded() || drawingRectangle == null) { return; }
			int eventCellColumn = grid.getCellColumn(event.getX());
			int eventCellRow = grid.getCellRow(event.getY());
			double eventCellX = grid.getCellX(eventCellColumn);
			double eventCellY = grid.getCellY(eventCellRow);

			if (eventCellColumn < grid.getCellColumn(drawingRectangle.getX())) {
				drawingRectangle.setX(eventCellX);
				drawingRectangle.setWidth(grid.getCellWidth() * (1 + drawingStartColumn - eventCellColumn));
			} else {
				if (eventCellColumn > drawingStartColumn) {
					drawingRectangle.setX(drawingStartX);
					drawingRectangle.setWidth(grid.getCellWidth() * (1 + eventCellColumn - drawingStartColumn));
				} else {
					drawingRectangle.setX(eventCellX);
					drawingRectangle.setWidth(grid.getCellWidth() * (1 + drawingStartColumn - eventCellColumn));
				}
			}

			if (eventCellRow < grid.getCellRow(drawingRectangle.getY())) {
				drawingRectangle.setY(eventCellY);
				drawingRectangle.setHeight(grid.getCellHeight() * (1 + drawingStartRow - eventCellRow));
			} else {
				if (eventCellRow > drawingStartRow) {
					drawingRectangle.setY(drawingStartY);
					drawingRectangle.setHeight(grid.getCellHeight() * (1 + eventCellRow - drawingStartRow));
				} else {
					drawingRectangle.setY(eventCellY);
					drawingRectangle.setHeight(grid.getCellHeight() * (1 + drawingStartRow - eventCellRow));
				}
			}
			drawingColumn = eventCellColumn;
			drawingRow = eventCellRow;
			if (checkDrawingRectangleTurn()) {
				drawingRectangle.setFill(Color.GREEN);
			} else {
				drawingRectangle.setFill(Color.RED);
			}
		});
	}

	public void setupLocalMatch(WindowEvent ignored) {
		gameInstance = new Game(35, 35, new LocalPlayer(), new LocalPlayer(), this);
		grid = new Grid(paneWithGrid, 35, 35);
		bindLabels();
	}

	private void bindTurnLabel() {
		turnLabel.textProperty().bind(Bindings.concat("Turn: Player ", gameInstance.playerIdTurnProperty()));
	}

	private void bindDiceLabel() {
		diceLabel.textProperty().bind(Bindings
				.concat("Dice: ", gameInstance.getDice1Property(), " & ", gameInstance.getDice2Property()));
	}

	private void bindScoreLabels() {
		player1ScoreLabel.textProperty().bind(Bindings
				.concat("Score: ", gameInstance.getPlayer1ScoreProperty()));
		player2ScoreLabel.textProperty().bind(Bindings
				.concat("Score: ", gameInstance.getPlayer2ScoreProperty()));
	}

	private void bindWinnerLabel() {
		winnerLabel.visibleProperty().bind(gameInstance.getGameEndedProperty());
		winnerLabel.textProperty().bind(Bindings.concat("Winner: Player ", gameInstance.getWinnerIdProperty()));
	}

	private void bindLabels() {
		bindTurnLabel();
		bindDiceLabel();
		bindScoreLabels();
		bindWinnerLabel();
	}

	public void addBotRectangle(BotPlayer bot) {
			Turn newTurn = bot.getLastTurn();
			int col1 = Math.min(newTurn.getBeginCellColumn(), newTurn.getEndCellColumn());
			int col2 = Math.max(newTurn.getBeginCellColumn(), newTurn.getEndCellColumn());
			int row1 = Math.min(newTurn.getBeginCellRow(), newTurn.getEndCellRow());
			int row2 = Math.max(newTurn.getBeginCellRow(), newTurn.getEndCellRow());

			double x = grid.getCellX(col1), y = grid.getCellY(row1);
			double width = grid.getCellWidth() + grid.getCellX(col2) - x;
			double height = grid.getCellHeight() + grid.getCellY(row2) - y;
			Rectangle botRectangle = new Rectangle(x, y, width, height);
			botRectangle.setFill(bot.getColor());
			botRectangle.setOpacity(0.8);

			paneWithGrid.getChildren().add(botRectangle);
	}

	public void setupCpuMatch(WindowEvent ignored) {
		gameInstance = new Game(35, 35, new LocalPlayer(), new BotPlayer(), this);
		grid = new Grid(paneWithGrid, 35, 35);
		bindLabels();
	}

	@FXML
	public void exit() {
		Main.setMenuScene();
		gameInstance = null;
		paneWithGrid.getChildren().clear();
		grid = null;
	}
}
