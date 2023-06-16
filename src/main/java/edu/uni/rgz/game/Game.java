package edu.uni.rgz.game;

import edu.uni.rgz.controllers.GameController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

public class Game {
	private Board board;
	private Player player1, player2;
	private Player currentPlayer;
	private SimpleIntegerProperty playerIdTurnProperty = new SimpleIntegerProperty();
	private SimpleIntegerProperty dice1Property = new SimpleIntegerProperty(), dice2Property = new SimpleIntegerProperty();
	private int dice1, dice2;
	private boolean gameEnded = false;
	private SimpleBooleanProperty gameEndedProperty = new SimpleBooleanProperty(gameEnded);
	private SimpleIntegerProperty winnerIdProperty = new SimpleIntegerProperty();
	private Player winner = null;
	private GameController controller;

	public Game(int width, int height, Player player1, Player player2, GameController controller) {
		this.player1 = player1;
		this.player2 = player2;
		player1.setId(0);
		player1.setColor(Color.PURPLE);
		player2.setId(1);
		player2.setColor(Color.CRIMSON);
		board = new Board(width, height, 0, 1);
		this.controller = controller;
		if (player1 instanceof BotPlayer bot) { bot.setupUpLeftPlayer(); }
		else if (player2 instanceof BotPlayer bot) { bot.setupBottomRightPlayer(width-1, height-1); }
		currentPlayer = player1;
		playerIdTurnProperty.setValue(currentPlayer.getId() + 1);
		newTurnDices();
	}

	public Board getBoard() {
		return board;
	}

	public void newTurnDices() {
		dice1 = ThreadLocalRandom.current().nextInt(1, 7);
		dice2 = ThreadLocalRandom.current().nextInt(1, 7);
		dice1Property.setValue(dice1);
		dice2Property.setValue(dice2);
	}

	public int getDice1() {
		return dice1;
	}

	public int getDice2() {
		return dice2;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public SimpleIntegerProperty getPlayer1ScoreProperty() {
		return player1.getScoreProperty();
	}

	public SimpleIntegerProperty getPlayer2ScoreProperty() {
		return player2.getScoreProperty();
	}

	public SimpleIntegerProperty getDice1Property() {
		return dice1Property;
	}

	public SimpleIntegerProperty getDice2Property() {
		return dice2Property;
	}

	public SimpleIntegerProperty playerIdTurnProperty() {
		return playerIdTurnProperty;
	}

	public SimpleBooleanProperty getGameEndedProperty() {
		return gameEndedProperty;
	}

	public boolean isGameEnded() {
		return gameEnded;
	}

	public SimpleIntegerProperty getWinnerIdProperty() {
		return winnerIdProperty;
	}

	public Player getWinner() {
		return winner;
	}

	public void makeTurn(Turn turn) {
		board.makeTurn(turn);
		currentPlayer.addScore(dice1 * dice2);
		currentPlayer = currentPlayer == player1 ? player2 : player1;
		playerIdTurnProperty.setValue(currentPlayer.getId() + 1);
		newTurnDices();
		if (!board.hasAvailableTurns(currentPlayer.getId(), dice1, dice2)) {

			winner = currentPlayer == player1 ? player2 : player1;
			winnerIdProperty.setValue(winner.getId()+1);
			gameEnded = true;
			gameEndedProperty.setValue(true);
		} else if (currentPlayer instanceof BotPlayer bot) {
			bot.updateInfo(board, dice1, dice2);
			board.makeTurn(bot.makeTurn(board, dice1, dice2));
			bot.addScore(dice1 * dice2);
			controller.addBotRectangle(bot);
			currentPlayer = currentPlayer == player1 ? player2 : player1;
			playerIdTurnProperty.setValue(currentPlayer.getId() + 1);
			newTurnDices();
		}
	}
}
