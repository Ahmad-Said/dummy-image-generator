package application;

import java.util.HashSet;
import java.util.Set;

public class NameGenerator {
	// class variable
	private static final String ALPHABET_LEXICON = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	private static String lexicon = ALPHABET_LEXICON;
	private static int length;

	private final static java.util.Random rand = new java.util.Random();

	// consider using a Map<String,Boolean> to say whether the identifier is being
	// used or not
	private final static Set<String> identifiers = new HashSet<String>();

	public static String getRandomIdentifier() {
		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			int curLength = rand.nextInt(5) + length;
			for (int i = 0; i < curLength; i++) {
				builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
			}
			if (identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
	}

	/**
	 * @return the length
	 */
	public static int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public static void setLength(int length) {
		NameGenerator.length = length;
	}

	/**
	 * @return the lexicon
	 */
	public static String getLexicon() {
		return lexicon;
	}

	/**
	 * @param lexicon the lexicon to set
	 */
	public static void setLexicon(String lexicon) {
		NameGenerator.lexicon = lexicon;
	}

	/**
	 * @return the alphabetLexicon
	 */
	public static String getAlphabetLexicon() {
		return ALPHABET_LEXICON;
	}
}
