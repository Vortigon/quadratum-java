package vortigon.quadratum.controllers;

import vortigon.quadratum.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MenuController {
	@FXML private AnchorPane menuRoot;
	@FXML private Button cpuMatchButton;
	@FXML private Button localMatchButton;
	@FXML private Button exitButton;
	@FXML private Button createServerButton;
	@FXML private Button joinServerButton;
	private GameController gameController;

	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	@FXML
	public void onCreateServerButtonClick() {
	}

	@FXML
	public void onJoinServerButtonClick() {

	}

	@FXML
	public void onCpuMatchButtonClick() {
		Main.setGameScene();
		gameController.setupCpuMatch(null);
	}

	@FXML
	public void onLocalMatchButtonClick() {
		Main.setGameScene();
		gameController.setupLocalMatch(null);
	}

	@FXML
	public void onExitButtonClick() {
		Platform.exit();
	}
}
