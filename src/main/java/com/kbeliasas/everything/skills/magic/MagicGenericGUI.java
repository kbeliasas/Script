package com.kbeliasas.everything.skills.magic;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.SkillGUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class MagicGenericGUI extends JFrame implements SkillGUI {
    public MagicGenericGUI(Main main) {

        setTitle("Everything Magic");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        var modeLabel = new JLabel("TYPE:");
        var modeComboBox = new JComboBox<>(MagicConfig.Type.values());
        var nextButton = new JButton("Next");

        nextButton.addActionListener((_event) -> {
            var type = (MagicConfig.Type) modeComboBox.getSelectedItem();
            switch (type) {
                case COMBAT:
                    SwingUtilities.invokeLater(() -> new MagicGUI(main));
                    break;
                case HIGH_ALCHEMY:
                    SwingUtilities.invokeLater(() -> new HighAlchemyGUI(main));
                    break;
            }
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
