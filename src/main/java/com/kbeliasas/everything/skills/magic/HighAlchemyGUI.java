package com.kbeliasas.everything.skills.magic;

import com.kbeliasas.everything.Config;
import com.kbeliasas.everything.Main;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class HighAlchemyGUI extends JFrame {
    public HighAlchemyGUI(Main main) {
        super();

        setTitle("Everything High Alchemy");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        var nextButton = new JButton("Next");

        nextButton.addActionListener((_event) -> {
            main.setConfig(new Config(false, false, false));
            main.setGenericSkill(new HAV2(main));
            main.setStart(true);
            dispose();
        });

        add(nextButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }
}
