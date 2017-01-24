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

    private static final Pattern PUNCTUATION = Pattern.compile("[\\[,.?!@+-=_:/{}\"\\]]|[)]|[(]|[><]");

    public static void main(String[] args) {
        String file = readFile("..\\blogs\\blogs\\F\\F-test1.txt");
        System.out.println("");
        file = removePunctuation(file);
        System.out.println("Removing punctuation....");
        System.out.println(file);
        System.out.println("");
        file = toLowerCase(file);
        System.out.println("Removing uppercase letters....");
        System.out.println(file);
        String[] tokens = createTokens(file);
        System.out.println("Tokenizing the file....");
        System.out.println(Arrays.toString(tokens));
        Map<String, Integer> tokenMap = countTokens(tokens);
        System.out.println("Counting the tokens...");
        System.out.println(tokenMap.toString());
        tokenMap = removeStopWords(tokenMap);
        System.out.println("Removing stopwords....");
        System.out.println(tokenMap.toString());
        System.out.println("Counting the tokens....");
        System.out.println(tokenMap.size());
        tokenMap = removeOnes(tokenMap);
        System.out.println("Removing tokens that occur once....");
        System.out.println(tokenMap.toString());
        System.out.println("Counting the tokens....");
        System.out.println(tokenMap.keySet().size());
    }

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
        // Remove tokens that occur once or twice
        tokenMap = removeOnes(tokenMap);
        // Remove single character words
        tokenMap = removeSingleChar(tokenMap);
        // Return the tokenMap
        return tokenMap;
    }

    public static String readFile(String filename) {
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

    public static String removePunctuation(String file) {
        file = PUNCTUATION.matcher(file).replaceAll("");
        return file;
    }

    public static String toLowerCase(String file) {
        file = file.toLowerCase();
        return file;
    }

    public static String[] createTokens(String file) {
        String[] tokens = file.split("\\s+");
        return tokens;
    }

    public static Map<String, Integer> countTokens(String[] tokens) {
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

    public static Map<String, Integer> removeStopWords(Map<String, Integer> tokenMap) {
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

    public static Map<String, Integer> removeOnes(Map<String, Integer> tokenMap) {
        Set<String> keySet = tokenMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            /*
            if (tokenMap.get(word) == 1 || tokenMap.get(word) == 2) {
                iterator.remove();
            }
            */
            if (tokenMap.get(word) < 20) {
                iterator.remove();
            }
        }
        return tokenMap;
    }

    public static Map<String, Integer> removeSingleChar(Map<String, Integer> tokenMap) {
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
