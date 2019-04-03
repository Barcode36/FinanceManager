package util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ExpensesIcon {
	public static final String IMAGE_LOC = "/resources/icon.png";

	public static void setIcon(Stage stage) {
		stage.getIcons().add(new Image(IMAGE_LOC));
	}
}
