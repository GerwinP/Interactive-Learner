import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;

/**
 * Created by Admin on 22-Jan-17.
 */
public class Controller implements ActionListener{

    private GUITest gui;
    private Learner learner;
    private JFileChooser chooser = new JFileChooser();
    private Controller controller;

    public Controller() {
        controller = this;
        gui = new GUITest(controller);
        gui.showGUI();
        learner = new Learner();
        this.learner.addObserver(this.gui);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JButton[] buttonList = gui.getButtonList();
        for (JButton button : buttonList) {
            button.addActionListener(this);
        }
        //TODO remove testDir. This is just for testing.
        File testDir = new File("C:\\Users\\Gerwin\\IdeaProjects\\Interactive_Learner\\");
        chooser.setCurrentDirectory(testDir);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals("selectTrainDirectory")) {
            int returnVal = chooser.showOpenDialog(gui);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
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
            learner.calcChiSquare();
        }

        /*
        if (e.getSource() == gui.selectTrainDirectoryButton) {
            int returnVal = chooser.showOpenDialog(gui);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                gui.directoryField.setText(file.getAbsolutePath());
            }
        } else if(e.getSource() == gui.classifyButton) {
            if (gui.directoryField.getText() != null || !gui.directoryField.getText().equals("")) {
                learner.learn(gui.directoryField.getText());
            } else {
                // Pathfield is still empty
            }
        } else if (e.getSource() == gui.addClassNameButton) {
            if (gui.directoryField.getText() != null || !gui.directoryField.getText().equals("")) {
                if (gui.nameOfTheClassTextField.getText() != null || !gui.nameOfTheClassTextField.getText().equals("")) {
                   boolean success = learner.addToVocab(gui.directoryField.getText(),gui.nameOfTheClassTextField.getText());
                }
            }

        }
        */
    }
}
