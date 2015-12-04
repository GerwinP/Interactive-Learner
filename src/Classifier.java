import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by Gerwin on 3-12-2015.
 * The classifier of the interactive learner
 */
public class Classifier {

    private Tokenizer tokenizer;
    private static final String prefix = "files/";
    private String filename;
    private String classifier;

    public static void main(String[] args) {
        Classifier classifier = new Classifier("test.txt");
        List<String> tokens = classifier.textTokenizer(classifier.filename);
        System.out.println(tokens);
    }

    public Classifier(String filename) {
        tokenizer = new Tokenizer();
        this.filename = filename;
    }

    public void classify(String filename, String classifier) {
        this.classifier = classifier;

    }

    public boolean addToVocab(List<String> words) {

        return false;
    }

    /**
     *
     * @param filename
     * @return
     */
    public List<String> textTokenizer(String filename) {
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
    private String readFile(String file) throws IOException{
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
