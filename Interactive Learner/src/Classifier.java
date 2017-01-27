import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Created by Gerwin Puttenstein on 21-12-2016.
 */
public class Classifier extends Observable{

    private Learner learner;
    private Tokenizer tokenizer = new Tokenizer();
    private Map<String, Integer> tokenMap;

    /**
     * The constructor of the Classifier
     * It gets a Learner as an arguments, so the classifier always had the correct Learner.
     * This means it can get the values and vocabularies from the Learner, and give the Learner new documents to add.
     * @param learner, the Learner belonging to this Classifier
     */
    public Classifier(Learner learner) {
        this.learner = learner;
    }

    /**
     * It predicts the class of the given file.
     * This prediction in based on the prior and conditional probabilities calculated in the Learner
     * @param filename, the location of the file to be classified
     * @return the class of the given file
     */
    public String predictClass(String filename) {
        tokenMap = new HashMap<>();
        tokenMap = tokenizer.tokenize(filename);
        int count = 0;
        for (int i : tokenMap.values()) {
            count += i;
        }
        Map<String, Double> probMap = new HashMap<String, Double>();
        for(String classifier : learner.getPriorProb().keySet()) {
            Map<String, Double> wordCondProb = learner.getClassCondProb(classifier);
            double classProb = Math.log(learner.getClassPriorProb(classifier));
            for (String word : tokenMap.keySet()) {
                if (wordCondProb.containsKey(word)) {
                    classProb += Math.log(wordCondProb.get(word));
                }
            }
            probMap.put(classifier, classProb);
        }
        Map.Entry<String, Double> maxEntry = null;
        for (Map.Entry<String, Double> entry : probMap.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        String predictedClass = maxEntry.getKey() == null ? "" : maxEntry.getKey();
        setChanged();
        Codes code = Codes.CLASSIFIED;
        code.setArg(predictedClass);
        notifyObservers(code);
        return predictedClass;
    }

    /**
     * Adds the given tokenMap of the new file to the vocabulary in the Learner.
     * This is with the given classifier
     * @param tokenMap, the map of tokens of the new file
     * @param classifier, the class of the new file
     */
    private void addToVocab(Map<String, Integer> tokenMap, String classifier) {
        learner.addToVocab(tokenMap, classifier);
    }

    /**
     * Retrains the learner after adding the new tokens to the vocabulary
     * @param classifier, the name of the class for which the tokens should be added
     */
    public void updateLearner(String classifier) {
        addToVocab(tokenMap, classifier);
        learner.reTrain();
        setChanged();
        notifyObservers(Codes.VERIFIED);
    }
}
