import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Gerwin Puttenstein on 20-12-2016.
 */
public class Tokenizer {

    // A pattern with common characters involved in the punctuation. Used to remove certain characters
    private static final Pattern PUNCTUATION = Pattern.compile("[\\[,.?!@+-=_:/{}\"\\]]|[)]|[(]|[><~*]");

    /**
     * A combination of all the methodes in the class to tokenize a given file
     * to a String without punctuation, upper case letters, stopwords or single character words
     * @param filename, the name of the file to be tokenized
     * @return A map representation of the file given, completely tokenized and counted.
     */
    public Map<String, Integer> tokenize(String filename) {
        // Read file to String
        String file = readFile(filename);
        // Remove punctuation from String
        file = removePunctuation(file);
        // Remove uppercase letters
        file = toLowerCase(file);
        // Create a list of tokens from the String
        String[] tokens = createTokens(file);
        // Give a number to the tokens on how often they occur
        Map<String, Integer> tokenMap = countTokens(tokens);
        // Remove stopwords as given on www.ranks.nl/stopwords
        tokenMap = removeStopWords(tokenMap);
        // Remove single character words
        tokenMap = removeSingleChar(tokenMap);
        // Return the tokenMap
        return tokenMap;
    }

    /**
     * Reads a file with the given filename and puts it in a String.
     * If no filename is given or the file cannot be found, it throws an exception
     * @param filename, the filename of the file to be converted to String
     * @return a string representation of the given file.
     */
    private static String readFile(String filename) {
        String file = "";
        Path path = Paths.get(filename);
        try {
            file = new String(Files.readAllBytes(path));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IOException thrown");
        }
        return file;
    }

    /**
     * Replaces all characters with an empty String if they match with any of the characters in PUNCTUATION.
     * @param file, a string representation of a file
     * @return the same String as given, but without the character in PUNCTUATION.
     */
    private static String removePunctuation(String file) {
        file = PUNCTUATION.matcher(file).replaceAll("");
        return file;
    }

    /**
     * Converts every upper case letter in the given String to a lower case letter.
     * @param file, a string representation of a file
     * @return the same String as given, but with all upper case letters to lower case letter
     */
    private static String toLowerCase(String file) {
        file = file.toLowerCase();
        return file;
    }

    /**
     * Creates tokens from a given String, split on spaces. This makes every word a token.
     * @param file, a string representation of a file
     * @return an array of the words in the given String
     */
    private static String[] createTokens(String file) {
        return file.split("\\s+");
    }

    /**
     * Counts all the words from the String array and puts them in a map.
     * The keys are the individual words and the values are how often they occur.
     * @param tokens, an array of words
     * @return a map of words
     */
    private static Map<String, Integer> countTokens(String[] tokens) {
        Map<String, Integer> tokenMap = new HashMap<String, Integer>();
        for (String word: tokens) {
            if(tokenMap.containsKey(word)) {
                tokenMap.put(word, tokenMap.get(word)+1);
            } else {
                tokenMap.put(word, 1);
            }
        }
        return tokenMap;
    }

    /**
     * Removes all stopwords from the given tokenMap. The stopwords can be found in Utils.
     * The stop words list comes from www.ranks.nl/stopwords
     * @param tokenMap A map with words as keys and Integers as the value.
     * @return the tokenMap without the stop words that are in Utils.
     */
    private static Map<String, Integer> removeStopWords(Map<String, Integer> tokenMap) {
        Set<String> keySet = tokenMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            if (Utils.getStopwords().contains(word)) {
                iterator.remove();
            }
        }
        return tokenMap;
    }

    /**
     * Removes all words in de tokenMap that consist of one single character
     * @param tokenMap, the tokenMap build by the tokenizer, just before the removal of single character words
     * @return the tokenMap, with the single character words removed.
     */
    private static Map<String, Integer> removeSingleChar(Map<String, Integer> tokenMap) {
        Set<String> keySet = tokenMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            if (word.length() == 1) {
                iterator.remove();
            }
        }
        return tokenMap;
    }
}
