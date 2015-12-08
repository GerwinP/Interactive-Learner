package test;

import classifier.Classifier;

import java.io.IOException;
import java.util.List;

/**
 * Created by Gerwin on 8-12-2015.
 */
public class ClassifierTest {

    private static Classifier classifier;

    public static void main(String[] args) {
        classifier = new Classifier("test2.txt", "test", "test2");
        List<String> words = classifier.textTokenizer("test2.txt");
        System.out.println(words.toString());
        try {
            classifier.addToVocab(words, "test2");
            System.out.println(classifier.getVocabList("test2").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
