import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

import static java.lang.Math.sqrt;

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

    public void calcChiSquare() {
        System.out.println("Calculating Chi-square");
        for (String className : vocab.keySet()) {
            wordCount.put(className, calculateWords(className));
        }
        Set<String> classSet = vocab.keySet();
        Set<String> wordList = buildWordList();
        Map<String, Double> chiMapping = new HashMap<>();
        for (String word : wordList) {
            List<List<Integer>> wordCounts = new LinkedList<List<Integer>>();
            List<Integer> classCounts = new LinkedList<Integer>();
            int w1 = 0;
            int w2 = 0;
            int N = 0;
            for (String className : classSet) {
                int w = 0;
                if (vocab.get(className).get(word) != null) {
                    w = vocab.get(className).get(word);
                } else {
                    w = 0;
                }
                w1 += w;
                int c = calculateWords(className);
                classCounts.add(c);
                N += c;
                int notW = c-w;
                w2 += notW;
                LinkedList<Integer> tuple = new LinkedList<>();
                tuple.add(w);
                tuple.add(notW);
                wordCounts.add(tuple);
            }
            // Calculate the expected values
            List<List<Double>> eValues = new LinkedList<List<Double>>();
            for (int j : classCounts) {
                LinkedList<Double> tuple = new LinkedList<Double>();
                tuple.add((double)(w1*j)/N);
                tuple.add((double)(w2*j)/N);
                eValues.add(tuple);
            }
            System.out.println("The evalues are: " + eValues.toString());

            double chiSquare = 0;
            // Calculate the Chi-Square values
            for (int i = 0 ; i < eValues.size(); i++) {
                chiSquare += sqrt(wordCounts.get(i).get(0)*eValues.get(i).get(0))/eValues.get(i).get(0);
                chiSquare += sqrt(wordCounts.get(i).get(1)*eValues.get(i).get(1))/eValues.get(i).get(1);
            }
            System.out.println("The chisquare for " + word + " is " + chiSquare);
            chiMapping.put(word, chiSquare);
        }
        System.out.println("Done calculating chisquare");
        System.out.println("The chi values: " + chiMapping.toString());
    }

    private int calculateWords(String className) {
        int count = 0;
        Map<String, Integer> classMap = vocab.get(className);
        for (Integer value : classMap.values()) {
            count += value;
        }
        return count;
    }

    private Set<String> buildWordList() {
        Set<String> wordList = new HashSet<String>();
        Set<String> classSet = vocab.keySet();
        for (String className : classSet) {
            wordList.addAll(vocab.get(className).keySet());
        }
        return wordList;
    }
}
