package com.kbeliasas.everything;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class ResultsGUI extends JFrame {

    public ResultsGUI(List<String> results) {
        super();
        setTitle("Results");

        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        results.forEach(result -> {
            JLabel label = new JLabel(result);
            add(label, "grow, wrap");
        });

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }

}
