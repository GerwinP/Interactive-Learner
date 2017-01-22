import javax.swing.*;
import java.awt.*;

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

    private Controller controller;

    public GUITest(Controller controller) {
        this.controller = controller;
        setActionListeners();
    }

    public void main() {
        JFrame frame = new JFrame("Interactive Learner");
        frame.setContentPane(this.mainPanel);
        mainPanel.setPreferredSize(new Dimension(480,480));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void setActionListeners() {
        selectTrainDirectoryButton.addActionListener(controller);
        addClassNameButton.addActionListener(controller);
        trainButton.addActionListener(controller);
        classifyButton.addActionListener(controller);
    }

    public void updateTextArea(String text) {
        classValues.append(text + "\n");
    }

    public void setDirectoryField(String text) {
        directoryField.setText(text);
    }

    public String getDirectoryText() {
        return directoryField.getText();
    }

    public String getClassName() {
        return nameOfTheClassTextField.getText();
    }
}
