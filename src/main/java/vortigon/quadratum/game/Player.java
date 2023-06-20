package vortigon.quadratum.game;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public abstract class Player {
	private int id;
	private Color color;
	private int score = 0;
	private SimpleIntegerProperty scoreProperty = new SimpleIntegerProperty();

	public Player() {
	}

	public int getScore() {
		return score;
	}

	public void addScore(int score) {
		this.score+=score;
		scoreProperty.setValue(this.score);
	}

	public SimpleIntegerProperty getScoreProperty() {
		return scoreProperty;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getId() {
		return id;
	}

	public Color getColor() {
		return color;
	}
}
