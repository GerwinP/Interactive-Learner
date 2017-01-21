import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by Gerwin on 21-12-2016.
 */
public class GUI extends JPanel{

    private static final int extraWindowWidth = 100;
    private JButton choosePathButton;
    private JButton classifyButton;
    private JButton trainButton;
    private JTextField pathField = new JTextField(40);
    private JTextArea classList = new JTextArea();
    private JTextField classField = new JTextField(20);
    private GUIController controller = new GUIController();

    private void addComponentToPane(Container pane) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Learner", createTab1());
        tabbedPane.addTab("Classifier", createTab2());
        pane.add(tabbedPane, BorderLayout.PAGE_START);
    }

    private JPanel createTab1() {
        /*
        JPanel card1 = new JPanel() {
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width += extraWindowWidth;
                return size;
            }
        };
        */
        JPanel card1 = new JPanel();
        card1.setPreferredSize(new Dimension(240, 320));
        BorderLayout borderLayout = new BorderLayout(5,5);
        card1.setLayout(borderLayout);
        choosePathButton = new JButton("Choose train directory");
        choosePathButton.addActionListener(controller);
        pathField.setEditable(false);
        classifyButton = new JButton("Classify files");
        classifyButton.addActionListener(controller);
        card1.add(choosePathButton, BorderLayout.PAGE_START);
        card1.add(pathField, BorderLayout.PAGE_START);
        card1.add(classField, BorderLayout.CENTER);
        card1.add(classifyButton, BorderLayout.CENTER);
        card1.add(classList, BorderLayout.PAGE_END);
        return card1;
    }

    private JPanel createTab2() {
        JPanel card2 = new JPanel();
        card2.add(new JTextField("Textfield", 40));
        return card2;
    }

    private static void createAndShowGUI() {
        JFrame classifierFrame = new JFrame("Tabs");
        classifierFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GUI gui = new GUI();
        gui.addComponentToPane(classifierFrame.getContentPane());
        classifierFrame.pack();
        classifierFrame.setLocationRelativeTo(null);
        classifierFrame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public class GUIController implements ActionListener {

        private JFileChooser chooser = new JFileChooser();
        private Learner learner = new Learner();

        public GUIController() {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //TODO remove testDir. This is just for testing.
            File testDir = new File("C:\\Development\\Interactive Learner\\");
            chooser.setCurrentDirectory(testDir);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == choosePathButton) {
                int returnVal = chooser.showOpenDialog(GUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    pathField.setText(file.getAbsolutePath());
                }
            } else if(e.getSource() == classifyButton) {
                if (pathField.getText() != null || !pathField.getText().equals("")) {
                    learner.learn(pathField.getText());
                } else {
                    // Pathfield is still empty
                }

            }
        }
    }
}
