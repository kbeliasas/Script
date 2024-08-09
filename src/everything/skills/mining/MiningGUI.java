package everything.skills.mining;

import everything.Config;
import everything.Main;
import everything.SkillGUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class MiningGUI extends JFrame implements SkillGUI {
    public MiningGUI(Main main) {
        super();
        setTitle("Everything Mining");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        var oreLabel = new JLabel("ORE:");
        var oreComboBox = new JComboBox<>(MiningConfig.Ore.values());
        var pickaxeLabel = new JLabel("PICKAXE:");
        var pickaxeComboBox = new JComboBox<>(MiningConfig.Pickaxe.values());
        var goalLabel = new JLabel("GOAL:");
        var goalSpinnerModel = new SpinnerNumberModel(0, 0, 100000, 1);
        var goalSpinner = new JSpinner(goalSpinnerModel);
        var nextButton = new JButton("Start");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        nextButton.addActionListener((_event) -> {
            var ore = (MiningConfig.Ore) oreComboBox.getSelectedItem();
            var pickaxe = (MiningConfig.Pickaxe) pickaxeComboBox.getSelectedItem();
            var goal = (Integer) goalSpinner.getValue();
            main.setConfig(new Config(false, true, false));
            main.setGenericSkill(
                    new MiningV2(main,
                            ore.getRockName(),
                            ore.getOreId(),
                            ore.getMine(),
                            pickaxe.getPickaxeId(),
                            ore.getDistance()));
            main.setGoal(goal);
            main.setStart(true);
            dispose();
        });

        // GUI layout:
        //
        // |-Label-|-ComboBox-|
        // |-----Button-------|

        add(oreLabel, "split"); // split will split the current row
        add(oreComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(pickaxeLabel, "split");
        add(pickaxeComboBox, "grow, wrap");
        add(goalLabel, "split");
        add(goalSpinner, "grow, wrap");
        add(nextButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }

}
