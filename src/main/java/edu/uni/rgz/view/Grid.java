package edu.uni.rgz.view;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class Grid{
	private Pane pane;
	private final int rows, columns;
	private final double cellWidth, cellHeight;

	public Grid(Pane pane, int rows, int columns) {
		this.pane = pane;
		this.rows = rows;
		this.columns = columns;

		double x = pane.getLayoutX();
		double y = pane.getLayoutY();
		double width = pane.getWidth();
		double height = pane.getHeight();

		cellWidth = width/columns;
		cellHeight = height/rows;
		for (int r = 0; r <= rows; ++r) {
			pane.getChildren().add(new Line(0, cellHeight*r, width, cellHeight*r));
		}
		for (int c = 0; c <= columns; ++c) {
			pane.getChildren().add(new Line(cellWidth*c, 0, cellWidth*c, height));
		}
	}

	public int getCellColumn(double x) {
		int column = (int)(x/cellWidth);
		if (column <= 0) { return 0; }
		if (column >= columns) { return columns-1; }
		return column;
	}

	public int getCellRow(double y) {
		int row = (int)(y/cellHeight);
		if (row <= 0) { return 0; }
		if (row >= columns) { return columns-1; }
		return row;
	}

	public double getCellWidth() {
		return cellWidth;
	}

	public double getCellHeight() {
		return cellHeight;
	}

	public double getCellX(int column) {
		return cellWidth * column;
	}

	public double getCellY(int row) {
		return cellHeight * row;
	}
}
