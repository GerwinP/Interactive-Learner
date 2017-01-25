import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.*;

/**
 * Created by Gerwin on 21-12-2016.
 */
public class Learner extends Observable {

    private Tokenizer tokenizer = new Tokenizer();
    private Map<String, Map<String, Integer>> vocab = new HashMap<String, Map<String, Integer>>();
    private Map<String, Map<String, Integer>> chiMap = new HashMap<String, Map<String, Integer>>();
    private Map<String, Integer> wordCount = new HashMap<String, Integer>();
    // A map that contains the class name as a key and the amount of documents used for training as key
    private Map<String, Integer> documents = new HashMap<String, Integer>();

    private Map<String, Double> priorProb = new HashMap<String, Double>();
    private Map<String, Map<String, Double>> condProb = new HashMap<String, Map<String, Double>>();

    // Critical value chi-squared. It is the value for P(Chi^2 >= c) = alpha
    // Where alpha is 0,001 and 1 degree of freedom, because of the 2 class corpus that were used in testing.
    private final double criticalValue = 10.83;

    private final double minPercent = 0.05;
    private final double maxPercent = 0.7;

    // The smoothing k for calculating probabilities, default k=1
    private int smoothingK = 1;

    public void addToVocab(String path, String classifier) {
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        documents.put(classifier, directoryListing.length);
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

    public void calculateProbs() {
        // Calculate the prior probability of every class
        int total = 0;
        for (int value : documents.values()) {
            total += value;
        }
        for (String className : documents.keySet()) {
            priorProb.put(className, (double)documents.get(className)/total);
        }
        // Calculate the conditional probabilities for every word
        Set<String> wordList = buildWordList();
        int wordCount = wordList.size();
        for (String classifier : vocab.keySet()) {
            Map<String, Double> probMap = new HashMap<>();
            for (String word : wordList) {
                int count = 0;
                if (vocab.get(classifier).get(word) != null) {
                    count = vocab.get(classifier).get(word);
                }
                double prob = (double)(count + smoothingK)/(calculateWords(classifier)+smoothingK*wordCount);
                probMap.put(word, prob);
            }
            condProb.put(classifier, probMap);
        }
    }

    /**
     * Calculates the Chi square value of all the words in the vocabulary
     * @return A Map of all the words with their respective chi square value.
     */
    public Map<String, Double> calcChiSquare() {
        for (String className : vocab.keySet()) {
            wordCount.put(className, calculateWords(className));
        }
        Set<String> wordList = buildWordList();
        Map<String, Double> chiMapping = new HashMap<>();
        for (String word : wordList) {
            double chiSquare = calcChiSquare(word);
            chiMapping.put(word, chiSquare);
        }
        return chiMapping;
    }

    public Double calcChiSquare(String word) {
        double chiSquare = 0;
        Set<String> classSet = vocab.keySet();
        for (String className : classSet) {
            wordCount.put(className, calculateWords(className));
        }
        List<List<Double>> wordCounts = new LinkedList<>();
        List<Double> classCounts = new LinkedList<>();
        double w1 = 0;
        double w2 = 0;
        double N = 0;
        for (String className : classSet) {
            double w = 0;
            if (vocab.get(className).get(word) != null) {
                w = vocab.get(className).get(word);
            } else {
                w = 0;
            }
            w1 += w;
            double c = wordCount.get(className);
            classCounts.add(c);
            N += c;
            double notW = c-w;
            w2 += notW;
            LinkedList<Double> tuple = new LinkedList<>();
            tuple.add(w);
            tuple.add(notW);
            wordCounts.add(tuple);
        }
        // Calculate the expected values
        List<List<Double>> eValues = new LinkedList<List<Double>>();
        for (double j : classCounts) {
            LinkedList<Double> tuple = new LinkedList<Double>();
            tuple.add((w1*j)/N);
            tuple.add((w2*j)/N);
            eValues.add(tuple);
        }
        // Calculate the Chi-Square values
        for (int i = 0 ; i < eValues.size(); i++) {
            chiSquare += pow(wordCounts.get(i).get(0)-eValues.get(i).get(0), 2)/eValues.get(i).get(0);
            chiSquare += pow(wordCounts.get(i).get(1)-eValues.get(i).get(1), 2)/eValues.get(i).get(1);
        }
        return chiSquare;
    }

    /**
     * Calculates the total amount of words in a class
     * @param className, the class to be counted
     * @return the amount of words in the given class
     */
    private int calculateWords(String className) {
        int count = 0;
        Map<String, Integer> classMap = vocab.get(className);
        for (Integer value : classMap.values()) {
            count += value;
        }
        return count;
    }

    /**
     * Builds a set of all unique words in all classes by adding the complete keyset of a class to the wordSet
     * All the words already in the set will not be added a second time.
     * @return the set of unique words in all classes
     */
    private Set<String> buildWordList() {
        Set<String> wordList = new HashSet<String>();
        Set<String> classSet = vocab.keySet();
        for (String className : classSet) {
            wordList.addAll(vocab.get(className).keySet());
        }
        return wordList;
    }

    /**
     * Removes the words from the vocabulary that have a value lower than the critical value set.
     * Because if they have a Chi square value lower than the critical value, they are not unique enough for a given class
     */
    public void removeOnChiSquare() {
        Map<String, Double> chiMapping = calcChiSquare();
        for (String className : vocab.keySet()) {
            int i = 0;
            Set<String> wordSet = vocab.get(className).keySet();
            Iterator<String> iterator = wordSet.iterator();
            while(iterator.hasNext()) {
                String word = iterator.next();
                if (chiMapping.get(word) < criticalValue) {
                    iterator.remove();
                    i++;
                }
            }
        }
        for (String classifier : vocab.keySet()) {
            System.out.println("The word list for class " + classifier + " consists of " + calculateWords(classifier));
            System.out.println("And has " + vocab.get(classifier).keySet().size() + " different words");
        }
    }

    /**
     * Remove a certain range of words based on the minPercent and maxPercent given.
     * This method removes the words that don't occur often or occur a lot.
     */
    public void removeRange() {
        for (String classifier : vocab.keySet()) {
            int minCount = (int) Math.floor(minPercent * calculateWords(classifier)/documents.get(classifier));
            int maxCount = (int) Math.ceil(maxPercent * calculateWords(classifier)/documents.get(classifier));
            Set<String> keySet = vocab.get(classifier).keySet();
            Iterator<String> iterator = keySet.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                String word = iterator.next();
                if (vocab.get(classifier).get(word) < minCount || vocab.get(classifier).get(word) > maxCount) {
                    iterator.remove();
                    i++;
                }
            }
        }

    }

    public void setK(int k) {
        this.smoothingK = k;
    }

    public Map<String, Double> getPriorProb() {
        return this.priorProb;
    }

    public Map<String, Map<String, Double>> getCondProb() {
        return this.condProb;
    }

    public double getMinPercent() { return this.minPercent; }

    public double getMaxPercent() { return this.maxPercent; }

    public double getClassPriorProb(String classifier) { return this.priorProb.get(classifier); }

    public Map<String, Double> getClassCondProb(String classifier) { return this.condProb.get(classifier); }

    /**
     * Sorts a map by its values
     * @param map, the map to be sorted
     * @return returns the sorted map
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
