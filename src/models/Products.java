package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Products {
	int productID;
	String productName, storeName;
	double price, quantity;

	public Products() {
		
	}
	public Products(String productName, String storeName, double price, double quantity) {
		setProductName(productName);
		setStoreName(storeName);
		setPrice(price);
		setQuantity(quantity);
		setProductID(productID);
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void insertIntoDB() throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false", "root",
					"!zH?x47Po!c?9");
			String sql = "INSERT INTO products(productName,storeName,price,quantity)" + "VALUES(?,?,?,?)";

			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setString(1, productName);
			preparedStatement.setString(2, storeName);
			preparedStatement.setDouble(3, price);
			preparedStatement.setDouble(4, 1);

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
