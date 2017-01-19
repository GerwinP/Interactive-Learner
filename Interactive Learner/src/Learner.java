import java.io.File;
import java.util.Map;

/**
 * Created by Gerwin on 21-12-2016.
 */
public class Learner {

    Tokenizer tokenizer = new Tokenizer();

    public boolean learn(String path) {
        boolean returnVal = false;
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String filename = child.getAbsolutePath();
                Map<String, Integer> tokenMap = tokenizer.tokenize(filename);
                System.out.println(tokenMap.toString());
            }
        } else {
            // dir is not really a directory
            return returnVal;
        }
        return returnVal;
    }

    private boolean writeToVocab(String classname, Map<String, Integer> tokenmap) {

        return false;
    }
}
