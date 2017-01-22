import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Admin on 22-Jan-17.
 */
public class Controller implements ActionListener{

    private GUITest gui;
    private Learner learner;
    private JFileChooser chooser = new JFileChooser();

    public Controller() {
        gui = new GUITest(this);
        learner = new Learner();
        gui.main();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //TODO remove testDir. This is just for testing.
        File testDir = new File("C:\\Development\\Interactive Learner\\");
        chooser.setCurrentDirectory(testDir);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println(e.getActionCommand());

        if (actionCommand.equals("selectTrainDirectory")) {
            int returnVal = chooser.showOpenDialog(gui);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                gui.setDirectoryField(file.getAbsolutePath());
            }
        } else if (actionCommand.equals("addClassName")) {
            if (!gui.getDirectoryText().equals("")) {
                if (!gui.getClassName().equals("")) {
                    boolean success = learner.addToVocab(gui.directoryField.getText(),gui.nameOfTheClassTextField.getText());
                }
            }
        } else if (actionCommand.equals("Train")) {

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
