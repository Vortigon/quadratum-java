package vortigon.quadratum;

import vortigon.quadratum.controllers.MenuController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vortigon.quadratum.controllers.GameController;

import java.io.IOException;

public class Main extends Application {
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;
	private static Stage primaryStage;
	private static Scene gameScene, menuScene;
	private static MenuController menuController;
	private static GameController gameController;
	@Override
	public void start(Stage stage) throws IOException {

		primaryStage = stage;
		FXMLLoader loader = new FXMLLoader((getClass().getResource("/vortigon/quadratum/fxml/menu.fxml")));
		loader.setControllerFactory(controllerClass -> new MenuController());

		menuScene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
		menuController = loader.getController();

		stage.setResizable(false);
		stage.setTitle("Quadratum");

		loader = new FXMLLoader((getClass().getResource("/vortigon/quadratum/fxml/game.fxml")));
		loader.setControllerFactory(controllerClass ->new GameController());

		gameScene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
		gameController = loader.getController();
		menuController.setGameController(gameController);

		stage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});

		setMenuScene();
		stage.show();
	}

	public static void setGameScene() {
		primaryStage.setScene(gameScene);
		gameController.setupMouseHandling();
	}

	public static void setMenuScene() {
		primaryStage.setScene(menuScene);
	}

	public static void main(String[] args) {
		launch();
	}
}