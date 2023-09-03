package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader= new FXMLLoader(getClass().getResource("klotski.fxml"));
		Parent root=loader.load();
		
		Controller controller = loader.getController();
        controller.setStage(stage);
		
		Scene scene= new Scene(root);
		Image icon= new Image("icon.png");
		stage.getIcons().add(icon);
		stage.setTitle("Klotski Game");

		stage.setResizable(false);
		
		stage.setScene(scene);
		stage.show();
	}
}