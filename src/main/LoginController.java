package main;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import security.PasswordGen;
import util.SceneChanger;;

public class LoginController implements Initializable {

	public static int oneWindow = 0;
	private int isValid = 0;
	private static String loadedPassword = null;
	public static int userID = 0;
	Preferences userPreferences = Preferences.userRoot();
	String name = null;
	String password = null;
	@FXML
	private JFXCheckBox keepMeLogged;

	@FXML
	private JFXPasswordField passwordField;

	@FXML
	private JFXTextField nameTextField;

	@FXML
	public void addNewUser(ActionEvent event) {
		if (oneWindow == 0) {
			SceneChanger.loadWindow(getClass().getResource("/views/NewUser.fxml"), "Add new user", null);
		}
		oneWindow++;
	}

	@FXML
	public void login(ActionEvent event)
			throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		loadData();
		password = passwordField.getText();
		if (isValid > 0) {
			nameTextField.getStyleClass().add("valid-input");
			if (PasswordGen.validatePassword(password, loadedPassword)) {
				if (keepMeLogged.isSelected()) {
					name = nameTextField.getText();
					password = passwordField.getText();
					userPreferences.put("name", name);
					userPreferences.put("password", password);
				} else {
					try {
						userPreferences.clear();
					} catch (BackingStoreException e) {
						e.printStackTrace();
					}
				}
				SceneChanger sc = new SceneChanger();
				sc.changeScenes(event, "/main/MainStage.fxml", "FinanceManager");
			} else {
				passwordField.getStyleClass().add("invalid-input");
			}
		} else {
			nameTextField.getStyleClass().add("invalid-input");
		}
	}

	@FXML
	public void reset(ActionEvent event) {
		if (oneWindow == 0) {
			SceneChanger.loadWindow(getClass().getResource("/views/ResetPassword.fxml"), "Reset password", null);
		}
		oneWindow++;
	}

	public void exit(ActionEvent event) {
		System.exit(0);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameTextField.setText(userPreferences.get("name", name));
		passwordField.setText(userPreferences.get("password", password));
		keepMeLogged.setSelected(true);

	}

	public void loadData() throws SQLException {
		Connection conn = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		String sql = "SELECT*FROM users WHERE userName= ?";
		try {
			conn = (Connection) DriverManager.getConnection("**");
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, nameTextField.getText());
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				isValid++;
				userID = resultSet.getInt("userID");
				loadedPassword = resultSet.getString("password");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (conn != null)
				conn.close();
			if (preparedStatement != null)
				preparedStatement.close();
			if (resultSet != null)
				resultSet.close();

		}
	}

}
