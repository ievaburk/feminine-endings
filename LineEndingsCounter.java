/* Permits loading of one or more 
iambic pentameter poems, counts total lines, 
counts the % of open lines with no punctuation at end, 
and % of feminine endings, and report the results  
in a format like this:  100 lines. 25 open = 25%; 
same format for feminine-ended lines.  
Defining feminine endings:  if an ending word or 
ending is on either the feminine ending or the 
feminine-ending word list, and not on either the 
masculine-ending or the masculine word list, it 
counts as feminine. 
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;


public class LineEndingsCounter {

// TODO: add in some kind of message to say that the file doesn't exist or something
	public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            File file = new File(args[0]);
            int total_count = countLines(file);
            int open_count = openLines(file);
            int fem_count = feminineEndings(file);
            float open_percentage = (float) Math.round(((float)(open_count * 100)/total_count) * 10) / 10;
            float fem_percentage = (float) Math.round(((float)(fem_count * 100)/total_count) * 10) / 10;
            System.out.println("Total line count: " + total_count);
			System.out.println("Open lines: " + open_count + "/" + total_count + ", " + open_percentage + "%.");
			System.out.println("Feminine endings: " + fem_count + "/" + total_count + ", " + fem_percentage + "%.");


			// File file_fem_words = new File("FEMWORDS.TXT");

			// System.out.println("Regex w word boundaries: " + createRegexFromTxtFileOfWords(file_fem_words));
    	}
    }	


	public static int countLines(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int line_count = 0;
		while (reader.readLine() != null) 
			line_count++;
		reader.close();
		return line_count;

	}

	public static int openLines(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		LinkedList<String> lines = new LinkedList<String>(); // create a new list
		
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.length() > 0) { // checks to see if it's not a blank line
				String lastWord = line.substring(line.lastIndexOf(" ")+1);
				lines.add(lastWord);
				// System.out.println(lastWord);
			}
		}

		String open_regex = ".*\\w+(]|\\))?$"; // regular expression to match a line with no punctuation at the end
		int open_count = 0;

		for (String word : lines) {
			if (word.matches(open_regex)) {
				open_count++;
				//System.out.println(word);
			}

		}

		reader.close();
		return open_count;

	}

	// Version 1: how accurate can we get with just the feminine endings

	public static int feminineEndings(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		LinkedList<String> lines = new LinkedList<String>(); // create a new list
		
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.length() > 0) { // checks to see if it's not a blank line
				String lastWord = line.substring(line.lastIndexOf(" ")+1);
				lines.add(lastWord);
			}
		}

		File file_fem_words = new File("FEMWORDS.TXT");
		String fem_words_regex = createRegexFromTxtFileOfWords(file_fem_words);

		File file_fem_endings = new File("FEMEND.END");
		String fem_endings_regex = createRegexFromTxtFileOfEndings(file_fem_endings);

		File file_masc = new File("MASCWRD.WRD");
		String masc_exceptions_regex = createRegexFromTxtFileOfWords(file_masc);

/* If the last word in the line ends with any of the specified feminine endings [FEMEND] and is not one of the specified masculine-word exceptions [MASCWRD], it’s a feminine ending.
If the last word in the line is any of the specified feminine endings words [FEMWRD], it’s a feminine ending.
Otherwise its ending is masculine.
Count resulting feminine lines.
*/
		int fem_count = 0;


		for (String word : lines) {
			if ((word.matches(fem_endings_regex) && !word.matches(masc_exceptions_regex)) || word.matches(fem_words_regex)) {
				fem_count++;
				//System.out.println(word);
			}
		}

		reader.close();
		return fem_count;
		// !word.matches(masc_exceptions_regex) = 313
		// word.matches(fem_words_regex) = 10
		// word.matches(masc_exceptions_regex) = 65
		// word.matches(fem_endings_regex) = 102
		// word.matches(fem_endings_regex) && !word.matches(masc_exceptions_regex) = 57
	}



	public static String createRegexFromTxtFileOfWords(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		// desired regular expression: 
		// .*(~all words in fem text files separated by "|" 
		// to denote or~)(]|\))?(.|:|;|,|\?|!)?"?$
		sb.append("(?i).*("); // build beginning of regular expression

		// list of full feminine words 
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
			String line = reader.readLine();
			
			String delim = "";
			while (line != null) {
				sb.append(delim);
				delim = "|";
				sb.append("\\b");
				sb.append(line);
				sb.append("\\b");
				line = reader.readLine();
			}

			reader.close();

			sb.append(")(]|\\))?(.|:|;|,|\\?|!)?\"?$"); // buildend of regular expression
			String everything = sb.toString().toLowerCase();
			return everything;

	}

	public static String createRegexFromTxtFileOfEndings(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		// desired regular expression: 
		// .*(~all words in fem text files separated by "|" 
		// to denote or~)(]|\))?(.|:|;|,|\?|!)?"?$
		sb.append("(?i).*("); // build beginning of regular expression

		// list of full feminine words 
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
			String line = reader.readLine();
			

			String delim = "";
			while (line != null) {
				sb.append(delim);
				delim = "\\b|";
				sb.append(line);
				line = reader.readLine();
			}

			reader.close();

			sb.append(")(]|\\))?(.|:|;|,|\\?|!)?\"?$"); // build end of regular expression
			String everything = sb.toString().toLowerCase();
			return everything;

	}

}