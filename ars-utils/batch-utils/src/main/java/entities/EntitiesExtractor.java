package entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntitiesExtractor {

	public static List<Token> detectTokens(List<Token> outcomeDetectors, List<Token> outcomeAnalyzedTokens, int distance) {
		List<Token> resultTokens = new ArrayList<>();

		Iterator<Token> analyzedIteратор = outcomeAnalyzedTokens.iterator();
		Iterator<Token> detectors = outcomeDetectors.iterator();

		while (analyzedIteратор.hasNext()) {
			Token analyzed = analyzedIteратор.next();

			while (detectors.hasNext()) {
				Token detector = detectors.next();

				if (analyzed.start - detector.start > distance) {
					break;
				}

				if (isDistanceBetweenTokensPossible(analyzed, detector, distance)) {
					resultTokens.add(analyzed);
				}
			}
		}
		//todo доделать сопоставление
		return null;
	}


	private static boolean isDistanceBetweenTokensPossible(Token analyzed, Token detector, int distance) {
		if (analyzed.start > detector.start) {
			return analyzed.start - detector.end <= distance;
		} else {
			return detector.start - analyzed.end <= distance;
		}
	}

}
