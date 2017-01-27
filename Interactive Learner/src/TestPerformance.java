import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Gerwin Puttenstein on 26-1-2017.
 * The class is used for training and classifying a lot of files at once.
 * This is done to get some performance measurements from several corpus more quickly.
 */
public class TestPerformance {

    private Learner learner = new Learner(1);
    private Classifier classifier = new Classifier(learner);
    private List<Integer> correctList = new LinkedList<>();
    private List<Integer> correctMailList = new LinkedList<>();

    /**
     * Constructor that runs all the training and test methods.
     */
    public TestPerformance() {
        this.trainBlog();
        this.testBlog();
        this.trainMail();
        this.testMail();
    }

    /**
     * Trains a Learner with a Classifier with the blogs corpus
     */
    private void trainBlog() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please provide location of first class: ");
        String first = scanner.next();
        System.out.println("Please give the class of this location: ");
        String classFirst = scanner.next();
        System.out.println("Adding....");
        learner.addAll(first, classFirst);
        System.out.println("Done adding!");
        System.out.println("Please provide location of second class");
        String second = scanner.next();
        System.out.println("Please give the class of this location");
        String classSecond = scanner.next();
        System.out.println("Adding....");
        learner.addAll(second, classSecond);
        System.out.println("Done adding!");
        System.out.println("Training the interactive learner");
        learner.train();
        System.out.println("Done training the interactive learner");
    }

    /**
     * Predicts the class of every file in the given directory for the blog corpus.
     * After every file you have to say whether it is correctly predicted or not.
     * After it is done predicting all the files, it gives some statistics on the amount of correct and incorrect predictions.
     */
    private void testBlog() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Now the learning and prediction phase starts.");
        System.out.println("Please give for every file the correct class when asked for.");
        System.out.println("Please use the class names given during training.");
        System.out.println("First give the test directory:");
        String testDir = scanner.next();
        File dir = new File(testDir);
        File[] directoryListing = dir.listFiles();
        for (File file : directoryListing) {
            String filePath = file.getAbsolutePath();
            String predClass = classifier.predictClass(filePath);
            System.out.println("The predicted of " + filePath + " class was " + predClass + ". Please give the correct class.");
            String correctClass = scanner.next();
            System.out.println("The system will be retrained....");
            if (predClass.equals(correctClass)) {
                correctList.add(1);
            } else {
                correctList.add(0);
            }
            classifier.updateLearner(correctClass);
            System.out.println("The system is retrained, get ready for the next one");
        }
        System.out.println("Nope, we are done!");
        int correct = 0;
        for (int i : correctList) {
            correct += i;
        }
        int notCorrect = correctList.size()-correct;
        System.out.println("The amount of correct predicted classes was: " + correct);
        System.out.println("The amount of not correct predicted classes was: " + notCorrect);
    }

    /**
     * Trains a Learner with a Classifier with the mails corpus
     */
    private void trainMail() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("");
        System.out.println("");
        System.out.println("Now we are going to test the mail corpus.");
        System.out.println("The learner and classifier will be reset.");
        this.learner = new Learner(1);
        this.classifier = new Classifier(this.learner);
        System.out.println("");
        System.out.println("Please provide location of first class: ");
        String first = scanner.next();
        System.out.println("Please give the class of this location: ");
        String classFirst = scanner.next();
        System.out.println("Adding....");
        learner.addAll(first, classFirst);
        System.out.println("Done adding!");
        System.out.println("Please provide location of second class");
        String second = scanner.next();
        System.out.println("Please give the class of this location");
        String classSecond = scanner.next();
        System.out.println("Adding....");
        learner.addAll(second, classSecond);
        System.out.println("Done adding!");
        System.out.println("Training the interactive learner");
        learner.train();
        System.out.println("Done training the interactive learner");
    }

    /**
     * Predicts the class of every file in the given directory for the mail corpus.
     * Based on the name of the file, it will check whether or not it predicted the class correctly.
     * This can be done, because the ham and spam messages have very distinct names.
     * After it is done predicting all the files, it gives some statistics on the amount of correct and incorrect predictions.
     */
    private void testMail() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Now the learning and prediction phase starts.");
        System.out.println("Please give for every file the correct class when asked for.");
        System.out.println("Please use the class names given during training.");
        System.out.println("First give the test directory:");
        String testDir = scanner.next();
        File dir = new File(testDir);
        File[] directoryListing = dir.listFiles();
        for (File file : directoryListing) {
            String filePath = file.getAbsolutePath();
            String fileName = file.getName();
            String predClass = classifier.predictClass(filePath);
            System.out.println("The predicted of " + filePath + " class was " + predClass + ".");
            System.out.println("The system will be retrained....");
            String correctClass = "h";
            if (fileName.contains("spm")) {
                correctClass = "s";
            }
            if (predClass.equals(correctClass)) {
                correctMailList.add(1);
            } else {
                correctMailList.add(0);
            }
            classifier.updateLearner(correctClass);
            System.out.println("The system is retrained, get ready for the next one");
        }
        System.out.println("Nope, we are done!");
        int correct = 0;
        for (int i : correctMailList) {
            correct += i;
        }
        int notCorrect = correctMailList.size()-correct;
        System.out.println("The amount of correct predicted classes was: " + correct);
        System.out.println("The amount of not correct predicted classes was: " + notCorrect);
    }

    public static void main(String[] args) {
        TestPerformance testPerformance = new TestPerformance();
    }
}
