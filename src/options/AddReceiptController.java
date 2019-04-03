package options;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.mysql.jdbc.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.LoginController;
import models.Budget;
import models.DailySpendings;
import models.Products;
import models.Receipts;

public class AddReceiptController implements Initializable {

	@FXML
	private StackPane pane;
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private JFXButton backBtn;
	@FXML
	private JFXDatePicker datePicker;
	@FXML
	private JFXButton receiptBtn;
	@FXML
	private JFXComboBox<String> storeComboBox;
	@FXML
	private JFXTextField amount;
	@FXML
	private JFXTextField productName;
	@FXML
	private JFXTextField storeName;
	@FXML
	private JFXTextField priceField;
	@FXML
	private JFXButton addToProducts;
	@FXML
	private JFXCheckBox leaveBox;
	@FXML
	private JFXTextField totalPrice;
	@FXML
	private JFXTextField searchField;
	@FXML
	private TableView<Products> yourProductsTable;
	@FXML
	private TableColumn<Products, String> nameColumn;
	@FXML
	private TableColumn<Products, String> storeNameColumn;
	@FXML
	private TableColumn<Products, Double> priceColumn;
	HashSet<String> comboSet = new HashSet<String>();

	@FXML
	private TableView<Products> receiptsTable;
	@FXML
	private TableColumn<Products, String> nameReceiptColumn;
	@FXML
	private TableColumn<Products, String> storeReceiptColumn;
	@FXML
	private TableColumn<Products, Double> quantityColumn;
	@FXML
	private TableColumn<Products, Double> priceReceiptColumn;
	ObservableList<Products> receiptList = FXCollections.observableArrayList();
	ObservableList<Products> productsList = FXCollections.observableArrayList();
	private double fixedQuantity = 1;
	private double totalPriceValue = 0;
	private double currentBalanceDB = 0;
	private double spendingsDB = 0;
	Receipts receipts;
	private int budgetExists = 0;

	public void addProduct(ActionEvent event) {
		Products productsAdd;
		amount.getStyleClass().remove("invalid-input");
		productsAdd = yourProductsTable.getSelectionModel().getSelectedItem();
		String amountInput = null;
		amountInput = amount.getText();
		if (productsAdd != null) {
			if (amountInput != null && amountInput.matches("[,.0-9]+")
					&& (amountInput.matches("\\d*\\.?\\d*") || amountInput.matches("\\d"))) {
				productsAdd.setQuantity(Double.parseDouble(amountInput));
				receiptsTable.getItems().add(productsAdd);
				totalPriceValue += calculateTotalPrice(Double.parseDouble(amountInput), productsAdd.getPrice());
				totalPrice.setText(String.format("%.2f", totalPriceValue));
				amount.setText(null);
				receiptBtn.setDisable(false);
			} else {
				amount.getStyleClass().add("invalid-input");
			}
		}
	}

	public double calculateTotalPrice(Double quantity, Double money) {
		return quantity * money;
	}

	public void clearFields() {

		productName.setText(null);
		priceField.setText(null);
		if (!leaveBox.isSelected()) {
			storeName.setText(null);
		}
	}

	public void clearCss() {
		productName.getStyleClass().remove("valid-input");
		storeName.getStyleClass().remove("valid-input");
		priceField.getStyleClass().remove("valid-input");
		productName.getStyleClass().remove("invalid-input");
		storeName.getStyleClass().remove("invalid-input");
		priceField.getStyleClass().remove("invalid-input");
	}

	public void addToYourProducts(ActionEvent event) throws SQLException {
		clearCss();
		Products products;
		String price = null;
		String productNameStr = null;
		String storeNameStr = null;
		price = priceField.getText();
		if (price != null) {
			price = price.replaceAll(",", ".");
		}
		int isValid = 0;
		productNameStr = productName.getText();
		storeNameStr = storeName.getText();

		if (productNameStr != null && productNameStr.length() > 0) {
			productName.getStyleClass().add("valid-input");

		} else {
			isValid++;
			productName.getStyleClass().add("invalid-input");
		}
		if (storeNameStr != null && storeNameStr.length() > 0) {
			storeName.getStyleClass().add("valid-input");

		} else {
			isValid++;
			storeName.getStyleClass().add("invalid-input");

		}
		if (price != null && price.matches("[,.0-9]+") && price.matches("\\d*\\.?\\d*") || price.matches("\\d")) {
			priceField.getStyleClass().add("valid-input");
		} else {
			isValid++;
			priceField.getStyleClass().add("invalid-input");

		}
		if (isValid == 0) {
			products = new Products(productName.getText(), storeName.getText(), Double.parseDouble(price),
					fixedQuantity);
			products.insertIntoDB();
			productsList.clear();
			yourProductsTable.getItems().clear();
			loadProducts();
			clearFields();
			clearCss();

		}

	}

	public void deleteProduct(ActionEvent event) {
		Products products;
		products = receiptsTable.getSelectionModel().getSelectedItem();
		receiptsTable.getItems().remove(products);
		totalPriceValue -= calculateTotalPrice(products.getQuantity(), products.getPrice());
		totalPrice.setText(String.format("%.2f", totalPriceValue));
	}

	public void receiptFinished(ActionEvent event) throws SQLException {
		int okDate = 0;
		if (datePicker.getValue().isAfter(LocalDate.now())) {
			okDate++;
			datePicker.setValue(LocalDate.now());
			BoxBlur blur = new BoxBlur(3, 3, 3);
			JFXDialogLayout content = new JFXDialogLayout();
			content.setHeading(new Text("Warning"));
			content.setBody(new Text("It is not possible to select future date."));
			JFXDialog dialog = new JFXDialog(pane, content, JFXDialog.DialogTransition.CENTER);
			JFXButton button = new JFXButton("Okay");
			button.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					dialog.close();
				}

			});
			content.setActions(button);
			dialog.show();
			dialog.setOnDialogClosed((JFXDialogEvent event1) -> {
				anchorPane.setEffect(null);
			});
			anchorPane.setEffect(blur);
		}
		if (!receiptsTable.getItems().isEmpty() && okDate == 0) {
			String totPrice = totalPrice.getText();
			String generatedNumberStr = generateReceiptNumber();
			totPrice = totPrice.replaceAll(",", ".");
			double priceToDB = Double.valueOf(totPrice);
			String selectedMonth = String.valueOf(LocalDate.now().getMonth());
			selectedMonth = selectedMonth.toLowerCase();
			selectedMonth = selectedMonth.substring(0, 1).toUpperCase() + selectedMonth.substring(1);
			addToMyAccount(selectedMonth, priceToDB);
			if (budgetExists > 0) {
				for (Products p : receiptsTable.getItems()) {
					int idDB = p.getProductID();
					Receipts receiptsToDB = new Receipts(LoginController.userID, idDB, p.getProductName(),
							p.getStoreName(), p.getQuantity(), p.getPrice(), datePicker.getValue(), generatedNumberStr);
					receiptsToDB.insertIntoDB();
				}
				addToDailySpendings();
				receiptsTable.getItems().clear();
				totalPrice.setText(null);

			}
		}
	}

	public void addToMyAccount(String selectedMonth, double totalPriceReceipt) throws SQLException {
		Budget budget = new Budget();
		String monthYear = selectedMonth + "/" + LocalDate.now().getYear();
		budgetExists = 0;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement.executeQuery("SELECT*FROM myAccount WHERE userID= " + LoginController.userID
					+ " AND monthYear= '" + monthYear + "'");

			while (resultSet.next()) {
				currentBalanceDB = resultSet.getDouble("currentBalance");
				spendingsDB = resultSet.getDouble("spendings");
				budgetExists++;
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (conn != null)
				conn.close();
			if (statement != null)
				statement.close();
			if (resultSet != null)
				resultSet.close();
		}
		if (budgetExists > 0) {
			budget.updateBudget(currentBalanceDB - totalPriceReceipt, spendingsDB + totalPriceReceipt, monthYear);
		} else {
			BoxBlur blur = new BoxBlur(3, 3, 3);
			JFXDialogLayout content = new JFXDialogLayout();
			content.setHeading(new Text("Manage your balance"));
			content.setBody(new Text("Budget for this month is not set up yet."));
			JFXDialog dialog = new JFXDialog(pane, content, JFXDialog.DialogTransition.CENTER);
			JFXButton button = new JFXButton("Okay");
			button.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					dialog.close();
				}

			});
			content.setActions(button);
			dialog.show();
			dialog.setOnDialogClosed((JFXDialogEvent event1) -> {
				anchorPane.setEffect(null);
			});
			anchorPane.setEffect(blur);
		}

	}

	public void addToDailySpendings() throws SQLException {
		DailySpendings dailySpendings;
		int theDay = datePicker.getValue().getDayOfMonth();
		int theMonth = datePicker.getValue().getMonthValue();
		int theYear = datePicker.getValue().getYear();
		String spendingsStr = totalPrice.getText();
		spendingsStr = spendingsStr.replaceAll(",", ".");
		double spendingsInDB = 0;
		double spendings = Double.parseDouble(spendingsStr);
		int recordExists = 0;
		dailySpendings = new DailySpendings(LoginController.userID, spendings, theDay, theMonth, theYear);
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement.executeQuery("SELECT*FROM dailySpendings WHERE userID= " + LoginController.userID
					+ " AND theDay= " + theDay + " AND theMonth= " + theMonth + " AND theYear= " + theYear);

			while (resultSet.next()) {
				spendingsInDB = resultSet.getDouble("sumOfSpendings");
				recordExists++;
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (conn != null)
				conn.close();
			if (statement != null)
				statement.close();
			if (resultSet != null)
				resultSet.close();
		}
		if (!receiptsTable.getItems().isEmpty()) {
			if (recordExists == 0) {
				dailySpendings.insertIntoDB();
			} else {
				dailySpendings.updateSpendings(spendings + spendingsInDB, theDay, theMonth, theYear);
			}
		}
	}

	public String generateReceiptNumber() throws SQLException {
		int number = 1;
		String date = LocalDate.now().toString();
		date = date.replaceAll("-", "");
		String receiptNumber = date + "-" + number;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement
					.executeQuery("SELECT receiptNumber FROM receipts WHERE userID= " + LoginController.userID);

			while (resultSet.next()) {
				receiptNumber = null;
				number++;
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (conn != null)
				conn.close();
			if (statement != null)
				statement.close();
			if (resultSet != null)
				resultSet.close();
		}
		receiptNumber = date + "-" + number;
		return receiptNumber;
	}

	public void changeStore(ActionEvent event) throws SQLException {

		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		productsList.clear();
		yourProductsTable.getItems().clear();
		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement
					.executeQuery("SELECT*FROM products WHERE storeName= '" + storeComboBox.getValue() + "'");

			while (resultSet.next()) {
				Products products = new Products(resultSet.getString("productName"), resultSet.getString("storeName"),
						resultSet.getDouble("price"), resultSet.getDouble("quantity"));
				productsList.add(products);

			}
			setFilteredList();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (conn != null)
				conn.close();
			if (statement != null)
				statement.close();
			if (resultSet != null)
				resultSet.close();

		}

	}

	public void back() {
		LoginController.oneWindow = 0;
		Stage stage = (Stage) backBtn.getScene().getWindow();
		stage.close();
	}

	public void loadProducts() throws SQLException {
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		storeComboBox.getItems().clear();
		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement.executeQuery("SELECT*FROM products");

			while (resultSet.next()) {
				Products products = new Products(resultSet.getString("productName"), resultSet.getString("storeName"),
						resultSet.getDouble("price"), resultSet.getDouble("quantity"));
				products.setProductID(resultSet.getInt("productID"));
				comboSet.add(resultSet.getString("storeName"));
				productsList.add(products);

			}
			setFilteredList();
			storeComboBox.getItems().addAll(comboSet);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (conn != null)
				conn.close();
			if (statement != null)
				statement.close();
			if (resultSet != null)
				resultSet.close();

		}

	}

	public void setFilteredList() {
		FilteredList<Products> filteredProducts = new FilteredList<>(productsList, e -> true);
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredProducts.setPredicate(products -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String lowerCaseFilter = newValue.toLowerCase();

				if (products.getProductName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				}
				return false;
			});
		});
		SortedList<Products> sortedData = new SortedList<>(filteredProducts);
		sortedData.comparatorProperty().bind(yourProductsTable.comparatorProperty());
		yourProductsTable.setItems(sortedData);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		receiptBtn.setDisable(true);
		amount.setText("1");

		nameColumn.setCellValueFactory(new PropertyValueFactory<Products, String>("productName"));
		storeNameColumn.setCellValueFactory(new PropertyValueFactory<Products, String>("storeName"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<Products, Double>("price"));

		nameReceiptColumn.setCellValueFactory(new PropertyValueFactory<Products, String>("productName"));
		storeReceiptColumn.setCellValueFactory(new PropertyValueFactory<Products, String>("storeName"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<Products, Double>("quantity"));
		priceReceiptColumn.setCellValueFactory(new PropertyValueFactory<Products, Double>("price"));
		datePicker.setValue(LocalDate.now());
		try {
			loadProducts();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		datePicker.setValue(LocalDate.now());

	}

}
