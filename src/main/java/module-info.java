module edu.uni.rgz {
	requires javafx.controls;
	requires javafx.fxml;

	opens edu.uni.rgz.controllers to javafx.fxml;
	opens edu.uni.rgz to javafx.fxml;
	exports edu.uni.rgz;
}