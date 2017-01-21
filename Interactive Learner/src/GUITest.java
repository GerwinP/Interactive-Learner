import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Admin on 21-Jan-17.
 */
public class GUITest extends JPanel{

    private JTabbedPane tabbedPane1;
    private JButton selectTrainDirectoryButton;
    private JTextField directoryField;
    private JTextField nameOfTheClassTextField;
    private JButton addClassNameButton;
    private JTextArea classValues;
    private JButton trainButton;
    private JButton classifyButton;
    private JPanel mainPanel;

    private Controller controller = new Controller();

    public GUITest() {
        setActionListeners();
    }

    public void main() {
        JFrame frame = new JFrame("Interactive Learner");
        frame.setContentPane(new GUITest().mainPanel);
        mainPanel.setPreferredSize(new Dimension(480,480));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void setActionListeners() {
        selectTrainDirectoryButton.addActionListener(controller);
        addClassNameButton.addActionListener(controller);
        trainButton.addActionListener(controller);
        classifyButton.addActionListener(controller);
    }

    public class Controller implements ActionListener {

        private JFileChooser chooser = new JFileChooser();
        private Learner learner = new Learner();

        public Controller() {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //TODO remove testDir. This is just for testing.
            File testDir = new File("C:\\Development\\Interactive Learner\\");
            chooser.setCurrentDirectory(testDir);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == selectTrainDirectoryButton) {
                int returnVal = chooser.showOpenDialog(GUITest.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    directoryField.setText(file.getAbsolutePath());
                }
            } else if(e.getSource() == classifyButton) {
                if (directoryField.getText() != null || !directoryField.getText().equals("")) {
                    learner.learn(directoryField.getText());
                } else {
                    // Pathfield is still empty
                }
            } else if (e.getSource() == addClassNameButton) {
                if (directoryField.getText() != null || !directoryField.getText().equals("")) {
                    if (nameOfTheClassTextField.getText() != null || !nameOfTheClassTextField.getText().equals("")) {
                        learner.addToVocab(directoryField.getText(), nameOfTheClassTextField.getText());
                    }
                }

            }
        }
    }
}
