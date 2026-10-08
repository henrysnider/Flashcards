package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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

public class Flashcards {
	/*
	 * TODO: - Flashcard PDF generation (print them out and cut them up)
	 * 
	 */
	
	final static String DEFAULT_DIR = "C:\\Users\\hsnid\\OneDrive\\Documents\\Coding stuff\\Tinkering\\tinkering\\resources\\Flashcards";

	final static int PILE_COUNT = 6;
	final static int BASE_PILE_POS = 0;

	final static double FLASHCARD_WIDTH = 700;
	final static double FLASHCARD_WIDTH_HEIGHT_RATIO = 0.7;
	final static double NODE_PADDING = 20;

	final static char CARD_DELIMITER = '~'; // TODO make this a string, and user-controlled

	ArrayList<ArrayList<Flashcard>> piles = new ArrayList<>(PILE_COUNT); // B/c can't do array for some stupid reason
	{
		for (int i = 1; i < PILE_COUNT; i++) {
			piles.add(i, new ArrayList<Flashcard>());
		}
	}
	
	Flashcard currentCard;
	ArrayList<Flashcard> currentPile;
	int currentPilePos = BASE_PILE_POS;

	// Frontend stuff
	Stage stage;
	public BorderPane pane = new BorderPane();
	Label cardView = new Label();
	{
		cardView.setPrefWidth(FLASHCARD_WIDTH);
		cardView.setPrefHeight(FLASHCARD_WIDTH * FLASHCARD_WIDTH_HEIGHT_RATIO);
		cardView.getStyleClass().add("flashcard");
		cardView.setAlignment(Pos.CENTER);
		cardView.setTextAlignment(TextAlignment.CENTER);
		cardView.setWrapText(true);
		cardView.setOnMouseClicked(evt -> revealAnswer());
		
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
		openFileButton.setOnAction(evt -> populateCards(fileChooser.showOpenDialog(stage)));
	}
	
	ArrayList<Label> stackDisplayNums = new ArrayList<>(PILE_COUNT);
	{
		VBox stackCounts = new VBox(NODE_PADDING);

		for (int i = 0; i < PILE_COUNT; i++) {
			ArrayList<Flashcard> stack = new ArrayList<>();
			piles.add(stack);
			stackCounts.getChildren().add(createStackDisplay(i));
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
	
	public Flashcards(Stage stage) {		
		this.stage = stage;
				
		Label title = new Label("Title");
		title.getStyleClass().add("title");
		
	    HBox titleBar = new HBox(20, title);
	    titleBar.setAlignment(Pos.CENTER);
	    
	    pane.setTop(titleBar);		
		pane.setLeft(openFileButton);
	}

	private void keyReleased(KeyEvent evt) {
		if (openFileButton.isFocused())
			return;

		switch (evt.getCode()) {
		case KeyCode.E:
		case KeyCode.SPACE:
			revealAnswer();
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

	private HBox createStackDisplay(int stackPos) {
		Label num = new Label("0");
		num.getStyleClass().add("flashcardStackDisplayNum");

		Label name = new Label(String.valueOf(stackPos));
		name.getStyleClass().add("flashcardStackDisplayNum");

		HBox display = new HBox(NODE_PADDING, name, num);
		display.setAlignment(Pos.CENTER);

		stackDisplayNums.add(num);

		return display;
	}

	private void populateCards(File file) {		
		if (file == null) return;
		
		piles.forEach(stack -> stack.clear());
		currentPile = piles.get(BASE_PILE_POS);
		currentPilePos = BASE_PILE_POS;
		
		pane.requestFocus();

		try {
			Scanner sc = new Scanner(file);

			while (sc.hasNextLine()) {
				currentPile.add(new Flashcard(sc.nextLine()));
			}

			sc.close();
		} catch (FileNotFoundException e) {
			return;
		}

		getNextCard();
	}

	private void revealAnswer() {
		if (currentCard == null)
			return;

		cardView.setText((cardView.getText() == currentCard.front) ? currentCard.back : currentCard.front);
	}

	private void answered(boolean correct) {
		ArrayList<Flashcard> baseStack = piles.get(BASE_PILE_POS);

		currentPile.remove(currentCard);

		// Move current card
		if (correct && currentPilePos < PILE_COUNT - 1)
			piles.get(currentPilePos + 1).add(currentCard);
		else
			baseStack.add(currentCard);

		// Move current stack
		if (currentPile.isEmpty()) {
			if (!baseStack.isEmpty()) {
				currentPilePos = BASE_PILE_POS;
				currentPile = baseStack;
			} else {
				currentPilePos++;
				currentPile = piles.get(currentPilePos);
			}
		}

		getNextCard();
	}

	private void getNextCard() {
		currentCard = currentPile.get((int) (Math.random() * (currentPile.size() - 1)));
		cardView.setText(currentCard.front);

		for (int i = 0; i < PILE_COUNT; i++) {
			stackDisplayNums.get(i).setText(String.valueOf(piles.get(i).size()));
		}
	}

	private class Flashcard {
		final String front;
		final String back;
		
		public Flashcard(String cardLine) {
			String front = "";
			String back = "";
			boolean inFront = true;

			for (char c : cardLine.toCharArray()) {
				if (c == CARD_DELIMITER) { // TODO make CARD_DELIMITER multiple characters
					inFront = false;
					continue;
				}

				if (inFront)
					front += c;
				else
					back += c;
			}
			
			this.front = front;
			this.back = back;
		}
	}
}
