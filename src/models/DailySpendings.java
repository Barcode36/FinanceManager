package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import main.LoginController;

public class DailySpendings {
	int userID, theDay, theYear, theMonth;
	double sumOfSpendings;

	public DailySpendings() {

	}

	public DailySpendings(int userID, double sumOfSpendings, int theDay, int theMonth, int theYear) {
		setUserID(userID);
		setSumOfSpendings(sumOfSpendings);
		setTheDay(theDay);
		setTheMonth(theMonth);
		setTheYear(theYear);

	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getTheDay() {
		return theDay;
	}

	public void setTheDay(int theDay) {
		this.theDay = theDay;
	}

	public int getTheYear() {
		return theYear;
	}

	public void setTheYear(int theYear) {
		this.theYear = theYear;
	}

	public double getSumOfSpendings() {
		return sumOfSpendings;
	}

	public void setSumOfSpendings(double sumOfSpendings) {
		this.sumOfSpendings = sumOfSpendings;
	}

	public int getTheMonth() {
		return theMonth;
	}

	public void setTheMonth(int theMonth) {
		this.theMonth = theMonth;
	}

	public void insertIntoDB() throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false", "root",
					"!zH?x47Po!c?9");
			String sql = "INSERT INTO dailySpendings(userID,sumOfSpendings,theDay,theMonth,theYear)"
					+ "VALUES(?,?,?,?,?)";

			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setInt(1, LoginController.userID);
			preparedStatement.setDouble(2, sumOfSpendings);
			preparedStatement.setInt(3, theDay);
			preparedStatement.setInt(4, theMonth);
			preparedStatement.setInt(5, theYear);

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

	public void updateSpendings(double spendings, int day, int month, int year) throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false", "root",
					"!zH?x47Po!c?9");
			String sql = "UPDATE dailySpendings SET sumOfSpendings=? WHERE userID=? AND theDay=? AND theMonth=? AND theYear=?";

			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setDouble(1, spendings);
			preparedStatement.setInt(2, LoginController.userID);
			preparedStatement.setInt(3, day);
			preparedStatement.setInt(4, month);
			preparedStatement.setInt(5, year);

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
