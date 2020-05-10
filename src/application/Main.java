package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/** Demo of tracking the loading of background images in JavaFX */
public class Main extends Application { // http://www.visualpharm.com/

	private static Stage primaryStage;

	@Override
	public void start(final Stage stage) {
		try {
			primaryStage = stage;
			stage.setTitle("Dummy Image Generator");
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/img/dummy-image-generator.png")));
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PhotoGenerator.fxml"));
			Parent root;
			Scene scene;
			root = loader.load();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
			stage.setOnCloseRequest(e -> {
				primaryStage.hide();
				Platform.exit();
				System.exit(0);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch();
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}
}