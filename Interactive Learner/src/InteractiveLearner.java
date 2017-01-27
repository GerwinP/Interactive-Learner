import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Gerwin Puttenstein on 22-Jan-17.
 */
public class InteractiveLearner implements ActionListener{

    private GUI gui;
    private Learner learner;
    private Classifier classifier;
    private JFileChooser dirChooser = new JFileChooser();
    private JFileChooser fileChooser = new JFileChooser();

    public InteractiveLearner() {
        gui = new GUI();
        gui.showGUI();
        // Default initialisation of a Learner with a smoothing k value of 1
        learner = new Learner(1);
        classifier = new Classifier(learner);
        this.learner.addObserver(this.gui);
        this.classifier.addObserver(this.gui);
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        JButton[] buttonList = gui.getButtonList();
        for (JButton button : buttonList) {
            button.addActionListener(this);
        }
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
                    SwingWorker worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                                learner.addAll(directoryText, className);
                            return null;
                        }
                    };
                    worker.execute();
                } else {
                    gui.updateTextArea("Please choose a name for the given class");
                }
            } else {
                gui.updateTextArea("Please choose a directory");
            }
        } else if (actionCommand.equals("Train")) {
            SwingWorker worker = new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    learner.train();
                    return null;
                }
            };
            worker.execute();
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
        } else if (actionCommand.equals("updateK")) {
            try {
                this.learner.setK(gui.getSmoothingK());
            } catch (NumberFormatException exc) {
                exc.printStackTrace();
            }
        } else if (actionCommand.equals("yesButton")) {
            String className = gui.getPredictedClassField();
            classifier.updateLearner(className);
        } else if (actionCommand.equals("noButton")) {
            String className = gui.getClassFileField();
            classifier.updateLearner(className);
        }
    }

    /**
     * Starts the interactive learner.
     */
    public static void main(String[] args) {
        InteractiveLearner interactiveLearner = new InteractiveLearner();
    }
}
