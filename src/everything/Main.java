package everything;

import everything.naturalmouse.api.MouseMotionFactory;
import everything.naturalmouse.support.DefaultMouseMotionNature;
import everything.naturalmouse.support.RsMouseInfoAccessor;
import everything.naturalmouse.support.RsSystemCalls;
import everything.naturalmouse.util.FactoryTemplates;
import everything.skills.*;
import org.dreambot.api.Client;
import org.dreambot.api.input.mouse.algorithm.StandardMouseAlgorithm;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.ViewportTools;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.core.Instance;

@ScriptManifest(name = "Everything", description = "My script description!", author = "Karolis",
        version = 1.0, category = Category.UTILITY, image = "")
public class Main extends AbstractScript {

    public static States state = States.IDLE;

    public static MouseMotionFactory mouseMotionFactory;

    private static States cashedState = States.IDLE;

    @Override
    public void onStart() {
        Logger.info("Welcome");
        var nature = new DefaultMouseMotionNature(new RsSystemCalls(), new RsMouseInfoAccessor());
        mouseMotionFactory = FactoryTemplates.createFastGamerMotionFactory(nature);
        Client.getInstance().setMouseMovementAlgorithm(new NaturalMouse());
    }

    @Override
    public int onLoop() {

//        Mining.mine();
//        Smithing.smelt();
//        Smithing.smith();
//        Woodcutting.cut();
        Crafting.makeLeatherArmor();
        stateTracker();
        return Calculations.random(1000, 2000);

//        if (Skills.getRealLevel(Skill.MAGIC) >= 17 || Inventory.count("Mind rune") <= 25) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                Logger.error("Something wrong with thread sleep", e);
//                throw new RuntimeException(e);
//            }
//            Logger.log("Target level reached!");
//            ScriptManager.getScriptManager().stop();
//        }

//        if (!Players.getLocal().isAnimating()) {
//
//            if (state.equals(States.BANKING)) {
//                Banking.putAllItems();
//            }
//            if (Inventory.isFull()) {
////                var portal = GameObjects.closest("Portal");
////                if (portal != null && portal.canReach()) {
////                    portal.interact("Exit");
////                    portal.interact("Use");
////                    if (Main.state.equals(States.BANKING)) {
////                        Walking.walk(portal.getTile());
////                        Logger.warn("Something wrong with teleport: " + portal);
////                        Logger.warn(portal.getActions());
////                    }
////                }
//                state = States.PRAYER;
////                Banking.putAllItems();
//            }

//            if (!Inventory.contains("Raw anchovies")) {
//                Logger.log("Inventory is full");
//                Inventory.dropAll("Burnt fish");

//            RuneCrafting.craft();
//            RuneEssence.mine();
//            everyting.skills.Cooking.cook();
//            everyting.Fishing.fish();

//            Bank.open(BankLocation.FALADOR_EAST);
//            Bank.open(BankLocation.VARROCK_WEST);
//            GrandExchange.open();
//            if (state.equals(States.PRAYER)) {
//                Prayering.buryBones();
//            } else {
//                Combating.attack();
//            }
//        }

    }

    @Override
    public void stop() {
        Client.getInstance().setMouseMovementAlgorithm(new StandardMouseAlgorithm());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Logger.error("Something wrong with thread sleep", e);
            throw new RuntimeException(e);
        }
    }

    private void stateTracker() {
        if (!state.equals(cashedState)) {
            Logger.info("Stated changed from " + cashedState.name() + " to " + state.name());
            cashedState = state;
        }
    }
}