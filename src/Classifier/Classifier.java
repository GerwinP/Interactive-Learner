package classifier;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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
        Classifier classifier = new Classifier();
        List<String> tokens = classifier.textTokenizer(classifier.filename);
        //classifier.classify();
        System.out.println(tokens);
    }

    public Classifier(String filename, String classifier, String value) {
        this.filename = filename;
        this.classifier = classifier;
        this.value = value;
    }

    public Classifier() {
        getUserInput();
        System.out.println(filename);
    }

    private void getUserInput() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter filename");
        this.filename = reader.next();
        System.out.println("Enter classifier");
        this.classifier = reader.next();
        System.out.println("Enter value");
        this.value = reader.next();
    }

    public Classifier(String filename) {
        tokenizer = new Tokenizer();
        this.filename = filename;
    }

    public void classify (List<String> tokens, String classifier) {
        this.classifier = classifier;
        for (String token: tokens) {

        }

    }

    public void moveFile () {
        try {
            Path path = Paths.get(prefix + classifier + "/" + value);
            if (Files.notExists(path) && (classifier != null || value != null)) {
                Files.createDirectories(path);
            }
            Files.move(Paths.get("files/test.txt"), Paths.get(prefix + classifier + "/" + value + "/" + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getVocabList (String value) throws IOException {
        List<String> vocabTokens = new LinkedList<>();
        Scanner scanner = new Scanner(new File(vocabPrefix + classifier + "/" + value + "/" + vocabFile));
        while (scanner.hasNext()) {
            vocabTokens.add(scanner.next());
        }
        return vocabTokens;
    }

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
            FileWriter writer = new FileWriter(vocabPrefix + classifier + "/" + value + "/" + vocabFile);
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
     *
     * @param filename
     * @return
     */
    public List<String> textTokenizer (String filename) {
        String content = "";
        try {
            content = readFile(prefix + filename);
            System.out.println(content);
        } catch (java.io.IOException e) {
            System.out.println(e.toString());
        }
        List<String> tokens = tokenizer.tokenize(content);
        tokens = tokenizer.removeExtras(tokens);
        return tokens;
    }

    /**
     *
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
