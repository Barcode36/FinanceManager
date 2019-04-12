package views;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import main.LoginController;
import models.User;
import security.PasswordGen;

public class NewUserController implements Initializable {

	@FXML
	private JFXTextField nameField;

	@FXML
	private JFXPasswordField passwordField;
	@FXML
	private JFXTextField questionField;
	@FXML
	private JFXPasswordField repeatField;

	@FXML
	private JFXTextField answerField;
	@FXML
	private JFXButton backBtn;

	private int validPassword = 0;

	@FXML
	public void add(ActionEvent event) throws NoSuchAlgorithmException, InvalidKeySpecException {
		int isValid = 0;
		if (nameField.getText().length() < 2 || !nameField.getText().matches("[a-zA-z]+")) {
			nameField.getStyleClass().add("invalid-input");
			isValid++;
		}else {
			nameField.getStyleClass().add("valid-input");
		}
		if (questionField.getText().isEmpty()) {
			questionField.getStyleClass().add("invalid-input");
			isValid++;
		}else {
			questionField.getStyleClass().add("valid-input");
		}
		if (answerField.getText().isEmpty()) {
			answerField.getStyleClass().add("invalid-input");
			isValid++;
		}else {
			answerField.getStyleClass().add("valid-input");
		}
		if (validPassword == 0) {
			passwordField.getStyleClass().add("invalid-input");
			repeatField.getStyleClass().add("invalid-input");
			isValid++;
		}else {
			passwordField.getStyleClass().add("valid-input");
			repeatField.getStyleClass().add("valid-input");
		}
		if (isValid == 0 && validPassword > 0) {
			try {
				User user = new User(nameField.getText(), PasswordGen.generatePassword(passwordField.getText()),
						questionField.getText(), answerField.getText());
				user.insertIntoDB();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@FXML
	public void back(ActionEvent event) {
		LoginController.oneWindow = 0;
		Stage stage = (Stage) backBtn.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void checkPassword(ActionEvent event) throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (passwordField.getText().length() > 4) {
			if (passwordField.getText().equals(repeatField.getText())) {
				passwordField.getStyleClass().add("valid-input");
				repeatField.getStyleClass().add("valid-input");
				validPassword++;
			} else {
				passwordField.getStyleClass().add("invalid-input");
				repeatField.getStyleClass().add("invalid-input");
			}
		} else {
			passwordField.getStyleClass().add("invalid-input");
		}
	}

	public void clearFields() {
		nameField.getStyleClass().remove("invalid-input");
		passwordField.getStyleClass().remove("invalid-input");
		repeatField.getStyleClass().remove("invalid-input");
		passwordField.getStyleClass().remove("valid-input");
		repeatField.getStyleClass().remove("valid-input");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
