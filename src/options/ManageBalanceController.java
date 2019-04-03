package options;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.mysql.jdbc.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ManageBalanceController implements Initializable {

	@FXML
	private StackPane pane;
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private JFXButton backBtn;
	@FXML
	private JFXButton cancelBtn;
	@FXML
	private JFXButton cancelEditBtn;
	@FXML
	private JFXButton addBtn;
	@FXML
	private JFXButton saveBtn;
	@FXML
	private JFXComboBox<String> monthBox;
	@FXML
	private JFXTextField budgetField;
	@FXML
	private JFXTextField editBudgetField;
	@FXML
	private JFXButton editBudget;
	@FXML
	private TableView<Budget> budgetTable;
	@FXML
	private TableColumn<Budget, String> monthColumn;
	@FXML
	private TableColumn<Budget, Double> spendingsColumn;
	@FXML
	private TableColumn<Budget, Double> budgetColumn;
	ObservableList<Budget> budgetList = FXCollections.observableArrayList();
	Budget budget;
	int budgetExists = 0;

	public void back() {
		LoginController.oneWindow = 0;
		Stage stage = (Stage) backBtn.getScene().getWindow();
		stage.close();
	}

	public void cancelEdit() {
		editBudgetField.setVisible(false);
		cancelEditBtn.setVisible(false);
		saveBtn.setVisible(false);
	}

	public void cancel() {
		monthBox.setVisible(false);
		budgetField.setVisible(false);
		addBtn.setVisible(false);
		cancelBtn.setVisible(false);
		editBudgetField.getStyleClass().remove("invalid-input");
	}

	public void setBudget() {
		monthBox.setVisible(true);
		budgetField.setVisible(true);
		addBtn.setVisible(true);
		cancelBtn.setVisible(true);
	}

	public void changeBudget() {
		budget = budgetTable.getSelectionModel().getSelectedItem();
		if (budget != null) {
			editBudgetField.setText(String.valueOf(budget.getMyBudget()));
			editBudgetField.getStyleClass().remove("invalid-input");
			editBudgetField.setVisible(true);
			cancelEditBtn.setVisible(true);
			saveBtn.setVisible(true);
		}
	}

	public void saveBudget() throws SQLException {
		editBudgetField.getStyleClass().remove("invalid-input");
		if (budget != null) {
			String editBudget = editBudgetField.getText();
			if (editBudget != null && editBudget.matches("[,.0-9]+")
					&& (editBudget.matches("\\d*\\.?\\d*") || editBudget.matches("\\d"))) {
				Double budgetToDB = Double.parseDouble(editBudget);
				Connection conn = null;
				PreparedStatement preparedStatement = null;
				try {
					conn = (Connection) DriverManager.getConnection("**");
					String sql = "UPDATE myAccount SET currentBalance=?, budget=? WHERE ID=? AND userID=?";

					preparedStatement = conn.prepareStatement(sql);
					preparedStatement.setDouble(1, budgetToDB - budget.getSpendings());
					preparedStatement.setDouble(2, budgetToDB);
					preparedStatement.setInt(3, budget.getID());
					preparedStatement.setInt(4, LoginController.userID);
					preparedStatement.executeUpdate();

				} catch (Exception e) {
					System.err.println(e.getMessage());
				} finally {
					if (preparedStatement != null)
						preparedStatement.close();
					if (conn != null)
						conn.close();
				}
				editBudgetField.setVisible(false);
				cancelEditBtn.setVisible(false);
				saveBtn.setVisible(false);
				budgetList.clear();
				budgetTable.getItems().clear();
				loadProducts();
			} else {
				editBudgetField.getStyleClass().add("invalid-input");
			}
		}
	}

	public void addBudget() throws SQLException {
		int isValid = 0;
		budgetExists = 0;
		String budgetValue = null;
		budgetValue = budgetField.getText();
		monthBox.getStyleClass().remove("invalid-input");
		budgetField.getStyleClass().remove("invalid-input");
		if (monthBox.getValue() != null) {
			if (budgetValue != null && budgetValue.matches("[,.0-9]+")
					&& (budgetValue.matches("\\d*\\.?\\d*") || budgetValue.matches("\\d"))) {
				checkBudgetMonth();
				if (budgetExists > 0) {
					BoxBlur blur = new BoxBlur(3, 3, 3);
					JFXDialogLayout content = new JFXDialogLayout();
					content.setHeading(new Text("Warning"));
					content.setBody(new Text("Budget for this month already exists in the database."));
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
					dialog.setOnDialogClosed((JFXDialogEvent event) -> {
						anchorPane.setEffect(null);
					});
					anchorPane.setEffect(blur);
				}
				if (isValid == 0 && budgetExists == 0) {
					budget = new Budget(LoginController.userID, generateMonthYear(), 0,
							Double.parseDouble(budgetValue));
					budget.insertIntoDB();
					monthBox.getStyleClass().remove("invalid-input");
					budgetField.getStyleClass().remove("invalid-input");
					monthBox.setValue(null);
					budgetField.setText(null);
					budgetExists = 0;
					budgetList.clear();
					budgetTable.getItems().clear();
					loadProducts();

				}

			} else {
				isValid++;
				budgetField.getStyleClass().add("invalid-input");
			}

		} else {
			isValid++;
			monthBox.getStyleClass().add("invalid-input");
		}

	}

	public String generateMonthYear() {
		return monthBox.getValue() + "/" + LocalDate.now().getYear();

	}

	public void checkBudgetMonth() throws SQLException {
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement.executeQuery("SELECT monthYear FROM myAccount WHERE userID= " + LoginController.userID
					+ " AND monthYear= '" + monthBox.getValue() + "/" + LocalDate.now().getYear() + "'");

			while (resultSet.next()) {
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
	}

	public void enableEditBudget() {
		editBudget.setDisable(false);
	}

	public void loadProducts() throws SQLException {
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();
			resultSet = statement.executeQuery("SELECT*FROM myAccount");

			while (resultSet.next()) {
				budget = new Budget(resultSet.getInt("userID"), resultSet.getString("monthYear"),
						resultSet.getDouble("spendings"), resultSet.getDouble("budget"));
				budget.setID(resultSet.getInt("ID"));
				budgetList.add(budget);

			}
			budgetTable.getItems().addAll(budgetList);

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		monthBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December");
		monthBox.setVisible(false);
		budgetField.setVisible(false);
		addBtn.setVisible(false);
		saveBtn.setVisible(false);
		editBudgetField.setVisible(false);
		cancelBtn.setVisible(false);
		editBudget.setDisable(true);
		cancelEditBtn.setVisible(false);

		monthColumn.setCellValueFactory(new PropertyValueFactory<Budget, String>("monthYear"));
		spendingsColumn.setCellValueFactory(new PropertyValueFactory<Budget, Double>("spendings"));
		budgetColumn.setCellValueFactory(new PropertyValueFactory<Budget, Double>("myBudget"));

		try {
			loadProducts();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
