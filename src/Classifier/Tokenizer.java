package classifier;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * Created by Gerwin on 1-12-2015.
 * classifier.Tokenizer for the Interactive Learner
 */
public class Tokenizer {

    private static final String testSentence = "This is a sentence, for testing !!! the tokenizer. So this sentence tests the tokenizer!!!!";

    public static void main(String[] args) {
        LinkedList<String> tokens = tokenize(testSentence);
        out.println(tokens.toString());
        //tokens = removeExtras(tokens);
        out.println(tokens.toString());
    }

    /**
     * Tokenizes the given string into a list by removing the capital letters
     * and deleting any punctuation.
     * @param string the String that has to be tokenized.
     */
    public static LinkedList<String> tokenize(String string) {
        LinkedList<String> tokenList = new LinkedList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        while(stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            token = token.toLowerCase();
            token = removePunctuation(token);
            tokenList.add(token);
        }
        return tokenList;
    }

    /**
     * Removes all the extra's in a list of strings.
     * @param list the list from which the extra's have to be removed.
     * @return Returns the new list without all the extra's
     */
    /*
    public static LinkedList<String> removeExtras(LinkedList<String> list) {
        LinkedList<String> tempList = new LinkedList<>();
        for (String token : list) {
            if (!tempList.contains(token) && !findPunctuation(token)) {
               tempList.add(token);
            }
        }
        return tempList;
    }
    */

    /**
     * Removes the punctuation from a string
     * @param token the string which could contain a punctuation
     * @return returns the string without the punctuation
     */
    public static String removePunctuation(String token) {
        token = token.replaceAll("([a-z,]+)[?:!.,;]*", "$1");
        return token;
    }

    /**
     * A method for checking if a token contains special characters
     * This method is there for making sure that tokens like "!!!" don't end up in de vocabulary
     * @param token
     * @return returns true or false depending on if the token contains any special characters
     */
    private static boolean findPunctuation(String token) {
        Pattern pattern = Pattern.compile("\\p{Punct}");
        Matcher matcher = pattern.matcher(token);
        return matcher.find();
    }
}
