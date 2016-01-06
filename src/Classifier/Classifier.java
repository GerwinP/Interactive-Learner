package classifier;

import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Gerwin on 3-12-2015.
 * The classifier of the interactive learner
 */
public class Classifier {

    private Tokenizer tokenizer;
    private static final String prefix = "files/";
    private static final String vocabPrefix = "vocabs/";
    private static final String vocabFile = "vocab.txt";
    private String filename;
    private String value;
    private String classifier;
    private final double k = 1;

    public static void main(String[] args) {
        new Classifier();

    }

    /**
     * The Classifier constructor that is used by the user for giving files to be classified
     */

    public Classifier () {
        classifier = "test";

        getUserInput();
        try {
            classify(textTokenizer());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor the Trainer uses for classification of files
     * @param filename
     * @param classifier
     * @param value
     */
    public Classifier(String filename, String classifier, String value) {
        this.filename = filename;
        this.classifier = classifier;
        this.value = value;
    }

    /**
     * A method for obtaining user input and storing this in variables
     */
    private void getUserInput() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter filename");
        this.filename = reader.next();
        System.out.println("Enter classifier");
        this.classifier = reader.next();
    }

    /**
     * The method that tokenizes and classifies the file
     * @return returns a String that says which value the document was classified to
     * @throws IOException
     */
    public String tokenizeAndClassify() throws IOException{
        HashMap<String, Integer> tokens = textTokenizer();
        if (value != null) {
            writeFileToVocab(tokens, value);
            moveFile();
        }
        return "Document is classified as " + value;
    }

    /**
     * classifies a file according to the multinomial naive Bayes classification
     * @param tokens the hashmap with all the words as keys and the values are how often the word occurs
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void classify(HashMap<String, Integer> tokens) throws IOException, ClassNotFoundException {
        File file = new File(vocabPrefix + classifier);
        String[] values = file.list();
        double chance = -1;
        String theValue = "";
        for (String value : values) {
            System.out.println("The current value is : " + value);
            LinkedList<Double> chances = calculate(tokens, value);
            double newChance = chanceValue(chances, value);
            if (newChance > chance) {
                chance = newChance;
                theValue = value;
            }
        }
        //Update the vocabulary
        HashMap<String, Integer> vocabMap = updateMap(readMapFile(theValue), tokens);
        writeFileToVocab(vocabMap, theValue);
        this.value = theValue;
        moveFile();
        System.out.println("The value is: " + theValue);
    }

    /**
     * The chance that a certain file is of that value
     * @param chances, the chances of each indiviual word
     * @param value, the possible value the chance is calculated for
     * @return
     */
    private double chanceValue(LinkedList<Double> chances, String value) {
        int x = filesPerValue(value);
        int y = totalValueFiles();
        double chance = x/y;
        for (double chanceValue : chances) {
            chance = chance * chanceValue;
        }
        return chance;
    }

    /**
     * the amount of files that are present for a certain value
     * @param value, the value the amount of files are counted for
     * @return returns an int with the amount of files per value
     */
    private int filesPerValue(String value) {
        File file = new File(prefix + classifier + "/" + value);
        return file.list().length;
    }

    /**
     * The total amount of files that are in a category
     * @return returns an int with the total amount
     */
    private int totalValueFiles() {
        File file = new File(prefix + classifier);
        String[] values = file.list();
        int total = 0;
        for (String value : values) {
            File fileValue = new File(prefix + classifier + "/" + value);
            total = total + fileValue.list().length;
        }
        return total;
    }

    /**
     * Calculates the chances per word of a file of a certain value
     * @param tokens, the file in tokens
     * @param value, the possible value the chances are calculated for
     * @return, returns an List with all the chances per word
     */
    public LinkedList<Double> calculate(HashMap<String, Integer> tokens, String value) {
        HashMap<String, Integer> vocabMap = null;
        LinkedList<Double> chances = new LinkedList<>();
        try {
            vocabMap = readMapFile(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (vocabMap == null) {
            System.out.println("Your vocabMap is null!!");
            return chances;
        }

        for (String word : tokens.keySet()) {
            int c = -1;
            try {
                c = vocabMap.get(word);
            } catch (NullPointerException e) {
                c = 0;
            }
            int n = calculateWordAmount(vocabMap);
            int v = vocabMap.keySet().size();
            int s = tokens.get(word);
            double chance = (c+k)/(n+k*v);
            chance = Math.pow(chance, (double) s);
            chances.add(chance);
        }
        return chances;
    }

    /**
     * Calculates the amount of words in a file
     * @param map, the map the words are counted for
     * @return returns the amount of words
     */
    public int calculateWordAmount(HashMap<String, Integer> map) {
        int amount = 0;
        for (String word : map.keySet()) {
            amount = amount + map.get(word);
        }
        return amount;
    }

    /**
     * The method that move the file from the main directory to the sub directory of the right value
     */
    public void moveFile () {
        try {
            Path path = Paths.get(prefix + classifier + "/" + value);
            if (Files.notExists(path) && (classifier != null || value != null)) {
                Files.createDirectories(path);
            }
            Files.move(Paths.get(prefix + filename), Paths.get(prefix + classifier + "/" + value + "/" + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tokenizes the text file using Tokenizer.java
     * and also turns it into an HashMap
     * @return return the text tokenized and converted to an Hashmap
     */
    public HashMap<String, Integer> textTokenizer () {
        String content = "";
        try {
            content = readFile(prefix + filename);
            System.out.println(content);
        } catch (java.io.IOException e) {
            System.out.println(e.toString());
        }
        LinkedList<String> tokens = tokenizer.tokenize(content);
        HashMap<String, Integer> tokenMap = makeMap(tokens);
        return tokenMap;
    }

    /**
     * Make a list of words into a HashMap with word and the times it occurs.
     * @param tokens the tokens of a file
     * @return HashMap of the tokens
     */
    public HashMap<String, Integer> makeMap(LinkedList<String> tokens) {
        HashMap<String, Integer> tokenMap = new HashMap<>();
        for (String token : tokens) {
            if (tokenMap.containsKey(token)) {
                tokenMap.put(token, tokenMap.get(token) + 1);
            } else {
                tokenMap.put(token, 1);
            }
        }
        return tokenMap;
    }

    public HashMap<String, Integer> updateMap(HashMap<String, Integer> vocabMap, HashMap<String, Integer> tokenMap) {
        for (String token : tokenMap.keySet()) {
            if (vocabMap.keySet().contains(token)) {
                vocabMap.put(token, vocabMap.get(token) + tokenMap.get(token));
            } else {
                vocabMap.put(token, tokenMap.get(token));
            }
        }
        return vocabMap;
    }

    /**
     * Reads a file into a string
     * @param file the file that has to be read
     * @return the string of a file
     * @throws IOException
     */
    private String readFile (String file) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            return stringBuilder.toString();
        } finally {
            bufferedReader.close();
        }
    }

    /**
     * Writes an HashMap of tokens to a vocabFile
     * @param tokenMap, the map that contains the tokens.
     * @param value, the value of the category
     * @throws IOException
     */
    private void writeFileToVocab(HashMap<String, Integer> tokenMap, String value) throws IOException {
        File file = new File(vocabPrefix + classifier + "/" + value + "/" + vocabFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(tokenMap);
        s.close();
    }

    /**
     * Reads an hashmap from a file.
     * @param value, the value of the category
     * @return returns an HashMap with the words in the vocab
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private HashMap<String, Integer> readMapFile(String value) throws IOException {
        File file = new File(vocabPrefix + classifier + "/" + value + "/" + vocabFile);
        if (file.length() == 0) {
            return null;
        }
        FileInputStream f = new FileInputStream(file);
        Object result = null;
        ObjectInputStream s = null;
        while (true) {
            try {
                s = new ObjectInputStream(f);
                result = s.readObject();
            }  catch (EOFException e) {
                s.close();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                break;
            }
        }
        return (HashMap<String, Integer>) result;
    }
}
