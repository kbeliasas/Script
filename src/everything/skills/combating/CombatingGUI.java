package everything.skills.combating;

import everything.Config;
import everything.Main;
import everything.SkillGUI;
import net.miginfocom.swing.MigLayout;
import org.dreambot.api.methods.skills.Skill;

import javax.swing.*;

public class CombatingGUI extends JFrame implements SkillGUI {
    public CombatingGUI(Main main, Skill skill) {
        super();
        setTitle("Everything Combat");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        var foodLabel = new JLabel("FOOD:");
        var foodComboBox = new JComboBox<>(CombatingConfig.Food.values());
        var foodMinLabel = new JLabel("FOOD MIN:");
        var foodMinSpinnerModel = new SpinnerNumberModel(3, 0, 30, 1);
        var foodMinSpinner = new JSpinner(foodMinSpinnerModel);
        var foodAmountLabel = new JLabel("FOOD Amount:");
        var foodAmountSpinnerModel = new SpinnerNumberModel(10, 0, 30, 1);
        var foodAmountSpinner = new JSpinner(foodAmountSpinnerModel);
        var hpMinLabel = new JLabel("HP MIN:");
        var hpMinSpinnerModel = new SpinnerNumberModel(25, 0, 120, 1);
        var hpMinSpinner = new JSpinner(hpMinSpinnerModel);
        var mobLabel = new JLabel("MOB:");
        var mobComboBox = new JComboBox<>(CombatingConfig.Mob.values());
        var priceMinLabel = new JLabel("PRICE MIN:");
        var priceMinSpinnerModel = new SpinnerNumberModel(400, 0, 100000000, 1);
        var priceMinSpinner = new JSpinner(priceMinSpinnerModel);
        var equipmentLabel = new JLabel("EQUIPMENT:");
        var equipmentComboBox = new JComboBox<>(CombatingConfig.Equipment.values());
        var goalLabel = new JLabel("GOAL XP K:");
        var goalSpinnerModel = new SpinnerNumberModel(0, 0, 100000, 1);
        var goalSpinner = new JSpinner(goalSpinnerModel);
        var startButton = new JButton("Start");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        startButton.addActionListener((_event) -> {
            var food = (CombatingConfig.Food) foodComboBox.getSelectedItem();
            var foodMin = (Integer) foodMinSpinner.getValue();
            var foodAmount = (Integer) foodAmountSpinner.getValue();
            var hpMin = (Integer) hpMinSpinner.getValue();
            var mob = (CombatingConfig.Mob) mobComboBox.getSelectedItem();
            var priceMin = (Integer) priceMinSpinner.getValue();
            var equipment = (CombatingConfig.Equipment) equipmentComboBox.getSelectedItem();
            var goal = (Integer) goalSpinner.getValue() * 1000;
            main.setConfig(new Config(false, false, true));
            main.setGenericSkill(new CombatingV2(main,
                    skill,
                    food.getId(),
                    foodMin,
                    foodAmount,
                    hpMin,
                    mob.getArea(),
                    mob.getName(),
                    priceMin,
                    mob.getBonesId(),
                    equipment.getEquipmentMap()
                    ));
            main.setGoalXp(goal);
            main.setStart(true);
            dispose();
        });

        // GUI layout:
        //
        // |-Label-|-ComboBox-|
        // |-----Button-------|

        add(mobLabel, "split"); // split will split the current row
        add(mobComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(foodLabel, "split");
        add(foodComboBox, "grow, wrap");
        add(foodMinLabel, "split");
        add(foodMinSpinner, "grow, wrap");
        add(foodAmountLabel, "split");
        add(foodAmountSpinner, "grow, wrap");
        add(hpMinLabel, "split");
        add(hpMinSpinner, "grow, wrap");
        add(priceMinLabel, "split");
        add(priceMinSpinner, "grow, wrap");
        add(equipmentLabel, "split");
        add(equipmentComboBox, "grow, wrap");
        add(goalLabel, "split");
        add(goalSpinner, "grow, wrap");
        add(startButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }
}
