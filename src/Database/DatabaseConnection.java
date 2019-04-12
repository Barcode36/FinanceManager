package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnection {
	private static Connection conn;
	private static Statement stmt = null;
	private static PreparedStatement pstmt = null;
	private static DatabaseConnection handler = null;

	static {
		try {
			createConnection();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public DatabaseConnection() {

	}

	public static DatabaseConnection getInstance() {
		if (handler == null) {
			handler = new DatabaseConnection();
		}
		return handler;
	}

	public void close() throws SQLException {
		conn.close();
	}

	public static void createConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false";
		conn = DriverManager.getConnection(url, "root", "!zH?x47Po!c?9");

	}
	public void loadFromDataBase() {
		
	}

}
