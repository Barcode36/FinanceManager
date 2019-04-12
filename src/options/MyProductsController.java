package options;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.mysql.jdbc.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.LoginController;
import models.Products;

public class MyProductsController implements Initializable {

	@FXML
	private JFXButton backBtn;
	@FXML
	private JFXButton deleteBtn;
	@FXML
	private JFXButton addBtn;
	@FXML
	private JFXTextField productName;
	@FXML
	private JFXTextField storeName;
	@FXML
	private JFXTextField price;
	@FXML
	private JFXTextField productNameEdit;
	@FXML
	private JFXTextField storeNameEdit;
	@FXML
	private JFXTextField priceEdit;
	@FXML
	private JFXButton saveBtn;
	@FXML
	private JFXButton clearBtn;
	@FXML
	private JFXButton editBtn;
	@FXML
	private JFXComboBox<String> storeComboBox;
	HashSet<String> comboSet = new HashSet<String>();
	@FXML
	private TableView<Products> productsTable;
	@FXML
	private TableColumn<Products, String> nameColumn;
	@FXML
	private TableColumn<Products, String> storeColumn;
	@FXML
	private TableColumn<Products, Double> priceColumn;
	ObservableList<Products> productsList = FXCollections.observableArrayList();
	private int fixedQuantity = 1;
	Products products;

	@FXML
	public void addProductPushed(ActionEvent event) throws SQLException {
		clearCss();
		String priceStr = null;
		String productNameStr = null;
		String storeNameStr = null;
		priceStr = price.getText();
		if (priceStr != null) {
			priceStr = priceStr.replace(',', '.');
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
		if (priceStr != null) {
			if (priceStr.matches("[,.0-9]+") && priceStr.matches("\\d*\\.?\\d*") || priceStr.matches("\\d")) {
				price.getStyleClass().add("valid-input");
			} else {
				isValid++;
				price.getStyleClass().add("invalid-input");
			}
		} else {
			isValid++;
			price.getStyleClass().add("invalid-input");
		}
		if (isValid == 0) {
			Products products = new Products(productName.getText(), storeName.getText(), Double.parseDouble(priceStr),
					fixedQuantity);
			products.insertIntoDB();
			productsList.clear();
			productsTable.getItems().clear();
			loadProducts();
			clearFields();
			clearCss();

		}
	}

	public void clearFields() {

		productName.setText(null);
		price.setText(null);
		storeName.setText(null);
	}

	public void clearCss() {
		productName.getStyleClass().remove("valid-input");
		storeName.getStyleClass().remove("valid-input");
		price.getStyleClass().remove("valid-input");
		productName.getStyleClass().remove("invalid-input");
		storeName.getStyleClass().remove("invalid-input");
		price.getStyleClass().remove("invalid-input");
	}

	@FXML
	public void backButtonPushed(ActionEvent event) {
		LoginController.oneWindow = 0;
		Stage stage = (Stage) backBtn.getScene().getWindow();
		stage.close();
	}

	public void changeStore(ActionEvent event) throws SQLException {
		Products products;
		productsList.clear();
		productsTable.getItems().clear();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT*FROM products WHERE storeName=?";

		try {
			conn = (Connection) DriverManager.getConnection("**");
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, storeComboBox.getValue());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				products = new Products(resultSet.getString("productName"), resultSet.getString("storeName"),
						resultSet.getDouble("price"), resultSet.getDouble("quantity"));
				products.setProductID(resultSet.getInt("productID"));
				productsList.add(products);

			}
			productsTable.getItems().addAll(productsList);

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

	@FXML
	public void deleteButtonPushed(ActionEvent event) throws SQLException {
		products = productsTable.getSelectionModel().getSelectedItem();

		if (productsTable.getItems().size() > 0 && products != null) {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			try {
				conn = (Connection) DriverManager.getConnection("**");
				String sql = "DELETE FROM products WHERE productID=?";

				preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setInt(1, products.getProductID());
				preparedStatement.executeUpdate();

			} catch (Exception e) {
				System.err.println(e.getMessage());
			} finally {
				if (preparedStatement != null)
					preparedStatement.close();
				if (conn != null)
					conn.close();
			}
			productsTable.getItems().clear();
			productsList.clear();
			loadProducts();
		}
	}

	public void clearButtonPushed() {
		saveBtn.setVisible(false);
		clearBtn.setVisible(false);
		productNameEdit.setVisible(false);
		storeNameEdit.setVisible(false);
		priceEdit.setVisible(false);
		editBtn.setDisable(true);
		addBtn.setDisable(false);
		deleteBtn.setDisable(false);
		storeComboBox.setDisable(false);
		productName.setEditable(true);
		storeName.setEditable(true);
		price.setEditable(true);
	}

	@FXML
	public void editButton(ActionEvent event) throws SQLException {
		products = productsTable.getSelectionModel().getSelectedItem();
		if (products != null) {
			productNameEdit.setText(products.getProductName());
			storeNameEdit.setText(products.getStoreName());
			priceEdit.setText(String.valueOf(products.getPrice()));

			saveBtn.setVisible(true);
			clearBtn.setVisible(true);
			productNameEdit.setVisible(true);
			storeNameEdit.setVisible(true);
			priceEdit.setVisible(true);
			productName.setEditable(false);
			storeName.setEditable(false);
			price.setEditable(false);
			deleteBtn.setDisable(true);
			addBtn.setDisable(true);
			storeComboBox.setDisable(true);

		}
	}

	@FXML
	public void saveButtonPushed(ActionEvent event) throws SQLException {
		String priceEditToDB = priceEdit.getText();
		priceEdit.getStyleClass().remove("valid-input");
		if (priceEditToDB != null) {
			priceEditToDB = priceEditToDB.replace(',', '.');
		}

		if (products != null) {
			if (priceEditToDB != null) {
				if (priceEditToDB.matches("[,.0-9]+") && priceEditToDB.matches("\\d*\\.?\\d*")
						|| priceEditToDB.matches("\\d")) {

					Connection conn = null;
					PreparedStatement preparedStatement = null;
					try {
						conn = (Connection) DriverManager.getConnection("**");
						String sql = "UPDATE products SET productName=?, storeName=?, price=? WHERE productID=?";

						preparedStatement = conn.prepareStatement(sql);
						preparedStatement.setString(1, productNameEdit.getText());
						preparedStatement.setString(2, storeNameEdit.getText());
						preparedStatement.setDouble(3, Double.parseDouble(priceEditToDB));
						preparedStatement.setInt(4, products.getProductID());
						preparedStatement.executeUpdate();

					} catch (Exception e) {
						System.err.println(e.getMessage());
					} finally {
						if (preparedStatement != null)
							preparedStatement.close();
						if (conn != null)
							conn.close();
					}
					productsTable.getItems().clear();
					productsList.clear();
					loadProducts();

					saveBtn.setVisible(false);
					clearBtn.setVisible(false);
					productNameEdit.setVisible(false);
					storeNameEdit.setVisible(false);
					priceEdit.setVisible(false);
					productName.setEditable(true);
					storeName.setEditable(true);
					price.setEditable(true);
					editBtn.setDisable(true);
					deleteBtn.setDisable(false);
					addBtn.setDisable(false);
					storeComboBox.setDisable(false);
				} else {
					priceEdit.getStyleClass().add("invalid-input");
				}
			}
		}
	}

	public void loadProducts() throws SQLException {
		Products products;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		storeComboBox.getItems().clear();

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement.executeQuery("SELECT*FROM products");

			while (resultSet.next()) {
				products = new Products(resultSet.getString("productName"), resultSet.getString("storeName"),
						resultSet.getDouble("price"), resultSet.getDouble("quantity"));
				products.setProductID(resultSet.getInt("productID"));
				productsList.add(products);
				comboSet.add(resultSet.getString("storeName"));

			}
			productsTable.getItems().addAll(productsList);
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

	public void editItemSelected() {
		editBtn.setDisable(false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		saveBtn.setVisible(false);
		clearBtn.setVisible(false);
		productNameEdit.setVisible(false);
		storeNameEdit.setVisible(false);
		priceEdit.setVisible(false);
		editBtn.setDisable(true);

		try {
			loadProducts();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		nameColumn.setCellValueFactory(new PropertyValueFactory<Products, String>("productName"));
		storeColumn.setCellValueFactory(new PropertyValueFactory<Products, String>("storeName"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<Products, Double>("price"));
	}

}
