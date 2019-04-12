package views;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.LoginController;
import security.PasswordGen;

public class ResetPasswordController implements Initializable {

	@FXML
	private JFXTextField question;

	@FXML
	private JFXTextField answerField;
	@FXML
	private JFXTextField nameField;
	@FXML
	private JFXPasswordField password;
	@FXML
	private JFXPasswordField repeat;
	@FXML
	private JFXButton backBtn;
	@FXML
	private Pane pane;
	@FXML
	private Pane pane1;

	private String answer = null;
	private int validName = 0;

	private String newPassword = null;

	@FXML
	public void back(ActionEvent event) {
		LoginController.oneWindow = 0;
		Stage stage = (Stage) backBtn.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void resetPassword(ActionEvent event) throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (validName > 0) {
			if (password.getText().equals(repeat.getText()) && !password.getText().isEmpty()
					&& !repeat.getText().isEmpty()) {
				newPassword = PasswordGen.generatePassword(password.getText());
				try {
					updatePassword();
					back(event);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				password.getStyleClass().add("invalid-input");
				repeat.getStyleClass().add("invalid-input");
			}
		}

	}

	public void checkUsername() throws SQLException {
		loadData();
		if (validName > 0) {
			nameField.getStyleClass().add("valid-input");
			nameField.setEditable(false);
			pane.setVisible(true);
		} else {
			nameField.getStyleClass().add("invalid-input");
		}
	}

	public void checkAnswer() {
		if (validName > 0) {
			if (answer.equals(answerField.getText())) {
				answerField.getStyleClass().add("valid-input");
				answerField.setEditable(false);
				pane.setVisible(false);
				pane1.setVisible(true);
			} else {
				answerField.getStyleClass().add("invalid-input");
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pane.setVisible(false);
		pane1.setVisible(false);
	}

	public void loadData() throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT*FROM users WHERE userName=?";
		try {
			conn = (Connection) DriverManager.getConnection("**");
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, nameField.getText());

			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				validName++;
				answer = resultSet.getString("helpAnswer");
				question.setText(resultSet.getString("helpQuestion"));

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

	public void updatePassword() throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = (Connection) DriverManager.getConnection("**");
			String sql = "UPDATE users set password=? " + "WHERE userName=?";

			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setString(1, newPassword);
			preparedStatement.setString(2, nameField.getText());

			preparedStatement.executeUpdate();

		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (preparedStatement != null)
				preparedStatement.close();
			if (conn != null)
				conn.close();
		}
	}

}
