package views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import main.LoginController;
import util.SceneChanger;

public class OptionsController implements Initializable {

	@FXML
	public void addReceipt() {
		if (LoginController.oneWindow == 0) {
			SceneChanger.loadWindow(getClass().getResource("/options/AddReceipt.fxml"), "Add new receipt", null);
			LoginController.oneWindow++;
		}
	}

	public void showProducts() {
		if (LoginController.oneWindow == 0) {
			SceneChanger.loadWindow(getClass().getResource("/options/MyProducts.fxml"), "My products", null);
			LoginController.oneWindow++;
		}
	}

	public void setGoals() {
		if (LoginController.oneWindow == 0) {
			SceneChanger.loadWindow(getClass().getResource("/options/Goals.fxml"), "Set goals", null);
			LoginController.oneWindow++;
		}
	}

	public void showReceipts() {
		if (LoginController.oneWindow == 0) {
			SceneChanger.loadWindow(getClass().getResource("/options/MyReceipts.fxml"), "My receipts", null);
			LoginController.oneWindow++;
		}
	}

	public void manageBalance() {
		if (LoginController.oneWindow == 0) {
			SceneChanger.loadWindow(getClass().getResource("/options/ManageBalance.fxml"), "Your account", null);
			LoginController.oneWindow++;
		}
	}

	@FXML
	public void logout(ActionEvent event) throws IOException {
		if (LoginController.oneWindow == 0) {
			SceneChanger sc = new SceneChanger();
			sc.changeScenes(event, "/main/Login.fxml", "Log in");
		}
	}

	@FXML
	public void exit() {
		if (LoginController.oneWindow == 0) {
			System.exit(0);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
