package com.kbeliasas.everything.skills.firemaking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class FiremakingConfig {
    @RequiredArgsConstructor
    @Getter
    enum Log {
        WILLOW(1519);

        private final Integer logId;
    }
}
