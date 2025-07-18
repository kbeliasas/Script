package com.kbeliasas.everything.skills.magic;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.skills.Banking;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

import java.util.Locale;

import static com.kbeliasas.everything.skills.magic.HAConfig.NATURE_RUNE;
import static com.kbeliasas.everything.skills.magic.HAConfig.STAFF_OF_FIRE;

@RequiredArgsConstructor
public class HAItemV2 implements MagicGeneric {

    private final Main main;
    private final HAConfig.Item item;

    private State state;
    private boolean ready = false;
    private boolean finished = false;
    private Integer profit = 0;
    private Integer startingCoins = 0;
    private Integer startingRunes = 0;


    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case PREP:
                if (Equipment.onlyContains(STAFF_OF_FIRE)) {
                    ready = true;
                    startingRunes = Inventory.count(NATURE_RUNE);
                    startingCoins = Inventory.count(item -> item.getName().toLowerCase(Locale.ROOT).contains("coin"));
                } else if (Inventory.contains(STAFF_OF_FIRE)) {
                    Equipment.equip(EquipmentSlot.WEAPON, STAFF_OF_FIRE);
                } else {
                    if (Banking.openBank()) {
                        Bank.depositAllItems();
                        Bank.depositAllEquipment();
                        Bank.withdraw(STAFF_OF_FIRE);
                        Sleep.sleep(Calculations.random(500, 1000));
                    }
                }
                break;
            case NO_RUNES:
                if (Banking.openBank()) {
                    Bank.withdrawAll(NATURE_RUNE);
                    Sleep.sleep(Calculations.random(500, 1000));
                    Bank.withdrawAll(item -> item.getName().toLowerCase(Locale.ROOT).contains("coin"));
                    Sleep.sleep(Calculations.random(1000, 2000));
                    startingRunes = Inventory.count(NATURE_RUNE);
                    startingCoins = Inventory.count(item -> item.getName().toLowerCase(Locale.ROOT).contains("coin"));
                }
                break;
            case BANKING:
                if (Banking.openBank()) {
                    var coins = Inventory.get(item -> item.getName().toLowerCase(Locale.ROOT).contains("coin"));
                    Bank.depositAllExcept(NATURE_RUNE, coins.getID());
                    Sleep.sleep(Calculations.random(500, 1000));

                    if (Bank.contains(item.getId())) {
                        Bank.withdrawAll(item.getId());
                        Sleep.sleep(Calculations.random(500, 1000));
                    } else {
                        finished = true;
                    }
                    var runesConsumption = startingRunes - Inventory.count(NATURE_RUNE);
                    profit = coins.getAmount() - startingCoins - (runesConsumption * LivePrices.get(NATURE_RUNE));
                    Sleep.sleep(Calculations.random(500, 1000));

                    main.addLoot(item.getId(), item.name(), Inventory.count(item.getId()));
                    main.setGoal(Bank.count(item.getId()));

                    Bank.close();
                }
                break;
            case ALCHING:
                Magic.castSpell(Normal.HIGH_LEVEL_ALCHEMY);
                Sleep.sleep(Calculations.random(1000, 1500));
                var inventoryItem = Inventory.get(item.getId());
                if (inventoryItem != null) {
                    inventoryItem.interact();
                }
                Sleep.sleep(Calculations.random(500, 1000));
                var runesConusmption = startingRunes - Inventory.count(NATURE_RUNE);
                var coins = Inventory.get(coin -> coin.getName().toLowerCase(Locale.ROOT).contains("coin"));
                profit = coins.getAmount() - startingCoins - (runesConusmption * LivePrices.get(NATURE_RUNE));
                break;
            case FINISHED:
                if (Banking.openBank()) {
                    Bank.depositAllItems();
                    Bank.depositAllEquipment();
                    Logger.log("Alched all items");
                    main.printResults();
                    main.showResults();
                    ScriptManager.getScriptManager().stop();
                }
                break;
            case FAILURE:
                Logger.error("ERROR State failed to set state;");
                main.printResults();
                main.showResults();
                ScriptManager.getScriptManager().stop();
                break;
        }
        main.setProfit(profit);
    }

    private enum State {
        PREP,
        NO_RUNES,
        SHOPPING,
        WAITING,
        SEARCHING_FOR_NEW_ITEM,
        BANKING,
        ALCHING,
        FINISHED,
        FAILURE
    }

    private void setState() {
        if (!ready) {
            state = State.PREP;
            return;
        }

        if (!Inventory.contains(NATURE_RUNE)) {
            state = State.NO_RUNES;
            return;
        }

        if (finished) {
            state = State.FINISHED;
            return;
        }

        if (!Inventory.contains(item.getId())) {
            state = State.BANKING;
            return;
        }

        if (Inventory.contains(item.getId())) {
            state = State.ALCHING;
            return;
        }

        state = State.FAILURE;
    }
}
