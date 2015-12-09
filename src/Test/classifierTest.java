package test;

import classifier.Classifier;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gerwin on 8-12-2015.
 */
public class ClassifierTest {

    private static Classifier classifier;
    private static final String file = "test4.txt";
    private static final String value = "test2";
    private static final String classi = "test";

    public static void main(String[] args) {
        testTokenizeAndClassify();
    }

    private static void testVocab() {
        List<String> words = classifier.textTokenizer();
        System.out.println(words.toString());
        try {
            classifier.addToVocab(words, value);
            System.out.println(classifier.getVocabList(value).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testValuenames() throws IOException{
        LinkedList<String> tokens = classifier.textTokenizer();
        classifier.classify(tokens);
    }

    private static void testTokenizeAndClassify() {
        classifier = new Classifier();
    }
}
