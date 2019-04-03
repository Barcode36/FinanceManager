package main;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import com.mysql.jdbc.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class MainStageController implements Initializable {

	@FXML
	private JFXDrawer drawer;
	@FXML
	private JFXHamburger hamburger;
	@FXML
	private LineChart<String, Double> monthToMonthChart;
	@FXML
	private CategoryAxis day;
	@FXML
	private NumberAxis amount;
	@FXML
	private PieChart budgetChart;
	@FXML
	private JFXComboBox<String> monthBox;
	private int monthNumber = 0;
	private int numberOfDaysInMonth = 0;
	private double sumOfSpendingsInMonth = 0;
	private double monthSum = 0;
	@FXML
	private Label firstLabel;
	@FXML
	private Label secondLabel;
	@FXML
	private JFXTextField firstField;
	@FXML
	private JFXTextField secondField;
	XYChart.Series dailySpendingsChart = new XYChart.Series<>();
	XYChart.Series monthlySpendingsChart = new XYChart.Series<>();
	ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

	public void initializeDrawer() {

		try {
			VBox box = FXMLLoader.load(getClass().getResource("/views/Options.fxml"));
			drawer.setSidePane(box);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HamburgerSlideCloseTransition hamburgerLook = new HamburgerSlideCloseTransition(hamburger);
		hamburgerLook.setRate(-1);
		hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
			drawer.toggle();
		});
		drawer.setOnDrawerOpening((event) -> {
			hamburgerLook.setRate(hamburgerLook.getRate() * -1);
			hamburgerLook.play();
			drawer.toFront();
		});
		drawer.setOnDrawerClosed((event) -> {
			drawer.toBack();
			hamburgerLook.setRate(hamburgerLook.getRate() * -1);
			hamburgerLook.play();
		});
		drawer.open();
	}

	public XYChart.Series<String, Double> loadMonthlySpendings() throws SQLException {
		monthlySpendingsChart.getData().clear();
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		Double monthSpendings = null;
		String monthName = null;
		day.setLabel("Month");

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement.executeQuery("SELECT * FROM myAccount WHERE userID= " + LoginController.userID);

			while (resultSet.next()) {
				monthSpendings = resultSet.getDouble("spendings");
				monthName = resultSet.getString("monthYear");
				String[] monthPart = monthName.split("/");
				monthName = monthPart[0];
				monthlySpendingsChart.getData().add(new XYChart.Data(monthName, monthSpendings));

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
		return monthlySpendingsChart;
	}

	public XYChart.Series<String, Double> loadDailySpendings() throws SQLException {
		monthlySpendingsChart.getData().clear();
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String dayOfMonth = null;
		int dayNumb = 0;
		double spentInTheDay = 0;
		day.setLabel("Day");

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement.executeQuery("SELECT * FROM dailySpendings WHERE userID= " + LoginController.userID
					+ " AND theMonth= " + monthNumber + " AND theYear= " + LocalDate.now().getYear());

			while (resultSet.next()) {
				sumOfSpendingsInMonth += resultSet.getDouble("sumOfSpendings");
				dayNumb = resultSet.getInt("theDay");
				dayOfMonth = String.valueOf(dayNumb);

				spentInTheDay = resultSet.getDouble("sumOfSpendings");

				dailySpendingsChart.getData().add(new XYChart.Data(dayOfMonth, spentInTheDay));
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
		return dailySpendingsChart;
	}

	public ObservableList<PieChart.Data> loadBudget(String monthName) throws SQLException {
		Connection conn = null;
		Statement statement = null;
		Statement statement2 = null;
		ResultSet resultSet = null;
		ResultSet resultSet2 = null;
		double monthSpendings = 0;
		monthSum = 0;
		String query1 = "SELECT currentBalance FROM myAccount WHERE monthYear= '" + monthName + "/"
				+ LocalDate.now().getYear() + "' AND userID= " + LoginController.userID;
		String query2 = "SELECT spendings FROM myAccount WHERE monthYear= '" + monthName + "/"
				+ LocalDate.now().getYear() + "' AND userID= " + LoginController.userID;

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();
			statement2 = (Statement) conn.createStatement();

			resultSet = statement.executeQuery(query1);
			resultSet2 = statement2.executeQuery(query2);

			while (resultSet.next()) {

				double monthBudget = resultSet.getDouble("currentBalance");
				data.add(new PieChart.Data("Your balance (" + monthBudget + ")", monthBudget));

			}
			while (resultSet2.next()) {

				monthSpendings = resultSet2.getDouble("spendings");
				monthSum = monthSpendings;
				data.add(new PieChart.Data("Your spendings (" + monthSpendings + ")", monthSpendings));

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
			if (resultSet2 != null)
				resultSet2.close();
			if (statement2 != null)
				statement2.close();

		}
		return data;
	}

	public void showMonthlySpendingsChart() throws SQLException {
		dailySpendingsChart.getData().clear();
		monthlySpendingsChart.getData().clear();
		monthToMonthChart.getData().clear();
		monthToMonthChart.getData().addAll(loadMonthlySpendings());
		monthToMonthChart.setTitle("Monthly spendings in " + LocalDate.now().getYear());
	}

	public void refreshButtonPushed(ActionEvent event) throws SQLException {
		changeMonthChart();
		calculateAverageDailySpendingsYear();
		loadDailySpendings();
	}

	public void changeMonthChart() throws SQLException {
		String selectedMonth = monthBox.getValue();
		selectedMonth = selectedMonth.toLowerCase();
		selectedMonth = selectedMonth.substring(0, 1).toUpperCase() + selectedMonth.substring(1);
		budgetChart.getData().clear();
		data.clear();
		budgetChart.setData(loadBudget(selectedMonth));
		budgetChart.setTitle(selectedMonth);
		firstLabel.setText("Your average daily spendings in " + selectedMonth + ":");
		switch (selectedMonth) {
		case ("January"):
			monthNumber = 1;
			numberOfDaysInMonth = 31;
			break;
		case ("February"):
			monthNumber = 2;
			if (LocalDate.now().getYear() % 4 == 0 || LocalDate.now().getYear() % 400 == 0) {
				numberOfDaysInMonth = 29;
			} else {
				numberOfDaysInMonth = 28;
			}
			break;
		case ("March"):
			monthNumber = 3;
			numberOfDaysInMonth = 31;
			break;
		case ("April"):
			monthNumber = 4;
			numberOfDaysInMonth = 30;
			break;
		case ("May"):
			monthNumber = 5;
			numberOfDaysInMonth = 31;
			break;
		case ("June"):
			monthNumber = 6;
			numberOfDaysInMonth = 30;
			break;
		case ("July"):
			monthNumber = 7;
			numberOfDaysInMonth = 31;
			break;
		case ("August"):
			monthNumber = 8;
			numberOfDaysInMonth = 31;
			break;
		case ("September"):
			monthNumber = 9;
			numberOfDaysInMonth = 30;
			break;
		case ("October"):
			monthNumber = 10;
			numberOfDaysInMonth = 31;
			break;
		case ("November"):
			monthNumber = 11;
			numberOfDaysInMonth = 30;
			break;
		case ("December"):
			numberOfDaysInMonth = 31;
			monthNumber = 12;
			break;
		}
		calculateDailySpendings();

	}

	public void calculateDailySpendings() {
		firstField.setText(String.format("%.2f", monthSum / numberOfDaysInMonth));
	}

	public void calculateAverageDailySpendingsYear() throws SQLException {
		double sumOfSpendingsInYear = 0;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = (Connection) DriverManager.getConnection("**");
			statement = (Statement) conn.createStatement();

			resultSet = statement
					.executeQuery("SELECT * FROM dailySpendings WHERE theYear= " + LocalDate.now().getYear());

			while (resultSet.next()) {
				sumOfSpendingsInYear += resultSet.getDouble("sumOfSpendings");
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
		secondField.setText(String.format("%.2f", sumOfSpendingsInYear / LocalDate.now().getDayOfYear()));
	}

	public void showDailySpendings() throws SQLException {
		monthToMonthChart.setTitle("Your daily spendings in " + monthBox.getValue());
		monthToMonthChart.getData().clear();
		monthToMonthChart.getData().addAll(loadDailySpendings());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeDrawer();
		monthBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December");
		String monthName = String.valueOf(LocalDate.now().getMonth());
		monthName = monthName.toLowerCase();
		monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
		monthBox.setValue(monthName);
		firstLabel.setText("Your average daily spendings in " + monthName + ":");
		secondLabel.setText("Your average daily spendings in " + LocalDate.now().getYear() + ":");
		try {
			changeMonthChart();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			monthToMonthChart.getData().addAll(loadDailySpendings());
			monthToMonthChart.setTitle("Your daily spendings in " + monthName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			calculateAverageDailySpendingsYear();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		calculateDailySpendings();

	}

}
