package everything.skills.firemaking;

import everything.Config;
import everything.Main;
import everything.SkillGUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class FiremakingGUI extends JFrame implements SkillGUI {
    public FiremakingGUI(Main main) {
        super();
        setTitle("Everything Firemaking");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        var logLabel = new JLabel("LOGS:");
        var logComboBox = new JComboBox<>(FiremakingConfig.Log.values());
        var goalLabel = new JLabel("GOAL XP K:");
        var goalSpinnerModel = new SpinnerNumberModel(0, 0, 100000, 1);
        var goalSpinner = new JSpinner(goalSpinnerModel);
        var nextButton = new JButton("Start");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        nextButton.addActionListener((_event) -> {
            var log = (FiremakingConfig.Log) logComboBox.getSelectedItem();
            var goal = (Integer) goalSpinner.getValue() * 1000;
            main.setConfig(new Config(false, false, true));
            main.setGenericSkill(new FiremakingV2(main,
                    log.getLogId()));
            main.setGoalXp(goal);
            main.setStart(true);
            dispose();
        });

        // GUI layout:
        //
        // |-Label-|-ComboBox-|
        // |-----Button-------|

        add(logLabel, "split"); // split will split the current row
        add(logComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(goalLabel, "split");
        add(goalSpinner, "grow, wrap");
        add(nextButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }
}
