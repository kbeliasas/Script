package everything.skills.runecrafting;

import everything.Config;
import everything.Main;
import everything.SkillGUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class RuneCraftingGUI extends JFrame implements SkillGUI {
    public RuneCraftingGUI(Main main) {
        super();
        setTitle("Everything RuneCrafting");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        JLabel modeLabel = new JLabel("RUNES:");
        JComboBox<RuneCraftingConfig.Runes> modeComboBox = new JComboBox<>(RuneCraftingConfig.Runes.values());
        JButton nextButton = new JButton("Start");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        nextButton.addActionListener((_event) -> {
            var runes = (RuneCraftingConfig.Runes) modeComboBox.getSelectedItem();
            main.setConfig(new Config(true, false, false));
            main.setGenericSkill(new RuneCraftingV2(main,
                    runes.getRuins(),
                    runes.getTiaraId(),
                    runes.getRunesCount()));
            main.setStart(true);
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
