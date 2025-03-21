package com.kbeliasas.everything.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.wrappers.items.Item;

@RequiredArgsConstructor
@Getter
@Setter
public class Loot {
    private final static Integer NATURE_RUNE = 561;

    private Integer id;
    private String name;
    private Integer amount;

    public Integer getProfit() {
        return LivePrices.get(id);
    }

    public boolean isHAProfit() {
        var item = new Item(id, 1);
        return item.getHighAlchValue() > getProfit();
    }

    public Integer getHaProfit() {
        var item = new Item(id, 1);
        return (item.getHighAlchValue() - LivePrices.get(NATURE_RUNE));
    }

    public Integer getGenericProfit() {
        if (isHAProfit()) {
            return getHaProfit() * amount;
        } else {
            return getProfit() * amount;
        }
    }

    public String getMessage() {
        StringBuilder message = new StringBuilder();

        message.append(name)
                .append(" ")
                .append(amount);
        if (isHAProfit()) {
            message.append(". HA Profit: ");
            message.append(getGenericProfit() / 1000);
        } else {
            message.append(". Profit: ");
            message.append(getGenericProfit() / 1000);
        }

        message.append("K ");
        return message.toString();
    }
}
