package main;

import java.io.File;
import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FlashcardsView {
	final static String DEFAULT_DIR = "C:\\Users\\hsnid\\OneDrive\\Documents\\Coding stuff\\Tinkering\\tinkering\\resources\\Flashcards";

	final static double FLASHCARD_WIDTH = 700;
	final static double FLASHCARD_WIDTH_HEIGHT_RATIO = 0.7;
	final static double NODE_PADDING = 20;

	final FlashcardsModel flModel;

	Stage stage;
	public BorderPane pane = new BorderPane();

	Label title = new Label("Title");
	{
		title.getStyleClass().add("title");

		HBox titleBar = new HBox(20, title);
		titleBar.setAlignment(Pos.CENTER);
		
		pane.setTop(titleBar);
	}

	Label cardView = new Label();
	{
		cardView.setPrefWidth(FLASHCARD_WIDTH);
		cardView.setPrefHeight(FLASHCARD_WIDTH * FLASHCARD_WIDTH_HEIGHT_RATIO);
		cardView.getStyleClass().add("flashcard");
		cardView.setAlignment(Pos.CENTER);
		cardView.setTextAlignment(TextAlignment.CENTER);
		cardView.setWrapText(true);
		cardView.setOnMouseClicked(evt -> flipCard());

		pane.setCenter(cardView);
	}

	FileChooser fileChooser = new FileChooser();
	{
		fileChooser.setTitle("Select Flashcards File");
		fileChooser.setInitialDirectory(new File(DEFAULT_DIR));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Flashcards", "*.txt"));
	}

	Button openFileButton = new Button("Choose file");
	{
		openFileButton.setOnAction(evt -> fileButtonPressed());
		
		pane.setLeft(openFileButton);
	}

	ArrayList<Label> stackDisplayNums = new ArrayList<>(FlashcardsModel.PILE_COUNT);
	{
		VBox stackCounts = new VBox(NODE_PADDING);

		for (int i = 0; i < FlashcardsModel.PILE_COUNT; i++) {
			stackCounts.getChildren().add(createStackDisplay(i, stackDisplayNums));
		}

		stackCounts.setAlignment(Pos.CENTER);
		stackCounts.getStyleClass().add("flashcardStackDisplay");
		pane.setRight(stackCounts);
	}

	{ // Set up card controls
		Button correct = new Button("Correct");
		correct.setOnAction(evt -> answered(true));

		Button incorrect = new Button("Incorrect");
		incorrect.setOnAction(evt -> answered(false));

		HBox cardButtons = new HBox(NODE_PADDING, incorrect, correct);
		cardButtons.setAlignment(Pos.CENTER);

		pane.setOnKeyReleased(evt -> keyReleased(evt));
		pane.setBottom(cardButtons);
	}

	public FlashcardsView(Stage stage, FlashcardsModel flashcardsModel) {
		this.flModel = flashcardsModel;
		this.stage = stage;
	}

	private void fileButtonPressed() {
		File file = fileChooser.showOpenDialog(stage);

		if (file == null)
			return;
		
		title.setText(file.getName().replace(".txt", ""));

		flModel.populateCards(file);

		update();
	}

	private HBox createStackDisplay(int stackPos, ArrayList<Label> stackDisplayNums) {
		Label num = new Label("0");
		num.getStyleClass().add("flashcardStackDisplayNum");
		stackDisplayNums.add(num);

		Label name = new Label(String.valueOf(stackPos));
		name.getStyleClass().add("flashcardStackDisplayNum");

		HBox display = new HBox(NODE_PADDING, name, num);
		display.setAlignment(Pos.CENTER);

		return display;
	}

	private void answered(boolean response) {
		flModel.answered(response);
		update();
	}

	private void flipCard() {
		cardView.setText(flModel.flipCard());
	}

	private void update() {
		pane.requestFocus();

		cardView.setText(flModel.getNextCard());

		for (int i = 0; i < FlashcardsModel.PILE_COUNT; i++) {
			stackDisplayNums.get(i).setText(String.valueOf(flModel.getPileSize(i)));
		}
	}

	private void keyReleased(KeyEvent evt) {
		if (openFileButton.isFocused())
			return;

		switch (evt.getCode()) {
		case KeyCode.E:
		case KeyCode.SPACE:
			flipCard();
			break;
		case KeyCode.W:
			answered(false);
			break;
		case KeyCode.R:
			answered(true);
			break;
		default:
			return;
		}

		evt.consume();
	}
}