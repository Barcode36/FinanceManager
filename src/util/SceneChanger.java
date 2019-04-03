package util;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import main.Main;

public class SceneChanger {

	public void changeScenes(ActionEvent event, String viewName, String title) throws IOException {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(viewName));
		Parent parent = loader.load();
		Scene scene = new Scene(parent);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
		stage.setResizable(false);
		ExpensesIcon.setIcon(stage);

	}

	public static Object loadWindow(URL loc, String title, Stage parentStage) {
		Object controller = null;
		try {
			FXMLLoader loader = new FXMLLoader(loc);
			Parent parent = loader.load();
			controller = loader.getController();
			Stage stage = null;
			if (parentStage != null) {
				stage = parentStage;
			} else {
				stage = new Stage(StageStyle.DECORATED);
			}
			stage.setTitle(title);
			stage.setScene(new Scene(parent));
			stage.show();
			stage.setResizable(false);
			ExpensesIcon.setIcon(stage);
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
		return controller;
	}

}
