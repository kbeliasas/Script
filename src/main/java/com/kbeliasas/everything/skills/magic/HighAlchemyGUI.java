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
        var modeLabel = new JLabel("TYPE:");
        var modeComboBox = new JComboBox<>(HAConfig.Type.values());

        var itemLabel = new JLabel("ITEM:");
        var itemComboBox = new JComboBox<>(HAConfig.Item.values());

        nextButton.addActionListener((_event) -> {
            var type = (HAConfig.Type) modeComboBox.getSelectedItem();
            switch (type) {
                case STANDARD: {
                    main.setConfig(new Config(false, false, false));
                    main.setGenericSkill(new HAV2(main));
                    break;
                }
                case ITEM: {
                    var item = (HAConfig.Item) itemComboBox.getSelectedItem();
                    main.setConfig(new Config(true, false, false));
                    main.setGenericSkill(new HAItemV2(main, item));
                }
            }
            main.setStart(true);
            dispose();
        });

        add(modeLabel, "split"); // split will split the current row
        add(modeComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(itemLabel, "split");
        add(itemComboBox, "grow, wrap");
        add(nextButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }
}
