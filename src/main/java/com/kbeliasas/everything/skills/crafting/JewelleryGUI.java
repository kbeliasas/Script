package com.kbeliasas.everything.skills.crafting;

import com.kbeliasas.everything.Config;
import com.kbeliasas.everything.Main;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class JewelleryGUI extends JFrame {
    public JewelleryGUI(Main main) {
        super();
        setTitle("com.kbeliasas.everything Jewellery");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        var productLabel = new JLabel("PRODUCT:");
        var productComboBox = new JComboBox<>(CraftingConfig.JewelleryProducts.values());
        var nextButton = new JButton("Start");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        nextButton.addActionListener((_event) -> {
            var product = (CraftingConfig.JewelleryProducts) productComboBox.getSelectedItem();
            main.setConfig(new Config(true, false, false));
            var config = new CraftingConfig().getJewelleryConfig(product);
            main.setGenericSkill(
                    new CraftingTiara(main,
                            config.getBarID()));
            main.setStart(true);
            dispose();
        });

        // GUI layout:
        //
        // |-Label-|-ComboBox-|
        // |-----Button-------|

        add(productLabel, "split"); // split will split the current row
        add(productComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(nextButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }

}
