package com.kbeliasas.everything.skills;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.container.impl.bank.Bank;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Banking {

    public static boolean openBank() {
        var bank = Bank.getClosestBankLocation();
        if (bank != null) {
            return Bank.open(bank);
        }
        return false;
    }
}
