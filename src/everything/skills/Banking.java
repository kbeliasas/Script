package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class Banking {


    public static boolean openBank() {
        Main.state = States.BANKING;
        var bank = Bank.getClosestBankLocation();
//        var bank = BankLocation.LUMBRIDGE;
        if (bank != null) {
            return Bank.open(bank);
        }
        return false;
    }

    public static void putAllItems() {
        Main.state = States.BANKING;
        var bank = Bank.getClosestBankLocation();
//        var bank = BankLocation.LUMBRIDGE;
        if (bank != null) {
            if (Bank.open(bank)) {
                Bank.depositAllItems();
//                Bank.depositAllExcept("Mind rune");
//                Sleep.sleep(Calculations.random(600, 900));
//                Bank.withdrawAll("Rune essence");
//                Sleep.sleep(Calculations.random(600, 900));
//                Bank.withdrawAll("Raw anchovies");
//                Bank.depositAllExcept("Small fishing net");
//                if (!Bank.contains("Rune essence")) {
//                    Logger.log("Goal reached");
//                    ScriptManager.getScriptManager().stop();
//                }
                Main.state = States.IDLE;
            }
        }
    }

    public static void putAllAndFinishWhen(String name, int amount) {
        Main.state = States.BANKING;
        var bank = Bank.getClosestBankLocation();
        if (bank != null) {
            if (Bank.open(bank)) {
                Bank.depositAllItems();
                var amountInBank = Bank.count(name);
                if (amountInBank >= amount) {
                    Logger.debug("Amount in Bank: " + amountInBank);
                    Logger.debug("Target: " + amount);
                    Logger.log("Goal reached");
                    ScriptManager.getScriptManager().stop();
                }
                Main.state = States.IDLE;
            }
        }
    }

    public static void putAllAndGetAllExcept(String name, String nameExcluded) {
        Main.state = States.BANKING;
        var bank = Bank.getClosestBankLocation();
        if (bank != null) {
            if (Bank.open(bank)) {
                Bank.depositAllExcept(nameExcluded);
                Sleep.sleep(Calculations.random(500, 800));
                if (!Bank.contains(name)) {
                    Logger.log("Goal reached");
                    ScriptManager.getScriptManager().stop();
                }
                Bank.withdrawAll(name);
                Main.state = States.IDLE;
            }
        }
    }

    public static void putAllAndGetAllExcept(String name, String nameExcluded1, String nameExcluded2) {
        Main.state = States.BANKING;
        var bank = Bank.getClosestBankLocation();
        if (bank != null) {
            if (Bank.open(bank)) {
                Bank.depositAllExcept(filter ->
                        filter.getName().equalsIgnoreCase(nameExcluded1)
                                || filter.getName().equalsIgnoreCase(nameExcluded2));
                Sleep.sleep(Calculations.random(500, 800));
                if (!Bank.contains(name)) {
                    Logger.log("Goal reached");
                    ScriptManager.getScriptManager().stop();
                }
                Bank.withdrawAll(name);
                Main.state = States.IDLE;
            }
        }
    }

    public static void putAllAndGet(String name1, int amount1, String name2, int amount2) {
        Main.state = States.BANKING;
        var bank = Bank.getClosestBankLocation();
        if (bank != null) {
            if (Bank.open(bank)) {
                Bank.depositAllItems();
                Sleep.sleep(Calculations.random(500, 800));
                if (!Bank.contains(name1) || !Bank.contains(name2)) {
                    Logger.log("Goal reached");
                    ScriptManager.getScriptManager().stop();
                }
                Bank.withdraw(name1, amount1);
                Sleep.sleep(Calculations.random(500, 800));
                Bank.withdraw(name2, amount2);
                Main.state = States.IDLE;
            }
        }
    }
}
