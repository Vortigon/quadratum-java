package edu.uni.rgz.controllers;

import edu.uni.rgz.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MenuController {
//	private AnchorPane gameRoot;
	@FXML private AnchorPane menuRoot;
	@FXML private Button cpuMatchButton;
	@FXML private Button localMatchButton;
	@FXML private Button exitButton;
	private GameController gameController;


//	public void setGameRoot(AnchorPane gameRoot) {
//		this.gameRoot = gameRoot;
//	}
//
	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	@FXML
	public void onCpuMatchButtonClick() {
		Main.setGameScene();
		gameController.setupCpuMatch(null);
	}

	@FXML
	public void onLocalMatchButtonClick() {
//		menuRoot.getScene().setRoot(gameRoot);
		Main.setGameScene();
		gameController.setupLocalMatch(null);
	}

	@FXML
	public void onExitButtonClick() {
		Platform.exit();
	}
}