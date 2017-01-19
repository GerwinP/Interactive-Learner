import javax.swing.*;
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
    private JTextField pathField = new JTextField(40);
    private GUIController controller = new GUIController();

    public void addComponentToPane(Container pane) {
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel card1 = new JPanel() {
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width += extraWindowWidth;
                return size;
            }
        };
        choosePathButton = new JButton("Choose train directory");
        choosePathButton.addActionListener(controller);
        card1.add(choosePathButton);
        pathField.setEditable(false);
        card1.add(pathField);
        classifyButton = new JButton("Classify files");
        classifyButton.addActionListener(controller);
        card1.add(classifyButton);
        JPanel card2 = new JPanel();
        card2.add(new JTextField("Textfield", 40));
        tabbedPane.addTab("Learner", card1);
        tabbedPane.addTab("Classifier", card2);

        pane.add(tabbedPane, BorderLayout.CENTER);
    }

    public static void createAndShowGUI() {
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
