package com.ibm.plugin;

import java.util.List;
import javax.annotation.Nonnull;
import org.sonar.plugins.cxx.CustomCxxRulesDefinition;
import org.sonarsource.api.sonarlint.SonarLintSide;

@SonarLintSide
public class CCheckRegistrar extends CustomCxxRulesDefinition {
    @Override
    public String repositoryName() {
        return CScannerRuleDefinition.REPOSITORY_NAME;
    }

    @Override
    public String repositoryKey() {
        return CScannerRuleDefinition.REPOSITORY_KEY;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class[] checkClasses() {
        return CRuleList.getChecks().toArray(new Class[0]);
    }

    public static @Nonnull List<Class<?>> checkClasses() {
        return CRuleList.getChecks();
    }

    public static @Nonnull List<Class<?>> testCheckClasses() {
        return List.of();
    }
}
