import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gerwin on 21-12-2016.
 */
public class Classifier {

    private Learner learner;
    private Tokenizer tokenizer = new Tokenizer();

    public Classifier(Learner learner) {
        this.learner = learner;
    }

    public String predictClass(String filename) {
        Map<String, Integer> tokenMap = tokenizer.tokenize(filename);
        System.out.println("The length of the tokenmap: " + tokenMap.size());
        int count = 0;
        for (int i : tokenMap.values()) {
            count += i;
        }
        System.out.println("Word amount is: " + count);
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
        System.out.println("The probmap is: " + probMap.toString());
        for (Map.Entry<String, Double> entry : probMap.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        String predictedClass = maxEntry.getKey() == null ? "" : maxEntry.getKey();
        System.out.println("The predicted class = " + predictedClass);
        return predictedClass;
    }

    private void removeRange(Map<String, Integer> tokenMap) {
        double minPercent = learner.getMinPercent();
        double maxPercent = learner.getMaxPercent();
        //int minCount = (int) Math.floor(minPercent * calculateWords(classifier)/documents.get(classifier));
        //int maxCount = (int) Math.ceil(maxPercent * calculateWords(classifier)/documents.get(classifier));

    }

    private void removeOnChiSquare(Map<String, Integer> tokenMap) {

    }
}
