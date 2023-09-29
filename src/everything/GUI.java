package everything;

import net.miginfocom.swing.MigLayout;
import org.dreambot.api.methods.skills.Skill;

import javax.swing.*;

public class GUI extends JFrame {
    public GUI() {
        super();

        setTitle("Everything");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        JLabel modeLabel = new JLabel("Skill:");
        JComboBox<Skill> modeComboBox = new JComboBox<>(Skill.values());
        JButton startScriptButton = new JButton("Start Script");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        startScriptButton.addActionListener((_event) -> {

            Main.skillToTrain = (Skill) modeComboBox.getSelectedItem();

            dispose();
        });

        // GUI layout:
        //
        // |-Label-|-ComboBox-|
        // |-----Button-------|

        add(modeLabel, "split"); // split will split the current row
        add(modeComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(startScriptButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }
}
