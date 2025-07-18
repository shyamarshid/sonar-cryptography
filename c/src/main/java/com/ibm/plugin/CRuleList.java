package com.ibm.plugin;

import com.ibm.plugin.rules.CInventoryRule;
import java.util.List;
import javax.annotation.Nonnull;

public final class CRuleList {
    private CRuleList() {}

    public static @Nonnull List<Class<?>> getChecks() {
        return List.of(CInventoryRule.class);
    }
}
