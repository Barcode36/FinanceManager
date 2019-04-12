package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Budget {
	String monthYear;
	double currentBalance, spendings, myBudget;
	int ID, userID;

	public Budget() {

	}

	public Budget(int userID, String monthYear, double spendings, double myBudget) {
		setUserID(userID);
		setMonthYear(monthYear);
		setCurrentBalance(currentBalance);
		setSpendings(spendings);
		setMyBudget(myBudget);
		setID(ID);
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getMonthYear() {
		return monthYear;
	}

	public void setMonthYear(String monthYear) {
		this.monthYear = monthYear;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public double getSpendings() {
		return spendings;
	}

	public void setSpendings(double spendings) {
		this.spendings = spendings;
	}

	public double getMyBudget() {
		return myBudget;
	}

	public void setMyBudget(double myBudget) {
		this.myBudget = myBudget;
	}

	public void insertIntoDB() throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false", "root",
					"!zH?x47Po!c?9");
			String sql = "INSERT INTO myAccount(userID,monthYear,currentBalance,spendings,budget)"
					+ "VALUES(?,?,?,?,?)";

			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setInt(1, userID);
			preparedStatement.setString(2, monthYear);
			preparedStatement.setDouble(3, myBudget);
			preparedStatement.setDouble(4, spendings);
			preparedStatement.setDouble(5, myBudget);

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

	public void updateBudget(double balanceMinus, double plusSpendings, String monthYear) throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false", "root",
					"!zH?x47Po!c?9");
			String sql = "UPDATE myAccount SET currentBalance=?, spendings=? WHERE monthYear=? ";

			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setDouble(1, balanceMinus);
			preparedStatement.setDouble(2, plusSpendings);
			preparedStatement.setString(3, monthYear);

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
