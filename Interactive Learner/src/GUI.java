import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Gerwin Puttenstein on 21-Jan-17.
 */
public class GUI extends JPanel implements Observer{

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
    private JTextField predictedClassField;
    private JFormattedTextField smoothingKField;
    private JButton updateSmoothingKButton;
    private JButton yesButton;
    private JButton noButton;

    private JButton[] buttonList = {selectTrainDirectoryButton, trainButton, classifyButton, addClassNameButton, chooseFileButton, updateSmoothingKButton, yesButton, noButton};

    public GUI() {
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

    public void updateTextArea(String text) {
        classValues.append(text + "\n");
    }

    public void setDirectoryField(String text) {
        directoryField.setText(text);
    }

    public void setFileDirectoryField(String text) {fileDirectoryField.setText(text); }

    public void setPredictedClassField(String text) { predictedClassField.setText(text); }

    public String getDirectoryText() {
        return directoryField.getText();
    }

    public String getClassName() {
        return nameOfTheClassTextField.getText();
    }

    public String getFileDirectoryField() { return fileDirectoryField.getText(); }

    public int getSmoothingK() throws NumberFormatException{
        return Integer.parseInt(smoothingKField.getText());
    }

    public String getPredictedClassField() { return predictedClassField.getText(); }

    public String getClassFileField() { return classFileField.getText(); }

    @Override
    public void update(Observable o, Object code) {
        if (code instanceof Codes) {
            switch ((Codes)code) {
                case ADDING: updateTextArea("Adding new class.... Please wait...."); break;
                case ADDED: updateTextArea("Added new class " + getClassName()); break;
                case CLASSIFIED: setPredictedClassField(((Codes) code).getArg()); break;
                case TRAINING: updateTextArea("Training....Please wait...."); break;
                case TRAINED: updateTextArea("Trained the given classes, you can now start classifying."); break;
                case VERIFIED: setPredictedClassField(""); break;
                case ERROR: ;
            }
        }
    }
}
