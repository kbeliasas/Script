package com.kbeliasas.everything.skills.magic;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class HAItem {
    private HAConfig.ItemStandard item;
    private Integer purchased = 0;
    private boolean bought = false;
}
