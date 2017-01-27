import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.*;

/**
 * Created by Gerwin Puttenstein on 21-12-2016.
 */
public class Learner extends Observable {

    // The class that tokenizes a given file to a map of tokens
    private Tokenizer tokenizer = new Tokenizer();
    // The vocabulary, it is a mapping of the class names with their map of words.
    private Map<String, Map<String, Integer>> vocab = new HashMap<String, Map<String, Integer>>();
    // A mapping of the class names and their word count
    private Map<String, Integer> wordCount = new HashMap<String, Integer>();
    // A map that contains the class name as a key and the amount of documents used for training as key
    private Map<String, Integer> documents = new HashMap<String, Integer>();

    // The mapping of the class names and their prior probabilities
    private Map<String, Double> priorProb = new HashMap<String, Double>();
    // The mapping of the class names and their maps of words with their conditional probabilities
    private Map<String, Map<String, Double>> condProb = new HashMap<String, Map<String, Double>>();

    // Critical value chi-squared. It is the value for P(Chi^2 >= c) = alpha
    // Where alpha is 0,001 and 1 degree of freedom, because of the 2 class corpus that were used in testing.
    private final double criticalValue = 10.83;

    // The min and max percentage used to calculate the really low count words
    // and the really high count words and remove these.
    private final double minPercent = 0.05;
    private final double maxPercent = 0.7;

    // The smoothing k for calculating probabilities, default k=1
    private int smoothingK = 1;

    /**
     * The constructor for the Learner.
     * A new Learner is constructed with a given smoothing K value, which is used for calculating the conditional probabilities
     * @param smoothingK, the value for the smoothing K
     */
    public Learner(int smoothingK) {
        this.smoothingK = smoothingK;
    }

    /**
     * Adds a map of tokens to the vocabulary of a given class.
     * The map is already tokenized.
     * @param tokenMap, the tokenized version of a file
     * @param classifier, the class to which the tokenMap belongs to
     */
    public void addToVocab(Map<String, Integer> tokenMap, String classifier) {
        Set<String> keys = tokenMap.keySet();
        if (vocab.get(classifier) == null) {
            vocab.put(classifier, tokenMap);
        } else {
            for (String word : keys) {
                if (vocab.get(classifier).containsKey(word)) {
                    vocab.get(classifier).put(word, vocab.get(classifier).get(word)+tokenMap.get(word));
                } else {
                    vocab.get(classifier).put(word, tokenMap.get(word));
                }
            }
        }
    }

    /**
     * Adds all files in the gives path to the vocabulary, while first tokenizing these values.
     * Also notifies its observers (the GUI) to let it know it started adding the files and to notify is when it is done
     * @param path, the directory location of the files.
     * @param classifier, the class the files belong to
     */
    public void addAll(String path, String classifier) {
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        documents.put(classifier, directoryListing.length);
        if (directoryListing != null) {
            setChanged();
            notifyObservers(Codes.ADDING);
            for (File child : directoryListing) {
                String filename = child.getAbsolutePath();
                Map<String, Integer> tokenMap = tokenizer.tokenize(filename);
                addToVocab(tokenMap, classifier);
            }
            setChanged();
            notifyObservers(Codes.ADDED);
        } else {
            setChanged();
            notifyObservers(Codes.ERROR);
        }
    }

    /**
     * Trains the interactive learner.
     * This is done by first removing a certain range of words, these are words that or occur very often, or very little
     * After that, it removes words based on their chi-square values
     * And it calculates the prior and conditional probabilities.
     */
    public void train() {
        setChanged();
        notifyObservers(Codes.TRAINING);
        removeRange();
        removeOnChiSquare();
        calculateProbs();
        setChanged();
        notifyObservers(Codes.TRAINED);
    }

    /**
     * Retrains the interactive learner.
     * This is done by removing words based on their chi-square values and recalculating the prior
     * and conditional probabilities
     */
    public void reTrain() {
        removeOnChiSquare();
        calculateProbs();
    }

    /**
     * Calculates the prior probabilities of every class in the vocabulary
     * Also calculates the conditional probabilities of every word in the vocabulary.
     * It saves these values in global Maps
     */
    private void calculateProbs() {
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
    private Map<String, Double> calcChiSquare() {
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

    /**
     * Calculates the chi-square value of a given word.
     * This uses the already filled vocabulary and uses this to calculate all the necessary values
     * @param word, the word the chi-square value is to be calculated of
     * @return the chi-square value of the given word.
     */
    private Double calcChiSquare(String word) {
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
    private void removeOnChiSquare() {
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
    }

    /**
     * Remove a certain range of words based on the minPercent and maxPercent given.
     * This method removes the words that don't occur often or occur a lot.
     * This is to improve performance during the initial training.
     */
    private void removeRange() {
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

    /**
     * Set the smoothing K value used in calculating the conditional probabilities
     * Will be 1 most of the time, but is changeable for testing
     * @param k, the new value of the smoothing k value.
     */
    public void setK(int k) {
        this.smoothingK = k;
    }

    /**
     * Returns the map with the prior probabilities as the values and the keys are the classes that were trained
     * @return a map of the classes and their prior probabilities
     */
    public Map<String, Double> getPriorProb() {
        return this.priorProb;
    }

    /**
     * A method that returns the prior probability of a given class
     * @param classifier, the class from which the prior probability is requested
     * @return the prior probability of the given classifier.
     */
    public double getClassPriorProb(String classifier) { return this.priorProb.get(classifier); }

    /**
     * Gets the map with the conditional probabilities of a given class
     * @param classifier, the class from which the map with conditional probabilities is requested
     * @return a map with words as keys and their respective conditional probabilities as the values
     */
    public Map<String, Double> getClassCondProb(String classifier) { return this.condProb.get(classifier); }
}
