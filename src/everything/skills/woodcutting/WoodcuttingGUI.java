package everything.skills.woodcutting;

import everything.Config;
import everything.Main;
import everything.SkillGUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class WoodcuttingGUI extends JFrame implements SkillGUI {
    public WoodcuttingGUI(Main main) {
        super();
        setTitle("Everything Woodcutting");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        var treeLabel = new JLabel("TREE:");
        var treeComboBox = new JComboBox<>(WoodcuttingConfig.Tree.values());
        var axeLabel = new JLabel("AXE:");
        var axeComboBox = new JComboBox<>(WoodcuttingConfig.Axe.values());
        var goalLabel = new JLabel("GOAL XP K:");
        var goalSpinnerModel = new SpinnerNumberModel(0, 0, 100000, 1);
        var goalSpinner = new JSpinner(goalSpinnerModel);
        var nextButton = new JButton("Start");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        nextButton.addActionListener((_event) -> {
            var tree = (WoodcuttingConfig.Tree) treeComboBox.getSelectedItem();
            var axe = (WoodcuttingConfig.Axe) axeComboBox.getSelectedItem();
            var goal = (Integer) goalSpinner.getValue() * 1000;
            main.setConfig(new Config(false, false, true));
            main.setGenericSkill(new WoodcuttingV2(main,
                    tree.getArea(),
                    axe.getAxeId(),
                    tree.getLogsId(),
                    tree.getTree()));
            main.setGoalXp(goal);
            main.setStart(true);
            dispose();
        });

        // GUI layout:
        //
        // |-Label-|-ComboBox-|
        // |-----Button-------|

        add(treeLabel, "split");
        add(treeComboBox, "grow, wrap");
        add(axeLabel, "split"); // split will split the current row
        add(axeComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(goalLabel, "split");
        add(goalSpinner, "grow, wrap");
        add(nextButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }
}
