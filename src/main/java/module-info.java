module edu.uni.rgz {
	requires javafx.controls;
	requires javafx.fxml;

	opens vortigon.quadratum.controllers to javafx.fxml;
	opens vortigon.quadratum to javafx.fxml;
	exports vortigon.quadratum;
}