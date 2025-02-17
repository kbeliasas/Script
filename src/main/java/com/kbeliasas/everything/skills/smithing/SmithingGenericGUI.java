package com.kbeliasas.everything.skills.smithing;

import com.kbeliasas.everything.GUIFactory;
import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.SkillGUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class SmithingGenericGUI extends JFrame implements SkillGUI {
    public SmithingGenericGUI(Main main) {
        super();

        setTitle("com.kbeliasas.everything Smithing");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        var modeLabel = new JLabel("TYPE:");
        var modeComboBox = new JComboBox<>(SmithingConfig.Type.values());
        var nextButton = new JButton("Next");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        nextButton.addActionListener((_event) -> {
            var type = (SmithingConfig.Type) modeComboBox.getSelectedItem();
            switch (type) {
                case SMITHING:
                    SwingUtilities.invokeLater(() -> new SmithingGUI(main));
                    break;
                case SMELTING:
                    SwingUtilities.invokeLater(() -> new SmeltingGUI(main));
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
