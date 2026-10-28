package main;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		
		FlashcardsView view = new FlashcardsView(stage, new FlashcardsModel());
		
		Scene scn = new Scene(view.pane, screenBounds.getWidth(), screenBounds.getHeight());
    	scn.getStylesheets().add("styles.css");

    	stage.setTitle("Flashcards");
    	stage.setResizable(true);
    	stage.setMaximized(true);
    	
		stage.setScene(scn);		
		stage.show();
				
		System.out.println("Running");
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
