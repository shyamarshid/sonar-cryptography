package com.ibm.plugin;

import java.util.List;
import javax.annotation.Nonnull;
import org.sonar.api.batch.rule.CheckRegistrar;
import org.sonarsource.api.sonarlint.SonarLintSide;

@SonarLintSide

public class CCheckRegistrar implements CheckRegistrar {
    @Override
    public void register(RegistrarContext registrarContext) {
        registrarContext.registerClassesForRepository(
                CScannerRuleDefinition.REPOSITORY_KEY, checkClasses(), testCheckClasses());
    }

    public static @Nonnull List<Class<?>> checkClasses() {
        return CRuleList.getChecks();
    }

    public static @Nonnull List<Class<?>> testCheckClasses() {
        return List.of();
    }
}
