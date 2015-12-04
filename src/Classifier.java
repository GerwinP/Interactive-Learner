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

    public static void main(String[] args) {
        Classifier classifier = new Classifier();
        List<String> tokens = classifier.textTokenizer("files/test.txt");
        System.out.println(tokens);
    }

    public Classifier() {
        tokenizer = new Tokenizer();
    }

    /**
     *
     * @param filename
     * @return
     */
    public List<String> textTokenizer(String filename) {
        String content = "";
        try {
            content = readFile(filename);
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
