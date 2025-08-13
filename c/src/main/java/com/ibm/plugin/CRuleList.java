package com.ibm.plugin;

import com.ibm.plugin.rules.CInventoryRule;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public final class CRuleList {
    private CRuleList() {}

    private static final Logger LOG = Loggers.get(CRuleList.class);
    private static final String ORIGIN = CRuleList.class.getSimpleName() + ".java";

    public static @Nonnull List<Class<?>> getChecks() {
        List<Class<?>> checks = List.of(CInventoryRule.class);
        LOG.info(
                "CXX {}: event=<rule-list> count={} checks=[{}]",
                ORIGIN,
                checks.size(),
                checks.stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
        return checks;
    }
}
