package everything.skills.smithing;

import everything.Config;
import everything.Main;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class SmithingGUI extends JFrame {
    public SmithingGUI(Main main) {
        super();
        setTitle("Everything Smithing");
        setLayout(new MigLayout("fill, gap 5, insets 10"));
        setResizable(false);

        var barLabel = new JLabel("BAR:");
        var barComboBox = new JComboBox<>(SmithingConfig.Platebody.values());
        var nextButton = new JButton("Start");

        // When the start script button is pressed, we let the script know which mode to run in and remove the GUI
        nextButton.addActionListener((_event) -> {
            var bar = (SmithingConfig.Platebody) barComboBox.getSelectedItem();
            main.setConfig(new Config(true, false, false));
            main.setGenericSkill(new SmithingV2(main, bar.getBarId()));
            main.setStart(true);
            dispose();
        });

        // GUI layout:
        //
        // |-Label-|-ComboBox-|
        // |-----Button-------|

        add(barLabel, "split"); // split will split the current row
        add(barComboBox, "grow, wrap");// grow will let it get as large as it can, wrap will end this row
        add(nextButton, "grow"); // on the second row now, let the button fill the entire row

        pack(); // Fit all of our components to the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Show the GUI
    }

}
