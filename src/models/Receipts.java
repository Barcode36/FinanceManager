package models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Receipts {
	String productName, storeName, receiptNumber;
	int productID, userID;
	double quantity, price;
	LocalDate theDay;

	public Receipts() {

	}

	public Receipts(int userID, int productID, String productName, String storeName, double quantity, double price,
			LocalDate theDay, String receiptNumber) {
		setUserID(userID);
		setProductID(productID);
		setProductName(productName);
		setStoreName(storeName);
		setQuantity(quantity);
		setPrice(price);
		setTheDay(theDay);
		setReceiptNumber(receiptNumber);

	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public LocalDate getTheDay() {
		return theDay;
	}

	public void setTheDay(LocalDate theDay) {
		this.theDay = theDay;
	}

	public void insertIntoDB() throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = (Connection) DriverManager.getConnection("**");
			String sql = "INSERT INTO receipts (userID,productID,productName,storeName,quantity,price,theDay,receiptNumber)"
					+ "VALUES(?,?,?,?,?,?,?,?)";

			preparedStatement = conn.prepareStatement(sql);
			Date db = Date.valueOf(theDay);

			preparedStatement.setInt(1, userID);
			preparedStatement.setInt(2, productID);
			preparedStatement.setString(3, productName);
			preparedStatement.setString(4, storeName);
			preparedStatement.setDouble(5, quantity);
			preparedStatement.setDouble(6, price);
			preparedStatement.setDate(7, db);
			preparedStatement.setString(8, receiptNumber);

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
