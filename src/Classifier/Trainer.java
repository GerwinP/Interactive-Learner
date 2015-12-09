package classifier;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Gerwin on 9-12-2015.
 */
public class Trainer {

    private Classifier classifier;
    private String filename;
    private String classi;
    private String value;

    public static void main(String[] args) {
        new Trainer();
    }

    /**
     * The constructor of the Trainer class
     */
    public Trainer () {
        getUserInput();
        train();
    }

    /**
     * A method that gets user input and stores this
     */
    private void getUserInput() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter filename");
        this.filename = reader.next();
        System.out.println("Enter classifier");
        this.classi = reader.next();
        System.out.println("Enter value");
        this.value = reader.next();
    }

    /**
     * The method that trains the classifier, using methods in the classifier
     * @return
     */
    private boolean train() {
        classifier = new Classifier(filename, classi, value);
        try {
            classifier.tokenizeAndClassify();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
