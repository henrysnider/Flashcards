package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FlashcardsModel {
	/*
	 * TODO: - Flashcard PDF generation (print them out and cut them up)
	 * 
	 */
	
	final static int PILE_COUNT = 6;
	final static int BASE_PILE_POS = 0;

	ArrayList<ArrayList<Flashcard>> piles = new ArrayList<>(PILE_COUNT + 1); // B/c can't do array for some stupid reason
	{
		for (int i = 0; i < PILE_COUNT; i++) {
			piles.add(new ArrayList<Flashcard>());
		}
	}
	
	Flashcard currentCard;
	ArrayList<Flashcard> currentPile;
	int currentPilePos = BASE_PILE_POS;
	boolean frontShowing = true;

	void populateCards(File file) {		
		if (file == null) return;
		
		piles.forEach(stack -> stack.clear());
		currentPile = piles.get(BASE_PILE_POS);
		currentPilePos = BASE_PILE_POS;
		
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
	
	String flipCard() {
		if (currentCard == null)
			return null;
		
		frontShowing = !frontShowing;
		
		return (!frontShowing) ? currentCard.back : currentCard.front;
	}

	void answered(boolean correct) {
		if (currentCard == null) return;
		
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
	}

	String getNextCard() {
		currentCard = currentPile.get((int) (Math.random() * (currentPile.size() - 1)));
		frontShowing = true;
		
		return currentCard.front;
	}
	
	int getPileSize(int pile) {
		return piles.get(pile).size();
	}

	private class Flashcard {
		private final static String CARD_DELIMITER = "~";

		final String front;
		final String back;
		
		public Flashcard(String cardLine) {
			String[] pieces = cardLine.split(CARD_DELIMITER);
			
			front = pieces[0];
			
			if (pieces.length > 1)
				back = pieces[1];
			else
				back = "";			
		}
	}
}
