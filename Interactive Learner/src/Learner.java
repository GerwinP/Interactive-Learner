import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Created by Gerwin on 21-12-2016.
 */
public class Learner extends Observable {

    private Tokenizer tokenizer = new Tokenizer();
    private Map<String, Map<String, Integer>> vocab = new HashMap<String, Map<String, Integer>>();
    private Map<String, Map<String, Integer>> chiMap = new HashMap<String, Map<String, Integer>>();
    private Map<String, Integer> wordCount = new HashMap<String, Integer>();

    public void addToVocab(String path, String classifier) {
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            setChanged();
            notifyObservers(Codes.ADDING);
            Map<String, Integer> classVocab = new HashMap<>();
            boolean first = true;
            for (File child : directoryListing) {
                String filename = child.getAbsolutePath();
                Map<String, Integer> tokenMap = tokenizer.tokenize(filename);
                if (first) {
                    classVocab = tokenMap;
                    first=false;
                } else {
                    Set<String> keys = tokenMap.keySet();
                    for (String word : keys) {
                        if (classVocab.containsKey(word)) {
                            classVocab.put(word, classVocab.get(word)+tokenMap.get(word));
                        } else {
                            classVocab.put(word, tokenMap.get(word));
                        }
                    }
                }
            }
            vocab.put(classifier, classVocab);
            setChanged();
            notifyObservers(Codes.ADDED);
            System.out.println("Done adding!");
            System.out.println(vocab.toString());

        } else {
            setChanged();
            notifyObservers(Codes.ERROR);
        }
    }

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

    private void calcChiSquare() {
        for (String className : vocab.keySet()) {
            wordCount.put(className, calculateWords(className));
        }


    }

    private int calculateWords(String className) {
        int count = 0;
        Map<String, Integer> classMap = vocab.get(className);
        Integer[] values = (Integer[]) classMap.values().toArray();
        for (Integer value : values) {
            count += value;
        }
        return count;
    }
}
