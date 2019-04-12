package options;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.LoginController;
import models.DailySpendings;
import models.Receipts;

public class MyReceiptsController implements Initializable {

	@FXML
	private JFXButton backBtn;
	@FXML
	private JFXButton yesBtn;
	@FXML
	private JFXButton cancelBtn;
	@FXML
	private Text text;
	@FXML
	private JFXButton deleteBtn;
	@FXML
	private JFXTextField numberField;
	@FXML
	private JFXDatePicker monthPicker;
	@FXML
	private TableView<Receipts> receiptsTable;
	@FXML
	private TableColumn<Receipts, String> productNameColumn;
	@FXML
	private TableColumn<Receipts, String> storeNameColumn;
	@FXML
	private TableColumn<Receipts, Double> quantityColumn;
	@FXML
	private TableColumn<Receipts, Double> priceColumn;
	@FXML
	private TableColumn<Receipts, String> receiptNumberColumn;
	ObservableList<Receipts> receiptsList = FXCollections.observableArrayList();
	double sumOfReceipt = 0;

	Receipts receipts;

	public void backButtonPushed(ActionEvent event) {
		LoginController.oneWindow = 0;
		Stage stage = (Stage) backBtn.getScene().getWindow();
		stage.close();
	}

	public Date getData() {
		Date db = Date.valueOf(monthPicker.getValue());
		return db;
	}

	public void deleteReceipt() {
		receipts = receiptsTable.getSelectionModel().getSelectedItem();
		if (receipts != null) {
			numberField.setText(receipts.getReceiptNumber());
			text.setVisible(true);
			yesBtn.setVisible(true);
			cancelBtn.setVisible(true);
			monthPicker.setDisable(true);
		}
	}

	public void yesButtonPushed() throws SQLException {
		numberField.getStyleClass().remove("invalid-input");
		String numb = null;
		numb = numberField.getText();
		if (receipts != null) {
			if (numb != null) {

				updateDailySpendings();
				Connection conn = null;
				PreparedStatement preparedStatement = null;
				try {
					conn = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false",
							"root", "!zH?x47Po!c?9");
					String sql = "DELETE FROM receipts WHERE receiptNumber=? AND userID=?";

					preparedStatement = conn.prepareStatement(sql);

					preparedStatement.setString(1, numberField.getText());
					preparedStatement.setInt(2, LoginController.userID);

					preparedStatement.executeUpdate();

				} catch (Exception e) {
					System.err.println(e.getMessage());
				} finally {
					if (preparedStatement != null)
						preparedStatement.close();
					if (conn != null)
						conn.close();
				}
				receiptsList.clear();
				receiptsTable.getItems().clear();
				loadReceipts();
				numberField.setText(null);
				cancelButtonPushed();
			} else {
				numberField.getStyleClass().add("invalid-input");
			}
		} else {
			numberField.getStyleClass().add("invalid-input");
		}
	}

	public void updateDailySpendings() throws SQLException {
		DailySpendings dailySpendings = new DailySpendings();
		int theDay = monthPicker.getValue().getDayOfMonth();
		int theMonth = monthPicker.getValue().getMonthValue();
		int theYear = monthPicker.getValue().getYear();
		double spendingsInDB = 0;
		int recordExists = 0;
		double toDelete = calculateReceiptSum();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT*FROM dailySpendings WHERE userID=? AND theDay=? AND theMonth=? AND theYear=?";

		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false", "root",
					"!zH?x47Po!c?9");
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, LoginController.userID);
			preparedStatement.setInt(2, theDay);
			preparedStatement.setInt(3, theMonth);
			preparedStatement.setInt(4, theYear);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				spendingsInDB = resultSet.getDouble("sumOfSpendings");
				recordExists++;
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
		if (recordExists > 0) {
			dailySpendings.updateSpendings(spendingsInDB - toDelete, theDay, theMonth, theYear);
		}
	}
	
	public double calculateReceiptSum() throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT SUM(price) FROM receipts WHERE receiptNumber=? AND userID=?";
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false", "root",
					"!zH?x47Po!c?9");
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, numberField.getText());
			preparedStatement.setInt(2, LoginController.userID);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				sumOfReceipt = resultSet.getDouble("SUM(price)");
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
		return sumOfReceipt;
	}

	public void cancelButtonPushed() {
		numberField.setText(null);
		text.setVisible(false);
		yesBtn.setVisible(false);
		cancelBtn.setVisible(false);
		numberField.getStyleClass().remove("invalid-input");
		monthPicker.setDisable(false);
	}

	public void loadReceipts() throws SQLException {
		receiptsList.clear();
		receiptsTable.getItems().clear();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT*FROM receipts WHERE theDay=? AND userID=?";
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/expenses?useSSL=false", "root",
					"!zH?x47Po!c?9");
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setDate(1, getData());
			preparedStatement.setInt(2, LoginController.userID);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				receipts = new Receipts(resultSet.getInt("userID"), resultSet.getInt("productID"),
						resultSet.getString("productName"), resultSet.getString("storeName"),
						resultSet.getDouble("quantity"), resultSet.getDouble("price"),
						resultSet.getDate("theDay").toLocalDate(), resultSet.getString("receiptNumber"));
				receiptsList.add(receipts);
			}
			receiptsTable.getItems().addAll(receiptsList);

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

	public void enableDeleteBtn() {
		deleteBtn.setDisable(false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		deleteBtn.setDisable(true);
		text.setVisible(false);
		yesBtn.setVisible(false);
		cancelBtn.setVisible(false);
		productNameColumn.setCellValueFactory(new PropertyValueFactory<Receipts, String>("productName"));
		storeNameColumn.setCellValueFactory(new PropertyValueFactory<Receipts, String>("storeName"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<Receipts, Double>("quantity"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<Receipts, Double>("price"));
		receiptNumberColumn.setCellValueFactory(new PropertyValueFactory<Receipts, String>("receiptNumber"));

	}

}
