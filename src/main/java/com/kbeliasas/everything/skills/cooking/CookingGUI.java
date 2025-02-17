package com.kbeliasas.everything.skills.cooking;

import com.kbeliasas.everything.Config;
import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.SkillGUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CookingGUI extends JFrame implements SkillGUI {
    public CookingGUI(Main main) {
        super();
        setTitle("com.kbeliasas.everything Cooking");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        JLabel fishLabel = new JLabel("FISH:");
        JComboBox<CookingConfig.Fish> fishComboBox = new JComboBox<>(CookingConfig.Fish.values());
        JLabel locationLabel = new JLabel("LOCATION:");
        JComboBox<CookingConfig.Location> locationComboBox = new JComboBox<>(CookingConfig.Location.values());
        JButton nextButton = new JButton("Start");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        nextButton.addActionListener((_event) -> {
            var fish = (CookingConfig.Fish) fishComboBox.getSelectedItem();
            var location = (CookingConfig.Location) locationComboBox.getSelectedItem();
            main.setConfig(new Config(true, false, false));
            main.setGenericSkill(new CookingV2(main,
                    fish.getRawFishId(),
                    fish.getFishId(),
                    location.getCookingLocation()));
            main.setStart(true);
            dispose();
        });

        // GUI layout:
        //
        // |-Label-|-ComboBox-|
        // |-----Button-------|

        add(fishLabel, "split"); // split will split the current row
        add(fishComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(locationLabel, "split");
        add(locationComboBox, "grow, wrap");
        add(nextButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }

}
