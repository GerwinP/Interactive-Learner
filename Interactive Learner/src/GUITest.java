import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Admin on 21-Jan-17.
 */
public class GUITest extends JPanel implements Observer{

    private JTabbedPane tabbedPane1;
    private JTextField directoryField;
    private JTextField nameOfTheClassTextField;
    private JTextArea classValues;
    private JPanel mainPanel;
    private JFrame frame;

    private JButton selectTrainDirectoryButton;
    private JButton trainButton;
    private JButton classifyButton;
    private JButton addClassNameButton;
    private JButton chooseFileButton;
    private JTextField fileDirectoryField;
    private JTextField classFileField;
    private JTextField smoothingKField;
    private JTextField predictedClassField;

    private JButton[] buttonList = {selectTrainDirectoryButton, trainButton, classifyButton, addClassNameButton, chooseFileButton};

    public GUITest(Controller controller) {
        setup();
    }

    private void setup() {
        frame = new JFrame("Interactive Learner");
        frame.setContentPane(this.mainPanel);
        mainPanel.setPreferredSize(new Dimension(600,320));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    public JButton[] getButtonList() { return this.buttonList;}

    public void showGUI() {
        frame.setVisible(true);
    }

    private void updateTextArea(String text) {
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

    @Override
    public void update(Observable o, Object code) {
        System.out.println("Received notification");
        if (code instanceof Codes) {
            switch ((Codes)code) {
                case ADDED: updateTextArea("Added new class " + getClassName());
                case ADDING: updateTextArea("Adding new class...");
                case ERROR: ;
            }
        }
    }
}
