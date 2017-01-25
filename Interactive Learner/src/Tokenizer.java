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

    private static final Pattern PUNCTUATION = Pattern.compile("[\\[,.?!@+-=_:/{}\"\\]]|[)]|[(]|[><~*]");

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

    private static String removePunctuation(String file) {
        file = PUNCTUATION.matcher(file).replaceAll("");
        return file;
    }

    private static String toLowerCase(String file) {
        file = file.toLowerCase();
        return file;
    }

    private static String[] createTokens(String file) {
        String[] tokens = file.split("\\s+");
        return tokens;
    }

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
