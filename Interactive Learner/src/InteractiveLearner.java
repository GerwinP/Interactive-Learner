import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Admin on 22-Jan-17.
 */
public class InteractiveLearner implements ActionListener{

    private GUITest gui;
    private Learner learner;
    private Classifier classifier;
    private JFileChooser dirChooser = new JFileChooser();
    private JFileChooser fileChooser = new JFileChooser();

    public InteractiveLearner() {
        gui = new GUITest();
        gui.showGUI();
        learner = new Learner();
        classifier = new Classifier(learner);
        this.learner.addObserver(this.gui);
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        JButton[] buttonList = gui.getButtonList();
        for (JButton button : buttonList) {
            button.addActionListener(this);
        }
        //TODO remove testDir. This is just for testing.
        File testDir = new File("C:\\Users\\Gerwin\\IdeaProjects\\Interactive_Learner\\");
        dirChooser.setCurrentDirectory(testDir);
        fileChooser.setCurrentDirectory(testDir);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals("selectTrainDirectory")) {
            int returnVal = dirChooser.showOpenDialog(gui);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = dirChooser.getSelectedFile();
                gui.setDirectoryField(file.getAbsolutePath());
            }
        } else if (actionCommand.equals("addClassName")) {
            String className = gui.getClassName();
            String directoryText = gui.getDirectoryText();
            if (!directoryText.equals("")) {
                if (!className.equals("")) {
                    learner.addToVocab(directoryText,className);
                }
            }
        } else if (actionCommand.equals("Train")) {
            learner.removeRange();
            learner.removeOnChiSquare();
            learner.calculateProbs();
        } else if (actionCommand.equals("Classify")) {
            String fileDirectory = gui.getFileDirectoryField();
            if (!fileDirectory.equals("")) {
                classifier.predictClass(fileDirectory);
            }
        } else if (actionCommand.equals("ChooseFile")) {
            int returnVal = fileChooser.showOpenDialog(gui);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                gui.setFileDirectoryField(file.getAbsolutePath());
            }
        }
    }

    public static void main(String[] args) {
        InteractiveLearner interactiveLearner = new InteractiveLearner();
    }
}
