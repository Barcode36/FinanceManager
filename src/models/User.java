package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class User {
	private String userName, password, helpQuestion, helpAnswer;

	public User() {

	}

	public User(String userName, String password, String helpQuestion, String helpAnswer) {
		setUserName(userName);
		setPassword(password);
		setHelpQuestion(helpQuestion);
		setHelpAnswer(helpAnswer);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHelpQuestion() {
		return helpQuestion;
	}

	public void setHelpQuestion(String helpQuestion) {
		this.helpQuestion = helpQuestion;
	}

	public String getHelpAnswer() {
		return helpAnswer;
	}

	public void setHelpAnswer(String helpAnswer) {
		this.helpAnswer = helpAnswer;
	}

	public void insertIntoDB() throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = (Connection) DriverManager.getConnection("**");
			String sql = "INSERT INTO users(userName,password,helpQuestion,helpAnswer)" + "VALUES(?,?,?,?)";

			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setString(1, userName);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, helpQuestion);
			preparedStatement.setString(4, helpAnswer);

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
