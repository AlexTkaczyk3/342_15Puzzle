import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

public class JavaFXTemplate extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass()
                .getResource("/FXML/gameIntro.fxml"));
		Scene clientSceneOne = new Scene(root, 1000, 700);
        clientSceneOne.getStylesheets().add("/styles/mainStyle.css");
        primaryStage.setTitle("Welcome to the 15 Puzzle Game");
        primaryStage.setScene(clientSceneOne);
        primaryStage.show();
	}

}
