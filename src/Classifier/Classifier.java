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

    public static void main(String[] args) {
        new Classifier();
    }

    /**
     * The Classifier constructor that is used by the user for giving files to be classified
     */
    public Classifier () {
        getUserInput();
        try {
            System.out.println(tokenizeAndClassify());
        } catch (IOException e) {
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
     * @return
     * @throws IOException
     */
    public String tokenizeAndClassify() throws IOException{
        LinkedList<String> tokens = textTokenizer();
        if (this.classify(tokens) && value != null) {
            addToVocab(tokens, value);
        }
        return "Document is classified as " + value;
    }

    /**
     * Classifies the list with tokens to a value if the value is not yet known.
     * Moves also the file to the correct directory
     * @param tokens
     * @return
     * @throws IOException
     */
    public boolean classify (List<String> tokens) throws IOException{
        if (value != null) {
            moveFile();
        } else {
            Map<String, Integer> tuple = new HashMap<>();
            List<String> values = new LinkedList<>();
            File classDir = new File(prefix + this.classifier);
            String[] valuesNames = classDir.list();
            for (String value: valuesNames) {
                if (new File(prefix + this.classifier + "/" + value).isDirectory()) {
                    values.add(value);
                }
            }
            if (values.size() != 0) {
                for (String v : values) {
                    int occurs = 0;
                    List<String> vocab = getVocabList(v);
                    for (String word : vocab) {
                        if (tokens.contains(word)) {
                            occurs++;
                        }
                    }
                    tuple.put(v, occurs);
                }
                Map.Entry<String, Integer> maxEntry = null;
                for (Map.Entry<String, Integer> entry : tuple.entrySet()) {
                    if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                        maxEntry = entry;
                    }
                }
                this.value = maxEntry.getKey();
                moveFile();
            }

        }
        return true;
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
     * Creates a list from a vocabulary
     * @param value
     * @return
     * @throws IOException
     */
    public LinkedList<String> getVocabList (String value) throws IOException {
        LinkedList<String> vocabTokens = new LinkedList<>();
        File file = new File(vocabPrefix + classifier + "/" + value + "/" + vocabFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            vocabTokens.add(scanner.next());
        }
        return vocabTokens;
    }

    /**
     * Adds all the new words that are not in the right vocabulary to the vocabulary
     * @param words
     * @param value
     * @return
     * @throws IOException
     */
    public boolean addToVocab (List<String> words, String value) throws IOException {
        File file = new File(vocabPrefix + classifier + "/" + value + "/" + vocabFile);
        List<String> vocab = new LinkedList<String>();
        if (file.exists() && !file.isDirectory()) {
            vocab = getVocabList(value);
        }
        if (vocab != null) {
            for (String word : words) {
                if (!vocab.contains(word)) {
                    vocab.add(word);
                }
            }
        }
        try {
            Path path = Paths.get(vocabPrefix + classifier + "/" + value);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            File fileVocab = new File(vocabPrefix + classifier + "/" + value + "/" + vocabFile);
            if (!fileVocab.exists()) {
                fileVocab.createNewFile();
            }
            FileWriter writer = new FileWriter(fileVocab);
            for (String word: vocab) {
                writer.write(word + " ");
            }
            writer.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Tokenizes the text file using Tokenizer.java
     * @return
     */
    public LinkedList<String> textTokenizer () {
        String content = "";
        try {
            content = readFile(prefix + filename);
            System.out.println(content);
        } catch (java.io.IOException e) {
            System.out.println(e.toString());
        }
        LinkedList<String> tokens = tokenizer.tokenize(content);
        tokens = tokenizer.removeExtras(tokens);
        return tokens;
    }

    /**
     * Reads a file into a string
     * @param file
     * @return
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
}
