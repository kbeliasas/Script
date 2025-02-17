package com.kbeliasas.everything;

import net.miginfocom.swing.MigLayout;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;

import javax.swing.*;

public class GUI extends JFrame {
    public GUI(Main main) {
        super();

        setTitle("com.kbeliasas.everything");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        JLabel modeLabel = new JLabel("Skill:");
        JComboBox<Skill> modeComboBox = new JComboBox<>(Skill.values());
        JButton nextButton = new JButton("Next");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        nextButton.addActionListener((_event) -> {
            var skillToTrain = (Skill) modeComboBox.getSelectedItem();
            main.setSkillToTrain(skillToTrain);
            SkillTracker.start(skillToTrain);
            var guiFactory = new GUIFactory(main);
            SwingUtilities.invokeLater(() -> guiFactory.createSkillGui(skillToTrain));
            dispose();
        });

        // GUI layout:
        //
        // |-Label-|-ComboBox-|
        // |-----Button-------|

        add(modeLabel, "split"); // split will split the current row
        add(modeComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(nextButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }
}
